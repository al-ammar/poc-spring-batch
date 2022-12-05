package ma.awb.poc.core.util;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static final String FORMAT_DATE_LOG = "dd/MM/yyyy hh:mm:ss";
	public static final String FORMAT_DATE_FILE_RESULT = "dd-MM-yyyy-hh-mm-ss";

	public static String format(final Date dateIn, final String formatIn) {
		final DateFormat format = new SimpleDateFormat(formatIn);
		return format.format(dateIn);
	}

	public static String format(final Date dateIn) {
		final DateFormat format = new SimpleDateFormat(FORMAT_DATE_LOG);
		return format.format(dateIn);
	}

	public static String format(final String formatIn) {
		final DateFormat format = new SimpleDateFormat(formatIn == null ? FORMAT_DATE_LOG : formatIn);
		return format.format(new Date());
	}

	public static String time(final Date dateDebut, final Date dateFin) {
		final NumberFormat format = NumberFormat.getInstance();
		format.setMaximumFractionDigits(3);
		format.setMaximumIntegerDigits(2);
		return format.format((dateFin.getTime() - dateDebut.getTime()) * 1000);
	}
}
