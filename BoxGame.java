import java.util.Arrays;

public class BoxGame {
  private static final int DEFAULT_PLAYER_COUNT = 2;

  private Player[] players;
  private Board board;
  
  public BoxGame() {
    this(DEFAULT_PLAYER_COUNT, new Board());
  }

  public BoxGame(int numPlayers) {
    this(numPlayers, new Board());
  }

  public BoxGame(Board board) {
    this(DEFAULT_PLAYER_COUNT, board);
  }
  
  public BoxGame(int numPlayers, int boardSize) {
    this(numPlayers, new Board(boardSize));
  }

  public BoxGame(int numPlayers, Board board) {
    players = new Player[numPlayers];
    for (int i = 0; i < numPlayers; i++) {
      players[i] = new Player(Character.forDigit(i + 1, 10), Strategy.MM);
    }
    this.board = board;
  }

  @Override
  public String toString() {
    return "BoxGame{" +
    "players:" + Arrays.toString(players) + "," +
    "board:\n" + board + 
    "}";
  }

  public static void main(String[] args) {
    BoxGame bg = new BoxGame(2, new Board(
      new BoardPosition(5, 2), new BoardPosition(5, 4),
      new BoardPosition(2, 3)
    ));
    System.out.println(bg);
  }
}