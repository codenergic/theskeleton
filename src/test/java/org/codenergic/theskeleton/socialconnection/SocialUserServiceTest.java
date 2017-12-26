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

package org.codenergic.theskeleton.socialconnection;

import org.codenergic.theskeleton.user.UserEntity;
import org.codenergic.theskeleton.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SocialUserServiceTest {
	@Mock
	private UserRepository userRepository;
	private SocialUserService socialUserService;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		socialUserService = new SocialUserService(userRepository);
	}

	@Test
	public void testLoadUserByUserId() {
		when(userRepository.findOne("123")).thenReturn(new UserEntity());
		socialUserService.loadUserByUserId("123");
		verify(userRepository).findOne("123");
	}
}
