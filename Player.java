public class Player {
  private char id;
  private Strategy strategy;
  
  public Player(char id, Strategy strategy) {
    this.id = id;
    this.strategy = strategy;
  }
}