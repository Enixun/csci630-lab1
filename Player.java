import java.util.HashSet;
import java.util.Set;

public class Player {
  private static final Piece DEFAULT_PIECE = new Piece(2, 3);
  private char id;
  private Strategy strategy;
  private Set<Piece> pieces;
  
  public Player(char id, Strategy strategy) {
    this.id = id;
    this.strategy = strategy;
    this.pieces = new HashSet<>();
    pieces.add(DEFAULT_PIECE);
    pieces.add(DEFAULT_PIECE.flip());
  }

  public char getId() {
    return id;
  }

  public void setStrategy(Strategy s) {
    this.strategy = s;
  }

  public Set<Piece> getPieces() {
    return pieces;
  }

  @Override
  public String toString() {
    return "Player(" +
    "id:" + id + "," +
    "strategy:" + strategy + "," +
    "pieces:" + pieces.toString() +
    ")";
  }

  public static void main(String[] args) {
    Player p1 = new Player('1', Strategy.MM);
    System.out.println(p1);
  }
}