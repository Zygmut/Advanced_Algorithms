package Model.Statistics;

import java.time.Instant;
import java.util.List;

import Model.DistanceType;
import Model.Path;
import utils.Algorithms;

public record Statistics(Path solution, Instant time, int nodes, double distance,
		DistanceType distanceType, int iterations, int numberOfVisitedNodes,
		long memoryUsed, Algorithms algorithm, List<AtomIteration> dataPerIteration) {
}
