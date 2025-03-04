import java.util.Arrays;
import java.util.ArrayList;

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

  public Board(BoardPosition... obstacles) throws InvalidBoardException {
    this(DEFAULT_BOARD_SIZE, obstacles);
  }

  public Board(int size, BoardPosition... obstacles) throws InvalidBoardException {
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

  public void addObstacle(BoardPosition... positions) throws InvalidBoardException {
    updateBoard(OBSTACLE, positions);
  }

  public void updateBoard(char symbol, BoardPosition... positions) throws InvalidBoardException {
    for (BoardPosition bp : positions) {
      int row = bp.row();
      int col = bp.col();
      if (row < 0 || row >= size || col < 0 || col >= size) 
        throw new InvalidBoardException("Piece out of bounds.");
      if (board[bp.row()][bp.col()] != AVAILABLE) 
        throw new InvalidBoardException("Cannot update unavailable slot.");
    }
    for (BoardPosition bp : positions) {
      board[bp.row()][bp.col()] = symbol;
    }
  }

  public Board copy() {
    Board copy = new Board(size);
    for (int i = 0; i < size; i++) {
      copy.board[i] = Arrays.copyOf(board[i], board[i].length);
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

  public ArrayList<BoardPosition> available() {
    ArrayList<BoardPosition> available = new ArrayList<>();
    for (int r = 0; r < size; r++) {
      for (int c = 0; c < size; c++) {
        if (board[r][c] == AVAILABLE) available.add(new BoardPosition(r, c));
      }
    }
    return available;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Board) {
      Board b = (Board) obj;
      if (b.size != this.size) return false;
      for (int r = 0; r < size; r++) {
        for (int c = 0; c < size; c++) {
          if (b.board[r][c] != this.board[r][c])
            return false;
        }
      }
      return true;
    }
    return false;
  }

  public static void main(String[] args) throws InvalidBoardException {
    Board b1 = new Board(new BoardPosition(5, 2), new BoardPosition(5, 4));
    b1.addObstacle(new BoardPosition(2, 3));
    System.out.println(b1.toString());
    Board b2 = new Board(new BoardPosition(5, 2), new BoardPosition(5, 4));
    b2.addObstacle(new BoardPosition(2, 3));
    System.out.println(b1.equals(b2));
  }
}