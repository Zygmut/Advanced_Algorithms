package Model;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;

public record Solution(Board board, Heuristic heuristic, List<Movement> movements, MemoStats memoStats,
        Duration timeStats) implements Serializable{
}
