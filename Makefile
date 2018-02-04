.PHONY: compile lexer xic run clean

compile: clean lexer xic

lexer: src/lexer/XiLexer.java
	javac -d bin -cp "bin:lib/*" src/lexer/XiLexer.java

bin/lexer/*:
	javac -d bin -cp "bin:lib/*" src/lexer/*.java

src/lexer/XiLexer.java: bin/lexer/*
	jflex -d src/lexer --nobak src/lexer/lexer.flex
	
xic:
	javac -d bin -cp "bin:lib/*" src/xic/Xic.java

jar: compile
	jar -cmf manifest.mf xic.jar lib -C bin .

clean:
	rm -rf bin src/lexer/XiLexer.java
