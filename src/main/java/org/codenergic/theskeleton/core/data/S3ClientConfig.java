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

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xmlpull.v1.XmlPullParserException;

import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.NoResponseException;
import io.minio.errors.RegionConflictException;

@Configuration
public class S3ClientConfig {
	@Bean
	public MinioClient s3Client(S3ClientProperties clientProps) throws InvalidEndpointException, InvalidPortException {
		return new MinioClient(clientProps.endpoint, clientProps.accessKey, clientProps.secretKey);
	}

	@Bean
	@ConfigurationProperties("s3.client")
	public S3ClientProperties s3ClientProperties() {
		return new S3ClientProperties();
	}

	@Autowired
	public void initializeBucket(MinioClient client) throws InvalidKeyException, InvalidBucketNameException,
			RegionConflictException, NoSuchAlgorithmException, InsufficientDataException, NoResponseException,
			ErrorResponseException, InternalException, IOException, XmlPullParserException {
		if (!client.bucketExists("theskeleton"))
			client.makeBucket("theskeleton");
	}

	protected static class S3ClientProperties {
		private String endpoint;
		private String accessKey;
		private String secretKey;

		public void setEndpoint(String endpoint) {
			this.endpoint = endpoint;
		}

		public void setAccessKey(String accessKey) {
			this.accessKey = accessKey;
		}

		public void setSecretKey(String secretKey) {
			this.secretKey = secretKey;
		}
	}
}
