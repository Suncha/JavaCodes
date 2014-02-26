import java.io.*;
import java.util.*;

public class Driver {
  public static void main (String [] args) throws IOException {
    Relation r=null;
    r.initializeDatabase(args[0]);
    boolean done;
    char ch,ch1;

    done = false;
    do {
      print_menu();
      System.out.print("Type in your option:");
      System.out.flush();
      ch = (char) System.in.read();
      ch1 = (char) System.in.read();
      System.out.println("");
      switch (ch) {
        case 'x' : cartesianProduct(r);
                   break;
        case 'p' : projection(r);
                   break;
        case 'e' : extendedProjection(r);
                   break;
        case 's' : selection(r);
                   break;
        case 'i' : inEvaluate(r);
                   break;
        case 'n' : notInEvaluate(r);
                   break;
        case 'c' : r.displaySchema();
                   break;
        case 'd' : displayRelation(r);
                   break;
        case 'r' : removeDuplicates(r);
                   break;
        case 'q' : done = true;
                   break;
        default  : System.out.println("Type in option again");
      }
      System.out.println("");
    } while (!done);

  } // main

  static void displayRelation(Relation r) {
    String rn = readEntry("Relation to display: ").toUpperCase();
    Relation s = r.getRelation(rn); 
    s.displayRelation();
  }

  static void removeDuplicates(Relation r) {
    String rn = readEntry("Relation to purge duplicates from: ").toUpperCase();
    Relation s1 = r.getRelation(rn); 
    System.out.println("Relation before removing duplicates");
    s1.displayRelation();
    s1.removeDuplicates();
    System.out.println("Relation after removing duplicates");
    s1.displayRelation();
  }

  static void projection(Relation r) {
    String rn = readEntry("Relation to project: ").toUpperCase();
    Relation s1 = r.getRelation(rn); 
    String cols = readEntry("Columns to project on (separate cols by spaces): ");
    StringTokenizer st = new StringTokenizer(cols);
    Vector cnames = new Vector();
    while (st.hasMoreTokens()) {
      String cn = st.nextToken().toUpperCase();
      cnames.addElement(cn);
    }
    Relation s = s1.projection(cnames);
    s.displayRelation();
  }

  static void extendedProjection(Relation r) {
    String rn = readEntry("Relation to project: ").toUpperCase();
    Relation s1 = r.getRelation(rn); 
    String cols = readEntry("Columns to project on (separate cols by spaces): ");
    StringTokenizer st = new StringTokenizer(cols);
    Vector ctypes = new Vector();
    Vector cnames = new Vector();
    cnames.addElement("12.50");
    ctypes.addElement("DECIMAL"); 
    while (st.hasMoreTokens()) {
      String cn = st.nextToken().toUpperCase();
      cnames.addElement(cn);
      ctypes.addElement("COLUMN");
      cnames.addElement("120");
      ctypes.addElement("INTEGER"); 
    }
    cnames.addElement("JustAString");
    ctypes.addElement("VARCHAR"); 
    Relation s = s1.extendedProjection(ctypes,cnames);
    s.displayRelation();
  }

  static void selection(Relation r) {
    String rn = readEntry("Relation to select from: ").toUpperCase();
    Relation s1 = r.getRelation(rn); 
    String lopType=null;
    String lopValue=null;
    String ropType=null;
    String ropValue=null;
    String lop = readEntry("Left Operand: ").trim().toUpperCase();
    if (lop.startsWith("'")) {
      lopType = "str";
      lopValue = lop.substring(1,lop.length()-1);
    } 
    else if ((lop.charAt(0) >= '0') && (lop.charAt(0) <= '9')) {
      lopType = "num";
      lopValue = lop;
    } 
    else {
      lopType = "col";
      lopValue = lop;
    }
    String comparison = readEntry("Comparison: ");
    String rop = readEntry("Right Operand: ").trim();
    if (rop.startsWith("'")) {
      ropType = "str";
      ropValue = rop.substring(1,rop.length()-1);
    } 
    else if ((rop.charAt(0) >= '0') && (rop.charAt(0) <= '9')) {
      ropType = "num";
      ropValue = rop;
    } 
    else {
      ropType = "col";
      ropValue = rop.toUpperCase();
    }
    Relation s = s1.selection(lopType,lopValue,comparison,ropType,ropValue);
    s.displayRelation();
  }

  static void inEvaluate(Relation r) {
    String rn = readEntry("Relation to check for membership: ").toUpperCase();
    Relation s1 = r.getRelation(rn); 
    String lop = readEntry("Left Operand: ").trim();
    String lopType=null;
    String lopValue=null;
    if (lop.startsWith("'")) {
      lopType = "str";
      lopValue = lop.substring(1,lop.length()-1);
    } 
    else if ((lop.charAt(0) >= '0') && (lop.charAt(0) <= '9')) {
      lopType = "num";
      lopValue = lop;
    } 
    if (s1.evalIn(lopType,lopValue))
      System.out.println(lopValue + " is present in " + rn);
    else
      System.out.println(lopValue + " is not present in " + rn);
  }
    
  static void notInEvaluate(Relation r) {
    String rn = readEntry("Relation to check for non-membership: ").toUpperCase();
    Relation s1 = r.getRelation(rn); 
    String lop = readEntry("Left Operand: ").trim();
    String lopType=null;
    String lopValue=null;
    if (lop.startsWith("'")) {
      lopType = "str";
      lopValue = lop.substring(1,lop.length()-1);
    } 
    else if ((lop.charAt(0) >= '0') && (lop.charAt(0) <= '9')) {
      lopType = "num";
      lopValue = lop;
    } 
    if (!s1.evalIn(lopType,lopValue))
      System.out.println(lopValue + " is present in " + rn);
    else
      System.out.println(lopValue + " is not present in " + rn);
  }
    
  static void cartesianProduct(Relation r) {
    String rn1 = readEntry("Relation 1: ").toUpperCase();
    Relation s1 = r.getRelation(rn1); 
    String rn2 = readEntry("Relation 2: ").toUpperCase();
    Relation s2 = r.getRelation(rn2); 
    Relation s = s1.cartesianProduct(s2);
    s.displayRelation();
  }

  //readEntry function -- to read input string
  static String readEntry(String prompt) {
     try {
       StringBuffer buffer = new StringBuffer();
       System.out.print(prompt);
       System.out.flush();
       int c = System.in.read();
       while(c != '\n' && c != -1) {
         buffer.append((char)c);
         c = System.in.read();
       }
       return buffer.toString().trim();
     } catch (IOException e) {
         return "";
       }
  }

  static void print_menu() {
    System.out.println("      DBEngine Driver\n");
    System.out.println("(x) Cartesian Product");
    System.out.println("(p) Projection");
    System.out.println("(e) Extended Projection");
    System.out.println("(s) Selection");
    System.out.println("(i) Evaluate Membership");
    System.out.println("(n) Evaluate Non Membership");
    System.out.println("(c) Display Catalog (Schema)");
    System.out.println("(d) Display Relation");
    System.out.println("(r) Remove Duplicates from Relation");
    System.out.println("(q) Quit\n");
  }
}