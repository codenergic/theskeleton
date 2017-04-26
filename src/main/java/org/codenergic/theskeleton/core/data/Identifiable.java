package org.codenergic.theskeleton.core.data;

import java.io.Serializable;

@FunctionalInterface
public interface Identifiable<PK> extends Serializable {
	PK getId();
}
