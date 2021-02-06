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

package org.codenergic.theskeleton.core.mail;

import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;
import org.springframework.lang.NonNull;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.Files;

import it.ozimov.springboot.mail.service.TemplateService;

class ThymeleafTemplateService implements TemplateService {
	private final SpringTemplateEngine thymeleafEngine;
	private final String thymeleafSuffix;

	ThymeleafTemplateService(SpringTemplateEngine thymeleafEngine, String thymeleafSuffix) {
		this.thymeleafEngine = thymeleafEngine;
		this.thymeleafSuffix = thymeleafSuffix;
	}

	@NonNull
	public String mergeTemplateIntoString(@NonNull String templateReference, @NonNull Map<String, Object> model) {
		String trimmedTemplateReference = templateReference.trim();
		Preconditions.checkArgument(!Strings.isNullOrEmpty(trimmedTemplateReference), "The given template is null, empty or blank");
		if (trimmedTemplateReference.contains(".")) {
			Preconditions.checkArgument(Objects.equals(this.getNormalizedFileExtension(trimmedTemplateReference), this.expectedTemplateExtension()), "Expected a Thymeleaf template file with extension '%s', while '%s' was given. To check the default extension look at 'spring.thymeleaf.suffix' in your application.properties file", this.expectedTemplateExtension(), this.getNormalizedFileExtension(trimmedTemplateReference));
		}

		Context context = new Context();
		context.setVariables(model);
		return this.thymeleafEngine.process(FilenameUtils.removeExtension(trimmedTemplateReference), context);
	}

	public String expectedTemplateExtension() {
		return this.thymeleafSuffix;
	}

	private String getNormalizedFileExtension(String templateReference) {
		return "." + Files.getFileExtension(templateReference);
	}
}
