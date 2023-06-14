package Model;

import java.io.Serializable;
import java.time.Duration;

public record Result(Duration time, Object result) implements Serializable{

}
