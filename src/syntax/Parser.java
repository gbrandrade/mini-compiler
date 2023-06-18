package syntax;

import exceptions.SyntaxException;
import lexical.Scanner;
import lexical.Token;
import utils.TokenType;

public class Parser {
  private Scanner scanner;
  private Token token;

  public Parser(Scanner scanner) {
    this.scanner = scanner;
  }

  public void programa() {
    next();
    isColon();
    next();
    if(!token.getContent().equals("DECLARACOES"))
      throw new SyntaxException("DECLARACOES expected, found " + token.getContent(), scanner.countLine(), scanner.countColumn());
    next();
    listaDeclaracoes();
    isColon();
    next();
    if(!token.getContent().equals("ALGORITMO"))
      throw new SyntaxException("ALGORITMO expected, found " + token.getContent(), scanner.countLine(), scanner.countColumn());
    next();
    listaComandos();
  }
  
  private void listaDeclaracoes() {
    declaracao();
    listaDeclaracoes2();
  }
  
  private void listaDeclaracoes2() {
    next();
    if(token.getType() != TokenType.COLON) {
      listaDeclaracoes();
    }
  }
  
  private void declaracao() {
    tipoVar();
    next();
    isColon();
    next();
    isId();
    next();
    isSemicolon();
  }

  private void tipoVar() {
    if(!token.getContent().equals("INTEIRO") && !token.getContent().equals("REAL"))
      throw new SyntaxException("INTEIRO or REAL expected, found " + token.getContent(), scanner.countLine(), scanner.countColumn());
  }

  private void listaComandos() {
    comando();
    listaComandos2();
  }
  
  private void listaComandos2() {
    next();
    if(this.token!=null) {
      listaComandos();
    }
  }
  
  private void comando() {
    switch(token.getContent()) {
    case "ASSIGN":
      comandoAtribuicao();
      break;
    case "INPUT":
      comandoEntrada();
      break;
    case "PRINT":
      comandoSaida();
      break;
    case "IF":
      comandoCondicao();
      break;
    case "ELSE":
      comandoCondicao2();
      break;
    case "WHILE":
      comandoRepeticao();
      break;
    }
  }
  
  private void comandoRepeticao() {
    next();
    expressaoRelacional();    
    comando();
  }

  private void comandoSaida() {
    next();
    if(token.getType() == TokenType.LEFT_PARENTHESIS) {
      next();
      if(token.getType()!= TokenType.STRING && token.getType()!= TokenType.IDENTYFIER)
        throw new SyntaxException("String or Identyfier expected, found " + token.getType(), scanner.countLine(), scanner.countColumn());
      next();
      if(token.getType() != TokenType.RIGHT_PARENTHESIS)
        throw new SyntaxException("')' expected, found " + token.getContent(), scanner.countLine(), scanner.countColumn());
    }
    next();
    isSemicolon();
  }

  private void comandoAtribuicao() {
    next();
    expressaoAritmetica();
    if(!token.getContent().equals("TO"))
      throw new SyntaxException("TO expected, found " + token.getContent(), scanner.countLine(), scanner.countColumn());
    next();
    isId();
    next();
    isSemicolon();
  }

  private void comandoCondicao() {
    next();
    expressaoRelacional();
    if(!token.getContent().equals("THEN"))
      throw new SyntaxException("THEN expected, found " + token.getContent(), scanner.countLine(), scanner.countColumn());
    next();
    comando();
    next();
    comandoCondicao2();
  }

  private void comandoCondicao2() {
    if(token != null) {
      if(token.getContent().equals("ELSE")) {
        next();
        comando();     
      }
    }
  }

  private void expressaoRelacional() {
    termoRelacional(); 
    expressaoRelacional2();
  }

  private void expressaoRelacional2() {
    if(!token.getContent().equals("THEN")) {
      operadorBooleano();
      next();
      expressaoRelacional();
    }
  }

  private void operadorBooleano() {
    if(!token.getContent().equals("AND") && !token.getContent().equals("OR"))
      throw new SyntaxException("AND or OR expected, found " + token.getContent(), scanner.countLine(), scanner.countColumn());
  }

  private void termoRelacional() {
    if(token.getType() == TokenType.LEFT_PARENTHESIS) {
      expressaoAritmetica();
      if(token.getType() != TokenType.RIGHT_PARENTHESIS)
        throw new SyntaxException("')' expected, found " + token.getContent(), scanner.countLine(), scanner.countColumn());
    }
    else {
      expressaoAritmetica();
      isRelOp();
      next();
      expressaoAritmetica();
    }     
  }

  private void expressaoAritmetica() {
    termoAritmetico();
    expressaoAritmetica2();
  }

  private void expressaoAritmetica2() {
    if(token.getContent().equals("+") || token.getContent().equals("-")) {
      expressaoAritmetica3();
      expressaoAritmetica2();    
    }
  }

  private void expressaoAritmetica3() {
    next();
    termoAritmetico();       
  }

  private void termoAritmetico() {
    fatorAritmetico();
    next();
    termoAritmetico2();
  }

  private void termoAritmetico2() { 
    if(token.getContent().equals("*") || token.getContent().equals("/")) {
      termoAritmetico3();
      next();
      termoAritmetico2();
    }
  }

  private void termoAritmetico3() {
    next();
    fatorAritmetico();    
  }

  private void fatorAritmetico() {
    if(token.getType() == TokenType.LEFT_PARENTHESIS) {
      next();
      expressaoAritmetica();
      if(token.getType() != TokenType.RIGHT_PARENTHESIS)
        throw new SyntaxException("')' expected, found " + token.getContent(), scanner.countLine(), scanner.countColumn());
    }
    else if(token.getType() != TokenType.NUMBER && token.getType() != TokenType.IDENTYFIER)
      throw new SyntaxException("Number, id or '(' expected, found " + token.getContent(), scanner.countLine(), scanner.countColumn());
  }

  private void comandoEntrada() {
    next();
    if(token.getType() != TokenType.IDENTYFIER)
      throw new SyntaxException("IDENTYFIER expected, found " + token.getContent(), scanner.countLine(), scanner.countColumn());
    next();
    isSemicolon();
  }

  //auxiliares
  private void next() {
    token = scanner.nextToken();
  }
  
  private void isColon() {
    if(token.getType() != TokenType.COLON)
      throw new SyntaxException("Colon expected, found " + token.getContent(), scanner.countLine(), scanner.countColumn());
  }
  
  private void isSemicolon() {
    if(token.getType() != TokenType.SEMICOLON)
      throw new SyntaxException("Semicolon expected, found " + token.getContent(), scanner.countLine(), scanner.countColumn());
  }
  
  private void isId() {
    if(token.getType() != TokenType.IDENTYFIER)
      throw new SyntaxException("Identyfier expected, found " + token.getType(), scanner.countLine(), scanner.countColumn());
  }
  
  private void isRelOp() {
    if(token.getType() != TokenType.RELATIONAL_OPERATOR)
      throw new SyntaxException("Relational Operator expected, found " + token.getContent(), scanner.countLine(), scanner.countColumn());
  }  
}