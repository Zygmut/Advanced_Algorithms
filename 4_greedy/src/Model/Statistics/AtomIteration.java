package Model.Statistics;

import java.time.Instant;

public record AtomIteration(int interation, Instant time,
		int numberOfVisitedNodes, long memoryUsed, double acumulatedDistance) {
}
