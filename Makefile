.PHONY: compile lexer xic run clean

compile: lexer xic

lexer:
	javac -d bin src/lexer/*.java &&\
	jflex -d src/lexer --nobak src/lexer/lexer.flex &&\
	javac -d bin -cp bin src/lexer/XiLexer.java
	
xic:
	javac -d bin -cp bin src/Xic.java

run: bin/Xic.class
	java -cp bin Xic tests/basic.xi

clean:
	rm -rf bin/* src/lexer/XiLexer.java
