package components;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Queue;

public class BoxGame {
  private static final int DEFAULT_PLAYER_COUNT = 2;
  private static final int DEFAULT_FIRST_TURN = 0;

  private Player[] players;
  private Board board;
  private int turnIndex;
  
  public BoxGame() {
    this(DEFAULT_PLAYER_COUNT, new Board());
  }

  public BoxGame(int numPlayers) {
    this(numPlayers, new Board());
  }

  public BoxGame(Board board) {
    this(DEFAULT_PLAYER_COUNT, board);
  }
  
  public BoxGame(int numPlayers, int boardSize) {
    this(numPlayers, new Board(boardSize));
  }

  public BoxGame(int numPlayers, Board board) {
    players = new Player[numPlayers];
    for (int i = 0; i < numPlayers; i++) {
      players[i] = new Player(Character.forDigit(i + 1, 10), Strategy.MM);
    }
    this.board = board;
    this.turnIndex = DEFAULT_FIRST_TURN;
  }

  public Board getBoard() {
    return board;
  }

  public int evaluate(Board board, int mover) {
    return minimax(board, mover - 1, players[mover - 1], Integer.MAX_VALUE);
    // return alphabeta(board, mover - 1, players[mover - 1], Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
  }

  private ArrayList<Board> possibleMoves(Board b, Player player) {
    // System.out.println("Possible moves for " + player.getId());
    ArrayList<Board> moves = new ArrayList<>();
    for (BoardPosition bp : b.available()) {
      for (Piece p : player.getPieces()) {
        Board copy = b.copy();
        BoardPosition[] coords = p.coordinates(bp);
        try {
          copy.updateBoard(player.getId(), coords);
          moves.add(copy);
        } catch (InvalidBoardException ibe) {
          // System.out.println(ibe.getMessage() + " Skipping...");
        }
      }
    }
    // System.out.println(player);
    // System.out.println(moves);
    return moves;
  }

  private int minimax(Board board, int turnIndex, Player max, int depth) {
    int nextIndex = (turnIndex + 1) % players.length;
    Player nextPlayer = players[nextIndex];
    boolean isMax = nextPlayer.equals(max);
    // System.out.println(p + " = " + max + " = " + isMax);
    int val = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    if (depth == 0) return score(board, nextPlayer);
    ArrayList<Board> moves = possibleMoves(board, nextPlayer);
    if (moves.size() == 0) {
      int score = score(board, max);
      // System.out.println("Terminal board for " + max + " score: " + score);
      // System.out.println(board);
      return score;
    }
    for (Board update : moves) {
      // System.out.println("possible\n" + update);
        int curVal = minimax(update, nextIndex, max, depth - 1);
        // System.out.println("Best so far: " + val + ", current: " + curVal + ", " + (isMax ? "maximizing" : "minimizing"));
        if ((isMax && curVal > val) || (!isMax && curVal < val)) val = curVal;
    }
    // if (terminal) val = finalScore(board, p);
    // System.out.println("minimax: " + p + " max: " + max + " best val: " + val);
    return val;
  }

  private int alphabeta(Board board, int turnIndex, Player max, int depth, int alpha, int beta) {
    int nextIndex = (turnIndex + 1) % players.length;
    Player nextPlayer = players[nextIndex];
    boolean isMax = nextPlayer.equals(max);
    int val = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    if (depth == 0) return score(board, nextPlayer);
      ArrayList<Board> moves = possibleMoves(board, nextPlayer);
      if (moves.size() == 0) {
        int score = score(board, max);
        return score;
      }
      for (Board update : moves) {
        int curVal = alphabeta(update, nextIndex, max, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE);
        if ((isMax && curVal > val) || (!isMax && curVal < val)) {
          val = curVal;
          if ((isMax && val > beta) || (!isMax && curVal < alpha)) break;
          if (isMax && val > alpha) alpha = val;
          else if (!isMax && val < beta) beta = val;
      }
    }
    return val;
  }
  
  public Board move(Board board, int mover) throws InvalidBoardException {
    int turnIndex = mover - 1;
    Player p = players[turnIndex];
    // System.out.println(p);
    Board bestBoard = null;
    double bestScore = Integer.MIN_VALUE;
    for (Board update : possibleMoves(board, p)) {
      // System.out.println("Update\n" + update);
      int curScore = evaluate(update, mover);
      // double curScore = minimax(update, turnIndex, p, Integer.MAX_VALUE);
      if (curScore > bestScore) {
        bestBoard = update;
        bestScore = curScore;
      }
    }
    if (bestBoard == null) throw new InvalidBoardException("No possible moves");
    return bestBoard;
  }

  private int score(Board board, Player p) {
    int playerScore = 0;
    int opponentScore = 0;
    HashSet<BoardPosition> visited = new HashSet<>();
    // System.out.println(p);

    for (int r = 0; r < board.getSize(); r++) {
      for (int c = 0; c < board.getSize(); c++) {
        BoardPosition bp = new BoardPosition(r, c);
        if (visited.contains(bp)) continue;
        char val = board.lookup(bp);
        if (val != Board.AVAILABLE && val != Board.OBSTACLE) {
          Queue<BoardPosition> q = new LinkedList<>();
          HashSet<BoardPosition> inQ = new HashSet<>();
          q.add(bp);
          inQ.add(bp);
          int count = 0;

          while (!q.isEmpty()) {
            // System.out.println(q);
            BoardPosition sp = q.remove();
            if (visited.contains(sp) || board.lookup(sp) != val) continue;
            visited.add(sp);
            count += 1;
            BoardPosition up = sp.add(-1, 0);
            BoardPosition down = sp.add(1, 0);
            BoardPosition left = sp.add(0, -1);
            BoardPosition right = sp.add(0, 1);
            if (sp.row() > 0 && !visited.contains(up) && !inQ.contains(up)) {
              q.add(up);
              inQ.add(up);
            }
            if (sp.row() < board.getSize() - 1 && !visited.contains(down) && !inQ.contains(down)) {
              q.add(down);
              inQ.add(down);
            }
            if (sp.col() > 0 && !visited.contains(left) && !inQ.contains(left)) {
              q.add(left);
              inQ.add(left);
            }
            if (sp.col() < board.getSize() - 1 && !visited.contains(right) && !inQ.contains(right)) {
              q.add(right);
              inQ.add(right);
            }
          }

          // System.out.println(val + " " + count);
          // Only able to do this because of consistent areas, need better way to track
          if (val == p.getId()) playerScore += (count / 6) - 1;
          else opponentScore += (count / 6) - 1;
        }
        visited.add(bp);
      }
    }

    // System.out.println(playerScore + ", " + opponentScore);
    // return 1.001 * playerScore - opponentScore;
    // return playerScore - 1.001 * opponentScore;
    return playerScore - opponentScore;
  }
  
  public double eval1(Board b, Player p) {
    // maximize points earned
    int playerScore = 0;
    int opponentScore = 0;
    HashSet<BoardPosition> visited = new HashSet<>();
    // System.out.println(p);

    for (int r = 0; r < board.getSize(); r++) {
      for (int c = 0; c < board.getSize(); c++) {
        BoardPosition bp = new BoardPosition(r, c);
        if (visited.contains(bp)) continue;
        char val = board.lookup(bp);
        if (val != Board.AVAILABLE && val != Board.OBSTACLE) {
          Queue<BoardPosition> q = new LinkedList<>();
          HashSet<BoardPosition> inQ = new HashSet<>();
          q.add(bp);
          inQ.add(bp);
          int count = 0;

          while (!q.isEmpty()) {
            // System.out.println(q);
            BoardPosition sp = q.remove();
            if (visited.contains(sp) || board.lookup(sp) != val) continue;
            visited.add(sp);
            count += 1;
            BoardPosition up = sp.add(-1, 0);
            BoardPosition down = sp.add(1, 0);
            BoardPosition left = sp.add(0, -1);
            BoardPosition right = sp.add(0, 1);
            if (sp.row() > 0 && !visited.contains(up) && !inQ.contains(up)) {
              q.add(up);
              inQ.add(up);
            }
            if (sp.row() < board.getSize() - 1 && !visited.contains(down) && !inQ.contains(down)) {
              q.add(down);
              inQ.add(down);
            }
            if (sp.col() > 0 && !visited.contains(left) && !inQ.contains(left)) {
              q.add(left);
              inQ.add(left);
            }
            if (sp.col() < board.getSize() - 1 && !visited.contains(right) && !inQ.contains(right)) {
              q.add(right);
              inQ.add(right);
            }
          }

          // System.out.println(val + " " + count);
          // Only able to do this because of consistent areas, need better way to track
          if (val == p.getId()) playerScore += (count / 6) - 1;
          else opponentScore += (count / 6) - 1;
        }
        visited.add(bp);
      }
    }

    // System.out.println(playerScore + ", " + opponentScore);
    // return 1.001 * playerScore - opponentScore;
    // return playerScore - 1.001 * opponentScore;
    return playerScore - opponentScore;
  }

  @Override
  public String toString() {
    return "BoxGame{" +
    "players:" + Arrays.toString(players) + "," +
    "turn:" + players[turnIndex].getId() + "," +
    "board:\n" + board + 
    "}";
  }

  public static void main(String[] args) throws InvalidBoardException {
    BoxGame bg = new BoxGame(2, new Board(
      new BoardPosition(5, 2), new BoardPosition(5, 4),
      new BoardPosition(2, 3)
    ));
    // System.out.println(bg);

    System.out.println("TURN 1");
    bg.board = bg.move(bg.board, 1);
    System.out.println(bg.board);

    System.out.println("TURN 2");
    bg.board = bg.move(bg.board, 2);
    System.out.println(bg.board);

    System.out.println("TURN 1");
    bg.board = bg.move(bg.board, 1);
    System.out.println(bg.board);

    System.out.println("TURN 2");
    bg.board = bg.move(bg.board, 2);
    System.out.println(bg.board);

    BoxGame bg2 = new BoxGame(2, new Board(
      7,
      new BoardPosition(1, 1), 
      new BoardPosition(4, 0)
    ));
    // System.out.println(bg);

    System.out.println("TURN 1");
    bg2.board = bg2.move(bg2.board, 1);
    System.out.println(bg2.board);

    System.out.println("TURN 2");
    bg2.board = bg2.move(bg2.board, 2);
    System.out.println(bg2.board);

    System.out.println("TURN 1");
    bg2.board = bg2.move(bg2.board, 1);
    System.out.println(bg2.board);

    System.out.println("TURN 2");
    bg2.board = bg2.move(bg2.board, 2);
    System.out.println(bg2.board);

    System.out.println("TURN 1");
    bg2.board = bg2.move(bg2.board, 1);
    System.out.println(bg2.board);

    System.out.println("TURN 2");
    bg2.board = bg2.move(bg2.board, 2);
    System.out.println(bg2.board);
  }
}