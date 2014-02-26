import java.util.Vector;

public class Program {
  private Vector<Rule> rules;
  
  public void setRules(Vector<Rule> i){
		rules=i;
	}

	public Vector<Rule> getRules(){
		return rules;
	}

	public void addRule(Rule r){
		rules.add(r);
	}

	@Override
	public String toString(){
		String str = "--------------INPUT QUERY-----------------\n";
		for(Rule r : rules){
			str += r.toString();
		}
		str+="------------------------------------------";
		return str;
	}
}

