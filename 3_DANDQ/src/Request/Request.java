package Request;

import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import Model.Point;

public class Request<T> {
	public final RequestCode code;
	public final String origin;
	public final Body<T> body;

	public Request(RequestCode code, String origin, Body<T> body) {
		this.code = code;
		this.origin = origin;
		this.body = body;
	}

	public Request(RequestCode code, Object origin, Body<T> body) {
		this.code = code;
		this.origin = origin.getClass().getSimpleName();
		this.body = body;
	}

	public Request(RequestCode code, String origin) {
		this.code = code;
		this.origin = origin;
		this.body = null;
	}

	public Request(RequestCode code, Object origin) {
		this.code = code;
		this.origin = origin.getClass().getSimpleName();
		this.body = null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (body != null) {
			sb.append(body.getType()).append(" ");
		}
		sb.append("Request ")
				.append(code)
				.append(" from ")
				.append(origin);
		if (body != null) {
			sb.append(" containing ");
			for (Entry<BodyCode, ?> entry : body.getContent().entrySet()) {
				switch (entry.getKey()) {
					case DATA -> {
						Point[] data = (Point[]) entry.getValue();
						sb.append(entry.getKey())
								.append(" [")
								.append(data[0])
								.append(", ")
								.append(data[1])
								.append(" ··· ")
								.append(data[data.length-2])
								.append(", ")
								.append(data[data.length-1])
								.append("]")
								.append(" of length ")
								.append(data.length);
					}
					case POINT_AMOUNT -> {
						sb.append(entry.getKey())
								.append(" = ")
								.append(entry.getValue());
					}
					case SEED -> {
						sb.append(entry.getKey())
								.append(" = ")
								.append(entry.getValue());
					}
					default -> {
						Logger.getLogger(this.getClass().getSimpleName())
								.log(Level.SEVERE, "{0} is not a valid body code.",
										entry.getKey());
					}
				}
			}
		}
		return sb.toString();
	}
}
