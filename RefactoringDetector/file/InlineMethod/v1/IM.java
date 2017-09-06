package file.InlineMethod.v1;

public class IM {
	void printOwing(double amount) {
		printBanner();
		System.out.println("amount:" + amount);
	}

	void printBanner() {
		// print banner
		System.out.println("**************************");
		System.out.println("***** Customer Owes ******");
		System.out.println("**************************");
	}
}
