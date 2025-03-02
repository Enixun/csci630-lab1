import java.util.HashSet;
import java.util.Set;

public class Player {
  private char id;
  private Strategy strategy;
  private Set<Piece> pieces;
  
  public Player(char id, Strategy strategy) {
    this.id = id;
    this.strategy = strategy;
    this.pieces = new HashSet<>();
    pieces.add(new Piece(2, 3));
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