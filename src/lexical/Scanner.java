//Gabriel Monteiro de Andrade  -  20190162570
package lexical;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;

import utils.TokenType;

public class Scanner {

	int pos;
	char[] contentTXT;
	int state;
	//Tabela de palavras reservadas
	HashSet<String> reservedWords = new HashSet<>();
	
	public Scanner(String filename) {
		try {
			String contentBuffer = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
			this.contentTXT = contentBuffer.toCharArray();
			this.pos = 0;
			
			//definindo palavras reservadas
			reservedWords.add("int");
			reservedWords.add("float");
			reservedWords.add("print");
			reservedWords.add("if");
			reservedWords.add("else");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Token nextToken() {
		this.state = 0;
		String content = "";
		char currentChar;

		while (true) {
			if (isEOF()) {
				return null;
			}
			currentChar = this.nextChar();
			if(isInvalidChar(currentChar))
			  throw new RuntimeException("Invalid Character! at line: " + this.countLine() + " and column: " + this.countColumn());
			switch (state) {
			case 0:
				if (isLetter(currentChar) || isUnderscore(currentChar)) {
					content += currentChar;
					state = 1;
				} else if (isSpace(currentChar)) {
					state = 0;
				} else if (isDigit(currentChar)) {
					content += currentChar;
					state = 2;
				}else if (isRelOp(currentChar)) {
					content += currentChar;
					state = 3;
				}else if(isMathOp(currentChar)) {
					content += currentChar;
					return new Token(TokenType.MATH_OPERATOR, content);				
				}else if (isLeftPar(currentChar)) {
					content+= currentChar;
					return new Token(TokenType.LEFT_PARENTHESIS, content);
				}else if (isRightPar(currentChar)) {
					content+= currentChar;
					return new Token(TokenType.RIGHT_PARENTHESIS, content);
				}else if (isDot(currentChar)) {
					content += currentChar;
					state = 4;
				}else if(isHash(currentChar)) {
					state = 5;
				}
				else {
					throw new RuntimeException("Invalid Character! at ");
				}
				break;
			case 1:
				if (isLetter(currentChar) || isDigit(currentChar) || isUnderscore(currentChar)) {
					content += currentChar;
					state = 1;
				} else {
					this.back();
					if (isReservedWord(content)) {
						return new Token(TokenType.RESERVED_WORD, content);
					}
					return new Token(TokenType.IDENTYFIER, content);
				}
				break;
			case 2:
				if(isDigit(currentChar)) {
					content += currentChar;
					state = 2;
				}else if(isDot(currentChar)) {
					content += currentChar;
					state = 4;
				}
				else if(isLetter(currentChar)) {
					throw new RuntimeException("Malformed Number!");
				} else {
					this.back();
					return new Token(TokenType.NUMBER, content);
				}
				break;
			case 3:
				if(currentChar == '=') {
					content += currentChar;
					return new Token(TokenType.RELATIONAL_OPERATOR, content);
				}else if (content.equals("=")) {
					this.back();
					return new Token(TokenType.ASSIGNMENT, content);
				}else if(content.equals("!")) {
					throw new RuntimeException("Malformed Relational Operator!");
				}else {
					this.back();
					return new Token(TokenType.RELATIONAL_OPERATOR, content);
				}
			case 4:
				if(isDigit(currentChar)) {
					content += currentChar;
					state = 4;
				}else if (isLetter(currentChar) || isDot(currentChar) || content.charAt(content.length()-1) == '.') {
					throw new RuntimeException("Malformed Number!");
				}else {
					this.back();
					return new Token(TokenType.NUMBER, content);
				}
				break;
			case 5: 
				if(isEOL(currentChar)) {
					state = 0;
				}else {
					state = 5;
				}
				break;
			}
		}
	}

	private char nextChar() {
		return this.contentTXT[this.pos++];
	}

	private void back() {
		this.pos--;
	}

	private boolean isLetter(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
	}

	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private boolean isRelOp(char c) {
		return c == '>' || c == '=' || c == '<' || c == '!';
	}

	private boolean isSpace(char c) {
		return c == ' ' || c == '\n' || c == '\t' || c == '\r';
	}

	private boolean isUnderscore(char c) {
		return c == '_';
	}
	
	private boolean isMathOp(char c) {
		return c == '+' || c == '-' || c == '*' || c == '/';
	}
	
	private boolean isLeftPar(char c) {
		return c == '(';
	}
	
	private boolean isRightPar(char c) {
		return c == ')';
	}
	
	private boolean isDot(char c) {
		return c == '.';
	}
	
	private boolean isHash(char c) {
		return c == '#';
	}
	
	private boolean isEOL(char c) {
		return c == '\n' || c == '\r';
	}

	private boolean isEOF() {
		if (this.pos >= this.contentTXT.length) {
			return true;
		}
		return false;
	}
	
	private boolean isReservedWord(String s) {
		return this.reservedWords.contains(s);
	}
	
	private boolean isInvalidChar(char c) {
	  if(!isLetter(c) && !isDigit(c) && !isRelOp(c) && !isSpace(c) && !isUnderscore(c) && !isMathOp(c) && !isLeftPar(c) 
	      && !isRightPar(c) && !isDot(c) && !isHash(c) && !isEOL(c)){
	    return true;
	  }
	  return false;
	}
	
	private int countLine() {
	  int count=0;
	  for(int i=0; i<pos; i++) {
	    if(isEOL(contentTXT[i]))
	      count++;
	  }
	  return count + 1;
	}
	
	private int countColumn() {
	  int count=0;
    for(int i=0; i<pos; i++) {
      if(isEOL(contentTXT[i]))
        count = 0;
      else
        count++;
    }
    return count;
	}
}
