package dao;

import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import com.avaje.ebean.Expr;

import models.User;
import play.db.ebean.Model.Finder;

public class UserDao extends GenericDao<Long, User> {

	private Finder<Long, User> finder = new Finder<Long, User>(Long.class, User.class);

	@Override
	protected Finder<Long, User> getFinder() {
		return finder;
	}

	@Override
	protected Comparator<User> getComparator() {
		return new Comparator<User>() {
			@Override
			public int compare(User user1, User user2) {
				return user1.login.compareTo(user2.login);
			}

			@Override
			public Comparator<User> reversed() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<User> thenComparing(Comparator<? super User> other) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U> Comparator<User> thenComparing(Function<? super User, ? extends U> keyExtractor,
					Comparator<? super U> keyComparator) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <U extends Comparable<? super U>> Comparator<User> thenComparing(
					Function<? super User, ? extends U> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<User> thenComparingInt(ToIntFunction<? super User> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<User> thenComparingLong(ToLongFunction<? super User> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Comparator<User> thenComparingDouble(ToDoubleFunction<? super User> keyExtractor) {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	public User findUser(String login) {
		return finder.where(Expr.eq("login", login)).findUnique();
	}
}
