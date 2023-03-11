package lexical;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import utils.TokenType;

public class Scanner {

	int pos;
	char[] contentTXT;
	int state;
	String[] reservedWords = {"int", "float", "print", "if", "else"};

	public Scanner(String filename) {
		try {
			String contentBuffer = new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
			this.contentTXT = contentBuffer.toCharArray();
			this.pos = 0;
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
					return new Token(TokenType.MATH_OP, content);				
				}else if (isParenthesis(currentChar)) {
					content+= currentChar;
					return new Token(TokenType.PARENTHESIS, content);
				}else if (isDot(currentChar)) {
					content += currentChar;
					state = 4;
				}
				else {
					throw new RuntimeException("Invalid Character!");
				}
				break;
			case 1:
				if (isLetter(currentChar) || isDigit(currentChar) || isUnderscore(currentChar)) {
					content += currentChar;
					state = 1;
				} else {
					this.back();
					if (Arrays.asList(reservedWords).contains(content)) {
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
					return new Token(TokenType.REL_OP, content);
				}else if (content.equals("=")) {
					this.back();
					return new Token(TokenType.ASSIGNMENT, content);
				}else if(content.equals("!")) {
					throw new RuntimeException("Malformed Relational Operator!");
				}else {
					this.back();
					return new Token(TokenType.REL_OP, content);
				}
			case 4:
				if(isDigit(currentChar)) {
					content += currentChar;
					state = 4;
				}else if (content.charAt(content.length()-1) == '.') {
					throw new RuntimeException("Malformed Number");
				}
				else {
					this.back();
					return new Token(TokenType.NUMBER, content);
				}
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
	
	private boolean isParenthesis(char c) {
		return c == '(' || c == ')';
	}
	
	private boolean isDot(char c) {
		return c == '.';
	}

	private boolean isEOF() {
		if (this.pos >= this.contentTXT.length) {
			return true;
		}
		return false;
	}
}
