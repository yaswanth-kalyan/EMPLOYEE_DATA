package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
	public static Date convertToDate(String StringDate) throws ParseException{
		DateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
		Date  date = formatter.parse(StringDate);
		return date;
	}
	
	public static String convertDateToString(Date date) throws ParseException{
		DateFormat formatter = new SimpleDateFormat("dd-mm-yyyy");
		return formatter.format(date);
	}

}
