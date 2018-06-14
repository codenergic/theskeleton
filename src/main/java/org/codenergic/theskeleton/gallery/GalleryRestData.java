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

import org.codenergic.theskeleton.core.data.RestData;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_GalleryRestData.Builder.class)
abstract class GalleryRestData implements RestData {
	static Builder builder() {
		return new AutoValue_GalleryRestData.Builder();
	}

	static Builder builder(GalleryEntity gallery) {
		return builder()
			.imageUrl(gallery.getImageUrl())
			.userId(gallery.getCreatedBy().getUserId());
	}

	abstract String getImageUrl();

	abstract String getUserId();

	@AutoValue.Builder
	interface Builder extends RestData.Builder {
		GalleryRestData build();

		Builder imageUrl(String imageUrl);

		Builder userId(String userId);
	}
}
