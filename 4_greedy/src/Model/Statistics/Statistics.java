package Model.Statistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Model.DistanceType;
import Model.Path;
import utils.Algorithms;

public class Statistics implements Serializable {

	private Path solution;
	private long time;
	private int nodes;
	private DistanceType distanceType;
	private int iterations;
	private int numberOfVisitedNodes;
	private long memoryUsed;
	private Algorithms algorithm;
	private List<List<AtomIteration>> dataPerIteration;

	public Statistics(DistanceType distanceType, Algorithms algorithm) {
		this.distanceType = distanceType;
		this.algorithm = algorithm;
		this.dataPerIteration = new ArrayList<>();
	}

	public Path getSolution() {
		return this.solution;
	}

	public void setSolution(Path solution) {
		this.solution = solution;
	}

	public long getTime() {
		return this.time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getNodes() {
		return this.nodes;
	}

	public void setNodes(int nodes) {
		this.nodes = nodes;
	}

	public DistanceType getDistanceType() {
		return this.distanceType;
	}

	public void setDistanceType(DistanceType distanceType) {
		this.distanceType = distanceType;
	}

	public int getIterations() {
		return this.iterations;
	}

	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	public int getNumberOfVisitedNodes() {
		return this.numberOfVisitedNodes;
	}

	public void setNumberOfVisitedNodes(int numberOfVisitedNodes) {
		this.numberOfVisitedNodes = numberOfVisitedNodes;
	}

	public long getMemoryUsed() {
		return this.memoryUsed;
	}

	public void setMemoryUsed(long memoryUsed) {
		this.memoryUsed = memoryUsed;
	}

	public Algorithms getAlgorithm() {
		return this.algorithm;
	}

	public void setAlgorithm(Algorithms algorithm) {
		this.algorithm = algorithm;
	}

	public List<List<AtomIteration>> getDataPerIteration() {
		return this.dataPerIteration;
	}

	public void setDataPerIteration(List<List<AtomIteration>> dataPerIteration) {
		this.dataPerIteration = dataPerIteration;
	}

	public void addIteration(List<AtomIteration> iteration) {
		this.dataPerIteration.add(iteration);
	}

	@Override
	public String toString() {
		return "{" +
				" solution='" + getSolution() + "'" +
				", time='" + getTime() + "'" +
				", nodes='" + getNodes() + "'" +
				", distanceType='" + getDistanceType() + "'" +
				", iterations='" + getIterations() + "'" +
				", numberOfVisitedNodes='" + getNumberOfVisitedNodes() + "'" +
				", memoryUsed='" + getMemoryUsed() + "'" +
				", algorithm='" + getAlgorithm() + "'" +
				", dataPerIteration='" + getDataPerIteration() + "'" +
				"}";
	}

}
