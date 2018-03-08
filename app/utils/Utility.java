package utils;

import java.util.Calendar;
import java.util.Date;

public class Utility {

	/**
	 * 
	 * @param from
	 * @param to
	 * @return
	 */

	public static boolean isSameDay(Date from, Date to) { // comparision of
															// dates (is dates
															// same)
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		calendar1.setTime(from);
		calendar2.setTime(to);
		return (calendar1.get(Calendar.ERA) == calendar2.get(Calendar.ERA)
				&& calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
				&& calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR));

	}

	public static String generateHtmlColorCode() {
		String[] letters = new String[15];
		letters = "0123456789ABCDEF".split("");
		String code = "#";
		for (int i = 0; i < 6; i++) {
			final double ind = Math.random() * 15;
			final int index = (int) Math.round(ind);
			code += letters[index];
		}
		return code;
	}
}
