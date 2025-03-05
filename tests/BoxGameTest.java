package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;

import components.Board;
import components.BoardPosition;
import components.BoxGame;
import components.InvalidBoardException;

public class BoxGameTest {
  // private BoxGame bg;

  // @Before
  // public void setUp() {
  //   bg = new BoxGame();
  // }

  @Test
  public void demo() {
    BoxGame bg;
    Board board = null;
    try {
      board = new Board(
        new BoardPosition(5, 2), 
        new BoardPosition(5, 4),
        new BoardPosition(2, 3)
      );
    } catch (InvalidBoardException ibe) {
      fail("Unable to initialize board");
    }
    bg = new BoxGame(2, board);
    assertNotNull(bg.getBoard());
    assertEquals("Expect board to match input", bg.getBoard(), board);
  }
  
}