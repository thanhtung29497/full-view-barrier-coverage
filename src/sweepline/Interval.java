package sweepline;

public class Interval{
	
	public Double low, high;
	public int id = -1;
	public Interval(Double low, Double high) {
		this.low = low;
		this.high = high;
	}
	public Interval(Double low, Double high, int id) {
		this.low = low;
		this.high = high;
		this.id = id;
	}
	public boolean doOverlap(Interval other) {
		if (low <= other.high && other.low <= high)
			return true;
		return false;
	}
	
	public int compareTo(Interval other) {
		if (high < other.low + 1.0e-9) return -1;
		else if(low > other.high - 1.0e-9) return 1;
		else if (id == other.id) return 0;
		else{
			if (id < other.id) return -1;
			else if (id > other.id) return 1;
			else return 0;
		}
	}
}

