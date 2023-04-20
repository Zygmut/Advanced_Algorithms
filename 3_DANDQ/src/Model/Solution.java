package Model;

public record Solution(PairPoint pair, double distance, long time) {
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[Pair: ").append(pair()).append(", Distance: ").append(distance()).append(", Time: ").append(time())
				.append("ms]");
		return sb.toString();
	}
}
