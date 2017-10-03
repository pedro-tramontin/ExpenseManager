package business.nubank;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import com.google.inject.Inject;

import business.excel.Excel;
import business.itau.AutoFill;
import business.statement.BaseStatementProcessor;
import business.statement.ProcessorResponse;
import dao.CategoryDao;
import dao.EarnDao;
import dao.ExpenseDao;
import dao.PeriodDao;
import models.Earn;
import models.Expense;
import models.Period;
import play.Logger;

public class NubankStatement extends BaseStatementProcessor {

	private Period period;

	private Date periodDate;

	private Excel excel;

	private ExpenseDao expenseDao;

	private EarnDao earnDao;

	private PeriodDao periodDao;

	// private CategoryDao categoryDao;

	private List<AutoFill> listAutoFill;

	private String originalFilename;

	@Inject
	public NubankStatement(EarnDao earnDao, ExpenseDao expenseDao, PeriodDao periodDao, CategoryDao categoryDao) {
		super();

		this.earnDao = earnDao;
		this.expenseDao = expenseDao;
		this.periodDao = periodDao;
		// this.categoryDao = categoryDao;
	}

	public Iterator<NubankStatementEntry> statementIterator() {
		if (!excel.isOpen()) {
			excel.openWorkbook();
		}

		return new NubankStatementEntryIterator(excel);
	}

	public ProcessorResponse run() throws ParseException {
		ProcessorResponse response = new ProcessorResponse();

		convertPeriodToDate();

		NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

		listAutoFill = new ArrayList<AutoFill>();
		// listAutoFill
		// .add(new AutoFill(".*PAO DE ACUC.*", "Pão de açúcar",
		// categoryDao.findCategory("Pão de açúcar")));
		// listAutoFill.add(new AutoFill(".*NETFLIX.*", "Netflix",
		// categoryDao.findCategory("Fixo p/ mês")));
		// listAutoFill.add(new AutoFill(".*PLAYSTATION.*", "Playstation Store",
		// categoryDao.findCategory("Diversos")));

		for (Iterator<NubankStatementEntry> it = statementIterator(); it.hasNext();) {
			NubankStatementEntry entry = it.next();

			Date entryDate = dateFormatter.toDate(entry.date + "/" + getDateYear(periodDate), "dd/MM/yyyy");
			if (!isSameYear(entryDate, periodDate)) {
				entryDate = dateFormatter.toDate(entry.date + "/" + (getDateYear(periodDate) - 1), "dd/MM/yyyy");
			}

			Instalment instalment = processInstalment(entry.bankDescription);

			if (entry.isExpense()) {
				Expense expense = new Expense();
				expense.datetime = entryDate;
				expense.bankDescription = entry.bankDescription.replace("\n", " ");
				expense.value = nf.parse("R$ " + entry.value).doubleValue();
				expense.period = period;

				List<Expense> searchExpenses = null;
				if (!instalment.equals(Instalment.NULL_INSTALMENT)) {
					searchExpenses = expenseDao.findInstalment(instalment.description);

					Logger.debug(String.format("Expenses found, quantidade %d", searchExpenses.size()));
					for (int i = 0; i < searchExpenses.size(); i++) {
						Logger.debug(searchExpenses.get(i).toString());
					}
				} else {
					searchExpenses = expenseDao.findExpenses(expense.bankDescription);
				}

				if ((searchExpenses != null) && (searchExpenses.size() > 0)) {
					Expense baseExpense = searchExpenses.get(searchExpenses.size() - 1);

					Instalment pastInstalment = processInstalment(baseExpense.description);
					if (!pastInstalment.equals(Instalment.NULL_INSTALMENT)) {
						expense.description = String.format("%s %d/%d", pastInstalment.description.trim(),
								instalment.currentPart, instalment.totalParts);

						expense.datetime = updateParcelamentoDate(expense.datetime, instalment.currentPart);
					} else {
						expense.description = baseExpense.description;
					}

					expense.category = baseExpense.category;
				}

				if ((expense.category == null) || (!"Parcelamento".equals(expense.category.name))) {
					String correctPeriod = dateFormatter.toString(expense.datetime, "yyyyMM");
					if (!correctPeriod.equals(period.value)) {
						expense.period = periodDao.findPeriod(expense.datetime);
					}
				}

				if (expense.category == null) {
					tryToFindCategory(expense);
				}

				response.addExpense(expense);
			} else {
				Earn earn = new Earn();
				earn.datetime = entryDate;
				earn.bankDescription = entry.bankDescription.replace("\n", " ");
				earn.value = nf.parse("R$ " + entry.value).doubleValue();
				earn.period = period;

				List<Earn> searchEarns = earnDao.findEarns(earn.bankDescription);
				if (searchEarns.size() > 0) {
					Earn baseEarn = searchEarns.get(searchEarns.size() - 1);
					earn.description = baseEarn.description;
					earn.category = baseEarn.category;
				}

				if ((earn.category == null) || (!"Parcelamento".equals(earn.category.name))) {
					String correctPeriod = dateFormatter.toString(earn.datetime, "yyyyMM");
					if (!correctPeriod.equals(period.value)) {
						earn.period = periodDao.findPeriod(earn.datetime);
					}
				}

				response.addEarn(earn);
			}
		}

		return response;
	}

	private void convertPeriodToDate() {
		periodDate = dateFormatter.toDate(period.value, "yyyyMM");
	}

	private boolean isSameYear(Date entryDate, Date periodDate) {
		DateTime entryDateTime = new DateTime(entryDate.getTime());
		DateTime periodDateTime = new DateTime(periodDate.getTime());

		int entryMonth = entryDateTime.getMonthOfYear();
		int periodMonth = periodDateTime.getMonthOfYear();

		if (entryMonth > periodMonth + 1) {
			return false;
		}

		return true;
	}

	public static Instalment processInstalment(String description) {
		Logger.debug(String.format("Processing instalment, description: %s", description));

		Instalment instalment = Instalment.NULL_INSTALMENT;

		Pattern parcelamentoRegex = Pattern.compile("(.+)([ ]*\\d{1,2}[ ]*)/([ ]*\\d{1,2}[ ]*)");
		Matcher matcher = parcelamentoRegex.matcher(description);

		if (matcher.matches()) {
			Logger.debug("Pattern matched");

			instalment = new Instalment();
			instalment.description = ripExtraSpaces(matcher.group(1));
			instalment.currentPart = Integer.parseInt(matcher.group(2).trim());
			instalment.totalParts = Integer.parseInt(matcher.group(3).trim());

			Logger.debug(String.format("Instalmente: description=%s, currentParts=%d, totalParts=%d",
					instalment.description, instalment.currentPart, instalment.totalParts));
		}

		return instalment;
	}

	private static String ripExtraSpaces(String str) {
		String before = str;
		String result = str.replace("  ", " ");

		while (result.length() != before.length()) {
			before = result;
			result = result.replace("  ", " ");
		}

		return result.trim();
	}

	public boolean tryToFindCategory(Expense expense) {
		for (AutoFill autoFill : listAutoFill) {
			Pattern pattern = Pattern.compile(autoFill.pattern);
			Matcher matcher = pattern.matcher(expense.bankDescription);
			if (matcher.find()) {
				expense.category = autoFill.category;
				expense.description = autoFill.description;

				return true;
			}
		}

		return false;
	}

	private Date updateParcelamentoDate(Date datetime, int parcela) {
		DateTime date = new DateTime(datetime.getTime());
		return date.plusMonths(parcela - 1).toDate();
	}

	@Override
	public void setPeriod(Period period) {
		this.period = period;
	}

	@Override
	public void setStatementFile(File excelFile) {
		this.excel = new Excel(excelFile);
		this.excel.setOriginalFilename(originalFilename);
	}

	@Override
	public void setStatementOriginalFile(String filename) {
		this.originalFilename = filename;
	}
}
