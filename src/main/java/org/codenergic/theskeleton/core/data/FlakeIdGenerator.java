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

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import com.github.rholder.fauxflake.IdGenerators;
import com.github.rholder.fauxflake.api.IdGenerator;

public class FlakeIdGenerator implements IdentifierGenerator {
	private IdGenerator flake = IdGenerators.newFlakeIdGenerator();

	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object object) {
		try {
			return flake.generateId(1_000).asString();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new HibernateException(e);
		}
	}
}
