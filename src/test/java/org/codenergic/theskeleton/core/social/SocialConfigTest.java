/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codenergic.theskeleton.core.social;

import org.codenergic.theskeleton.socialconnection.SocialConnectionRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SocialConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SocialConfigTest {
	@MockBean
	private ConnectionFactoryLocator connectionFactoryLocator;
	@MockBean
	private SocialConnectionRepository socialConnectionRepository;
	@MockBean
	private UsersConnectionRepository usersConnectionRepository;
	@MockBean
	private UserDetailsService userDetailsService;

	@Autowired
	private SocialConfig socialConfig;

	@Test
	public void initTest() {
		// do nothing
	}
}
