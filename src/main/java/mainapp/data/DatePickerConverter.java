package mainapp.data;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class DatePickerConverter {
	public static Timestamp convertToTimestamp (LocalDate date, int hours, int minutes) {
		if (date == null) {
			return null;
		}
		LocalTime time = LocalTime.of(hours, minutes);
		return Timestamp.valueOf(LocalDateTime.of(date, time));
	}
	
	public static LocalDate extractLocalDate (Timestamp timestamp) {
		if (timestamp == null) {
			return null;
		}
		LocalDateTime ldt = timestamp.toLocalDateTime();
		return LocalDate.from(ldt);
	}
	
	public static int[] extractTime (Timestamp timestamp) {
		if (timestamp == null) {
			return new int[] {};
		}
		LocalDateTime ldt = timestamp.toLocalDateTime();
		int hours = ldt.getHour(), minutes = ldt.getMinute();
		return new int[] {hours, minutes};
	}
}
