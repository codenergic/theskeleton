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

import org.codenergic.theskeleton.core.data.AbstractAuditingEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.InputStream;

@Entity
@Table(name = "ts_gallery")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class GalleryEntity extends AbstractAuditingEntity {
	@NotNull
	@Lob
	@Column(name = "image_url")
	private String imageUrl;
	@Transient
	private InputStream image;
	@Transient
	private String format;

	public String getFormat() {
		return format;
	}

	public GalleryEntity setFormat(String format) {
		this.format = format;
		return this;
	}

	public InputStream getImage() {
		return image;
	}

	public GalleryEntity setImage(InputStream image) {
		this.image = image;
		return this;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public GalleryEntity setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
		return this;
	}
}
