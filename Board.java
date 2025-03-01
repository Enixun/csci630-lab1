public class Board {
  private static final char AVAILABLE = '.';
  private static final char OBSTACLE = '*';
  
  private int size;
  private char[][] board;
  
  public Board() {
    this(6);
  }

  public Board(int size) {
    this.size = size;
    this.board = new char[size][size];
  }

  public void init() {
    for (int r = 0; r < size; r++) {
      for (int c = 0; c < size; c++) {
        board[r][c] = Board.AVAILABLE;
      }
    }
  }

  public void addObstacle(BoardPosition ...positions) {
    for (BoardPosition bp : positions) {
      board[bp.row()][bp.col()] = Board.OBSTACLE;
    }
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
    Board b = new Board();
    b.init();
    b.addObstacle(new BoardPosition(2, 3));
    System.out.println(b.toString());
  }
}