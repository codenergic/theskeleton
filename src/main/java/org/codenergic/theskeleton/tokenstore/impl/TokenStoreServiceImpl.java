package org.codenergic.theskeleton.tokenstore.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.codenergic.theskeleton.core.mail.EmailService;
import org.codenergic.theskeleton.registration.*;
import org.codenergic.theskeleton.tokenstore.TokenStoreEntity;
import org.codenergic.theskeleton.tokenstore.TokenStoreRepository;
import org.codenergic.theskeleton.tokenstore.TokenStoreService;
import org.codenergic.theskeleton.tokenstore.TokenStoreType;
import org.codenergic.theskeleton.user.UserEntity;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenStoreServiceImpl implements TokenStoreService {

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
	public TokenStoreEntity findByTokenAndType(String token, TokenStoreType type) {
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
