import play.Application;
import play.GlobalSettings;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;

import dao.CategoryDao;
import dao.EarnDao;
import dao.ExpenseDao;
import dao.PeriodDao;

public class Global extends GlobalSettings {
	private Injector injector;

	@Override
	public void onStart(Application application) {
		injector = Guice.createInjector(new AbstractModule() {

			@Override
			protected void configure() {
				bind(CategoryDao.class).in(Singleton.class);
				bind(PeriodDao.class).in(Singleton.class);
				bind(EarnDao.class).in(Singleton.class);
				bind(ExpenseDao.class).in(Singleton.class);
			}
		});
	}

	@Override
	public <T> T getControllerInstance(Class<T> aClass) throws Exception {
		return injector.getInstance(aClass);
	}
}
