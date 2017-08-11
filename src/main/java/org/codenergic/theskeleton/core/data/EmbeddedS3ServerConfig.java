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
package org.codenergic.theskeleton.core.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.findify.s3mock.S3Mock;
import io.findify.s3mock.S3Mock.Builder;

@Configuration
@ConditionalOnClass(name = "io.findify.s3mock.S3Mock")
public class EmbeddedS3ServerConfig {
	@Bean(destroyMethod = "stop")
	public S3Mock s3Mock(EmbeddedS3ServerProperties props) {
		Builder builder = new S3Mock.Builder().withPort(props.port);
		if (props.inMemory)
			return builder.withInMemoryBackend().build();
		else
			return builder.withFileBackend(props.storageLocation).build();
	}

	@Bean
	@ConfigurationProperties("s3.server")
	public EmbeddedS3ServerProperties embeddedS3ServerProperties() {
		return new EmbeddedS3ServerProperties();
	}

	@Autowired
	public void startEmbeddedS3Server(S3Mock s3Mock) {
		s3Mock.start();
	}

	protected static class EmbeddedS3ServerProperties {
		private int port = 8081;
		private boolean inMemory = false;
		private String storageLocation;

		public void setPort(int port) {
			this.port = port;
		}

		public void setInMemory(boolean inMemory) {
			this.inMemory = inMemory;
		}

		public void setStorageLocation(String storageLocation) {
			this.storageLocation = storageLocation;
		}
	}
}
