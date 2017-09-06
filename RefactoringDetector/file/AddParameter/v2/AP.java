package file.AddParameter.v2;

import java.io.IOException;

public class AP {
	void printOwing(double amount) throws IOException {

		printDetails(amount);

	}

	public void printDetails(double amount) {
		System.out.println("amount" + amount);
	}
}
