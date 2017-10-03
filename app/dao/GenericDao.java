package dao;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;

public abstract class GenericDao<ID, T extends Model> implements Dao<ID, T> {
	public T byId(ID id) {
		return getFinder().byId(id);
	}

	public List<T> all() {
		List<T> entities = getFinder().all();

		Collections.sort(entities, getComparator());

		return entities;
	}

	public void create(T entity) {
		entity.save();
	}

	public void delete(ID id) {
		getFinder().ref(id).delete();
	}

	protected abstract Finder<ID, T> getFinder();

	protected abstract Comparator<T> getComparator();
}
