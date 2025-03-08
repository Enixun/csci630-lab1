package components;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Queue;
import java.util.Random;

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
      players[i] = new Player(String.valueOf(i + 1), Strategy.MM);
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
    max.setStrategyCalls(max.getStrategyCalls() + 1);
    int nextIndex = (turnIndex + 1) % players.length;
    Player nextPlayer = players[nextIndex];
    boolean isMax = nextPlayer.equals(max);
    // System.out.println(p + " = " + max + " = " + isMax);
    int val = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    if (depth == 0) return max.getEval() == 1 ?  greedyPoints(board, max) : blockMost(board, max);
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
    max.setStrategyCalls(max.getStrategyCalls() + 1);
    int nextIndex = (turnIndex + 1) % players.length;
    Player nextPlayer = players[nextIndex];
    boolean isMax = nextPlayer.equals(max);
    int val = isMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;
    if (depth == 0) return max.getEval() == 1 ? greedyPoints(board, max) : blockMost(board, max);
    ArrayList<Board> moves = possibleMoves(board, nextPlayer);
    if (moves.size() == 0) {
      int score = score(board, max);
      return score;
    }
    for (Board update : moves) {
      int curVal = alphabeta(update, nextIndex, max, depth - 1, alpha, beta);
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
    int bestScore = Integer.MIN_VALUE;
    int alpha = Integer.MIN_VALUE;
    int beta = Integer.MAX_VALUE;
    Strategy s = p.getStrategy();
    for (Board update : possibleMoves(board, p)) {
      // System.out.println("Update\n" + update);
      // int curScore = evaluate(update, mover);
      // double curScore = minimax(update, turnIndex, p, Integer.MAX_VALUE);
      int curScore = s == Strategy.MM ? minimax(update, turnIndex, p, p.getDepth()) : alphabeta(update, turnIndex, p, p.getDepth(), alpha, beta);
      if (curScore > bestScore) {
        bestBoard = update;
        bestScore = curScore;
      }
      if (s == Strategy.AB && curScore > alpha) alpha = curScore;
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
        String val = board.lookup(bp);
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
  
  public int greedyPoints(Board board, Player p) {
    // maximize points earned
    int playerScore = 0;
    HashSet<BoardPosition> visited = new HashSet<>();

    // largely duplicated, would use a callback in JS or Python but not familiar with Java strategy
    for (int r = 0; r < board.getSize(); r++) {
      for (int c = 0; c < board.getSize(); c++) {
        BoardPosition bp = new BoardPosition(r, c);
        if (visited.contains(bp)) continue;
        String val = board.lookup(bp);
        if (val == p.getId()) {
          Queue<BoardPosition> q = new LinkedList<>();
          HashSet<BoardPosition> inQ = new HashSet<>();
          q.add(bp);
          inQ.add(bp);
          int count = 0;

          while (!q.isEmpty()) {
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

          // Only able to do this because of consistent areas, need better way to track
          playerScore += (count / 6) - 1;
        }
        visited.add(bp);
      }
    }
    return playerScore;
  }

  public int blockMost(Board board, Player p) {
    // border most possible opponent edges
    int options = 0;
    HashSet<BoardPosition> visited = new HashSet<>();

    // largely duplicated, would use a callback in JS or Python but not familiar with Java strategy
    for (int r = 0; r < board.getSize(); r++) {
      for (int c = 0; c < board.getSize(); c++) {
        BoardPosition bp = new BoardPosition(r, c);
        if (visited.contains(bp)) continue;
        String val = board.lookup(bp);
        if (val == p.getId()) {
          BoardPosition up = bp.add(-1, 0);
          BoardPosition down = bp.add(1, 0);
          BoardPosition left = bp.add(0, -1);
          BoardPosition right = bp.add(0, 1);
          if (bp.row() > 0 && board.lookup(up) != p.getId() && board.lookup(up) != Board.AVAILABLE && board.lookup(up) != Board.OBSTACLE) {
            options++;
          }
          if (bp.row() < board.getSize() - 1 && board.lookup(down) != p.getId() && board.lookup(down) != Board.AVAILABLE && board.lookup(down) != Board.OBSTACLE) {
            options++;
          }
          if (bp.col() > 0 && board.lookup(left) != p.getId() && board.lookup(left) != Board.AVAILABLE && board.lookup(left) != Board.OBSTACLE) {
            options++;
          }
          if (bp.col() < board.getSize() - 1 && board.lookup(right) != p.getId() && board.lookup(right) != Board.AVAILABLE && board.lookup(right) != Board.OBSTACLE) {
            options++;
          }
        }
        visited.add(bp);
      }
    }
    return options;
  }

  public Board play(Board board, String alg1, int depth1, int ev1, String alg2, int depth2, int ev2) throws Exception {
    if (!alg1.equals("MM") && !alg1.equals("AB")) throw new Exception("Error with alg1: Invalid algorithm");
    players[0].setStrategy(alg1.equals("MM") ? Strategy.MM : Strategy.AB);
    players[0].setStrategyCalls(0);
    players[0].setDepth(depth1);
    players[0].setEval(ev1);
    if (!alg2.equals("MM") && !alg2.equals("AB")) throw new Exception("Error with alg2: Invalid algorithm");
    players[1].setStrategy(alg2.equals("MM") ? Strategy.MM : Strategy.AB);
    players[1].setStrategyCalls(0);
    players[1].setDepth(depth2);
    players[1].setEval(ev2);

    Board state = board;
    int mover = DEFAULT_FIRST_TURN + 1;
    
    while (true) {
      try {
        state = move(state, mover);
      } catch (InvalidBoardException ibe) {
        break;
      }
      mover = mover % 2 + 1;
    }
    
    return state;
  }

  public double[] expect(int bsize, int obs, int runs, String alg1, int depth1, int ev1, String alg2, int depth2, int ev2) {
    int wins = 0;
    int totalScore = 0;
    Random rng = new Random();
    long startTime = System.currentTimeMillis();

    for (int i = 0; i < runs; i++) {
      BoardPosition[] obstacles = new BoardPosition[obs];
      int oi = 0;
      HashSet<BoardPosition> uniqueObstacles = new HashSet<>();
      while (oi < obs) {
        BoardPosition bp = new BoardPosition(rng.nextInt(bsize), rng.nextInt(bsize));
        if (!uniqueObstacles.contains(bp)) obstacles[oi++] = bp;
        uniqueObstacles.add(bp);
      }
      Board end = null;
      try {
        Board start = new Board(bsize, obstacles);
        end = play(start, alg1, depth1, ev1, alg2, depth2, ev2);
      } catch (Exception e) {
        i--;
        continue;
      }

      // System.out.println(end);
      int score = score(end, players[0]);
      if (score > 0) wins++;
      totalScore += score;
      // System.out.println(end);
      this.board = end;
      System.out.println(this);
    }

    System.out.print("Ran in " + (System.currentTimeMillis() - startTime) + "ms ");
    double[] tuple = { wins, ((double) totalScore / runs) };
    return tuple;
  }

  @Override
  public String toString() {
    return "BoxGame{" +
    "players:" + Arrays.toString(players) + "," +
    "turn:" + players[turnIndex].getId() + "," +
    "board:\n" + board + 
    "}";
  }

  public static void main(String[] args) throws Exception {
    BoxGame bg1 = new BoxGame(2, new Board(
      new BoardPosition(5, 2), new BoardPosition(5, 4),
      new BoardPosition(2, 3)
    ));
    System.out.println(bg1);

    BoxGame bg2 = new BoxGame(2, new Board(
      7,
      new BoardPosition(1, 1), 
      new BoardPosition(4, 0)
    ));
    System.out.println(bg2);

    System.out.println(bg1.play(bg1.getBoard(), "MM", Integer.MAX_VALUE, 1, "AB", Integer.MAX_VALUE, 1));
    System.out.println(bg1);
    System.out.println(bg2.play(bg2.getBoard(), "MM", 3, 1, "MM", 3, 2));
    System.out.println(bg2);
    System.out.println(bg2.play(bg2.getBoard(), "AB", 3, 1, "AB", 3, 2));
    System.out.println(bg2);

    System.out.println(Arrays.toString(
      bg1.expect(6, 2, 25, "MM", Integer.MAX_VALUE, 1, "MM", Integer.MAX_VALUE, 1)
    ));
    System.out.println(Arrays.toString(
      bg1.expect(6, 2, 25, "MM", 3, 1, "MM", Integer.MAX_VALUE, 1)
    ));
    System.out.println(Arrays.toString(
      bg1.expect(6, 2, 25, "MM", 3, 2, "MM", Integer.MAX_VALUE, 1)
    ));
    System.out.println(Arrays.toString(
      bg1.expect(8, 5, 25, "MM", 3, 1, "MM", 3, 2)
    ));
    System.out.println(Arrays.toString(
      bg1.expect(6, 2, 25, "AB", Integer.MAX_VALUE, 1, "AB", Integer.MAX_VALUE, 2)
    ));
    System.out.println(Arrays.toString(
      bg1.expect(8, 5, 25, "AB", 5, 1, "AB", 5, 2)
    ));
  }
}