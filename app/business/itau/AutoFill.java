package business.itau;

import models.Category;

public class AutoFill {
	public String pattern;

	public String description;

	public Category category;

	public AutoFill(String pattern, String description, Category category) {
		this.pattern = pattern;
		this.description = description;
		this.category = category;
	}
}
