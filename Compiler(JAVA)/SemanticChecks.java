import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

//import edu.gsu.cs.dbengine.*;

public class SemanticChecks {
	boolean answerFoundinHead = false;
	boolean answerNotFoundInBody = true;
	boolean headbodyVariableMismatch = false; // variable Name in head doesnot
												// appear in body
	String dblocation = "";

	int ruleNo = 0; // indicates the rule number which fails the semantic checks
	HashMap<String, IDBPredicate> hmap, tempMap;
	List<IDBPredicate> sortedHMapList;

	public SemanticChecks(String dblocation) {
		hmap = new HashMap<String, IDBPredicate>();
		tempMap = new HashMap<String, IDBPredicate>();
		this.dblocation = dblocation;
	}

	public void incrementRuleNo() {
		ruleNo++;
	}

	public void resetRuleNo() {
		ruleNo = 0;
	}

	// check if answer predicate is found in the head of the rule and
	// not in the body of rule
	public boolean doAnswerPredicateCheck(Program p) {
		Vector<Rule> rules = p.getRules();
		for (Rule r : rules) {
			Predicate headPredicate = r.getHeadPredicate();
			String predName = headPredicate.getPredName();
			incrementRuleNo();
			if (predName.equalsIgnoreCase("answer")) {
				answerFoundinHead = true; // answer is found in head which is
											// required condition
				break;
			}
		}
		if (answerFoundinHead == false) {
			System.out
					.println("SEMANTIC ERROR: Answer predicate not found in head of Rule no "
							+ ruleNo);
			return false;
		} else {
			resetRuleNo();
			for (Rule r : rules) {
				incrementRuleNo();
				Vector<Predicate> bodyPredicates = r.getBodyPredicates();
				for (Predicate pred : bodyPredicates) {
					if (null != pred.getPredName()
							&& pred.getPredName().equalsIgnoreCase("answer")) {
						answerNotFoundInBody = false; // answer found in body
						break;
					}
				}
				if (answerNotFoundInBody == false) {
					System.out
							.println("SEMANTIC ERROR: Answer predicate found in body of Rule no "
									+ ruleNo);
					return false;
				}
			}
		}
		return true;
	}

	// performs safety check
	public boolean doSafetyCheck(Program p) {
		Vector<String> variableArgumentsinHead;
		Vector<String> variableArgumentsinBody;
		Vector<Rule> rules = p.getRules();
		resetRuleNo();
		for (Rule r : rules) {
			variableArgumentsinHead = new Vector<String>();
			variableArgumentsinBody = new Vector<String>();
			incrementRuleNo();
			Predicate headPredicate = r.getHeadPredicate();
			for (Argument a : headPredicate.getArguments()) {
				variableArgumentsinHead.add(a.getArgName());
			}
			Vector<Predicate> bodyPredicate = r.getBodyPredicates();
			for (Predicate pre : bodyPredicate) {
				if (null != pre.getArguments()) {
					for (Argument arg : pre.getArguments()) {
						variableArgumentsinBody.add(arg.getArgName());
						if (arg.isComplex()
								&& null != arg.getComplexPredicate()
										.getArguments()) {
							for (Argument innerArg : arg.getComplexPredicate()
									.getArguments()) {
								variableArgumentsinBody.add(innerArg
										.getArgName());
							}
						}
					}
				}
			}

			for (String str : variableArgumentsinHead) {
				if (!variableArgumentsinBody.contains(str)) {
					System.out
							.println("SEMANTIC ERROR: Head Predicate contains variable not found in regular body predicate in Rule "
									+ ruleNo);
					return false;
				}
			}
		}

		Vector<String> arginNegPredicates;
		Vector<String> arginBodyPredicates;
		resetRuleNo();
		for (Rule r : rules) {
			arginNegPredicates = new Vector<String>();
			arginBodyPredicates = new Vector<String>();
			incrementRuleNo();
			Vector<Predicate> bodyPredicate = r.getBodyPredicates();
			for (Predicate pre : bodyPredicate) {
				if (null != pre.getArguments()) {
					if (pre.isNegated()) {
						for (Argument arg : pre.getArguments()) {
							// System.out.println("Arguments in negated predicate "+arg.getArgName());
							if (!(arg.isConstant() || arg.getArgName()
									.equalsIgnoreCase("_"))) {
								arginNegPredicates.add(arg.getArgName());
							}
						}
					} else {
						for (Argument arg : pre.getArguments()) {
							// System.out.println("The argument name is "+arg.getArgName()+"== "+arg.getArgDataType());
							if (!(arg.isConstant() || arg.getArgName()
									.equalsIgnoreCase("_"))) {
								arginBodyPredicates.add(arg.getArgName());
							}
						}
					}
				}
			}

			for (String str : arginNegPredicates) {

				if (!arginBodyPredicates.contains(str)) {
					System.out
							.println("SEMANTIC ERROR: Argument in Negative Predicate doesnot appear in body in Rule "
									+ ruleNo + str);
					return false;
				}
			}
		}
		return true;
	}

	// populates the hashmap
	public boolean populateHashMap(Program p) {
		Vector<Rule> rules = p.getRules();
		Vector<String> argDataType;
		boolean arityCheckFailed = false;
		String arityCheckFailedPredName = "";
		IDBPredicate idb;

		// populating hashmap for head predicates
		for (Rule r : rules) {
			Predicate headPredicate = r.getHeadPredicate();
			String key = headPredicate.getPredName();

			// if HashMap already contains a rule with the same name- true for
			// predicates with two rules
			if (hmap.containsKey(key)) {
				IDBPredicate temp = hmap.get(key);
				temp.addRule(r);
				// argDataType = new Vector<String>();
				argDataType = temp.getArgDataType();
				if (argDataType.size() != r.getHeadPredicate().getArguments()
						.size()) {
					arityCheckFailed = true;
					arityCheckFailedPredName = key;
					break;
				}
			} else { // Predicate is a new entry to Hashmap
				argDataType = new Vector<String>();
				for (Argument arg : r.getHeadPredicate().getArguments()) {
					if (null != arg.getArgName()) {
						argDataType.add(arg.getArgDataType());
					}
				}
				idb = new IDBPredicate();
				idb.addRule(r);
				idb.setPredicateName(key);
				idb.setArgDataType(argDataType);
				idb.setStratum(0);
				hmap.put(key, idb);
			}
		}

		// // setting level of all bodypredicates to 0
		// for (Rule r : rules) {
		// Vector<Predicate> bodyPredicate = r.getBodyPredicates();
		// for (Predicate pred : bodyPredicate) {
		// pred.setLevel(0);
		// }
		// }

		if (arityCheckFailed) {
			System.out
					.println("SEMANTIC ERROR: The number of arguments is not correct in IDBPredicate named "
							+ arityCheckFailedPredName);
			return true;
		}
		// System.out.println("Hashmap Populated");
		return false;
	}

	public boolean doStratification(Program prg) {
		// System.out.println("At the beginning of Stratification");
		boolean arityCheckFailed = populateHashMap(prg);
		if (arityCheckFailed) {
			// System.out.println("Arity check failed during stratification");
			return false;
		} else {
			// displayHashMap();
			// System.out.println("Doing Stratification");
			boolean stratumChanged;
			Vector<Predicate> bodyPredicates = null;
			Predicate headPredicate = null;
			int noOfPredicates = 0;
			int prevStratum = 0;
			noOfPredicates = hmap.size();
			while (true) {
				stratumChanged = false;
				for (Rule r : prg.getRules()) {
					headPredicate = r.getHeadPredicate();
					String key = headPredicate.getPredName();
					IDBPredicate currentIDBPred = hmap.get(key);
					// System.out.println("Inside outer for");
					prevStratum = currentIDBPred.getStratum();
					int maxBodyStratum = 0;
					bodyPredicates = r.getBodyPredicates();
					for (Predicate pred : bodyPredicates) {
						// System.out.println("Inside inner for");
						if (hmap.containsKey(pred.getPredName())) {
							if (hmap.get(pred.getPredName()).getStratum() > maxBodyStratum) {
								maxBodyStratum = hmap.get(pred.getPredName())
										.getStratum();
							}
						}
					}

					// setting stratum to max stratum in body + 1
					currentIDBPred.setStratum(maxBodyStratum + 1);

					if (currentIDBPred.getStratum() > noOfPredicates) { // denotes
																		// recursion
						// System.out.println("SEMANTIC ERROR: There is a recursion");
						return false;
					}
					// if the value of stratum is changed then stratumChanged
					// will
					// be set to true
					if (currentIDBPred.getStratum() - prevStratum > 0) {
						stratumChanged = true;
					}
				}
				// System.out.println("----------inside while------");
				if (!stratumChanged) {
					// displayHashMap();
					return false;
				}
			}

		}
	}

	public Boolean doSchemaCheck(Program p) {
		// #1 Regular Body Predicate Check
		// System.out.println("Inside schema check");
		Vector<String> predicatesinHead;
		Vector<Rule> rules = p.getRules();
		boolean regBodyPredicateRuleFailed = false;
		boolean headPredCheckFailed = false;
		Relation.initializeDatabase(dblocation);
		predicatesinHead = new Vector<String>();
		resetRuleNo();
		for (Rule r : rules) {
			incrementRuleNo();
			Predicate headPredicate = r.getHeadPredicate();
			// System.out.println(headPredicate.getPredName()+" ---- "+
			// Relation.relationExists(headPredicate.getPredName().toUpperCase()));
			if (!Relation.relationExists(headPredicate.getPredName())) {
				predicatesinHead.add(headPredicate.getPredName());
			} else {
				headPredCheckFailed = true;
				break;
			}

		}
		if (headPredCheckFailed) {
			System.out
					.println("SEMANTIC ERROR: Head Predicate check failed in Rule no "
							+ ruleNo);
			return false;
		}

		// # 2 Head Predicate Check
		resetRuleNo();// initializing the rule no to 1
		for (Rule r : rules) {
			incrementRuleNo();
			for (Predicate bodyPred : r.getBodyPredicates()) {
				// System.out.println("The body predicate is "+bodyPred.getPredName());
				if (null != bodyPred.getPredName()) {
					if (!predicatesinHead.contains(bodyPred.getPredName())) {

						if (!Relation.relationExists(bodyPred.getPredName())) {
							regBodyPredicateRuleFailed = true;
							// System.out.println(bodyPred.getPredName()
							// + "  does not exists in database");
							break;
						}
					}
				}
			}
		}
		if (regBodyPredicateRuleFailed) {
			System.out
					.println("SEMANTIC ERROR: Regular Body Predicate check failed in Rule no "
							+ ruleNo);
			return false;
		}

		// stratification for recursive query check
		if (!doStratification(p)) {
			// System.out.println("****************Stratification returned true*************");
			// displayHashMap();
			// List<IDBPredicate> list = new
			// ArrayList<IDBPredicate>(hmap.values());
			// Collections.sort(list);

			// displayHashMap();
			// System.out.println(list);
			sortHashMap();
			return true;
		}
		// System.out
		// .println("****************Stratification returned false*************");
		// displayHashMap();
		return true;
	}

	public Boolean doArityCheck(Program prg) {
		// System.out.println("Inside arity check");
		Map<String, Integer> arityMap;
		for (Rule r : prg.getRules()) {
			arityMap = new HashMap<String, Integer>();
			Vector<Predicate> bodyPredicate = r.getBodyPredicates();
			for (Predicate pred : bodyPredicate) {
				if (null != pred.getPredName()) {
					if (arityMap.containsKey(pred.getPredName())) {
						Integer argSize = arityMap.get(pred.getPredName());
						if (argSize != pred.getArguments().size()) {
							System.out
									.println("SEMANTIC ERROR: Number of arity mismatch in Database Predicate named "
											+ pred.getPredName());
							return false;
						}
					} else {
						Integer argSizeInPred = pred.getArguments().size();
						arityMap.put(pred.getPredName(), argSizeInPred);
					}
				}
			}
		}
		return true;
	}

	public boolean doComplexArgumentCheck(Program prg) {
		// Map<String, Integer> arityMap = new HashMap<String, Integer>();
		for (Rule r : prg.getRules()) {
			Vector<Predicate> bodyPredicate = r.getBodyPredicates();
			for (Predicate pred : bodyPredicate) {
				int noOfComplexArguments = 0;
				if (null != pred.getPredName()) {
					Vector<Argument> argVec = pred.getArguments();
					for (Argument arg : argVec) {
						if (arg.isComplex()) {
							if (pred.isNegated()) {
								System.out
										.println("SEMANTIC ERROR: Complex Arguments are not allowed in Negated Predicates");
								return false;
							}
							noOfComplexArguments++;
							if (noOfComplexArguments >= 2) {
								System.out
										.println("SEMANTIC ERROR: Only one complex argument allowed per predicate");
								return false;
							} else {
								int numOfStarOrHashesBeforeColon = arg
										.getNumberOfStarsOrHashes();
								int numOfStarOrHashesAfterColon = 0;
								Predicate complexPred = arg
										.getComplexPredicate();
								Vector<Argument> complexArg = complexPred
										.getArguments();
								//System.out
										//.println("The complex predicate name is "
										//		+ complexPred.getPredName());
								for (Argument cArg : complexArg) {
									//System.out
											//.println("The complex argument name is "
												//	+ cArg.getArgName());
									if (null == cArg.getArgName()) {
										if (!cArg.isConstant()) {
											System.out
													.println("SEMANTIC ERROR: Only *, #, _ and constants are allowed as arguments in Complex Predicate");
											return false;
										}
									} else {
										if (cArg.getArgName().equalsIgnoreCase(
												"*")
												|| cArg.getArgName()
														.equalsIgnoreCase("#")
												|| cArg.getArgName()
														.equalsIgnoreCase("_")) {
											if (cArg.getArgName()
													.equalsIgnoreCase("*")
													|| cArg.getArgName()
															.equalsIgnoreCase(
																	"#")) {
												numOfStarOrHashesAfterColon++;
											}
										} else {
											System.out
													.println("SEMANTIC ERROR: Only *, #, _ and constants are allowed as arguments in Complex Predicate");
											return false;
										}
									}

								}
								if (numOfStarOrHashesBeforeColon != numOfStarOrHashesAfterColon) {
									System.out
											.println("SEMANTIC ERROR: number of stars/hashes donot matches up on both sides of COLON");
									return false;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

	public boolean doTypeCheck(Program p) {
		Vector<Rule> rules = p.getRules();
		Map<String, String> argMap;
		for (Rule r : rules) {
			incrementRuleNo();
			for (Predicate bodyPred : r.getBodyPredicates()) {
				/*
				 * System.out.println("The body predicate is " +
				 * bodyPred.getPredName());
				 */
				argMap = new HashMap<String, String>();
				if (Relation.relationExists(bodyPred.getPredName())) {
					if (!bodyPred.isComparison()) {
						Relation rel = Relation.getRelation(bodyPred
								.getPredName());
						// int argSize = bodyPred.getArguments().size();
						Vector<String> colTypes = rel.getDomains(); // gives me
																	// column
																	// types
						/*
						 * for (String str : colTypes) {
						 * System.out.println("The column type is " + str); }
						 */
						// column types
						int j = 0;
						for (Argument arg : bodyPred.getArguments()) {
							if (arg.isConstant()
									|| !arg.getArgName().equalsIgnoreCase("_")) {
								/*
								 * System.out.println("The argname is " +
								 * arg.getArgName() + " and argType is " +
								 * arg.getArgDataType());
								 */
								if (arg.isConstant()) {
									if (arg.getArgDataType().equalsIgnoreCase(
											"STRING")) {
										if (!colTypes.elementAt(j)
												.equalsIgnoreCase("VARCHAR")) {
											/*
											 * System.out .println(
											 * "SEMANTIC ERROR: Data Type Mismatch in Body Predicate "
											 * + bodyPred
											 * .getPredName()+" and argument "
											 * +arg.getArgDataType() +
											 * " Found number in place of string --"
											 * +
											 * colTypes.elementAt(j)+" and j is "
											 * +j);
											 */
											System.out
													.println("SEMANTIC ERROR: Data Type Mismatch in Body Predicate "
															+ bodyPred
																	.getPredName()
															+ " Found number in place of string");
											return false;
										}
									} else {
										if (!(colTypes.elementAt(j)
												.equalsIgnoreCase("INTEGER") || colTypes
												.elementAt(j).equalsIgnoreCase(
														"DECIMAL"))) {
											System.out
													.println("SEMANTIC ERROR: Data Type Mismatch in Body Predicate "
															+ bodyPred
																	.getPredName()
															+ " Found string in place of number");
											return false;
										}
									}
								} else {
									// argument is variable
									if (!argMap.containsKey(arg.getArgName())) {
										argMap.put(arg.getArgName(),
												colTypes.elementAt(j));
									} else {
										String argType = argMap.get(arg
												.getArgName());
										if (!argType.equalsIgnoreCase(colTypes
												.elementAt(j))) {
											System.out
													.println("SEMANTIC ERROR: Repeated variable check failed for variable "
															+ arg.getArgName());
											return false;
										}
									}
								}

							}
							j++;
						}
					} else {
						// if the predicate is comparison predicate
						Relation rel = Relation.getRelation(bodyPred
								.getPredName());

						Argument rightOperand = bodyPred.getRightOperand();
						Argument leftOperand = bodyPred.getLeftOperand();

						Vector<String> colTypes = rel.getDomains(); // gives me
						// column
						// types
						/*
						 * for (String str : colTypes) {
						 * System.out.println("The column type is " + str); }
						 */
						// column types
						int j = 0;
						if (rightOperand.isConstant()) {
							for (Argument arg : bodyPred.getArguments()) {
								if (!arg.getArgName().equalsIgnoreCase("_")) {
									if (arg.getArgName().equalsIgnoreCase(
											leftOperand.getArgName())) {
										if (rightOperand.getArgDataType()
												.equalsIgnoreCase("STRING")) {
											if (!(colTypes.elementAt(j)
													.equalsIgnoreCase("VARCHAR"))) {
												System.out
														.println("SEMANTIC ERROR: Type check failed in comparison predicate "
																+ bodyPred
																		.getPredName()
																+ " Found "
																+ arg.getArgDataType()
																+ " in place of STRING");
												return false;
											}
										} else if (rightOperand
												.getArgDataType()
												.equalsIgnoreCase("NUMBER")) {
											if (!(colTypes.elementAt(j)
													.equalsIgnoreCase("INTEGER"))
													|| (colTypes.elementAt(j)
															.equalsIgnoreCase("DECIMAL"))) {
												System.out
														.println("SEMANTIC ERROR: Type check failed in comparison predicate "
																+ bodyPred
																		.getPredName()
																+ " Found "
																+ arg.getArgDataType()
																+ " in place of NUMBER");
												return false;
											}
										}

									}
								}
							}
						}

					}
				}
			}
		}
		return true;
	}

	void displayHashMap() {
		System.out.println("Displaying Stratification");
		Iterator<Entry<String, IDBPredicate>> it = hmap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, IDBPredicate> pairs = (Map.Entry<String, IDBPredicate>) it
					.next();
			System.out.println(pairs.getKey() + " = "
					+ pairs.getValue().getStratum());
		}
	}

	void sortHashMap() {
		sortedHMapList = new ArrayList<IDBPredicate>(hmap.values());
		Collections.sort(sortedHMapList);
		tempMap = new HashMap<String, IDBPredicate>();
		for (IDBPredicate idb : sortedHMapList) {
			tempMap.put(idb.getPredicateName(), idb);
			// System.out.println("The predicate name is "
			// + idb.getPredicateName() + " and stratum is "
			// + idb.getStratum());
		}
		// hmap.clear();
		// hmap = tempMap;

		// displayTempHashMap();
		// displayTempHashMap();

	}

	void displayTempHashMap() {
		Iterator<Entry<String, IDBPredicate>> it = tempMap.entrySet()
				.iterator();
		while (it.hasNext()) {
			Map.Entry<String, IDBPredicate> pairs = (Map.Entry<String, IDBPredicate>) it
					.next();
			System.out.println(pairs.getKey() + " = "
					+ pairs.getValue().getStratum());
		}
	}

	public void Process(Program p) {
		// displayHashMap();
		Vector<Rule> ruleVecs = new Vector<Rule>();
		// Iterator<Entry<String, IDBPredicate>> it =
		// hmap.entrySet().iterator();

		// checking to see if there are predicates with complex argument
		for (Rule r : p.getRules()) {
			for (Predicate pred : r.getBodyPredicates()) {
				if (null != pred.getArguments()) {
					for (Argument arg : pred.getArguments()) {
						if (arg.isComplex()) {
							pred.setComplex(true);
						}
					}
				}
			}
		}

		for (IDBPredicate idbPred : sortedHMapList) {
			String key = idbPred.getPredicateName();
			// System.out.println("Starting processing for predicate name " +
			// key);
			for (Rule rule : p.getRules()) {
				if (key.equalsIgnoreCase(rule.getHeadPredicate().getPredName())) {
					ruleVecs.add(rule);
				}
			}
			// evaluating the head predicates
			for (Rule rule : ruleVecs) {
				Relation rel = null;
				for (Predicate prd : rule.getBodyPredicates()) {

					if (prd.isComplex()) {
						// System.out.println("Evaluating complex predicates");
						// rel = evaluateComplexPredicate(prd);
						if (rel == null) {
							rel = evaluateComplexPredicate(prd);
						} else {
							// rel.displayRelation();
							// System.out
							// .println("-----------------------------------");

							// changed here
							// Relation tempRel = evaluateComplexPredicate(prd);
							rel = rel.join(evaluateComplexPredicate(prd));
							// tempRel.displayRelation();
							// rel = rel.join(tempRel);

						}

					} else if (prd.isComparison()) {
						// System.out.println("The predicate is comparison "
						// + prd.isComparison());
						String lopType = "col";
						String lopValue = prd.getLeftOperand().getArgName();
						String comparison = prd.getComparisonOperator();
						String ropType = "";
						if (prd.getRightOperand().isConstant()) {
							if (prd.getRightOperand().getArgDataType()
									.equalsIgnoreCase("NUMBER")) {
								ropType = "num";
							} else {
								ropType = "str";
							}
						} else {
							ropType = "col";
						}
						String ropValue = "";
						if (ropType.equalsIgnoreCase("str")) {
							ropValue = prd
									.getRightOperand()
									.getArgValue()
									.substring(
											1,
											prd.getRightOperand().getArgValue()
													.length() - 1);
						} else if (ropType.equalsIgnoreCase("num")) {
							ropValue = prd.getRightOperand().getArgValue();
						} else {
							ropValue = prd.getRightOperand().getArgName();
						}
						// System.out.println("The values are " + lopType +
						// " - "
						// + lopValue + " - " + comparison + " - "
						// + ropType + " - " + ropValue);
						// rel.displayRelation();
						// rel.displayRelationSchema();
						rel = rel.selection(lopType, lopValue, comparison,
								ropType, ropValue);
					} else if (prd.isNegated()) {
						// rel = rel.minus(evaluatePredicate(prd));
						// rel = evaluateNegPredicate(rule);

						if (rel == null) {
							rel = evaluateNegPredicate(rule);
						} else {
							rel = rel.join(evaluateNegPredicate(rule));
						}
					} else {
						if (rel == null) {
							rel = evaluatePredicate(prd);
							// System.out
							// .println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
						} else {
							rel = rel.join(evaluatePredicate(prd));
							// System.out.println("The join is done here ............................................................");
							// rel.displayRelation();
						}

					}
				}

				// System.out
				// .println("---------------Setting relation to head predicate--------------------");
				if (null == hmap.get(rule.getHeadPredicate().getPredName())
						.getRelation()) {
					hmap.get(rule.getHeadPredicate().getPredName())
							.setRelation(rel);
					// System.out.println("Setting relation for the first time "
					// + rule.getHeadPredicate().getPredName());
				} else {
					Relation rel1 = hmap.get(
							rule.getHeadPredicate().getPredName())
							.getRelation();
					// System.out.println("THe rel1 is " + rel1);
					hmap.get(rule.getHeadPredicate().getPredName())
							.setRelation(rel1.union(rel));
				}

				rel = evaluatePredicate(rule.getHeadPredicate());
				hmap.get(rule.getHeadPredicate().getPredName())
						.setRelation(rel);

			}

			// System.out.println("Displaying key and stratum -----"
			// + pairs.getKey() + " = " + pairs.getValue().getStratum());
		}
		Relation finalRel = hmap.get("ANSWER").getRelation();
		System.out.println("ANSWER Relation is:");
		finalRel.displayRelation();
	}

	public Relation evaluateNegPredicate(Rule rule) {
		Relation rel = null;
		Vector<String> argVec;
		Predicate negPred = null;
		for (Predicate pred : rule.getBodyPredicates()) {
			if (pred.isNegated()) {
				negPred = pred;
			}
		}
		for (Argument arg : negPred.getArguments()) {
			// System.out.println("The negative arguments size is "
			// + negPred.getArguments().size());
			if (!arg.isConstant() && !arg.isComplex() && !arg.isUnderscore()) {
				String argName = arg.getArgName();
				// System.out.println("The negative argName is " + argName
				// + "and is " + arg.isConstant());
				for (Predicate pred : rule.getBodyPredicates()) {
					if (!pred.isNegated() && !pred.isComparison()) {
						for (Argument args : pred.getArguments()) {
							if (args.getArgName() != null
									&& argName.equalsIgnoreCase(args
											.getArgName())) {
								// System.out.println("Neg arg " + argName
								// + " found in predicate named "
								// + pred.getPredName());
								argVec = new Vector<String>();
								argVec.add(args.getArgName());
								if (rel == null) {
									rel = evaluatePredicate(pred).projection(
											argVec);
									// rel.displayRelation();
								} else {
									rel = rel
											.cartesianProduct(evaluatePredicate(
													pred).projection(argVec));
								}
							}
						}
					}
				}
			}
		}
		// System.out.println("Printing before minus");
		// Relation temp = evaluatePredicate(negPred);
		// temp.displayRelation();
		// System.out.println("Printing middle before minus");
		// rel.displayRelation();
		rel = rel.minus(evaluatePredicate(negPred));
		// System.out.println("Printing after evalauting negative predicate");
		// rel.displayRelation();
		return rel;
	}

	public Relation evaluatePredicate(Predicate p) {
		// System.out.println("The predicate name is " + p.getPredName()
		// + " and isEDB value is " + isEDB(p.getPredName()));
		Relation rel = null;// , temp = null;
		if (isEDB(p.getPredName())) {
			rel = Relation.getRelation(p.getPredName());
		} else {
			rel = hmap.get(p.getPredName()).getRelation();
		}

		Vector<String> cols = new Vector<String>();
		for (int i = 0; i < p.getArguments().size(); i++) {
			cols.add("C" + i);
		}
		// renaming functions
		// rel.displayRelationSchema();
		rel = rel.rename(cols);

		// handle the constant
		int j = 0;
		for (Argument arg : p.getArguments()) {
			if (arg.isConstant()) {
				String lopType = "col";
				String lopValue = "C" + j;
				// System.out.println("The column constant is C" + j);
				String comparison = "=";
				String ropType;
				if (arg.getArgDataType().equalsIgnoreCase("NUMBER")) {
					ropType = "num";
				} else {
					ropType = "str";
				}
				String ropValue = "";
				if (ropType.equalsIgnoreCase("str")) {
					ropValue = arg.getArgValue().substring(1,
							arg.getArgValue().length() - 1);
				} else {
					ropValue = arg.getArgValue();
				}
				// System.out.println("The values are " + lopType + " - "
				// + lopValue + " - " + comparison + " - " + ropType
				// + " - " + ropValue);
				rel = rel.selection(lopType, lopValue, comparison, ropType,
						ropValue);

			}
			j++;
		}

		// handle repeated variables
		Vector<String> finishedRepeatedVariables = new Vector<String>();

		for (int i = 0; i < p.getArguments().size(); i++) {
			for (int k = i + 1; k < p.getArguments().size(); k++) {
				String argOne = p.getArguments().get(i).getArgName();
				String argTwo = p.getArguments().get(k).getArgName();
				// System.out.println("The argOne and argTwo is "+argOne+" and "+argTwo);
				if (null != argOne && argOne.equalsIgnoreCase(argTwo + k)) {
					if (!finishedRepeatedVariables.contains(argTwo + k)) {
						finishedRepeatedVariables.add(argTwo + k);
						rel = evaluateRepeatedVariables(argOne, argTwo, i, k,
								rel);
					}
				}
			}
		}
		// rel.displayRelationSchema();

		// projection on unique variables
		Vector<String> uniqueCols = new Vector<String>();
		Vector<String> colNames = new Vector<String>();
		int k = 0;
		for (Argument arg : p.getArguments()) {
			if (!arg.isConstant() && !arg.isComplex() && !arg.isUnderscore()) {
				if (!uniqueCols.contains(arg.getArgName())) {
					uniqueCols.add("C" + k);
					colNames.add(arg.getArgName());
					// System.out.println("The unique column is C" + k);
				}
			}
			k++;
		}
		rel = rel.projection(uniqueCols);
		rel = rel.rename(colNames);
		rel.setRelationName(p.getPredName());
		return rel;

	}

	public Relation evaluateRepeatedVariables(String argOne, String argTwo,
			int i, int k, Relation rel) {
		String lopType = "col";
		String lopValue = "C" + i;
		String comparison = "=";
		String ropType = "col";
		String ropValue = "C" + k;
		// System.out.println("The values for repeated variables are " + lopType
		// + " - " + lopValue + " - " + comparison + " - " + ropType
		// + " - " + ropValue);
		rel = rel.selection(lopType, lopValue, comparison, ropType, ropValue);
		return rel;
	}

	public Relation evaluateComplexPredicate(Predicate prd) {
		boolean isArgStar = false;

		Vector<Argument> tempVecsInner, tempVecsOuter;
		Vector<String> outerRelScheme = new Vector<String>();
		Vector<String> innerRelScheme = new Vector<String>();
		Vector<String> answerRelSchema = new Vector<String>();
		Predicate innerRelPred, outerRelPred;
		outerRelPred = new Predicate();
		innerRelPred = new Predicate();
		outerRelPred.setPredName(prd.getPredName());
		int index = 1;
		// System.out.println("No of argmuents is " +
		// prd.getArguments().size());
		tempVecsOuter = new Vector<Argument>();
		for (Argument arg : prd.getArguments()) {
			if (arg.isComplex()) {
				if (arg.getArgName().equalsIgnoreCase("*")) {
					isArgStar = true;
				}
				// System.out.println("Number of hashes is "
				// + arg.getNumberOfStarsOrHashes());
				for (int i = 1; i <= arg.getNumberOfStarsOrHashes(); i++) {
					Argument tempArg = new Argument();
					tempArg.setArgName("_X" + i);
					tempVecsOuter.add(tempArg);
					outerRelScheme.add("_X" + i);
					// System.out.println("Adding to outerrelschema " + "_X" +
					// i);
				}
				innerRelPred.setPredName(arg.getComplexPredicate()
						.getPredName());
				tempVecsInner = new Vector<Argument>();
				for (Argument args : arg.getComplexPredicate().getArguments()) {
					// System.out.println("The arg size in comples predicate is "
					// + arg.getComplexPredicate().getArguments().size()
					// + " and " + args.isComplex());
					// System.out.println("Number of inner hashes is "
					// + args.getNumberOfStarsOrHashes());
					// System.out.println("Argument is " + args.getArgName());
					if (!args.isConstant()) {
						if (args.getArgName().equalsIgnoreCase("*")
								|| args.getArgName().equalsIgnoreCase("#")) {
							Argument innerArg = new Argument();
							innerArg.setArgName("_X" + index);
							innerRelScheme.add("_X" + index);
							tempVecsInner.add(innerArg);
							index++;
						}
					} else {
						// innerRelScheme.add(args.getArgValue().toString());
						tempVecsInner.add(args);
					}
				}
				innerRelPred.setArguments(tempVecsInner);
			} else {
				tempVecsOuter.add(arg);
				if (!arg.isConstant()) {
					answerRelSchema.add(arg.getArgName());
					outerRelScheme.add(arg.getArgName());
				} else {
					answerRelSchema.add(arg.getArgValue().toString());
					outerRelScheme.add(arg.getArgValue().toString());
				}

			}
			outerRelPred.setArguments(tempVecsOuter);

		}

		answerRelSchema.remove("_");
		innerRelScheme.remove("_");
		outerRelScheme.remove("_");
		// System.out.print("The outer predicate is " + outerRelPred + "\n");
		// System.out.print("The inner predicate is " + innerRelPred + "\n");
		// display(innerRelScheme, "innerRelScheme");
		// display(outerRelScheme, "outerRelScheme");
		// display(answerRelSchema, "answerRelSchema");
		Relation rel = null, innerRel = null, outerRel = null;
		innerRel = evaluatePredicate(innerRelPred);
		// innerRel.displayRelation();
		outerRel = evaluatePredicate(outerRelPred);
		// outerRel.displayRelation();

		if (isArgStar) {
			rel = outerRel.projection(answerRelSchema);
			rel = rel.cartesianProduct(innerRel);
			rel = rel.projection(outerRelScheme);
			rel = rel.minus(outerRel);
			rel = rel.projection(answerRelSchema);
			rel = outerRel.projection(answerRelSchema).minus(rel);
		} else {
			rel = outerRel.projection(innerRelScheme);
			rel = rel.minus(innerRel);
			rel = outerRel.join(rel);
			rel = rel.projection(answerRelSchema);
			rel = outerRel.projection(answerRelSchema).minus(rel);
		}
		// System.out
		// .println("+++++++++++++++++++Displaying relation at complex part+++++++++++++++++");
		// rel.displayRelation();
		return rel;
	}

	void display(Vector<String> args, String argName) {
		System.out.println("The name is " + argName);
		for (String name : args) {
			System.out.print(name + " ");
		}
		System.out.println();
	}

	public boolean isEDB(String predName) {
		if (Relation.relationExists(predName)) {
			return true;
		} else {
			return false;
		}
	}
}
