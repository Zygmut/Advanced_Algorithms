package Model;

import java.io.Serializable;
import java.math.BigInteger;

public class MemoStats implements Serializable{

	private int statesVisited;
	private BigInteger totalStates;
	private int memoRef;
	private int memoHit;

	public MemoStats(int size) {
		this.memoRef = 0;
		this.memoHit = 0;
		this.statesVisited = 0;
		this.totalStates = factorial(BigInteger.valueOf(size).pow(2));
	}

	public static BigInteger factorial(BigInteger n) {
		if (n.compareTo(BigInteger.ONE) <= 0) {
			return BigInteger.ONE;
		}

		BigInteger result = BigInteger.ONE;
		while (n.compareTo(BigInteger.ONE) > 0) {
			result = result.multiply(n);
			n = n.subtract(BigInteger.ONE);
		}
		return result;
	}

	public void addRef() {
		this.memoRef++;
	}

	public void addHit() {
		this.memoHit++;
	}

	public void addState() {
		this.statesVisited++;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("memo refs     : ").append(memoRef).append("\n");
		sb.append("memo hits     : ").append(memoHit).append(" (")
				.append(this.memoRef == 0 ? 0 : ((float) this.memoHit / this.memoRef) * 100).append("%)").append("\n");
		sb.append("visited states: ").append(statesVisited).append("\n");
		sb.append("total states  : ").append(totalStates).append("\n");
		sb.append("skipped states: ").append(totalStates.subtract(BigInteger.valueOf(statesVisited))).append(" (")
				.append(totalStates.subtract(BigInteger.valueOf(statesVisited)).multiply(BigInteger.valueOf(100))
						.divide(totalStates))
				.append("%)").append("\n");

		return sb.toString();
	}
}
