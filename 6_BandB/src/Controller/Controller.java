package Controller;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import Model.Board;
import Model.Heuristic;
import Model.MemoStats;
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
		MemoStats stats = new MemoStats(board.getState().length);
		PriorityQueue<Node> pQueue = new PriorityQueue<>(Comparator.comparingInt(e -> this.cost(e, heuristic)));
		pQueue.add(new Node(board, 0, Collections.emptyList()));

		Set<Board> memo = new HashSet<>();
		memo.add(board);

		while (!pQueue.isEmpty()) {
			Node node = pQueue.poll();

			if (node.board().isSolved()) {
				return new Solution(board, heuristic, node.movements(), stats, Duration.between(start, Instant.now()));
			}

			for (Movement move : Movement.values()) {
				Board cpBoard = new Board(node.board());
				stats.addState();
				if (!cpBoard.move(move)) {
					continue;
				}
				stats.addRef();
				if (memo.contains(cpBoard)) {
					stats.addHit();
					continue;
				}

				List<Movement> cpMovements = new ArrayList<>(node.movements());
				cpMovements.add(move);
				pQueue.add(new Node(cpBoard, node.depth() + 1, cpMovements));
				memo.add(cpBoard);
			}
		}

		return new Solution(board, heuristic, Collections.emptyList(), stats, Duration.between(start, Instant.now()));
	}

	public int cost(Node node, Heuristic heuristic) {
		return heuristic.apply(node.board()) + node.depth();
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
