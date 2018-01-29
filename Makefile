lexer:
	jflex -d src/lexer src/lexer.flex && javac -d bin src/lexer/XiLexer.java 
	
test:
	javac -d bin -cp bin src/Xic.java && java -cp bin Xic text

clean:
	rm -rf bin/*
