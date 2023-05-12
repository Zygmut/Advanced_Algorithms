package Model.Statistics;

import java.io.Serializable;

public record AtomIteration(int interation, long time,
		int numberOfVisitedNodes, long memoryUsed) implements Serializable {

	@Override
	public String toString() {
		return "AtomIteration [interation=" + this.interation + ", time="
				+ this.time + ", numberOfVisitedNodes="
				+ this.numberOfVisitedNodes + ", memoryUsed=" + this.memoryUsed
				+ "]";
	}
}
