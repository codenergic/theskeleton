package org.codenergic.theskeleton.core.data;

import java.util.ArrayList;
import java.util.List;

public class S3ClientProperties {
	String accessKey;
	List<S3BucketProperties> buckets = new ArrayList<>();
	String endpoint;
	String secretKey;

	public List<S3BucketProperties> getBuckets() {
		return buckets;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public String getEndpoint() {
		return endpoint;
	}
}
