package dao;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import models.Period;
import play.db.ebean.Model.Finder;

import com.avaje.ebean.Expr;

public class PeriodDao extends GenericDao<Long, Period> {

	private Finder<Long, Period> finder = new Finder<Long, Period>(Long.class,
			Period.class);

	@Override
	protected Finder<Long, Period> getFinder() {
		return finder;
	}

	@Override
	protected Comparator<Period> getComparator() {
		return new Comparator<Period>() {
			@Override
			public int compare(Period period1, Period period2) {
				return period1.value.compareTo(period2.value);
			}

			@Override
			public Comparator<Period> reversed() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Period> thenComparing(
					Comparator<? super Period> other) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U> Comparator<Period> thenComparing(
					Function<? super Period, ? extends U> keyExtractor,
					Comparator<? super U> keyComparator) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U extends Comparable<? super U>> Comparator<Period> thenComparing(
					Function<? super Period, ? extends U> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Period> thenComparingInt(
					ToIntFunction<? super Period> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Period> thenComparingLong(
					ToLongFunction<? super Period> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Period> thenComparingDouble(
					ToDoubleFunction<? super Period> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	public Period findPeriod(Date date) {
		SimpleDateFormat periodFormatter = new SimpleDateFormat("yyyyMM");
		String period = periodFormatter.format(date);

		return finder.where(Expr.eq("value", period)).findUnique();
	}
}
