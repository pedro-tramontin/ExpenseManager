package dao;

import java.util.List;

import play.db.ebean.Model;

public interface Dao<ID, T extends Model> {
	public T byId(ID id);

	public List<T> all();

	public void create(T entity);

	public void delete(ID id);
}
