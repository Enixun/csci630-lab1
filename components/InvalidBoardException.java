package components;
public class InvalidBoardException extends Exception {
  public InvalidBoardException() {
    super();
  }

  public InvalidBoardException(String msg) {
    super(msg);
  }
}