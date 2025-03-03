public class BoardPosition {
  private int row;
  private int col;

  public BoardPosition(int row, int col) {
    this.row = row;
    this.col = col;
  }

  public int row() {
    return this.row;
  }

  public int col() {
    return this.col;
  }

  public BoardPosition add(int row, int col) {
    return new BoardPosition(this.row + row, this.col + col);
  }

  public BoardPosition add(BoardPosition bp) {
    return new BoardPosition(this.row + bp.row, this.col + bp.col);
  }

  @Override
  public String toString() {
    return row + "," + col;
  }
}