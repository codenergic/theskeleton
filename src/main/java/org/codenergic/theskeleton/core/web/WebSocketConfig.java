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

package org.codenergic.theskeleton.core.web;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic");
		registry.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("ws").setAllowedOrigins("*").withSockJS();
	}

	@Bean
	public UTCMessageSender serverTimeMessageSender(SimpMessagingTemplate messagingTemplate) {
		return new UTCMessageSender(messagingTemplate);
	}

	private static class UTCMessageSender {
		private final SimpMessagingTemplate messagingTemplate;

		private UTCMessageSender(SimpMessagingTemplate messagingTemplate) {
			this.messagingTemplate = messagingTemplate;
		}

		@Scheduled(fixedRate = 1_000)
		public void sendCurrentUTCTime() {
			String currentUTCTime = Instant.now().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_DATE_TIME);
			messagingTemplate.convertAndSend("/topic/server-time", currentUTCTime);
		}
	}
}
