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

import models.Earn;
import play.db.ebean.Model.Finder;
import auth.AuthUtil;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;

public class EarnDao extends GenericDao<Long, Earn> {

	private Expression equalsUserLogin() {
		return Expr.eq("user.login", AuthUtil.getSessionLogin());
	}

	@Override
	public List<Earn> all() {
		List<Earn> entities = getFinder().where(equalsUserLogin()).findList();

		Collections.sort(entities, getComparator());

		return entities;
	}

	public static Finder<Long, Earn> finder = new Finder<Long, Earn>(
			Long.class, Earn.class);

	@Override
	protected Finder<Long, Earn> getFinder() {
		return finder;
	}

	@Override
	protected Comparator<Earn> getComparator() {
		return new Comparator<Earn>() {
			@Override
			public int compare(Earn earn1, Earn earn2) {
				return earn1.datetime.compareTo(earn2.datetime);
			}

			@Override
			public Comparator<Earn> reversed() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Earn> thenComparing(Comparator<? super Earn> other) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U> Comparator<Earn> thenComparing(
					Function<? super Earn, ? extends U> keyExtractor,
					Comparator<? super U> keyComparator) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U extends Comparable<? super U>> Comparator<Earn> thenComparing(
					Function<? super Earn, ? extends U> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Earn> thenComparingInt(
					ToIntFunction<? super Earn> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Earn> thenComparingLong(
					ToLongFunction<? super Earn> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Earn> thenComparingDouble(
					ToDoubleFunction<? super Earn> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	@Override
	public Earn byId(Long id) {
		return finder.fetch("category").fetch("period")
				.where(Expr.and(Expr.idEq(id), equalsUserLogin())).findUnique();
	}

	public List<Earn> findEarns(String bankDescription) {
		return finder
				.fetch("category")
				.fetch("period")
				.where(Expr.and(Expr.eq("bankDescription", bankDescription),
						equalsUserLogin())).findList();
	}

	public boolean earnExists(Earn earn) {
		Map<String, Object> attr = new HashMap<String, Object>();
		attr.put("datetime", earn.datetime);
		attr.put("bankDescription", earn.bankDescription);
		attr.put("value", earn.value);

		return finder.where(Expr.and(Expr.allEq(attr), equalsUserLogin()))
				.findRowCount() > 0;
	}
}
