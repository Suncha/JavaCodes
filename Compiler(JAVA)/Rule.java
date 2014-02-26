import java.util.Vector;

public class Rule {
	private Predicate headPredicate;
	private Vector<Predicate> bodyPredicates;
	private Vector<Predicate> regularBodyPredicates;
	private Vector<Predicate> comparisonBodyPredicates;

	public Predicate getHeadPredicate() {
		return headPredicate;
	}

	public void setHeadPredicate(Predicate headPredicate) {
		this.headPredicate = headPredicate;
	}

	public Vector<Predicate> getBodyPredicates() {
		return bodyPredicates;
	}

	public void setBodyPredicates(Vector<Predicate> e){
		this.bodyPredicates = e;
	}

	public Vector<Predicate> getRegularBodyPredicates() {
		return regularBodyPredicates;
	}

	public void setRegularBodyPredicates(Vector<Predicate> e) {
		this.regularBodyPredicates=e;
	}

	public Vector<Predicate> getComparisonBodyPredicates() {
		return comparisonBodyPredicates;
	}

	public void setComparisonBodyPredicates(Vector<Predicate> e) {
		this.comparisonBodyPredicates=e;
	}
	
	@Override
	public String toString(){
		String str = "";
		str += headPredicate.toString() + " :- ";
		for(Predicate p : bodyPredicates){
			str += "\n   "+p.toString()+", ";
		}
		str = str.substring(0,str.length()-2);
		str+=".\n";
		return str;
	}

}
