package Model;

import java.util.Objects;

public record ExecResultDataTreeNode(String id, ExecResultDataTreeNode[] children) {

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof ExecResultDataTreeNode)) {
			return false;
		}
		ExecResultDataTreeNode execResultDataTreeNode = (ExecResultDataTreeNode) o;
		return Objects.equals(id, execResultDataTreeNode.id)
				&& Objects.equals(children, execResultDataTreeNode.children);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, children);
	}

	@Override
	public String toString() {
		return "{" + " id='" + this.id + "'" + ", children='" + this.children + "'" + "}";
	}

}
