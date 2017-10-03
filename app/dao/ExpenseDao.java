package dao;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import models.Expense;
import play.db.ebean.Model.Finder;
import auth.AuthUtil;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;

public class ExpenseDao extends GenericDao<Long, Expense> {

	public static Finder<Long, Expense> finder = new Finder<Long, Expense>(
			Long.class, Expense.class);

	@Override
	protected Finder<Long, Expense> getFinder() {
		return finder;
	}

	private Expression equalsUserLogin() {
		return Expr.eq("user.login", AuthUtil.getSessionLogin());
	}

	@Override
	public List<Expense> all() {
		List<Expense> entities = getFinder().where(equalsUserLogin())
				.findList();

		Collections.sort(entities, getComparator());

		return entities;
	}

	@Override
	protected Comparator<Expense> getComparator() {
		return new Comparator<Expense>() {
			@Override
			public int compare(Expense expense1, Expense expense2) {
				return expense1.datetime.compareTo(expense2.datetime);
			}

			@Override
			public Comparator<Expense> reversed() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Expense> thenComparing(
					Comparator<? super Expense> other) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U> Comparator<Expense> thenComparing(
					Function<? super Expense, ? extends U> keyExtractor,
					Comparator<? super U> keyComparator) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U extends Comparable<? super U>> Comparator<Expense> thenComparing(
					Function<? super Expense, ? extends U> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Expense> thenComparingInt(
					ToIntFunction<? super Expense> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Expense> thenComparingLong(
					ToLongFunction<? super Expense> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Expense> thenComparingDouble(
					ToDoubleFunction<? super Expense> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	@Override
	public Expense byId(Long id) {
		return finder.fetch("category").fetch("period").where(Expr.idEq(id))
				.findUnique();
	}

	public List<Expense> findExpenses(String bankDescription) {
		return finder.fetch("category").fetch("period")
				.where(Expr.eq("bankDescription", bankDescription)).findList();
	}

	public List<Expense> findInstalment(String description) {
		return finder.fetch("category").fetch("period")
				.where(Expr.like("bankDescription", description + "%"))
				.orderBy("datetime").findList();
	}

	public boolean expenseExists(Expense expense) {
		Map<String, Object> attr = new HashMap<String, Object>();
		attr.put("datetime", expense.datetime);
		attr.put("bankDescription", expense.bankDescription);
		attr.put("value", expense.value);

		return finder.where(Expr.allEq(attr)).findRowCount() > 0;
	}

	public List<Expense> filterByPeriodAndOrderByCategory(String period) {
		return finder
				.fetch("category")
				.fetch("period")
				.where(Expr.and(Expr.eq("period.value", period),
						Expr.eq("user.login", AuthUtil.getSessionLogin())))
				.orderBy("category.name").findList();
	}
}
