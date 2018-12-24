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
package org.codenergic.theskeleton.gallery;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xmlpull.v1.XmlPullParserException;

import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidArgumentException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.NoResponseException;

@Service
@Transactional(readOnly = true)
class GalleryServiceImpl implements GalleryService {
	private static final String GALLERY_BUCKET_NAME = "galleries";
	private final GalleryRepository galleryRepository;
	private final MinioClient minioClient;

	public GalleryServiceImpl(GalleryRepository galleryRepository, MinioClient minioClient) {
		this.galleryRepository = galleryRepository;
		this.minioClient = minioClient;
	}

	@Override
	@Transactional
	public void deleteImages(String userId, String... imagesId) {
		for (String imageId : imagesId) {
			galleryRepository.delete(imageId);
		}
	}

	@Override
	public Page<GalleryEntity> findImageByUser(String userId, Pageable pageable) {
		return galleryRepository.findByCreatedByUserIdOrderByIdDesc(userId, pageable);
	}

	@Override
	@Transactional
	public GalleryEntity saveImage(String userId, GalleryEntity image) throws Exception {
		String imageObjectName = saveImageToS3(userId, image);
		image.setImageUrl(imageObjectName);
		return galleryRepository.save(image);
	}

	private String saveImageToS3(String userId, GalleryEntity image) throws InvalidBucketNameException, NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException, NoResponseException, XmlPullParserException, ErrorResponseException, InternalException, InvalidArgumentException {
		String imageObjectName = StringUtils.join(userId, "/", Long.toHexString(Instant.now().toEpochMilli()),
			"-", UUID.randomUUID().toString());
		minioClient.putObject(GALLERY_BUCKET_NAME, imageObjectName, image.getImage(), image.getFormat());
		return minioClient.getObjectUrl(GALLERY_BUCKET_NAME, imageObjectName);
	}
}
