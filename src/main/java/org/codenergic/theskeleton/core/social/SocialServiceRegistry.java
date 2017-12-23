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

import java.util.ArrayList;
import java.util.List;

import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;

public class SocialServiceRegistry extends ConnectionFactoryRegistry implements SocialServiceLocator {
	private List<SocialService<?>> socialServices = new ArrayList<SocialService<?>>();

	public void addSocialService(SocialService<?> socialService) {
		super.addConnectionFactory(socialService.getConnectionFactory());
		socialServices.add(socialService);
	}

	public void addSocialServices(List<SocialService<?>> socialServices) {
		socialServices.forEach(socialService -> addSocialService(socialService));
	}

	@Override
	public void addConnectionFactory(ConnectionFactory<?> connectionFactory) {
		throw new IllegalArgumentException("Use addSocialService(socialService) instead");
	}

	@Override
	public SocialService<?> getSocialService(String name) {
		return socialServices.stream()
				.filter(service -> service.getConnectionFactory().equals(getConnectionFactory(name)))
				.findFirst()
				.orElse(null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> SocialService<T> getSocialService(Class<T> apiType) {
		return socialServices.stream()
				.filter(service -> service.getConnectionFactory().equals(getConnectionFactory(apiType)))
				.map(service -> (SocialService<T>) service)
				.findFirst()
				.orElse(null);
	}
}
