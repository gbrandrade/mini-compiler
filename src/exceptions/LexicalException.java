package exceptions;

public class LexicalException extends RuntimeException{
  public LexicalException(String msg, int line, int column) {
    super(msg + " at position " + line + ":" + column);
  }
}
