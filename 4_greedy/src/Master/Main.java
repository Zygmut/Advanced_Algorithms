package Master;

import java.util.Arrays;
import java.util.Random;

import Model.Connection;
import Model.Node;
import Model.NodeState;
import mesurament.Mesurament;
import utils.Config;

public class Main {

	public static void main(String[] args) {
		if (!Config.DEBUG) {
			Mesurament.mesura();
		}
		// MVC mvc = new MVC(Config.VIEW_MAIN_WIN_CONFIG_PATH);
		// mvc.start();
		int nNodes = 5;
		Random rng = new Random();
		Node[] nodes = new Node[nNodes];
		for (int i = 0; i < nNodes; i++) {
			nodes[i] = new Node(Integer.toString(rng.nextInt()), NodeState.MIDDLE, new Connection[]{});
		}

		for (Node node : nodes) {
			System.out.println(node);
		}

	}
}
