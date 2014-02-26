//package edu.gsu.cs.dbengine;

import java.io.*;
import java.util.*;

// This class support the creation of database relations, several
// relational algebra operators and basic data manipulation operators.

public class Relation {

  // F I E L D S

  // Name of the relation.
  private String     relName=null;

  // Attribute names for the relation
  private Vector  attributes=null;

  // Domain classes (types of attributes)
  private Vector   domains=null;

  // Actual data storage (list of tuples) for the relation.
  private Vector       table=null;

  // Counter to facilitate iterator on tuples
  private int      counter=0;

  // Map associating relation names with relation memory images.
  private static Map   catalogMap;

  public static void initializeDatabase(String dir) {
    catalogMap=new HashMap();
    FileInputStream fin,fin2 = null;
    BufferedReader infile,infile2 = null;

    try {
      fin = new FileInputStream(dir+"/catalog.dat");
      infile = new BufferedReader(new InputStreamReader(fin));

      int numRelations=0;
      String s = infile.readLine();
      try {
        numRelations = Integer.parseInt(s);
      } catch (NumberFormatException e) {
          System.out.println("Invalid number");
        }
      for (int i=0; i<numRelations; i++) {
        // Code to set Relation Scheme in Catalog
        String rname = infile.readLine();
        s = infile.readLine();
        int numAttributes=0;
        try {
          numAttributes = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number");
          }
        Vector attrs = new Vector();
        Vector doms = new Vector();
        for (int j=0; j<numAttributes; j++) {
          String aname = infile.readLine();
          String atype = infile.readLine();
          attrs.addElement(aname);
          doms.addElement(atype);
        }
        Relation r = new Relation(rname,attrs,doms);
        // Code to populate r with tuples
        String fname=dir+"/"+rname+".dat";
        fin2 = new FileInputStream(fname);
        infile2 = new BufferedReader(new InputStreamReader(fin2));
        s = infile2.readLine();
        int numTuples=0;
        try {
          numTuples = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number 1");
          }
        for (int k=0; k<numTuples; k++) {
          Comparable [] tuple = new Comparable[numAttributes]; 
          for (int j=0; j<numAttributes; j++) {
            s = infile2.readLine();
            if (r.domains.elementAt(j).equals("VARCHAR")) // is varchar
              tuple[j] = s;
            else if (r.domains.elementAt(j).equals("INTEGER")) { // is integer
              Integer ival = null;
              try {
                ival = new Integer(Integer.parseInt(s));
              } catch (NumberFormatException e) {
                  System.out.println("Invalid number 2");
                }
              tuple[j] = ival;
            } else { // is  decimal
                Double dval = null;
                try {
                  dval = new Double(Double.parseDouble(s));
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number 3");
                  }
                tuple[j] = dval;
              }
          }
          r.table.addElement(tuple);
        } 
        catalogMap.put(rname,r);
      }

    } catch (IOException e) {
      }
  }

  // C O N S T R U C T O R S

  /*******************************************************************
   * Construct a relation with given domain names.
   * @param  relName     name of the relation
   * @param  attributes  attribute names for the relation
   * @param  dNames      domain names (type names of attributes)
   * @param  primaryKey  attribute making up the primary key
   */
  public Relation (String    relName, Vector attributes, Vector dNames) {
    counter = 0;
    this.relName = relName;
    this.attributes = attributes;
    this.domains = dNames;
    table = new Vector ();
  }; // Relation

  // M E T H O D S

  /*******************************************************************
   * Method gets  access to the memory image of the relation named 'relName'.
   * @param   relName   name of the relation
   * @return  Relation  reference to relation in memory, null if not there
   */
  public static Relation getRelation (String relName) {
    if (catalogMap.containsKey(relName)) {
      Relation x = (Relation) catalogMap.get(relName);
      return x;
    } else
        return null;
  }; // getRelation

  public static boolean relationExists(String rname) {
    Relation r = getRelation(rname);
    return (r != null);
  }

  public static boolean attributeExists(String rname, String aname) {
    Relation r = getRelation(rname);
    if (r != null) {
      for (int i=0; i<r.attributes.size(); i++) {
        String a = (String) r.attributes.elementAt(i);
        if (a.equals(aname))
          return true;
      }
      return false;
    }
    else
      return false;
  }

  public static String attributeType(String rname, String aname) {
    Relation r = getRelation(rname);
    if (r != null) {
      for (int i=0; i<r.attributes.size(); i++) {
        String a = (String) r.attributes.elementAt(i);
        if (a.equals(aname))
          return (String) r.domains.elementAt(i);
      }
      return null;
    }
    else
      return null;
  }

  public static void saveCatalog() {
    try {
      OutputStream f = new FileOutputStream("catalog2.dat");
      PrintStream outfile = new PrintStream(f);
  
      int numRelations=catalogMap.size();
      outfile.println(""+numRelations);
      Set s = catalogMap.entrySet();
      Iterator i=s.iterator();
      while (i.hasNext()) {
        Map.Entry e = (Map.Entry) i.next();
        String rname = (String) e.getKey();
        if (rname.startsWith("$"))
          continue;
        Relation r= (Relation) e.getValue();
        outfile.println(rname);
        outfile.println(""+r.attributes.size());
        for (int j=0; j<r.attributes.size(); j++) {
          String aname = (String) r.attributes.elementAt(j);
          String atype = (String) r.domains.elementAt(j);
          outfile.println(aname); 
          outfile.println(atype); 
        }
        String fname="db2/"+rname+".dat";
        OutputStream f2 = new FileOutputStream(fname);
        PrintStream outfile2 = new PrintStream(f2);
        int nTuples = r.table.size(); 
        outfile2.println(nTuples);
        for (int k=0; k<nTuples; k++) {
          Comparable [] tup = (Comparable []) r.table.elementAt(k);
          for (int m=0; m<r.attributes.size(); m++) {
            String atype = (String) r.domains.elementAt(m);
            if (atype.equals("VARCHAR")) {
              String sval = (String) tup[m];
              outfile2.println(sval);
            }
            else if (atype.equals("INTEGER")) {
              Integer ival = (Integer) tup[m];
              outfile2.println(ival.intValue());
            } else {
                Double dval = (Double) tup[m];
                outfile2.println(dval.doubleValue());
              }
          } // for m
        } // for k
      } // while
    } catch (IOException e) {
      }

  }

  public static void printCatalog() {
    Relation r=null;
    Set s = catalogMap.entrySet();
    Iterator i=s.iterator();
    while (i.hasNext()) {
      Map.Entry e = (Map.Entry) i.next();
      String rname = (String) e.getKey();
      r = (Relation) e.getValue();
      System.out.print(rname+"(");
      for (int j=0; j<r.attributes.size(); j++) {
        String aname = (String) r.attributes.elementAt(j);
        String atype = (String) r.domains.elementAt(j);
        System.out.print(aname+":"); 
        System.out.print(atype); 
        if (j<(r.attributes.size()-1))
          System.out.print(",");
      }
      System.out.println(")");
      //printRelation(rname);
      int nTuples = r.table.size(); 
      System.out.println("Number of tuples = "+nTuples);
      for (int k=0; k<nTuples; k++) {
        Comparable [] tup = (Comparable []) r.table.elementAt(k);
        for (int m=0; m<r.attributes.size(); m++) {
          String atype = (String) r.domains.elementAt(m);
          if (atype.equals("VARCHAR")) {
            String sval = (String) tup[m];
            System.out.print(sval+":");
          }
          else if (atype.equals("INTEGER")) {
            Integer ival = (Integer) tup[m];
            System.out.print(ival.intValue()+":");
          } else {
              Double dval = (Double) tup[m];
              System.out.print(dval.doubleValue()+":");
            }
        } // for m
        System.out.println("");
      } // for k
    } // while
  }

  public static void displaySchema() {
    Relation r=null;
    Set s = catalogMap.entrySet();
    Iterator i=s.iterator();
    while (i.hasNext()) {
      Map.Entry e = (Map.Entry) i.next();
      String rname = (String) e.getKey();
      r = (Relation) e.getValue();
      System.out.print(rname+"(");
      for (int j=0; j<r.attributes.size(); j++) {
        String aname = (String) r.attributes.elementAt(j);
        String atype = (String) r.domains.elementAt(j);
        System.out.print(aname+":"); 
        System.out.print(atype); 
        if (j<(r.attributes.size()-1))
          System.out.print(",");
      }
      System.out.println(")");
    }
  }

  public void displayRelationSchema() {
    System.out.print(relName+"(");
    for (int j=0; j<attributes.size(); j++) {
      String aname = (String) attributes.elementAt(j);
      String atype = (String) domains.elementAt(j);
      System.out.print(aname+":"); 
      System.out.print(atype); 
      if (j<(attributes.size()-1))
        System.out.print(",");
    }
    System.out.println(")");
  }

  public void removeDuplicates() {
    int nTuples = table.size();
    for (int i=0; i<nTuples; i++) {
      Comparable [] tup1 = (Comparable []) table.elementAt(i);
      Tuple t1 = new Tuple(tup1,attributes,domains);
      int j = i+1;
      while (j < nTuples) {
        Comparable [] tup2 = (Comparable []) table.elementAt(j);
        Tuple t2 = new Tuple(tup2,attributes,domains);
        if (t1.equals(t2)) {
          table.removeElementAt(j);
          nTuples--;
        }
        else
          j++;
      }
    }
  }

  public void displayRelation() {
    int nTuples = table.size();
    System.out.println("\nNumber of tuples = "+nTuples);
    for (int k=0; k<nTuples; k++) {
      Comparable [] tup = (Comparable []) table.elementAt(k);
      for (int m=0; m<attributes.size(); m++) {
        String atype = (String) domains.elementAt(m);
        if (atype.equals("VARCHAR")) {
          String sval = (String) tup[m];
          System.out.print(sval+":");
        }
        else if (atype.equals("INTEGER")) {
          Integer ival = (Integer) tup[m];
          System.out.print(ival.intValue()+":");
        } else {
            Double dval = (Double) tup[m];
            System.out.print(dval.doubleValue()+":");
          }
      } // for m
      System.out.println("");
    } // for k
    System.out.println("");
  }

  public Tuple first() {
    if (table.isEmpty())
      return null;
    counter = 0;
    Comparable [] tup = (Comparable []) table.elementAt(counter);
    Tuple t = new Tuple(tup,attributes,domains);
    return t;
  }

  public Tuple next() {
    if (table.isEmpty())
      return null;
    if (table.size() <= (counter+1))
      return null;
    counter++;
    Comparable [] tup = (Comparable []) table.elementAt(counter);
    Tuple t = new Tuple(tup,attributes,domains);
    return t;
  }

  public Tuple current() {
    if (table.isEmpty())
      return null;
    if (table.size() > (counter+1))
      return null;
    Comparable [] tup = (Comparable []) table.elementAt(counter);
    Tuple t = new Tuple(tup,attributes,domains);
    return t;
  }

  public void setRelationName(String rn) {
    relName = rn;
  }

  public boolean existsTuples() {
    return (table.size() > 0);
  }

  public Relation union(Relation r2) {
    Vector attr = new Vector();
    Vector dom = new Vector();
    int numAttr = attributes.size();
    for (int i=0; i<numAttr; i++) {
      String a = (String) attributes.elementAt(i);
      attr.addElement(a);
      String d = (String) domains.elementAt(i);
      dom.addElement(d);
    }
    Relation r = new Relation(null,attr,dom);
    // Now lets add the tuples to r
    int numTuples = table.size();
    for (int i=0; i<numTuples; i++) {
      Comparable [] t = (Comparable []) table.elementAt(i);
      Comparable [] tup = new Comparable[numAttr];
      for (int k=0; k<numAttr; k++)
        tup[k] = t[k];
      r.table.addElement(tup);
    }
    int numTuples2 = r2.table.size();
    for (int i=0; i<numTuples2; i++) {
      Comparable [] t = (Comparable []) r2.table.elementAt(i);
      Comparable [] tup = new Comparable[numAttr];
      for (int k=0; k<numAttr; k++)
        tup[k] = t[k];
      r.table.addElement(tup);
    }
    r.removeDuplicates();
    return r;
  }
    
  public Relation minus(Relation r2) {
    Vector attr = new Vector();
    Vector dom = new Vector();
    int numAttr = attributes.size();
    for (int i=0; i<numAttr; i++) {
      String a = (String) attributes.elementAt(i);
      attr.addElement(a);
      String d = (String) domains.elementAt(i);
      dom.addElement(d);
    }
    Relation r = new Relation(null,attr,dom);
    // Now lets add the tuples to r
    int numTuples = table.size();
    for (int i=0; i<numTuples; i++) {
      Comparable [] t = (Comparable []) table.elementAt(i);
      Comparable [] t2 = new Comparable[numAttr];
      for (int k=0; k<numAttr; k++)
        t2[k] = t[k];
      Tuple tup = new Tuple(t2,attributes,domains);
      if (!r2.member(tup)) {
        r.table.addElement(t2);
      }
    }
    return r;
  }
    
  public Relation intersect(Relation r2) {
    Vector attr = new Vector();
    Vector dom = new Vector();
    int numAttr = attributes.size();
    for (int i=0; i<numAttr; i++) {
      String a = (String) attributes.elementAt(i);
      attr.addElement(a);
      String d = (String) domains.elementAt(i);
      dom.addElement(d);
    }
    Relation r = new Relation(null,attr,dom);
    // Now lets add the tuples to r
    int numTuples = table.size();
    for (int i=0; i<numTuples; i++) {
      Comparable [] t = (Comparable []) table.elementAt(i);
      Comparable [] t2 = new Comparable[numAttr];
      for (int k=0; k<numAttr; k++)
        t2[k] = t[k];
      Tuple tup = new Tuple(t2,attributes,domains);
      if (r2.member(tup)) {
        r.table.addElement(t2);
      }
    }
    return r;
  }
    
  public Relation join(Relation r2) {
    Vector attr = new Vector();
    Vector dom = new Vector();
    Vector leftJoinCols = new Vector();
    Vector rightJoinCols = new Vector();
    Vector lJoinDoms = new Vector();
    Vector rJoinDoms = new Vector();
    int nColsInJoin = 0;

    int numAttr1 = attributes.size();
    for (int i=0; i<numAttr1; i++) {
      String a = (String) attributes.elementAt(i);
      String d = (String) domains.elementAt(i);
      attr.addElement(a);
      dom.addElement(d);
      nColsInJoin++;
    }
    int numAttr2 = r2.attributes.size();
    for (int i=0; i<numAttr2; i++) {
      String a = (String) r2.attributes.elementAt(i);
      String rdom = (String) r2.domains.elementAt(i);
      int index = attributes.indexOf(a);
      if (index != -1) {
        leftJoinCols.addElement(new Integer(index));
        rightJoinCols.addElement(new Integer(i));
        String ldom = (String) domains.elementAt(index);
        lJoinDoms.addElement(ldom);
        rJoinDoms.addElement(rdom);
      }
      else {
        attr.addElement(a);
        dom.addElement(rdom);         
        nColsInJoin++;
      }
    }
    Relation r = new Relation(null,attr,dom);

    // Now lets add the tuples to r
    int numTuples = table.size();
    int numTuples2 = r2.table.size();
    for (int i=0; i<numTuples; i++) {
      Comparable [] t1 = (Comparable []) table.elementAt(i);
      Tuple tup1 = new Tuple(t1,attributes,domains);
      for (int j=0; j<numTuples2; j++) {
        Comparable [] t2 = (Comparable []) r2.table.elementAt(j);
        Tuple tup2 = new Tuple(t2,r2.attributes,r2.domains);
        Comparable [] t = new Comparable[nColsInJoin];
        if (tup1.joins(tup2,leftJoinCols,rightJoinCols,lJoinDoms,rJoinDoms)) {
          for (int k=0; k<numAttr1; k++)
            t[k] = t1[k];
          int rNum = 0;
          for (int k=0; k<numAttr2; k++) {
            Integer kInt = new Integer(k);
            int ind = rightJoinCols.indexOf(kInt);
            if (ind == -1) {
              t[numAttr1+rNum] = t2[k];
              rNum++;
            }
          }
          r.table.addElement(t);
        }
      }
    }
   // System.out.println("----------From join method--------");
   // r.displayRelation();
    return r;
  }

  public Relation cartesianProduct(Relation r2) {
    Vector attr = new Vector();
    Vector dom = new Vector();

    int numAttr = attributes.size();
    for (int i=0; i<numAttr; i++) {
      String a = (String) attributes.elementAt(i);
      int index = r2.attributes.indexOf(a);
      if (index != -1)
        attr.addElement(relName+"."+a);         
      else
        attr.addElement(a);
      String d = (String) domains.elementAt(i);
      dom.addElement(d);
    }

    int numAttr2 = r2.attributes.size();
    for (int i=0; i<numAttr2; i++) {
      String a = (String) r2.attributes.elementAt(i);
      int index = attributes.indexOf(a);
      if (index != -1)
        attr.addElement(r2.relName+"."+a);         
      else
        attr.addElement(a);
      String d = (String) r2.domains.elementAt(i);
      dom.addElement(d);         
    }
    Relation r = new Relation(null,attr,dom);

    // Now lets add the tuples to r
    int numTuples = table.size();
    int numTuples2 = r2.table.size();
    for (int i=0; i<numTuples; i++) {
      Comparable [] t = (Comparable []) table.elementAt(i);
      for (int j=0; j<numTuples2; j++) {
        Comparable [] t2 = (Comparable []) r2.table.elementAt(j);
        Comparable [] tup = new Comparable[numAttr+numAttr2];
        for (int k=0; k<numAttr; k++)
          tup[k] = t[k];
        for (int k=0; k<numAttr2; k++)
          tup[numAttr+k] = t2[k];
        r.table.addElement(tup);
      }
    }
    return r;
  }

  public Relation rename(Vector cnames) {
    Vector attr = new Vector();
    Vector dom = new Vector();
    for (int i=0; i<cnames.size(); i++) {
      String aname = (String) cnames.elementAt(i);
      String atype = (String) domains.elementAt(i);
      attr.addElement(aname);
      dom.addElement(atype);
    }
    Relation r = new Relation(null,attr,dom);
    int numAttr = attr.size();
    int numTuples = table.size();
    for (int i=0; i<numTuples; i++) {
      Comparable [] t = (Comparable []) table.elementAt(i);
      Comparable [] tup = new Comparable[numAttr];
      for (int k=0; k<numAttr; k++) {
        tup[k] = t[k];
      }
      r.table.addElement(tup);
    }
    return r;    
  }

 
  public Relation projection(Vector cnames) {
    Vector attr = new Vector();
    Vector dom = new Vector();
    for (int i=0; i<cnames.size(); i++) {
      String aname = (String) cnames.elementAt(i);
      int index = attributes.indexOf(aname);
      if (index != -1) {
        String atype = (String) domains.elementAt(index);
        attr.addElement(aname);
        dom.addElement(atype);
      }
      else
        return null;
    }
    Relation r = new Relation(null,attr,dom);

    // Now lets add the tuples to r
    int numAttr = attr.size();
    int numTuples = table.size();
    for (int i=0; i<numTuples; i++) {
      Comparable [] t = (Comparable []) table.elementAt(i);
      Comparable [] tup = new Comparable[numAttr];
      for (int k=0; k<numAttr; k++) {
        String aname = (String) attr.elementAt(k);
        int index = attributes.indexOf(aname);
        tup[k] = t[index];
      }
      r.table.addElement(tup);
    }
    r.removeDuplicates();
    return r;    
  }

  public Relation extendedProjection(Vector colType, Vector colValue) {
    Vector attr = new Vector();
    Vector dom = new Vector();
    for (int i=0; i<colType.size(); i++) {
      String ctype = (String) colType.elementAt(i);
      if (ctype.equals("COLUMN")) {
        String aname = (String) colValue.elementAt(i);
        int index = attributes.indexOf(aname);
        if (index != -1) {
          String atype = (String) domains.elementAt(index);
          attr.addElement(aname);
          dom.addElement(atype);
        }
        else
          return null;
      }
      else if (ctype.equals("VARCHAR")) {
        // this column has "DUMMY" as its name
        attr.addElement("DUMMY");
        dom.addElement("VARCHAR");
      }
      else if (ctype.equals("INTEGER")) {
        // this column has "DUMMY" as its name
        attr.addElement("DUMMY");
        dom.addElement("INTEGER");
      }
      else { // it should be DECIMAL
        // this column has "DUMMY" as its name!
        attr.addElement("DUMMY");
        dom.addElement("DECIMAL");
      }
    }
    Relation r = new Relation(null,attr,dom);

    // Now lets add the tuples to r
    int numAttr = attr.size();
    int numTuples = table.size();
    for (int i=0; i<numTuples; i++) {
      Comparable [] t = (Comparable []) table.elementAt(i);
      Comparable [] tup = new Comparable[numAttr];
      for (int k=0; k<numAttr; k++) {
        String aname = (String) attr.elementAt(k);
        if (aname.equals("DUMMY")) {
          String ctype = (String) dom.elementAt(k);
          if (ctype.equals("VARCHAR"))
            tup[k] = (String) colValue.elementAt(k);
          else if (ctype.equals("INTEGER")) {
            String sval = (String) colValue.elementAt(k);
            Integer ival = null;
            try {
              ival = new Integer(Integer.parseInt(sval));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number");
              }
            tup[k] = ival;
          }
          else if (ctype.equals("DECIMAL")) {
            String sval = (String) colValue.elementAt(k);
            Double dval = null;
            try {
              dval = new Double(Double.parseDouble(sval));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number");
              }
            tup[k] = dval;
          }
        }
        else {
          int index = attributes.indexOf(aname);
          tup[k] = t[index];
        }
      }
      r.table.addElement(tup);
    }
    return r;    
  }

  public Relation selection(String lopType, String lopValue, String comparison, 
                            String ropType, String ropValue) {
    Vector attr = new Vector();
    Vector dom = new Vector();
  //  System.out.println("The attribute size is "+attributes.size());
    for (int i=0; i<attributes.size(); i++) {
      String aname = (String) attributes.elementAt(i);
      String atype = (String) domains.elementAt(i);
      attr.addElement(aname);
      dom.addElement(atype);
    }
    Relation r = new Relation(null,attr,dom);

    // Now lets add the tuples to r
    int numAttr = attr.size();
    int numTuples = table.size();
  //  System.out.println("The table size is "+table.size());
    for (int i=0; i<numTuples; i++) {
      Comparable [] t = (Comparable []) table.elementAt(i);
      Tuple tup = new Tuple(t,attr,dom);
      if (tup.select(lopType, lopValue, comparison, ropType, ropValue))
        r.table.addElement(t);
    }
    return r;
  }

  public Relation notSelection(String lopType, String lopValue, String comparison, 
                               String ropType, String ropValue) {
    Vector attr = new Vector();
    Vector dom = new Vector();
    for (int i=0; i<attributes.size(); i++) {
      String aname = (String) attributes.elementAt(i);
      String atype = (String) domains.elementAt(i);
      attr.addElement(aname);
      dom.addElement(atype);
    }
    Relation r = new Relation(null,attr,dom);

    // Now lets add the tuples to r
    int numAttr = attr.size();
    int numTuples = table.size();
    for (int i=0; i<numTuples; i++) {
      Comparable [] t = (Comparable []) table.elementAt(i);
      Tuple tup = new Tuple(t,attr,dom);
      if (!tup.select(lopType, lopValue, comparison, ropType, ropValue))
        r.table.addElement(t);
    }
    return r;
  }

  public boolean evalIn(String lopType, String lopValue) {
  // This operator will be invoked only on single attribute relation
  // lopValue will contain a "value" whose type will match the single attribute
  // lopType will ALWAYS be "str" or "num" - should not be "col"
    int numTuples = table.size();
    for (int i=0; i<numTuples; i++) {
      Comparable [] t = (Comparable []) table.elementAt(i);
      Tuple tup = new Tuple(t,attributes,domains);
      String ropColName = (String) attributes.elementAt(0);
      if (tup.select(lopType, lopValue, "=", "col", ropColName))
        return true;
    }
    return false;     
  }

  public boolean member(Tuple t) {
    for (int i=0; i<table.size(); i++) {
      Comparable [] tt = (Comparable []) table.elementAt(i);
      Tuple tup = new Tuple(tt,attributes,domains);
      if (tup.equals(t))
        return true;
    }
    return false;
  }

  public Relation selectionIn(String lopType, String lopValue, Relation subQuery) {
    Vector attr = new Vector();
    Vector dom = new Vector();
    for (int i=0; i<attributes.size(); i++) {
      String aname = (String) attributes.elementAt(i);
      String atype = (String) domains.elementAt(i);
      attr.addElement(aname);
      dom.addElement(atype);
    }
    Relation r = new Relation(null,attr,dom);

    // Now lets add the tuples to r
    int numAttr = attr.size();
    int numTuples = table.size();
    for (int i=0; i<numTuples; i++) {
      Comparable [] t = (Comparable []) table.elementAt(i);
      String ldt = null;
      String lv = null;
      if (lopType.equals("col")) {
        int index = attr.indexOf(lopValue);
        String lopDataType = (String) dom.elementAt(index);
        if (lopDataType.equals("VARCHAR")) {
          ldt = "str";
          lv = (String) t[index];
        }
        else if (lopDataType.equals("INTEGER")) {
          ldt = "num";
          Integer lvi = (Integer) t[index];
          lv = lvi.toString();
        }
        else {
          ldt = "num";
          Double lvi = (Double) t[index];
          lv = lvi.toString();
        }
      }
      else if (lopType.equals("num")) {
        ldt = "num";
        lv = lopValue;
      } 
      else {
        ldt = "str";
        lv = lopValue;
      }
      if (subQuery.evalIn(ldt, lv))
        r.table.addElement(t);
    }
    return r;
  }

  public Relation notSelectionIn(String lopType, String lopValue, Relation subQuery) {
    Vector attr = new Vector();
    Vector dom = new Vector();
    for (int i=0; i<attributes.size(); i++) {
      String aname = (String) attributes.elementAt(i);
      String atype = (String) domains.elementAt(i);
      attr.addElement(aname);
      dom.addElement(atype);
    }
    Relation r = new Relation(null,attr,dom);

    // Now lets add the tuples to r
    int numAttr = attr.size();
    int numTuples = table.size();
    for (int i=0; i<numTuples; i++) {
      Comparable [] t = (Comparable []) table.elementAt(i);
      String ldt = null;
      String lv = null;
      if (lopType.equals("col")) {
        int index = attr.indexOf(lopValue);
        String lopDataType = (String) dom.elementAt(index);
        if (lopDataType.equals("VARCHAR")) {
          ldt = "str";
          lv = (String) t[index];
        }
        else if (lopDataType.equals("INTEGER")) {
          ldt = "num";
          Integer lvi = (Integer) t[index];
          lv = lvi.toString();
        }
        else {
          ldt = "num";
          Double lvi = (Double) t[index];
          lv = lvi.toString();
        }
      }
      else if (lopType.equals("num")) {
        ldt = "num";
        lv = lopValue;
      } 
      else {
        ldt = "str";
        lv = lopValue;
      }
      if (!subQuery.evalIn(ldt, lv))
        r.table.addElement(t);
    }
    return r;
  }

  public void prefixColumnNames(String pre) {
    for (int i=0; i<attributes.size(); i++) {
      String aname = (String) attributes.elementAt(i);
      attributes.setElementAt(pre+"."+aname,i);
    }
  }

  public void insertTuple(Tuple t) {
    table.addElement(t.tuple);
  }

  public int relationSize() {
    return table.size();
  }

  public Relation cloneRelation() {
    return selection("num","1","=","num","1");
  }

  public Vector getAttributes() {
    return attributes;
  }

  public Vector getDomains() {
    return domains;
  }

  public Vector getTable() {
    return table;
  }

  public static Map getCatalogMap() {
    return catalogMap;
  }

  public boolean isRelationEmpty() {
    return (table.isEmpty());
  }

} // Relation