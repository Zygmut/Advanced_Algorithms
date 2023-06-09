package Model;

import java.util.List;

public record Node(Board board, int depth, List<Movement> movements) {
}
