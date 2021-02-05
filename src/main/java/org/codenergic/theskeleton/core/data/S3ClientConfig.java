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

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;

@Configuration
class S3ClientConfig {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Bean
	public ScheduledFuture<List<String>> createBuckets(MinioClient minioClient, ScheduledExecutorService executorService, S3ClientProperties clientProps) {
		return executorService.schedule(() -> clientProps.buckets.stream()
			.peek(bucket -> logger.info("Checking bucket [{}]", bucket.name))
			.peek(bucket -> {
				try {
					if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket.name).build())) {
						logger.info("Bucket doesn't exists, creating one");
						minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket.name).build());
						logger.info("Bucket created");
					} else {
						logger.info("Bucket already exists");
					}
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			})
			.map(bucket -> bucket.name)
			.collect(Collectors.toList()), 5, TimeUnit.SECONDS);
	}

	@Bean
	public MinioClient s3Client(S3ClientProperties clientProps) throws MalformedURLException {
		return MinioClient.builder()
			.endpoint(URI.create(clientProps.endpoint).toURL())
			.credentials(clientProps.accessKey, clientProps.secretKey)
			.build();
	}

	@Bean
	@ConfigurationProperties("s3.client")
	public S3ClientProperties s3ClientProperties() {
		return new S3ClientProperties();
	}

}
