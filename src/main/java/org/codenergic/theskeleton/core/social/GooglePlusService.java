/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenergic.theskeleton.core.social;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.plus.Person;

public class GooglePlusService extends AbstractSocialService<Google> {
	public GooglePlusService(OAuth2ConnectionFactory<Google> google) {
		super(google);
	}

	@Override
	public UserEntity createUser(Connection<?> connection) {
		Google google = (Google) connection.getApi();
		Person profile = google.plusOperations().getGoogleProfile();
		String randomUsername = RandomStringUtils.randomAlphanumeric(6);
		return new UserEntity()
			.setUsername(StringUtils.join(profile.getGivenName(), profile.getFamilyName(), randomUsername))
			.setEmail(profile.getEmailAddresses().iterator().next())
			.setPassword(RandomStringUtils.randomAlphanumeric(8));
	}
}
