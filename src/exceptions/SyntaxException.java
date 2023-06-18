package exceptions;

public class SyntaxException extends RuntimeException{
  
  public SyntaxException(String msg) {
    super(msg);
  }
  
  public SyntaxException(String msg, int line, int column) {
    super(msg + " at position " + line + ":" + column);
  }
}