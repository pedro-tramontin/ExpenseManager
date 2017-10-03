package business.statement;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;

import models.Period;

public abstract class BaseStatementProcessor implements StatementProcessor {

	protected DateFormatter dateFormatter;

	public BaseStatementProcessor() {
		dateFormatter = new DateFormatter();
	}

	@Override
	public ProcessorResponse run() throws ParseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPeriod(Period period) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStatementFile(File file) {
		// TODO Auto-generated method stub

	}

	public class DateFormatter {
		private SimpleDateFormat dateFormatter;

		public Date toDate(String dateString, String pattern) {
			createDateFormatterIfNotExists();
			setDateFormatterPattern(pattern);

			try {
				return dateFormatter.parse(dateString);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}

		public String toString(Date date, String pattern) {
			createDateFormatterIfNotExists();
			setDateFormatterPattern(pattern);

			return dateFormatter.format(date);
		}

		private void setDateFormatterPattern(String pattern) {
			dateFormatter.applyPattern(pattern);
		}

		private void createDateFormatterIfNotExists() {
			if (dateFormatter == null) {
				dateFormatter = new SimpleDateFormat();
			}
		}
	}

	public int getDateDay(Date date) {
		DateTime dateTime = new DateTime(date.getTime());

		return dateTime.getDayOfMonth();
	}

	public int getDateMonth(Date date) {
		DateTime dateTime = new DateTime(date.getTime());

		return dateTime.getMonthOfYear();
	}

	public int getDateYear(Date date) {
		DateTime dateTime = new DateTime(date.getTime());

		return dateTime.getYear();
	}
}
