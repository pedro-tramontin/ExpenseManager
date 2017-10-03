package business.statement;

import java.util.ArrayList;
import java.util.List;

import models.Earn;
import models.Expense;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProcessorResponse {
	private List<Expense> expenses;

	private List<Earn> earns;

	public ProcessorResponse() {
		expenses = new ArrayList<Expense>();
		earns = new ArrayList<Earn>();
	}

	public String asJson() throws JsonProcessingException {
		StatementLists lists = new StatementLists();
		lists.expenses = expenses;
		lists.earns = earns;

		return new ObjectMapper().writeValueAsString(lists);
	}

	public void addExpense(Expense expense) {
		expenses.add(expense);
	}

	public void addEarn(Earn earn) {
		earns.add(earn);
	}

	public List<Expense> getExpenses() {
		return expenses;
	}

	public List<Earn> getEarns() {
		return earns;
	}
}
