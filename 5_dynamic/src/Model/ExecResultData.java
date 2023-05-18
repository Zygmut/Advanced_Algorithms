package Model;

import java.util.Objects;

public record ExecResultData(double value, String[] connnections, String id) {

	@Override
	public boolean equals(Object obj) {
		if (Objects.isNull(obj) || !(obj instanceof ExecResultData)) {
			return false;
		}
		ExecResultData other = (ExecResultData) obj;
		return this.value == other.value && this.id.equals(other.id)
				&& this.connnections.length == other.connnections.length;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, connnections, id);
	}

	@Override
	public String toString() {
		return "ExecResultData [value=" + value + ", connnections=" + connnections + ", id=" + id + "]";
	}
}
