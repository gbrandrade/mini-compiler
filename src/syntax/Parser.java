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
    if(token.getContent() != "DECLARACOES")
      throw new SyntaxException("DECALARACOES expected, found " + token.getContent());
    listaDeclaracoes();
    next();
    isSemicolon();
    next();
    isColon();
    next();
    if(token.getContent() != "ALGORITMO")
      throw new SyntaxException("ALGORITMO expected, found " + token.getContent());
    listaComandos();
    next();
    isSemicolon();
  }
  //problema na recursao com listaDeclaracoes2
  private void listaDeclaracoes() {
    declaracao();
    listaDeclaracoes2();
  }
  
  private void listaDeclaracoes2() {
    next();
    if(token.getType() != TokenType.SEMICOLON) {
      listaDeclaracoes();
    }
  }
  
  private void declaracao() {
    tipoVar();
    next();
    isColon();
    next();
    isId();
  }

  private void tipoVar() {
    next();
    if(token.getContent() != "INTEIRO" && token.getContent() != "REAL")
      throw new SyntaxException("INTEIRO or REAL expected, found " + token.getContent());
  }

  private void listaComandos() {
    comando();
    next();
    listaComandos2();
  }
  
  private void listaComandos2() {
    if(token.getType() != TokenType.SEMICOLON) {
      listaComandos();
    }
  }
  
  private void comando() {
    next();
    switch(token.getContent()) {
    case "ASSIGN":
      comandoArtibuicao();
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
    expressaoRelacional();
    comando();
  }

  private void comandoCondicao() {
    expressaoRelacional();
    next();
    if(token.getContent() != "THEN")
      throw new SyntaxException("'THEN' expected, found " + token.getContent());
    comando();
    comandoCondicao2();
  }

  private void comandoCondicao2() {
    next();
    if(token.getType() != TokenType.SEMICOLON) {
      if(token.getContent() != "ELSE")
        throw new SyntaxException("'ELSE' expected, found " + token.getContent());
      comando();
    } 
  }

  private void expressaoRelacional() {
    termoRelacional();
    expressaoRelacional2();
  }

  private void expressaoRelacional2() {
    next();
    if(token.getType() != TokenType.SEMICOLON) {
      operadorBooleano();
      expressaoRelacional();
    }
  }

  private void operadorBooleano() {
    if(token.getContent() != "AND" && token.getContent() != "OR")
      throw new SyntaxException("'AND' or 'OR' expected, found " + token.getContent());
  }

  private void termoRelacional() {
    next();
    if(token.getType() == TokenType.LEFT_PARENTHESIS) {
      expressaoAritmetica();
      next();
      if(token.getType() != TokenType.RIGHT_PARENTHESIS)
        throw new SyntaxException("')' expected, found " + token.getType());
    }
    else {
      expressaoAritmetica();
      next();
      isRelOp();
      expressaoAritmetica();
    }     
  }

  private void comandoSaida() {
    next();  
    isId(); //falta identificar cadeias
  }

  private void comandoEntrada() {
    next();
    isId();
  }

  private void comandoArtibuicao() {
    expressaoAritmetica();
    next();
    if(token.getContent() != "TO")
      throw new SyntaxException("'TO' expected, found " + token.getContent());
    next();
    isId();
  }

  private void expressaoAritmetica() {
    termoAritmetico();
    expressaoAritmetica2();
  }

  private void expressaoAritmetica2() {
    next();
    if(token.getType() != TokenType.SEMICOLON) {
      expressaoAritmetica3();
      expressaoAritmetica2();
    }
  }

  private void expressaoAritmetica3() {
    if(token.getContent() != "+" && token.getContent() != "-")
      throw new SyntaxException("'+' or '-' expected, found " + token.getContent());
    termoAritmetico();   
  }

  private void termoAritmetico() {
    fatorAritmetico();
    termoAritmetico2();
  }

  private void termoAritmetico2() {
    next();
    if(token.getType() != TokenType.SEMICOLON) {
      termoAritmetico3();
      termoAritmetico2();
    }    
  }

  private void termoAritmetico3() {
    next();
    if(token.getContent() != "*" && token.getContent() != "/")
      throw new SyntaxException("'*' or '/' expected, found " + token.getContent());
    fatorAritmetico();
  }

  private void fatorAritmetico() {
    next();
    if(token.getType() == TokenType.LEFT_PARENTHESIS) {
      expressaoAritmetica();
      next();
      if(token.getType() != TokenType.RIGHT_PARENTHESIS)
        throw new SyntaxException("')' expected, found " + token.getType());
    }
    else if(token.getType() != TokenType.NUMBER && token.getType() != TokenType.IDENTYFIER)
      throw new SyntaxException("Number, identyfier or '(' expected, found " + token.getType());
  }

  //auxiliares
  
  private void next() {
    token = scanner.nextToken();
  }
  
  private void isColon() {
    if(token.getType() != TokenType.COLON)
      throw new SyntaxException("Colon expected, found " + token.getType());
  }
  
  private void isSemicolon() {
    if(token.getType() != TokenType.SEMICOLON)
      throw new SyntaxException("Semicolon expected, found " + token.getType());
  }
  
  private void isId() {
    if(token.getType() != TokenType.IDENTYFIER)
      throw new SyntaxException("Identyfier expected, found " + token.getType());
  }
  
  private void isRelOp() {
    if(token.getType() != TokenType.RELATIONAL_OPERATOR)
      throw new SyntaxException("Relational operator expected, found " + token.getType());
  }
  
}