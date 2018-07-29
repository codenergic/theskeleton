/*
 * Copyright 2018 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codenergic.theskeleton.user;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/{username}/socials")
public class UserSocialRestController {
	private final ConnectionRepository connectionRepository;
	private final UserMapper userMapper = UserMapper.newInstance();

	public UserSocialRestController(ConnectionRepository connectionRepository) {
		this.connectionRepository = connectionRepository;
	}

	private UserSocialRestData mapConnections(Map.Entry<String, List<Connection<?>>> e) {
		Connection<?> connection = e.getValue().get(0);
		return userMapper.toUserSocialData(connection, connection.createData());
	}

	@DeleteMapping
	public void removeUserSocialConnection(@RequestBody String provider) {
		connectionRepository.removeConnections(provider);
	}

	@GetMapping
	public Map<String, UserSocialRestData> findUserSocialConnections() {
		return connectionRepository.findAllConnections().entrySet()
			.stream()
			.map(this::mapConnections)
			.collect(Collectors.toMap(UserSocialRestData::getProvider, Function.identity()));
	}
}
