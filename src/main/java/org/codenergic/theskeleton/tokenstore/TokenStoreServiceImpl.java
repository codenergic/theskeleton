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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.RandomStringUtils;
import org.codenergic.theskeleton.core.mail.EmailService;
import org.codenergic.theskeleton.registration.RegistrationException;
import org.codenergic.theskeleton.user.UserEntity;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class TokenStoreServiceImpl implements TokenStoreService {

	@Value("${email.baseurl}")
	private String baseUrl;

	private EmailService emailService;
	private TokenStoreRepository tokenStoreRepository;

	public TokenStoreServiceImpl(EmailService emailService, TokenStoreRepository tokenStoreRepository) {
		this.emailService = emailService;
		this.tokenStoreRepository = tokenStoreRepository;
	}

	@Override
	public TokenStoreEntity sendTokenNotification(TokenStoreType type, UserEntity user) {
		String token = RandomStringUtils.randomAlphabetic(24);
		TokenStoreEntity tokenStoreEntity = new TokenStoreEntity()
			.setToken(token)
			.setUser(user)
			.setType(type)
			.setExpiryDate(DateTime.now().plusDays(15).toDate());
		sendEmail(tokenStoreEntity, user);
		return tokenStoreRepository.save(tokenStoreEntity);
	}

	@Override
	public Optional<TokenStoreEntity> findByTokenAndType(String token, TokenStoreType type) {
		return tokenStoreRepository.findByTokenAndType(token, type);
	}

	@Override
	@Transactional
	public void deleteTokenByUser(UserEntity userEntity) {
		tokenStoreRepository.deleteTokenStoreEntityByUser(userEntity);
	}


	private void sendEmail(TokenStoreEntity token, UserEntity user) {
		Map<String, Object> params = new HashMap<>();
		String subject;
		String template;
		if (TokenStoreType.USER_ACTIVATION.equals(token.getType())){
			params.put("activationUrl", baseUrl + "/registration/activate?at=" + token.getToken());
			subject = "Registration Confirmation";
			template = "email/registration.html";
		} else {
			params.put("changepassUrl", baseUrl + "/changepass/update?rt=" + token.getToken());
			subject = "Reset Password Confirmation";
			template = "email/changepass.html";
		}
		try {
			emailService.sendEmail(null, new InternetAddress(user.getEmail()), subject, params, template);
		} catch (AddressException e) {
			throw new RegistrationException("Unable to send activation link");
		}
	}
}
