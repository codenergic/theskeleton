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
import io.minio.policy.PolicyType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Configuration
class S3ClientConfig {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Bean
	public ScheduledFuture<List<String>> createBuckets(MinioClient minioClient, ScheduledExecutorService executorService, S3ClientProperties clientProps) {
		return executorService.schedule(() -> {
			try {
				List<String> bucketNames = new ArrayList<>();
				for (S3BucketProperties bucket : clientProps.buckets) {
					bucketNames.add(bucket.name);
					logger.info("Checking bucket [{}]", bucket.name);
					if (!minioClient.bucketExists(bucket.name)) {
						logger.info("Bucket doesn't exists, creating one");
						minioClient.makeBucket(bucket.name);
						logger.info("Bucket created");
					} else {
						logger.info("Bucket already exists");
					}
					for (S3BucketPolicyProperties policy : bucket.getPolicies()) {
						if (policy.policy == null || StringUtils.isBlank(policy.prefix))
							continue;
						logger.info("Setting policy [{}] to bucket [{}] with prefix [{}]", policy.policy, bucket.name, policy.prefix);
						minioClient.setBucketPolicy(bucket.name, policy.prefix, policy.policy);
					}
				}
				return Collections.unmodifiableList(bucketNames);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			return Collections.emptyList();
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

	public static class S3ClientProperties {
		private String accessKey;
		private List<S3BucketProperties> buckets = new ArrayList<>();
		private String endpoint;
		private String secretKey;

		public List<S3BucketProperties> getBuckets() {
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

	public static class S3BucketProperties {
		private String name;
		private List<S3BucketPolicyProperties> policies = new ArrayList<>();

		public List<S3BucketPolicyProperties> getPolicies() {
			return policies;
		}

		public S3BucketProperties setName(String name) {
			this.name = name;
			return this;
		}
	}

	public static class S3BucketPolicyProperties {
		private PolicyType policy;
		private String prefix = "*";

		public S3BucketPolicyProperties setPolicy(PolicyType policy) {
			this.policy = policy;
			return this;
		}

		public S3BucketPolicyProperties setPrefix(String prefix) {
			this.prefix = prefix;
			return this;
		}
	}
}
