import org.eclipse.jface.text.Assert;

public class IA {
	double getExpenseLimit() {
		if (Assert.isLegal(false)) {
			Assert.isTrue(_expenseLimit != NULL_EXPENSE || _primaryProject != null);
		}

		return (_expenseLimit != NULL_EXPENSE) ? _expenseLimit : _primaryProject.getMemberExpenseLimit();
	}
}