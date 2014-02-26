import java.util.Vector;

public class IDBPredicate implements Comparable<IDBPredicate> {
	String predicateName;
	Vector<Rule> rules;
	Vector<String> argDataType;
	Integer stratum;
	Relation relation = null;

	public Relation getRelation() {
		return relation;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	public IDBPredicate() {
		rules = new Vector<Rule>();
	}

	public String getPredicateName() {
		return predicateName;
	}

	public void setPredicateName(String predicateName) {
		this.predicateName = predicateName;
	}

	public Vector<Rule> getRules() {
		return rules;
	}

	public void setRules(Vector<Rule> rules) {
		this.rules = rules;
	}

	public Vector<String> getArgDataType() {
		return argDataType;
	}

	public void setArgDataType(Vector<String> argDataType) {
		this.argDataType = argDataType;
	}

	public Integer getStratum() {
		return stratum;
	}

	public void setStratum(Integer stratum) {
		this.stratum = stratum;
	}

	public void addRule(Rule r) {
		this.rules.add(r);
	}

	public boolean equals(Object o) {
		if (!(o instanceof IDBPredicate))
			return false;
		IDBPredicate idb = (IDBPredicate) o;
		return idb.stratum == stratum;
	}

	public int hashCode() {
		return 31 * predicateName.hashCode();
	}

	public String toString() {
		return stratum.toString();
	}

	public int compareTo(IDBPredicate other) {
		return this.stratum.compareTo(other.stratum);
	}

}
