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

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Configuration
public class S3ClientConfig {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Bean
	public ScheduledFuture<List<String>> createBuckets(MinioClient minioClient, ScheduledExecutorService executorService, S3ClientProperties clientProps) {
		return executorService.schedule(() -> {
			try {
				for (String bucket : clientProps.buckets) {
					logger.info("Checking bucket: {}", bucket);
					if (minioClient.bucketExists(bucket))
						continue;
					logger.info("Bucket doesn't exist, creating one");
					minioClient.makeBucket(bucket);
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				logger.info("Bucket successfully created");
			}
			return clientProps.buckets;
		}, 5, TimeUnit.SECONDS);
	}

	@Bean
	public MinioClient s3Client(S3ClientProperties clientProps) throws InvalidEndpointException, InvalidPortException {
		return new MinioClient(clientProps.endpoint, clientProps.accessKey, clientProps.secretKey);
	}

	@Bean
	@ConfigurationProperties("s3.client")
	public S3ClientProperties s3ClientProperties() {
		return new S3ClientProperties();
	}

	protected static class S3ClientProperties {
		private String accessKey;
		private List<String> buckets = new ArrayList<>();
		private String endpoint;
		private String secretKey;

		public List<String> getBuckets() {
			return buckets;
		}

		public void setAccessKey(String accessKey) {
			this.accessKey = accessKey;
		}

		public void setEndpoint(String endpoint) {
			this.endpoint = endpoint;
		}

		public void setSecretKey(String secretKey) {
			this.secretKey = secretKey;
		}
	}
}
