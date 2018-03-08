package bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import models.leave.Holidays;
import play.Logger;

public class HolidaysBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Long id;

	public String holidayDate;

	public String holidayFor;

	public Boolean compensatory;

	public String compensatoryDay;

	public Holidays toHolidays() {
		Holidays holidays = null;
		if (id != null && !id.toString().isEmpty()) {
			holidays = holidays.find.byId(id);
			if (holidays != null) {
				if (!holidayDate.isEmpty() && !holidayFor.trim().toString().isEmpty()) {
					try {
						final Date comingDate = new SimpleDateFormat("dd-MM-yyyy").parse(holidayDate.trim());
						// Logger.debug(comingDate+"");
						holidays.holidayDate = comingDate;
						holidays.holidayFor = holidayFor;
						if (!compensatory.toString().isEmpty()) {
							if (compensatory && !compensatoryDay.toString().isEmpty()) {
								// Logger.debug(compensatoryDay +"
								// cjojdsddsdjklsj" + id);
								try {
									final Date comDay = new SimpleDateFormat("dd-MM-yyyy")
											.parse(compensatoryDay.trim());
									// Logger.debug(comDay+"");
									holidays.Compensatory = compensatory;
									holidays.correspondingWorkingDay = comDay;
								} catch (final Exception e) {
									// TODO Auto-generated catch block
									// Logger.error("date is not parsing",e);
								}
							} else {
								// Logger.debug("mesasasas >>>."+compensatory);
								holidays.Compensatory = compensatory;
								holidays.correspondingWorkingDay = null;
							}
						}
					} catch (final Exception e) {
						// TODO Auto-generated catch block
						// Logger.error("date is not parsing",e);
					}
					holidays.update();

				}
			}

		} else {
			
			if (!holidayDate.isEmpty() && !holidayFor.trim().toString().isEmpty() ) {
				holidays = new Holidays();
				Holidays checkholidayDate = null;
				Holidays checkCompensatoryDate = null;
				try {
					final Date comingDate = new SimpleDateFormat("dd-MM-yyyy").parse(holidayDate.trim());
					// Logger.debug(comingDate+"");
					checkholidayDate = Holidays.find.where().eq("holidayDate", comingDate).findUnique();
					holidays.holidayDate = comingDate;
					holidays.holidayFor = holidayFor;
					if (!compensatory.toString().isEmpty()) {
						if (compensatory && !compensatoryDay.toString().isEmpty()) {
							try {
								final Date comDay = new SimpleDateFormat("dd-MM-yyyy").parse(compensatoryDay.trim());
								// Logger.debug(comDay+"");
								checkCompensatoryDate = Holidays.find.where().eq("correspondingWorkingDay", comDay).findUnique();
								holidays.Compensatory = compensatory;
								holidays.correspondingWorkingDay = comDay;
							} catch (final Exception e) {
								// TODO Auto-generated catch block
								// Logger.error("date is not parsing",e);
							}
						} else {
							holidays.Compensatory = false;
						}
					}
				} catch (final Exception e) {
					// TODO Auto-generated catch block
					// Logger.error("date is not parsing",e);
				}
				if(checkholidayDate == null && checkCompensatoryDate == null){
					holidays.save();
				}

			}
		}
		return holidays;

	}

}
