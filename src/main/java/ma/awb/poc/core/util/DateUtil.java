package ma.awb.poc.core.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	private static final String FORMAT_DATE_FILE_RESULT = "yyyy-MM-dd-hh-mm-ss";

	public static String format(final Date dateIn) {
		final DateFormat format = new SimpleDateFormat(FORMAT_DATE_FILE_RESULT);
		return format.format(dateIn);
	}

	public static String format() {
		final DateFormat format = new SimpleDateFormat(FORMAT_DATE_FILE_RESULT);
		return format.format(new Date());
	}

}
