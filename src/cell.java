import java.awt.Color;

import javalib.impworld.WorldScene;
import javalib.worldimages.LineImage;
import javalib.worldimages.OutlineMode;
import javalib.worldimages.Posn;
import javalib.worldimages.RectangleImage;
import javalib.worldimages.WorldImage;
import tester.Tester;

// represents a single cell in a maze
class Cell {

  // The coordinates of the cell
  Posn xy;

  // the size of a single side of the cell, in px
  int size;

  // color of the cell
  Color color;

  // if it has been visited or not
  boolean visited;

  // its neighboring cells
  Cell left;
  Cell right;
  Cell top;
  Cell bottom;

  Cell(Posn xy, int size, Color color, boolean visited) {
    this.xy = xy;
    this.size = size;
    this.color = color;
    this.visited = visited;
    this.left = null;
    this.right = null;
    this.top = null;
    this.bottom = null;
  }

  Cell(Posn xy) {
    this(xy, 8, Color.gray, false);
  }

  // draws a single cell
  public WorldImage draw() {
    return new RectangleImage(this.size, this.size, OutlineMode.SOLID, this.color);
  }

  // draws a cell at a specific x and y coordinates
  public void drawAt(int col, int row, WorldScene background) {
    int xCord = (col * this.size) + (this.size / 2);
    int yCord = (row * this.size) + (this.size / 2);
    background.placeImageXY(this.draw(), xCord, yCord);

    // draws a wall in the direction(s) that the cell does not have a connection to
    if (this.right == null) {
      WorldImage line = new LineImage(new Posn(0, this.size), Color.BLACK);
      background.placeImageXY(line, (int) (xCord + this.size / 2.0), yCord);
    }

    if (this.left == null) {
      WorldImage line = new LineImage(new Posn(0, this.size), Color.BLACK);
      background.placeImageXY(line, (int) (xCord - this.size / 2.0), yCord);
    }

    if (this.top == null) {
      WorldImage line = new LineImage(new Posn(this.size, 0), Color.BLACK);
      background.placeImageXY(line, xCord, (int) (yCord + this.size / 2.0));
    }

    if (this.bottom == null) {
      WorldImage line = new LineImage(new Posn(this.size, 0), Color.BLACK);
      background.placeImageXY(line, xCord, (int) (yCord - this.size / 2.0));
    }

  }

}

class ExamplesCell {

  Cell cell = new Cell(new Posn(10, 10), 25, Color.gray, false);
  Cell cell1 = new Cell(new Posn(10, 10), 20, Color.blue, false);
  Cell cell0 = new Cell(new Posn(10, 10));
  WorldScene scene = new WorldScene(100, 100);
  WorldScene scene1 = new WorldScene(100, 100);

  // testing the draw method
  void testDraw(Tester t) {
    t.checkExpect(cell.draw(), new RectangleImage(25, 25, OutlineMode.SOLID, Color.GRAY));
    t.checkExpect(cell1.draw(), new RectangleImage(20, 20, OutlineMode.SOLID, Color.blue));
    t.checkExpect(cell0.draw(), new RectangleImage(8, 8, OutlineMode.SOLID, Color.GRAY));

  }

  // testing drawAt method
  void testDrawAt(Tester t) {
    cell.drawAt(10, 10, scene);
    int xCord = (10 * 25) + (25 / 2);
    scene1.placeImageXY(cell.draw(), xCord, xCord);
    t.checkExpect(scene, scene1);

  }

}
