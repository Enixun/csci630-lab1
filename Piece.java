// import java.util.Arrays;

public class Piece {
  private int length;
  private int width;
  
  public Piece(int length, int width) {
    this.length = length < width ? length : width;
    this.width = this.length == length ? width : length;
  }

  public BoardPosition[] coordinates(BoardPosition start) {
    BoardPosition[] c = new BoardPosition[length * width];
    int ci = 0;
    for (int i = 0; i < length; i++) {
      for (int j = 0; j < width; j++) {
        c[ci++] = start.add(i, j);
        // System.out.println(Arrays.toString(c));
      }
    }
    return c;
  }

  @Override
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + length;
    hash = hash * 31 + width;
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Piece) {
      Piece p = (Piece) obj;
      return this.length == p.length && this.width == p.width;
    }
    return false;
  }

  @Override
  public String toString() {
    return this.length + "x" + this.width;
  }

  public static void main(String[] args) {
    Piece a = new Piece(2, 3);
    Piece b = new Piece(3, 2);

    System.out.println(a);
    System.out.println(b);

    System.out.println(a.equals(b));
    System.out.println(b.equals(a));

    System.out.println(a.hashCode());
    System.out.println(b.hashCode());
  }
}