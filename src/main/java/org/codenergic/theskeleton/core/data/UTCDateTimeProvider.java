package org.codenergic.theskeleton.core.data;

import java.util.Calendar;
import java.util.TimeZone;

import org.springframework.data.auditing.DateTimeProvider;

public class UTCDateTimeProvider implements DateTimeProvider {
	@Override
	public Calendar getNow() {
		return Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	}
}
