package controllers;

import java.io.File;

import com.google.inject.Inject;

import auth.AuthUtil;
import business.credicard.CredicardStatement;
import business.itau.ItauStatement;
import business.nubank.NubankStatement;
import business.statement.ProcessorResponse;
import business.statement.StatementLists;
import business.statement.StatementProcessor;
import dao.EarnDao;
import dao.ExpenseDao;
import dao.PeriodDao;
import dao.UserDao;
import models.Earn;
import models.Expense;
import models.Period;
import play.Logger;
import play.data.Form;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Result;
import play.mvc.Results;

public class Upload extends Controller {

	private static final String PERIOD_FORM_NAME = "period";

	@Inject
	private ExpenseDao expenseDao;

	@Inject
	private EarnDao earnDao;

	@Inject
	private PeriodDao periodDao;

	@Inject
	private UserDao userDao;

	@Inject
	private ItauStatement itauStatement;

	@Inject
	private CredicardStatement credicardStatement;

	@Inject
	private NubankStatement nubankStatement;

	public Result upload() {
		try {
			RequestBody requestBody = new RequestBody();

			if (!requestBody.isValid()) {
				return requestBody.getValidateResult();
			}

			StatementProcessor processor = getStatementProcessor(requestBody);
			ProcessorResponse response = processor.run();

			return ok(response.asJson());
		} catch (Exception e) {
			return Results.internalServerError(String.format("Exception: %s", e.getMessage()));
		}
	}

	private StatementProcessor getStatementProcessor(RequestBody requestBody) {
		// default statement processor
		StatementProcessor processor = credicardStatement;

		if (requestBody.getUploadedFilename().endsWith(".txt")) {
			processor = itauStatement;
		} else if (requestBody.getUploadedFilename().endsWith(".xlsx")) {
			processor = nubankStatement;
		}

		processor.setStatementOriginalFile(requestBody.getUploadedFilename());
		processor.setStatementFile(requestBody.getUploadedFile());
		processor.setPeriod(requestBody.getUploadedPeriod());

		return processor;
	}

	@BodyParser.Of(BodyParser.Json.class)
	public Result uploadBatch() {
		Form<StatementLists> filledForm = Form.form(StatementLists.class).bindFromRequest();
		if (filledForm.hasErrors()) {
			return badRequest(filledForm.errorsAsJson());
		} else {
			StatementLists statementLists = filledForm.get();
			expensesInserter(statementLists);
			earnsInserter(statementLists);
		}

		return ok("OK");
	}

	private void earnsInserter(StatementLists statementLists) {
		Logger.of(Upload.class).debug("Listing earns, size: {}", statementLists.earns.size());
		for (Earn earn : statementLists.earns) {
			Logger.of(Upload.class).debug(earn.toString());
			if (earnFilled(earn)) {
				if (!earnDao.earnExists(earn)) {
					Logger.of(Upload.class).debug("Earn doesn't exist, inserting");
					earn.user = userDao.findUser(AuthUtil.getSessionLogin());
					earnDao.create(earn);
				} else {
					Logger.of(Upload.class).debug("Already exists.");
				}
			} else {
				Logger.of(Upload.class).debug("Not filled");
			}
		}
	}

	private void expensesInserter(StatementLists statementLists) {
		Logger.of(Upload.class).debug("Listing expenses, size: {}", statementLists.expenses.size());
		for (Expense expense : statementLists.expenses) {
			Logger.of(Upload.class).debug(expense.toString());

			if (expenseFilled(expense)) {
				if (!expenseDao.expenseExists(expense)) {

					Logger.of(Upload.class).debug("Expense doesn't exist, inserting");
					expense.user = userDao.findUser(AuthUtil.getSessionLogin());
					expenseDao.create(expense);
				} else {
					Logger.of(Upload.class).debug("Already exists.");
				}
			} else {
				Logger.of(Upload.class).debug("Not filled");
			}
		}
	}

	private boolean expenseFilled(Expense expense) {
		return !"".equals(expense.description) && (expense.category != null) && !expense.category.id.equals(0);
	}

	private boolean earnFilled(Earn earn) {
		return !"".equals(earn.description) && (earn.category != null) && !earn.category.id.equals(0);
	}

	private class RequestBody {
		private MultipartFormData formData;

		private Result validateResult;

		public RequestBody() {
			formData = request().body().asMultipartFormData();
		}

		public boolean isValid() {
			boolean r = true;

			if (formData.asFormUrlEncoded().get(PERIOD_FORM_NAME).length <= 0) {
				validateResult = Results.badRequest("O campo 'period' não foi enviado.");

				r = false;
			}

			if (formData.getFiles().size() <= 0) {
				validateResult = Results.badRequest("O arquivo não foi enviado");

				r = false;
			}

			return r;
		}

		public Result getValidateResult() {
			return validateResult;
		}

		private String getUploadedFilename() {
			return formData.getFiles().get(0).getFilename();
		}

		private File getUploadedFile() {
			return formData.getFiles().get(0).getFile();
		}

		private Period getUploadedPeriod() {
			long periodId = Long.parseLong(formData.asFormUrlEncoded().get(PERIOD_FORM_NAME)[0]);

			return periodDao.byId(periodId);
		}
	}
}