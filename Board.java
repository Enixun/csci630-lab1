import java.util.Arrays;

public class Board {
  private static final int DEFAULT_BOARD_SIZE = 6;
  private static final char AVAILABLE = '.';
  private static final char OBSTACLE = '*';

  private int size;
  private char[][] board;
  
  public Board() {
    this(Board.DEFAULT_BOARD_SIZE);
  }

  public Board(int size) {
    this.size = size;
    this.board = new char[size][size];
    this.init();
  }

  public Board(BoardPosition... obstacles) {
    this(DEFAULT_BOARD_SIZE, obstacles);
  }

  public Board(int size, BoardPosition... obstacles) {
    this(size);
    for (BoardPosition bp : obstacles) {
      addObstacle(bp);
    }
  }

  public void init() {
    for (int r = 0; r < size; r++) {
      for (int c = 0; c < size; c++) {
        board[r][c] = Board.AVAILABLE;
      }
    }
  }

  public void addObstacle(BoardPosition... positions) {
    updateBoard(OBSTACLE, positions);
  }

  public void updateBoard(char symbol, BoardPosition... positions) {
    for (BoardPosition bp : positions) {
      if (board[bp.row()][bp.col()] == AVAILABLE) board[bp.row()][bp.col()] = symbol;
    }
  }

  public Board copy() {
    Board copy = new Board(size);
    for (int i = 0; i < size; i++) {
      copy.board[i] = board[i];
    }
    return copy;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    for (char[] row : board) {
      sb.append('|');
      for (char col : row) {
        sb.append(col);
        sb.append('|');
      }
      sb.append('\n');
    }

    return sb.toString();
  }

  public static void main(String[] args) {
    Board b = new Board(new BoardPosition(5, 2), new BoardPosition(5, 4));
    b.addObstacle(new BoardPosition(2, 3));
    System.out.println(b.toString());
  }
}