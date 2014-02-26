import java.util.Vector;

public class Argument {
	private boolean isConstant = false; // true if constant argument
	private boolean isUnderscore = false; // true if _ argument
	private String argDataType; // data type for argument : "NUMBER" or "STRING"
	private String argName; // name of argument - if variable or if "*" or "#"
	private String argValue; // value of argument in case of constant argument
	private boolean isComplex; // true for complex arguments
	private Predicate complexPredicate; // stores predicate after : in complex
										// argument; may be null
	private int numberOfStarsOrHashes; // Store the number of stars or hashes in
										// complex argument

	public boolean isConstant() {
		return isConstant;
	}

	public void setConstant(boolean isConstant) {
		this.isConstant = isConstant;
	}

	public boolean isUnderscore() {
		return isUnderscore;
	}

	public void setUnderscore(boolean isUnderscore) {
		this.isUnderscore = isUnderscore;
	}

	public String getArgDataType() {
		return argDataType;
	}

	public void setArgDataType(String argDataType) {
		this.argDataType = argDataType;
	}

	public String getArgName() {
		return argName;
	}

	public void setArgName(String argName) {
		this.argName = argName;
	}

	public String getArgValue() {
		return argValue;
	}

	public void setArgValue(String argValue) {
		this.argValue = argValue;
	}

	public boolean isComplex() {
		return isComplex;
	}

	public void setComplex(boolean isComplex) {
		this.isComplex = isComplex;
	}

	public Predicate getComplexPredicate() {
		return complexPredicate;
	}

	public void setComplexPredicate(Predicate complexPredicate) {
		this.complexPredicate = complexPredicate;
	}

	public int getNumberOfStarsOrHashes() {
		return numberOfStarsOrHashes;
	}

	public void setNumberOfStarsOrHashes(Vector<Argument> a) {
		this.numberOfStarsOrHashes = a.size();
	}

	public String toString(){
		String str = "";
		if(isConstant) 
			str=argValue;
		else if(isUnderscore) str="_";
		else if(isComplex) {
			str+="[";
			for(int i=0; i< numberOfStarsOrHashes;i++){
				if(argName.equals("#")) str+="#,";
				else str+="*,";
			}
			str = str.substring(0,str.length()-1);
			str+="]:";
			str += complexPredicate.toString();
		}
		else
			str = argName;
			
		return str;
	}

}
