import java.util.Vector;

public class Predicate {
	private String predName;
	private Vector<Argument> arguments;
	private boolean isNegated;
	private boolean isComparison;
	private Argument leftOperand;
	private String comparisonOperator;
	private Argument rightOperand;
	private boolean complex;

	public boolean isComplex() {
		return complex;
	}

	public void setComplex(boolean complex) {
		this.complex = complex;
	}

	public String getPredName() {
		return predName;
	}

	public void setPredName(String predName) {
		this.predName = predName;
	}

	public Vector<Argument> getArguments() {
		return arguments;
	}

	public void setArguments(Vector<Argument> e) {
		this.arguments = e;
	}

	public boolean isNegated() {
		return isNegated;
	}

	public void setNegated(boolean isNegated) {
		this.isNegated = isNegated;
	}

	public boolean isComparison() {
		return isComparison;
	}

	public void setComparison(boolean isComparison) {
		this.isComparison = isComparison;
	}

	public Argument getLeftOperand() {
		return leftOperand;
	}

	public void setLeftOperand(Argument leftOperand) {
		this.leftOperand = leftOperand;
	}

	public String getComparisonOperator() {
		return comparisonOperator;
	}

	public void setComparisonOperator(String comparisonOperator) {
		this.comparisonOperator = comparisonOperator;
	}

	public Argument getRightOperand() {
		return rightOperand;
	}

	public void setRightOperand(Argument rightOperand) {
		this.rightOperand = rightOperand;
	}

	@Override
	public String toString() {
		String str = "";
		if (isNegated)
			str += "NOT ";
		if (isComparison) {
			str += leftOperand + " " + comparisonOperator + " " + rightOperand;
		} else {
			str += predName + "(";
			for (Argument a : arguments) {
				str += a.toString() + ",";
			}
			str = str.substring(0, str.length() - 1);
			str += ")";
		}

		return str;
	}

}
