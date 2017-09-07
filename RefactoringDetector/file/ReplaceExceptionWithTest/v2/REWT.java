public class REWT {
	double getValueForPeriod(int periodNumber) {
		
		
		if (periodNumber >= _values.length)
			return 0;
		else {
			return _values[periodNumber];
		}
		
	}
}