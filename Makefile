.PHONY: compile lexer xic jar payload zip clean

compile: clean lexer xic

bin/lexer/*:
	javac -d bin -cp "bin:lib/*" src/lexer/*.java

src/lexer/XiLexer.java: bin/lexer/*
	jflex -d src/lexer --nobak src/lexer/lexer.flex

lexer: src/lexer/XiLexer.java
	javac -d bin -cp "bin:lib/*" src/lexer/XiLexer.java
	
xic:
	javac -d bin -cp "bin:lib/*" src/xic/Xic.java

jar: compile
	jar -cmf manifest.mf xic.jar lib resources -C bin .

payload: jar
	cat stub.sh xic.jar > xic && chmod u+x xic

zip: clean
	git log > pa1.log &&\
	zip -r xic.zip lib resources src tests Makefile manifest.mf stub.sh xic-build

clean:
	rm -rf bin src/lexer/XiLexer.java xic xic.jar xic.zip
