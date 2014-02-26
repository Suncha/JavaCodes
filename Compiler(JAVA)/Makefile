DLOG.class:	DLOG.java Lexer.java parser.java sym.java
	javac DLOG.java
Lexer.java:	dlog.flex
	jflex dlog.flex
sym.java:	dlog.cup
parser.java:	dlog.cup
	java java_cup.Main dlog.cup
clean:
	rm -f *.class, sym.java, parser.java, Lexer.java
