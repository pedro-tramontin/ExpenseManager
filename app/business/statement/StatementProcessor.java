package business.statement;

import java.io.File;
import java.text.ParseException;

import models.Period;

public interface StatementProcessor {
	public ProcessorResponse run() throws ParseException;

	public void setPeriod(Period period);

	public void setStatementFile(File file);

	public void setStatementOriginalFile(String filename);
}
