/*
 * Copyright 2018 the original author or authors.
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

import org.codenergic.theskeleton.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

@RestController
@RequestMapping("/api/galleries")
public class GalleryRestController {
	private final GalleryService galleryService;

	public GalleryRestController(GalleryService galleryService) {
		this.galleryService = galleryService;
	}

	@DeleteMapping
	public void deleteImages(@AuthenticationPrincipal UserEntity userEntity, @RequestBody String... imagesId) {
		galleryService.deleteImages(userEntity.getId(), imagesId);
	}

	@GetMapping
	public Page<GalleryRestData> findImageByUser(@AuthenticationPrincipal UserEntity userEntity, Pageable pageable) {
		return galleryService.findImageByUser(userEntity.getId(), pageable)
			.map(g -> GalleryRestData.builder().fromGalleryEntity(g).build());
	}

	@PostMapping(consumes = {"images/*"})
	public GalleryRestData saveImage(@AuthenticationPrincipal UserEntity userEntity, HttpServletRequest request) throws Exception {
		try (InputStream inputStream = request.getInputStream()) {
			GalleryEntity galleryEntity = galleryService.saveImage(userEntity.getId(), new GalleryEntity()
				.setImage(inputStream).setFormat(request.getContentType()));
			return GalleryRestData.builder().fromGalleryEntity(galleryEntity).build();
		}
	}


}
