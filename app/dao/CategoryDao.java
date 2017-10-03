package dao;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import models.Category;
import play.db.ebean.Model.Finder;

import com.avaje.ebean.Expr;

public class CategoryDao extends GenericDao<Long, Category> {

	private Finder<Long, Category> finder = new Finder<Long, Category>(
			Long.class, Category.class);

	@Override
	protected Finder<Long, Category> getFinder() {
		return finder;
	}

	@Override
	protected Comparator<Category> getComparator() {
		return new Comparator<Category>() {
			@Override
			public int compare(Category category1, Category category2) {
				return category1.name.compareTo(category2.name);
			}

			@Override
			public Comparator<Category> reversed() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Category> thenComparing(
					Comparator<? super Category> other) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U> Comparator<Category> thenComparing(
					Function<? super Category, ? extends U> keyExtractor,
					Comparator<? super U> keyComparator) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U extends Comparable<? super U>> Comparator<Category> thenComparing(
					Function<? super Category, ? extends U> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Category> thenComparingInt(
					ToIntFunction<? super Category> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Category> thenComparingLong(
					ToLongFunction<? super Category> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<Category> thenComparingDouble(
					ToDoubleFunction<? super Category> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	public Category findCategory(String name) {
		return finder.where(Expr.eq("name", name)).findUnique();
	}
}
