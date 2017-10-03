package business.itau;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.Earn;
import models.Expense;
import models.Period;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import business.itau.csv.CSV;
import business.statement.BaseStatementProcessor;
import business.statement.ProcessorResponse;

import com.google.inject.Inject;

import dao.CategoryDao;
import dao.EarnDao;
import dao.ExpenseDao;
import dao.PeriodDao;

public class ItauStatement extends BaseStatementProcessor {

	private Period period;

	private CSV csv;

	private ExpenseDao expenseDao;

	private EarnDao earnDao;

	private PeriodDao periodDao;

	private CategoryDao categoryDao;

	private List<AutoFill> listAutoFill;

	private String originalFilename;

	@Inject
	public ItauStatement(EarnDao earnDao, ExpenseDao expenseDao, PeriodDao periodDao, CategoryDao categoryDao) {
		this.earnDao = earnDao;
		this.expenseDao = expenseDao;
		this.periodDao = periodDao;
		this.categoryDao = categoryDao;
	}

	public Iterator<ItauStatementEntry> statementIterator() {
		if (!csv.isOpen()) {
			csv.open();
		}

		return new ItauStatementEntryIterator(csv);
	}

	public ProcessorResponse run() throws ParseException {
		ProcessorResponse response = new ProcessorResponse();

		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
		SimpleDateFormat periodFormatter = new SimpleDateFormat("yyyyMM");

		NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

		listAutoFill = new ArrayList<AutoFill>();
		listAutoFill.add(new AutoFill("CXE.+SAQUE.*", "Saque", categoryDao.findCategory("Saque")));
		listAutoFill.add(new AutoFill(".*CONTABIL.*", "Contabilidade", categoryDao.findCategory("Fixo p/ mês")));
		listAutoFill.add(new AutoFill(".*MAXICONTA.*", "Tarifa maxiconta", categoryDao.findCategory("Fixo p/ mês")));
		listAutoFill.add(new AutoFill("INT DAS.*", "DAS", categoryDao.findCategory("Fixo p/ mês")));
		listAutoFill.add(new AutoFill("DA.+NET.*", "NET", categoryDao.findCategory("Fixo p/ mês")));
		listAutoFill.add(new AutoFill(".*PAO DE ACUC.*", "Pão de açúcar", categoryDao.findCategory("Pão de açúcar")));
		listAutoFill.add(new AutoFill(".*SABOR DE SA.*", "Sabor de saúde", categoryDao.findCategory("Restaurante")));

		Iterator<ItauStatementEntry> it = statementIterator();
		for (; it.hasNext();) {
			ItauStatementEntry entry = it.next();

			DateTime entryDateTime = formatter.parseDateTime(entry.date);
			// Instalment instalment = processInstalment(entry.bankDescription);

			if (entry.isExpense()) {
				Expense expense = new Expense();
				expense.datetime = entryDateTime.toDate();
				expense.bankDescription = entry.bankDescription.replace("\n", " ");
				expense.value = nf.parse("R$ " + entry.value).doubleValue();
				expense.period = period;

				List<Expense> searchExpenses = null;
				searchExpenses = expenseDao.findExpenses(expense.bankDescription);
				if ((searchExpenses != null) && (searchExpenses.size() > 0)) {
					Expense baseExpense = searchExpenses.get(searchExpenses.size() - 1);

					expense.description = baseExpense.description;
					expense.category = baseExpense.category;
				}

				if ((expense.category == null) || (!"Parcelamento".equals(expense.category.name))) {
					String correctPeriod = periodFormatter.format(expense.datetime);
					if (!correctPeriod.equals(period.value)) {
						expense.period = periodDao.findPeriod(expense.datetime);
					}
				}

				if (expense.category == null) {
					tryToFindCategory(expense);
				}

				// expenses.add(expense);
				response.addExpense(expense);
			} else {
				Earn earn = new Earn();
				earn.datetime = entryDateTime.toDate();
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
					String correctPeriod = periodFormatter.format(earn.datetime);
					if (!correctPeriod.equals(period.value)) {
						earn.period = periodDao.findPeriod(earn.datetime);
					}
				}

				// earns.add(earn);
				response.addEarn(earn);
			}
		}

		return response;
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

	@Override
	public void setPeriod(Period period) {
		this.period = period;
	}

	@Override
	public void setStatementFile(File file) {
		this.csv = new CSV(file);
	}

	@Override
	public void setStatementOriginalFile(String filename) {
		this.originalFilename = filename;
	}
}
