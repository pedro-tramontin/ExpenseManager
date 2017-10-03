package controllers;

import java.util.List;

import models.CategoryExpenses;
import models.Expense;
import models.PeriodCategoryExpenses;
import models.PeriodExpenses;
import play.data.Form;
import play.mvc.Call;
import play.mvc.Result;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import dao.Dao;
import dao.ExpenseDao;
import dao.UserDao;

public class Expenses extends BaseCRUD<Long, Expense> {

	private Form<Expense> form = Form.form(Expense.class);

	@Inject
	private ExpenseDao expenseDao;

	@Inject
	private UserDao userDao;

	@Override
	protected Form<Expense> getForm() {
		return form;
	}

	@Override
	protected Dao<Long, Expense> getDao() {
		return expenseDao;
	}

	@Override
	protected Call getListRoute() {
		return routes.Expenses.list();
	}

	@Override
	protected Long getId(Expense entity) {
		return entity.id;
	}

	public static Result byCategory() {
		return (ok(views.html.expense.byCategory.render(CategoryExpenses.all())));
	}

	public static Result byPeriod() {
		return (ok(views.html.expense.byPeriod.render(PeriodExpenses.all())));
	}

	public static Result byPeriodAndCategory() {
		return (ok(views.html.expense.byPeriodAndCategory
				.render(PeriodCategoryExpenses.all())));
	}

	public Result filteredByPeriodAndOrderedByDate(String period)
			throws JsonProcessingException {
		List<Expense> list = expenseDao
				.filterByPeriodAndOrderByCategory(period);

		return ok(new ObjectMapper().writeValueAsString(list));
	}

	@Override
	protected void setAuth(Expense entity, String login) {
		entity.user = userDao.findUser(login);
	}
}
