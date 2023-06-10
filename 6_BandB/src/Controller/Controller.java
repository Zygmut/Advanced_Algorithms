package Controller;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import Model.Board;
import Model.Heuristic;
import Model.ExecStats;
import Model.Movement;
import Model.Node;
import Model.Solution;
import Services.Service;
import Services.Comunication.Content.Body;
import Services.Comunication.Request.Request;
import Services.Comunication.Request.RequestCode;
import Services.Comunication.Response.Response;
import Services.Comunication.Response.ResponseCode;

public class Controller implements Service {

	public Controller() {
		// Initialize controller things here
	}

	@Override
	public void start() {
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Controller started.");
	}

	@Override
	public void stop() {
		Logger.getLogger(this.getClass().getSimpleName())
				.log(Level.INFO, "Controller stopped.");
	}

	public Solution solve(Board board, Heuristic heuristic) {
		Instant start = Instant.now();
		ExecStats stats = new ExecStats(board.getState().length);
		PriorityQueue<Node> pQueue = new PriorityQueue<>(Comparator.comparingInt(e -> this.cost(e, heuristic)));
		Integer lowerBound = Integer.MAX_VALUE;
		Solution currentSol = null;
		pQueue.add(new Node(board, Collections.emptyList()));

		HashMap<Board, Integer> memo = new HashMap<>();
		memo.put(board, Integer.MAX_VALUE);

		while (!pQueue.isEmpty()) {
			stats.addState();
			final Node node = pQueue.poll();
			final int size = this.cost(node, heuristic);

			if (size > lowerBound) {
				stats.addPrune();
				continue;
			}

			if (node.board().isSolved()) {
				lowerBound = size;
				stats.setime(Duration.between(start, Instant.now()));
				currentSol = new Solution(board, heuristic, node.movements(), stats);
			}

			for (Movement move : Movement.values()) {
				Board cpBoard = new Board(node.board());
				if (!cpBoard.move(move)) {
					continue;
				}

				List<Movement> cpMovements = new ArrayList<>(node.movements());
				cpMovements.add(move);
				final Node cpNode = new Node(cpBoard, cpMovements);
				final int cost = this.cost(cpNode, heuristic);

				stats.addRef();
				if (memo.getOrDefault(cpBoard, null) != null) {
					stats.addHit();
					continue;
				}

				pQueue.add(cpNode);
				memo.put(cpBoard, cost);
			}
		}

		if (currentSol == null) {
			stats.setime(Duration.between(start, Instant.now()));
			return new Solution(board, heuristic, Collections.emptyList(), stats);
		}

		return currentSol;
	}

	public int cost(Node node, Heuristic heuristic) {
		return heuristic.apply(node.board()) + node.movements().size();
	}

	@Override
	public void notifyRequest(Request request) {
		if (request.code != RequestCode.CALCULATE) {
			Logger.getLogger(this.getClass().getSimpleName())
					.log(Level.SEVERE, "{0} is not implemented.", request);
			return;
		}

		final Object[] params = (Object[]) request.body.content;
		final Board board = (Board) params[0];
		final Heuristic heuristic = (Heuristic) params[1];
		final Solution sol = this.solve(board, heuristic);
		this.sendResponse(new Response(ResponseCode.CALCULATE, this, new Body(sol)));
	}

}
