package Model;

import java.io.Serializable;
import java.util.List;

public record Solution(Board board, Heuristic heuristic, List<Movement> movements, ExecStats stats)
		implements Serializable {
}
