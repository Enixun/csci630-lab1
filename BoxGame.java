import java.util.Arrays;

public class BoxGame {
  private static final int DEFAULT_PLAYER_COUNT = 2;
  private static final int DEFAULT_FIRST_TURN = 0;

  private Player[] players;
  private Board board;
  private int turnIndex;
  
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
    this.turnIndex = DEFAULT_FIRST_TURN;
  }

  public Board getBoard() {
    return board;
  }

  public int evaluate(Board board, int mover) {
    return minimax(board, mover - 1, players[mover - 1], Integer.MAX_VALUE);
  }

  private int minimax(Board board, int turnIndex, Player max, int depth) {
    Player p = players[turnIndex];
    boolean isMax = p.equals(max);
    int val = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    boolean terminal = true;
    if (depth > 0) {
      for (BoardPosition bp : board.available()) {
        for (Piece piece : p.getPieces()) {
          Board update = board.copy();
          try {
            update.updateBoard(p.getId(), piece.coordinates(bp));
            int curVal = minimax(update, (turnIndex + 1) % players.length, max, depth - 1);
            if ((isMax && curVal > val) || (!isMax && curVal < val)) val = curVal;
            terminal = false;
          } catch (InvalidBoardException ibe) {
            // System.err.println("Cannot place piece at " + bp + ", " + ibe.getMessage());
          }
        }
      }
    }
    return terminal ? -1 : val;
  }

  // public void move(Board board, int mover) {
  //   Player p = players[mover - 1];
  //   for (Piece piece : p.getPieces()) {
  //     Board update = board.copy();
  //     update.updateBoard(p.getId(), piece.coordinates(bp));
  //     System.out.println(update);
  //   }
  // }

  @Override
  public String toString() {
    return "BoxGame{" +
    "players:" + Arrays.toString(players) + "," +
    "turn:" + players[turnIndex].getId() + "," +
    "board:\n" + board + 
    "}";
  }

  public static void main(String[] args) throws InvalidBoardException {
    BoxGame bg = new BoxGame(2, new Board(
      new BoardPosition(5, 2), new BoardPosition(5, 4),
      new BoardPosition(2, 3)
    ));
    System.out.println(bg);
    bg.evaluate(bg.getBoard(), 1);
  }
}