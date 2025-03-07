package components;
import java.util.HashSet;
import java.util.Set;

public class Player {
  private static final Piece DEFAULT_PIECE = new Piece(2, 3);
  private static final int DEFAULT_DEPTH = Integer.MAX_VALUE;
  private char id;
  private Strategy strategy;
  private int depth;
  private Set<Piece> pieces;
  
  public Player(char id, Strategy strategy) {
    this(id, strategy, DEFAULT_DEPTH);
  }
  
  public Player(char id, Strategy strategy, int depth) {
    this.id = id;
    this.strategy = strategy;
    this.depth = depth;
    this.pieces = new HashSet<>();
    pieces.add(DEFAULT_PIECE);
    pieces.add(DEFAULT_PIECE.flip());
  }

  public char getId() {
    return id;
  }

  public Strategy getStrategy() {
    return this.strategy;
  }

  public void setStrategy(Strategy s) {
    this.strategy = s;
  }

  public int getDepth() {
    return depth;
  }

  public void setDepth(int depth) {
    this.depth = depth;
  }

  public Set<Piece> getPieces() {
    return pieces;
  }

  @Override
  public String toString() {
    return "Player(" +
    "id:" + id + "," +
    "strategy:" + strategy + "," +
    "depth:" + depth + "," +
    "pieces:" + pieces.toString() +
    ")";
  }

  public static void main(String[] args) {
    Player p1 = new Player('1', Strategy.MM);
    System.out.println(p1);
  }
}