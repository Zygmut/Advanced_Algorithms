package Master;

import java.util.Arrays;
import java.util.Random;

import Model.Connection;
import Model.Node;
import Model.NodeState;
import mesurament.Mesurament;
import utils.Config;

public class Main {

	private static Node[] generateNodes(int number) {
		Node[] nodes = new Node[number];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = new Node(Integer.toString(i), NodeState.MIDDLE, new Connection[] {});
		}
		return nodes;
	}

	private static Node[] generateRandomConnections(Random rng, Node[] nodes, int connectionsPerNode) {
		Node[] generatedNodes = new Node[nodes.length];

		for (int i = 0; i < nodes.length; i++) {
			Node node = nodes[i];
			for (int j = 0; j < connectionsPerNode; j++) {
				Node randomNode = nodes[rng.nextInt(0, nodes.length)];
				double randomWeight = rng.nextDouble();
				node = node.addConnection(randomNode, randomWeight);
			}
			generatedNodes[i] = node;
		}

		return generatedNodes;
	}

	public static void main(String[] args) {
		if (!Config.DEBUG) {
			Mesurament.mesura();
		}
		// MVC mvc = new MVC(Config.VIEW_MAIN_WIN_CONFIG_PATH);
		// mvc.start();

		Node[] nodes = generateNodes(5);
		Random rng = new Random(27);

		Node[] nodesWithConnections = generateRandomConnections(rng, nodes, 3);

		System.out.println("ORIGINAL NODES: ");
		for (Node node : nodes) {
			System.out.println(node);
		}

		System.out.println("GENERATED CONNECTIONS: ");
		for (Node node : nodesWithConnections) {
			System.out.println(node);
		}
	}
}
