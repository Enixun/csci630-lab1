package components;
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
    return this.add(new BoardPosition(row, col));
  }

  public BoardPosition add(BoardPosition bp) {
    return new BoardPosition(this.row + bp.row, this.col + bp.col);
  }

  @Override
  public int hashCode() {
    return this.toString().hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof BoardPosition) {
      BoardPosition bp = (BoardPosition) o;
      return bp.row == this.row && bp.col == this.col;
    }
    return false;
  }

  @Override
  public String toString() {
    return row + "," + col;
  }

  public static void main(String[] args) {
    BoardPosition bp1 = new BoardPosition(0, 0);
    BoardPosition bp2 = new BoardPosition(0, 0);
    System.out.println(bp1);
    System.out.println(bp2);
    System.out.println(bp1.hashCode() == bp2.hashCode());
  }
}