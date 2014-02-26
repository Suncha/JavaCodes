import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;



//import edu.gsu.cs.dbengine.*;

public class DLOG {
	public static void main(String args[]) {
		try {
			String dblocation = ""; // provided as a command line argument
									// which
			// specifies the db location to be used
			String fileName = ""; // holds the filename to read the query
			System.out.println("type \"help;\" for usage...");
			try {
				if (true) {
					dblocation = args[0];
					System.out
							.println("Message: Database Provided: Database Directory is ./"
									+ dblocation);
					boolean dbExists = true; // set as default to true for phase
												// 1
					if (dbExists) {
						do {
							String input = readInput().trim();
							if (input.toUpperCase().equals("EXIT;")) {
								System.out.println("Exiting...");
								break;
							} else if (input
									.matches("[@][A-Za-z_][A-Za-z_0-9]*[;]")) {
								fileName = input.substring(1,
										input.length() - 1);
								try {
									File file = new File(fileName);
									boolean exists = file.exists();
									if (exists) {
										parseAndPrintContent(fileName,
												dblocation);
									} else {
										System.out.println("Error: File "
												+ fileName + " Not Found");
									}
								} catch (Exception e) {
									System.out.println(e.getMessage());
									break;
								}
							} else {
								System.out
										.println("The filename must follow the convention @fileName \n"
												+ "or If the filename is correct, there is Error in input query...");
							}
						} while (true);
					} else {
						System.out.println("Error: Database not found in "
								+ dblocation + " Not Found");
					}
				}
			} catch (Exception e) {
				System.out.println("Parse Error - Please fix and re-run");
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void parseAndPrintContent(String fileName, String dblocation) {
		try {
			parser p = new parser(new Lexer(new FileReader(fileName)));
			Program prg = (Program) p.parse().value;
			System.out.println(prg);
			doSemanticChecks(prg, dblocation);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static boolean doSemanticChecks(Program p, String dblocation) {
		SemanticChecks sc = new SemanticChecks(dblocation);
		boolean safetyCheck = true;
		IDBPredicate idb = new IDBPredicate();
		try {
			// System.out.println("Check 1");
			safetyCheck = sc.doAnswerPredicateCheck(p);
			if (!safetyCheck)
				return false;
			// System.out.println("Check 2");
			safetyCheck = sc.doSafetyCheck(p);
			if (!safetyCheck)
				return false;
			// System.out.println("Check 3 --Recursion and others");
			safetyCheck = sc.doSchemaCheck(p);
			if (!safetyCheck)
				return false;
			// System.out.println("Check 3.4");
			safetyCheck = sc.doArityCheck(p);
			if (!safetyCheck)
				return false;
			// System.out.println("Check 3.3");
			safetyCheck = sc.doComplexArgumentCheck(p);
			if (!safetyCheck)
				return false;
			//System.out.println("Check 4");
			safetyCheck = sc.doTypeCheck(p);
			if (safetyCheck)
				System.out.println("NO SEMANTIC ERROR");
			sc.Process(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return safetyCheck;
	}

	static String readInput() {
		try {
			InputStreamReader isr = new InputStreamReader(System.in);
			BufferedReader br = new BufferedReader(isr);
			System.out.print("DLOG> ");
			return br.readLine().trim();
		} catch (IOException e) {
			return "";
		}
	}

}
