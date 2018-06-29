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

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.minio.MinioClient;
import io.minio.errors.InvalidBucketNameException;
import io.minio.policy.PolicyType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class S3ClientConfigTest {
	private S3ClientConfig s3ClientConfig = new S3ClientConfig();
	private S3ClientConfig.S3ClientProperties s3ClientProperties = new S3ClientConfig.S3ClientProperties();
	@Mock
	private MinioClient minioClient;
	@Mock
	private ScheduledExecutorService executorService;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		s3ClientProperties.setAccessKey("");
		s3ClientProperties.setSecretKey("");
		s3ClientProperties.setEndpoint("");
		Stream.of("test1", "test2").forEach(bucketName -> {
			S3ClientConfig.S3BucketProperties bucketProperties = new S3ClientConfig.S3BucketProperties();
			bucketProperties.setName(bucketName);
			bucketProperties.getPolicies().add(new S3ClientConfig.S3BucketPolicyProperties()
				.setPolicy(PolicyType.NONE)
				.setPrefix("*"));
			s3ClientProperties.getBuckets().add(bucketProperties);
		});
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateBucket() throws Exception {
		ArgumentCaptor<Callable<List<String>>> argumentCaptor = ArgumentCaptor.forClass(Callable.class);
		when(executorService.schedule(argumentCaptor.capture(), anyLong(), any())).then(invocation -> {
			Callable<List<String>> callable = invocation.getArgument(0);
			callable.call();
			return null;
		});
		when(minioClient.bucketExists(eq("test1"))).thenReturn(true);
		when(minioClient.bucketExists(eq("test2"))).thenReturn(false);
		doThrow(new RuntimeException()).when(minioClient).setBucketPolicy(eq("test2"), anyString(), any());
		s3ClientConfig.createBuckets(minioClient, executorService, s3ClientProperties);
		verify(minioClient, times(2)).bucketExists(anyString());
		verify(minioClient).makeBucket(eq("test2"));
		verify(executorService).schedule(argumentCaptor.capture(), anyLong(), any());
		verify(minioClient, times(2)).setBucketPolicy(anyString(), anyString(), any());
		when(minioClient.bucketExists(anyString())).thenThrow(InvalidBucketNameException.class);
		s3ClientConfig.createBuckets(minioClient, executorService, s3ClientProperties);
	}
}
