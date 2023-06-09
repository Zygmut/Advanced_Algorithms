package Model;

import java.time.Duration;
import java.util.List;

public record Solution(List<Movement> movements, MemoStats memoStats, Duration timeStats) {
}
