package vision.apollo.common.indexcode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jef.tools.string.RandomData;

public class IndexCodeUtil {
	public static String genDateTimeRandomIndexCode() {
		DateFormat df = new SimpleDateFormat("MMddhhmmssSSS");
		return RandomData.randomInteger(1, 100000000) + df.format(new Date());
	}
}
