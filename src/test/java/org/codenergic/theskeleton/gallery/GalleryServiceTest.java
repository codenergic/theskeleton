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

package org.codenergic.theskeleton.gallery;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import org.codenergic.theskeleton.core.data.S3ClientProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GalleryServiceTest {
	@Mock
	private GalleryRepository galleryRepository;
	@Mock
	private MinioClient minioClient;
	private GalleryService galleryService;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		galleryService = new GalleryServiceImpl(galleryRepository, minioClient, new S3ClientProperties());
	}

	@Test
	public void testDeleteImages() {
		galleryService.deleteImages("123", "1234", "12345");
		verify(galleryRepository).deleteById("1234");
		verify(galleryRepository).deleteById("12345");
	}

	@Test
	public void testFindImageByUser() {
		when(galleryRepository.findByCreatedByUserIdOrderByIdDesc(eq("1234"), any()))
			.thenReturn(new PageImpl<>(new ArrayList<>()));
		Page<GalleryEntity> result = galleryService.findImageByUser("1234", PageRequest.of(0, 10));
		assertThat(result).hasSize(0);
		assertThat(result.getTotalPages()).isEqualTo(1);
		verify(galleryRepository).findByCreatedByUserIdOrderByIdDesc(eq("1234"), any());
	}

	@Test
	public void testSaveImage() throws Exception {
		when(minioClient.putObject(any())).thenReturn(new ObjectWriteResponse(null, "bucket", "region", "object", "etag", "version"));
		galleryService.saveImage("12345", new GalleryEntity()
			.setFormat("image/png")
			.setImage(new ByteArrayInputStream(new byte[0])));
		verify(minioClient).putObject(any());
	}

}
