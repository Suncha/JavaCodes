//package edu.gsu.cs.dbengine;

import java.util.*;
public class Tuple {

  public Vector attributes;
  public Vector domains;
  public Comparable [] tuple;

  Tuple (Comparable [] tup, Vector attr, Vector dom) {
    attributes = new Vector();
    for (int i=0; i<attr.size(); i++) {
      String s = (String) attr.elementAt(i);
      attributes.addElement(s);
    }
    domains = new Vector();
    for (int i=0; i<dom.size(); i++) {
      String s = (String) dom.elementAt(i);
      domains.addElement(s);
    }
    int numAttr = attr.size();
    tuple = new Comparable [numAttr];
    for (int i=0; i<numAttr; i++) {
      tuple[i] = tup[i];
    }
  }

  public boolean equals(Tuple t) {
    // assumes schemas are compatible
    for (int i=0; i<attributes.size(); i++) {
      String atype = (String) domains.elementAt(i);
      if (atype.equals("VARCHAR")) {
        String t1val = (String) t.tuple[i];      
        String t2val = (String) tuple[i];      
        if (!t1val.equals(t2val))
          return false;
      }
      else if (atype.equals("INTEGER")) {
        Integer t1v = (Integer) t.tuple[i];      
        int t1val = t1v.intValue();
        Integer t2v = (Integer) tuple[i];      
        int t2val = t2v.intValue();
        if (t1val != t2val)
          return false;
      }
      else {
        Double t1v = (Double) t.tuple[i];      
        double t1val = t1v.intValue();
        Double t2v = (Double) tuple[i];      
        double t2val = t2v.intValue();
        if (t1val != t2val)
          return false;
      }
    }
    return true;
  }

  public Tuple project(Vector cn) {
    Vector attr = new Vector();
    Vector doms = new Vector();
    Comparable [] tup = new Comparable[cn.size()]; 
    for (int i=0; i<cn.size(); i++) {
      String cname = (String) cn.elementAt(i);
      int index = attributes.indexOf(cname);
      String ctype = (String) domains.elementAt(index);
      doms.addElement(ctype);
      tup[i] = tuple[index];
    }
    Tuple t = new Tuple(tup,cn,doms);
    return t; 
  }

  public Tuple extendedProject(Vector ct, Vector cn) {
    Vector attr = new Vector();
    Vector doms = new Vector();
    Comparable [] tup = new Comparable[cn.size()]; 
    for (int i=0; i<cn.size(); i++) {
      String colType = (String) ct.elementAt(i);
      if (colType.equals("COLUMN")) {
        String cname = (String) cn.elementAt(i);
        int index = attributes.indexOf(cname);
        String ctype = (String) domains.elementAt(index);
        doms.addElement(ctype);
        tup[i] = tuple[index];
      }
      else if (colType.equals("VARCHAR")) {
        tup[i] = (String) cn.elementAt(i);
        doms.addElement("VARCHAR");
      }
      else if (colType.equals("INTEGER")) {
        String sval = (String) cn.elementAt(i);
        Integer ival = null;
        try {
          ival = new Integer(Integer.parseInt(sval));
        } catch (NumberFormatException e) {
            System.out.println("Invalid number");
          }
        tup[i] = ival;
        doms.addElement("INTEGER");
      } 
      else if (colType.equals("DECIMAL")) {
        String sval = (String) cn.elementAt(i);
        Double dval = null;
        try {
          dval = new Double(Double.parseDouble(sval));
        } catch (NumberFormatException e) {
            System.out.println("Invalid number");
          }
        tup[i] = dval;
        doms.addElement("DECIMAL");
      } 
    }
    Tuple t = new Tuple(tup,cn,doms);
    return t; 
  }

  public boolean select(String lopType, String lopValue, String comparison, 
                        String ropType, String ropValue) {
    if (lopType.equals("str") && ropType.equals("str")) {
      if (comparison.equals("="))
        return lopValue.equals(ropValue);
      else if (comparison.equals("<>"))
        return (!lopValue.equals(ropValue));
      else if (comparison.equals("<"))
        return (lopValue.compareTo(ropValue) < 0);
      else if (comparison.equals(">"))
        return (lopValue.compareTo(ropValue) > 0);
      else if (comparison.equals("<="))
        return (lopValue.compareTo(ropValue) <= 0);
      else if (comparison.equals(">="))
        return (lopValue.compareTo(ropValue) >= 0);
    }
    else if (lopType.equals("num") && ropType.equals("num")) {
      double lval = Double.parseDouble(lopValue);
      double rval = Double.parseDouble(ropValue);
      if (comparison.equals("="))
        return (lval == rval);
      else if (comparison.equals("<>"))
        return (lval != rval);
      else if (comparison.equals("<"))
        return (lval < rval);
      else if (comparison.equals(">"))
        return (lval > rval);
      else if (comparison.equals("<="))
        return (lval <= rval);
      else if (comparison.equals(">="))
        return (lval >= rval);
    }
    else if (lopType.equals("col") && ropType.equals("num")) {
      int index = attributes.indexOf(lopValue);
      String lcolType = (String) domains.elementAt(index);
      if (lcolType.equals("INTEGER")) {
        Integer lvall = (Integer) tuple[index];
        double lval = lvall.doubleValue();
        double rval = Double.parseDouble(ropValue);
        if (comparison.equals("="))
          return (lval == rval);
        else if (comparison.equals("<>"))
          return (lval != rval);
        else if (comparison.equals("<"))
          return (lval < rval);
        else if (comparison.equals(">"))
          return (lval > rval);
        else if (comparison.equals("<="))
          return (lval <= rval);
        else if (comparison.equals(">="))
          return (lval >= rval);
      } 
      else {
        Double lvall = (Double) tuple[index];
        double lval = lvall.doubleValue();
        double rval = Double.parseDouble(ropValue);
        if (comparison.equals("="))
          return (lval == rval);
        else if (comparison.equals("<>"))
          return (lval != rval);
        else if (comparison.equals("<"))
          return (lval < rval);
        else if (comparison.equals(">"))
          return (lval > rval);
        else if (comparison.equals("<="))
          return (lval <= rval);
        else if (comparison.equals(">="))
          return (lval >= rval);
      }
    }
    else if (lopType.equals("col") && ropType.equals("str")) {
      int index = attributes.indexOf(lopValue);
      String lcolType = (String) domains.elementAt(index);
      if (lcolType.equals("VARCHAR")) {
        String lval = (String) tuple[index];
        if (comparison.equals("="))
          return lval.equals(ropValue);
        else if (comparison.equals("<>"))
          return (!lval.equals(ropValue));
        else if (comparison.equals("<"))
          return (lval.compareTo(ropValue) < 0);
        else if (comparison.equals(">"))
          return (lval.compareTo(ropValue) > 0);
        else if (comparison.equals("<="))
          return (lval.compareTo(ropValue) <= 0);
        else if (comparison.equals(">="))
          return (lval.compareTo(ropValue) >= 0);
      } 
    } 
    else if (lopType.equals("num") && ropType.equals("col")) {
      int index = attributes.indexOf(ropValue);
      String rcolType = (String) domains.elementAt(index);
      if (rcolType.equals("INTEGER")) {
        Integer rvall = (Integer) tuple[index];
        double rval = rvall.doubleValue();
        double lval = Double.parseDouble(lopValue);
        if (comparison.equals("="))
          return (lval == rval);
        else if (comparison.equals("<>"))
          return (lval != rval);
        else if (comparison.equals("<"))
          return (lval < rval);
        else if (comparison.equals(">"))
          return (lval > rval);
        else if (comparison.equals("<="))
          return (lval <= rval);
        else if (comparison.equals(">="))
          return (lval >= rval);
      } 
      else {
        Double rvall = (Double) tuple[index];
        double rval = rvall.doubleValue();
        double lval = Double.parseDouble(lopValue);
        if (comparison.equals("="))
          return (lval == rval);
        else if (comparison.equals("<>"))
          return (lval != rval);
        else if (comparison.equals("<"))
          return (lval < rval);
        else if (comparison.equals(">"))
          return (lval > rval);
        else if (comparison.equals("<="))
          return (lval <= rval);
        else if (comparison.equals(">="))
          return (lval >= rval);
      }
    }
    else if (lopType.equals("str") && ropType.equals("col")) {
      int index = attributes.indexOf(ropValue);
      String rcolType = (String) domains.elementAt(index);
      if (rcolType.equals("VARCHAR")) {
        String rval = (String) tuple[index];
        if (comparison.equals("="))
          return lopValue.equals(rval);
        else if (comparison.equals("<>"))
          return (!lopValue.equals(rval));
        else if (comparison.equals("<"))
          return (lopValue.compareTo(rval) < 0);
        else if (comparison.equals(">"))
          return (lopValue.compareTo(rval) > 0);
        else if (comparison.equals("<="))
          return (lopValue.compareTo(rval) <= 0);
        else if (comparison.equals(">="))
          return (lopValue.compareTo(rval) >= 0);
      } 
    } 
    else if (lopType.equals("col") && ropType.equals("col")) {
      int lIndex = attributes.indexOf(lopValue);
      int rIndex = attributes.indexOf(ropValue);
      String lcolType = (String) domains.elementAt(lIndex);
      String rcolType = (String) domains.elementAt(rIndex);
      if (lcolType.equals("VARCHAR") && rcolType.equals("VARCHAR")) {
        String lval = (String) tuple[lIndex];
        String rval = (String) tuple[rIndex];
        if (comparison.equals("="))
          return lval.equals(rval);
        else if (comparison.equals("<>"))
          return (!lval.equals(rval));
        else if (comparison.equals("<"))
          return (lval.compareTo(rval) < 0);
        else if (comparison.equals(">"))
          return (lval.compareTo(rval) > 0);
        else if (comparison.equals("<="))
          return (lval.compareTo(rval) <= 0);
        else if (comparison.equals(">="))
          return (lval.compareTo(rval) >= 0);
      }
      else { // both columns are numeric (INTEGER or DECIMAL)
        double lval = 0.0;
        if (lcolType.equals("INTEGER")) {
          Integer lvall = (Integer) tuple[lIndex];
          lval = lvall.doubleValue();
        } else {
            Double lvall = (Double) tuple[lIndex];
            lval = lvall.doubleValue(); 
          }
        double rval = 0.0;
        if (rcolType.equals("INTEGER")) {
          Integer rvall = (Integer) tuple[rIndex];
          rval = rvall.doubleValue();
        } else {
            Double rvall = (Double) tuple[rIndex];
            rval = rvall.doubleValue(); 
          }
        if (comparison.equals("="))
          return (lval == rval);
        else if (comparison.equals("<>"))
          return (lval != rval);
        else if (comparison.equals("<"))
          return (lval < rval);
        else if (comparison.equals(">"))
          return (lval > rval);
        else if (comparison.equals("<="))
          return (lval <= rval);
        else if (comparison.equals(">="))
          return (lval >= rval);
      }
    }
    return false;
  }

  String extractColumnValueAsString(String colName) {
    int index = attributes.indexOf(colName);
    String colType = (String) domains.elementAt(index);
    String cval = null;
    if (colType.equals("VARCHAR"))
      cval = (String) tuple[index];
    else if (colType.equals("INTEGER")) {
      Integer ival = (Integer) tuple[index];
      cval = "" + ival.intValue();
    }
    else {
      Double dval = (Double) tuple[index];
      cval = "" + dval.doubleValue();
    }
    return cval;
  }

  void printTuple() {
    for (int i=0; i<attributes.size(); i++) {
      String aname = (String) attributes.elementAt(i);
      String atype = (String) domains.elementAt(i);
      if (atype.equals("VARCHAR")) {
        String aval = (String) tuple[i];
        System.out.print(aname+":"+atype+":"+aval+",");
      }
      else if (atype.equals("INTEGER")) {
        Integer avall = (Integer) tuple[i];
        int aval = avall.intValue();
        System.out.print(aname+":"+atype+":"+aval+",");
      }
      else {
        Double avall = (Double) tuple[i];
        double aval = avall.doubleValue();
        System.out.print(aname+":"+atype+":"+aval+",");
      }
    }
    System.out.println("");
  }

  boolean joins(Tuple t2, Vector leftJoinCols, Vector rightJoinCols, 
                          Vector lJoinDoms, Vector rJoinDoms) {
    for (int i=0; i<leftJoinCols.size(); i++) {
      Integer ljoinCol = (Integer) leftJoinCols.get(i);
      int ljcol = ljoinCol.intValue();
      Integer rjoinCol = (Integer) rightJoinCols.get(i);
      int rjcol = rjoinCol.intValue();
      String ldom = (String) lJoinDoms.get(i);
      String rdom = (String) rJoinDoms.get(i);
      if (ldom.equals("VARCHAR")) {
        String lval = (String) tuple[ljcol];
        String rval = (String) t2.tuple[rjcol];
        if (!lval.equals(rval))
          return false;
      } 
      else { // ldom and rdom are INTEGER or DECIMAL 
        double lval = 0.0;
        if (ldom.equals("INTEGER")) {
          Integer lvall = (Integer) tuple[ljcol];
          lval = lvall.doubleValue();
        } else {
            Double lvall = (Double) tuple[ljcol];
            lval = lvall.doubleValue();
          }
        double rval = 0.0;
        if (rdom.equals("INTEGER")) {
          Integer rvall = (Integer) t2.tuple[rjcol];
          rval = rvall.doubleValue();
        } else {
            Double rvall = (Double) t2.tuple[rjcol];
            rval = rvall.doubleValue();
          }
        if (lval != rval)
          return false;
      }
    } 
    return true;
  }
}