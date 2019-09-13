/*
 * Copyright 2018 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.codenergic.theskeleton.tokenstore;

import java.io.IOException;
import java.time.Instant;
import java.time.Period;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.codenergic.theskeleton.core.mail.EmailService;
import org.codenergic.theskeleton.registration.RegistrationException;
import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.SignerVerifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
class TokenStoreServiceImpl implements TokenStoreService {
	private final String emailBaseUrl;
	private final EmailService emailService;
	private final UserRepository userRepository;
	private final ObjectMapper objectMapper;

	public TokenStoreServiceImpl(@Value("${email.baseurl}") String emailBaseUrl, EmailService emailService,
								 UserRepository userRepository, ObjectMapper objectMapper) {
		this.emailBaseUrl = emailBaseUrl;
		this.emailService = emailService;
		this.userRepository = userRepository;
		this.objectMapper = objectMapper;
	}

	private SignerVerifier createSignerVerifier(UserEntity user) {
		return new MacSigner(user.getPassword() + user.getLastModifiedDate().toInstant().toString());
	}

	@Override
	public TokenStoreRestData findAndVerifyToken(String token) {
		try {
			Jwt jwt = JwtHelper.decode(token);
			TokenStoreRestData data = objectMapper.readValue(jwt.getClaims(), TokenStoreRestData.class);
			UserEntity user = userRepository.findById(data.getUserId())
				.orElseThrow(() -> new UsernameNotFoundException(data.getUserId()));
			SignerVerifier verifier = createSignerVerifier(user);
			jwt.verifySignature(verifier);
			return ImmutableTokenStoreRestData.builder()
				.from(data)
				.signedToken(jwt.getEncoded())
				.user(user)
				.build();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private void sendEmail(String token, TokenStoreType type, String email) {
		Map<String, Object> params = new HashMap<>();
		String subject;
		String template;
		if (TokenStoreType.USER_ACTIVATION.equals(type)) {
			params.put("activationUrl", emailBaseUrl + "/registration/activate?at=" + token);
			subject = "Registration Confirmation";
			template = "email/registration.html";
		} else {
			params.put("changepassUrl", emailBaseUrl + "/changepass/update?rt=" + token);
			subject = "Reset Password Confirmation";
			template = "email/changepass.html";
		}
		try {
			emailService.sendEmail(null, new InternetAddress(email), subject, params, template);
		} catch (AddressException e) {
			throw new RegistrationException("Unable to send activation link");
		}
	}

	@Override
	public TokenStoreRestData sendTokenNotification(TokenStoreType type, UserEntity user) {
		try {
			TokenStoreRestData data = ImmutableTokenStoreRestData.builder()
				.userId(user.getId())
				.expiryDate(Date.from(Instant.now().plus(Period.ofDays(1))))
				.tokenType(type)
				.uuid(UUID.randomUUID())
				.build();
			SignerVerifier signer = createSignerVerifier(user);
			Jwt token = JwtHelper.encode(objectMapper.writeValueAsString(data), signer);
			sendEmail(token.getEncoded(), type, user.getEmail());
			return ImmutableTokenStoreRestData.builder()
				.from(data)
				.signedToken(token.getEncoded())
				.user(user)
				.build();
		} catch (JsonProcessingException e) {
			throw new IllegalStateException(e);
		}
	}
}
