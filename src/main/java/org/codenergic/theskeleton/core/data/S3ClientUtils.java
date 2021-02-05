package org.codenergic.theskeleton.core.data;

import org.apache.commons.lang3.StringUtils;

public final class S3ClientUtils {
	public static String getObjectUrl(S3ClientProperties properties, String bucket, String objectName) {
		return StringUtils.join(properties.getEndpoint(), "/", bucket, "/", objectName);
	}

}
