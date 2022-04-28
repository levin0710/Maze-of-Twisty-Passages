import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Queue;
import java.util.Stack;

import javalib.impworld.*;
import javalib.worldimages.Posn;
import tester.Tester;

// represents a maze
class MazeWorld extends World {

  // length of the maze
  int length;

  // width of the maze
  int width;

  // List of all the cells in the maze
  ArrayList<ArrayList<Cell>> board;

  // List of all the edges between cells;
  ArrayList<Edge> edges;

  // maps a cell position to a cell position that it can go to.
  HashMap<Posn, Posn> representatives;

  // tracks from what cell did a specific cell came from.
  HashMap<Posn, Posn> cameFromEdge;

  // the worklist for bfs.
  Queue<Cell> bfsworklist;

  // the worklist of dfs
  Stack<Cell> dfsworklist;

  // determine if the program should start the bfs
  boolean bfs;

  // determine if the program should start the dfs
  boolean dfs;

  // the current color that a cell should be colored.
  double colorCode;

  // determine if the maze should start reconstructing the passage to the begining
  boolean reconstruct;

  // the starting position of the maze
  Posn reconST;

  MazeWorld(int width, int length) {
    this.length = length;
    this.width = width;
    this.board = new ArrayList<>();
    this.edges = new ArrayList<>();
    this.representatives = new HashMap<>();
    this.setBoard();
    this.spanningTree();

    this.bfsworklist = new ArrayDeque<>();
    this.bfsworklist.add(this.board.get(0).get(0));

    this.dfsworklist = new Stack<>();
    this.dfsworklist.add(this.board.get(0).get(0));

    this.cameFromEdge = new HashMap<>();
    this.bfs = false;
    this.dfs = false;
    this.reconstruct = false;
    this.reconST = new Posn(0, 0);
    this.colorCode = 0;

  }

  MazeWorld() {
    this(20, 15);
  }

  // sets the maze board with all its cells.
  void setBoard() {

    // puts each cell in the board
    for (int x = 0; x < this.width; x++) {
      // first for loop is for columns
      ArrayList<Cell> row = new ArrayList<Cell>();
      this.board.add(row);
      for (int y = 0; y < this.length; y++) {
        row.add(new Cell(new Posn(x, y)));
      }
    }

    // connects every cell to each other and adss all those edges to the list of
    // edges
    for (int x = 0; x < this.width; x++) {
      for (int y = 0; y < this.length; y++) {

        Cell cell = this.board.get(x).get(y);

        if (cell.xy.x + 1 < this.width) {
          Cell right = this.board.get(x + 1).get(y);
          this.edges.add(new Edge(cell.xy, right.xy));
        }

        if (cell.xy.y + 1 < this.length) {
          Cell bottom = this.board.get(x).get(y + 1);
          this.edges.add(new Edge(cell.xy, bottom.xy));
        }
      }
    }

  }

  // creates the maze's spanning tree
  void spanningTree() {

    // initialize every cell's representative to itself
    for (int x = 0; x < this.width; x++) {
      for (int y = 0; y < this.length; y++) {
        Cell cell = this.board.get(x).get(y);
        this.representatives.put(cell.xy, cell.xy);
      }
    }

    ArrayList<Edge> worklist = this.edges;

    // sorts the list of edges by the wieght of each edge
    Collections.sort(worklist, new Comparator<Edge>() {
      @Override
      public int compare(Edge o1, Edge o2) {
        return o1.weight - o2.weight;
      }
    });

    ArrayList<Edge> edgesInTree = new ArrayList<>();

    // Kruskal’s algorithm to create the spanning tree
    int i = 0;
    while (edgesInTree.size() + 1 != this.length * this.width) {

      Edge e = worklist.get(i);
      // Pick the next cheapest edge of the graph: suppose it connects X and Y.
      if (!this.find(e.from).equals(this.find(e.to))) {
        // find(representatives, X) equals find(representatives, Y):
        // they're already connected
        // discard this edge
        edgesInTree.add(e);
        this.representatives.replace(this.find(e.from), this.find(e.to));

      }
      i++;
      // Record this edge in edgesInTree

    }

    this.edges = edgesInTree;

    // connects each cell to each of the cells that they share an edge with
    for (Edge e : edges) {
      Cell from = this.board.get(e.from.x).get(e.from.y);
      Cell to = this.board.get(e.to.x).get(e.to.y);

      if (from.xy.x + 1 == to.xy.x) {
        from.right = to;
        to.left = from;
      }

      if (from.xy.x - 1 == to.xy.x) {
        from.left = to;
        to.right = from;
      }

      if (from.xy.y - 1 == to.xy.y) {
        from.bottom = to;
        to.top = from;
      }
      if (from.xy.y + 1 == to.xy.y) {
        from.top = to;
        to.bottom = from;
      }
    }
  }

  // finds the last position where a cell can go
  Posn find(Posn p) {
    if (p.equals(this.representatives.get(p))) {
      return p;
    }
    else {
      return this.find(this.representatives.get(p));
    }

  }

  @Override
  // draws the maze
  public WorldScene makeScene() {
    // TODO Auto-generated method stub
    this.board.get(0).get(0).color = new Color(100, 255, 100); // Color.green;
    this.board.get(this.board.size() - 1).get(this.board.get(0).size() - 1).color = new Color(0,
        100, 0); // Color.orange;
    WorldScene scene = this.getEmptyScene();
    for (int i = 0; i < this.width; i++) {
      for (int j = 0; j < this.length; j++) {
        this.board.get(i).get(j).drawAt(i, j, scene);
      }
    }

    return scene;
  }

  // restarts the maze with a new maze
  public void onKeyEvent(String k) {
    if (k.equals("r")) {
      this.board = new ArrayList<>();
      this.edges = new ArrayList<>();
      this.representatives = new HashMap<>();
      this.setBoard();
      this.spanningTree();
      this.bfs = false;
      this.bfsworklist = new ArrayDeque<>();
      bfsworklist.add(this.board.get(0).get(0));
      this.colorCode = 0.0;
      this.dfs = false;
      this.dfsworklist = new Stack<>();
      this.dfsworklist.add(this.board.get(0).get(0));

      this.cameFromEdge = new HashMap<>();
      this.reconstruct = false;
    }

    if (k.equals("b")) {
      this.dfs = false;
      this.bfs = true;
    }

    if (k.equals("d")) {
      this.bfs = false;
      this.dfs = true;
    }
  }

  // performs breadth first search.
  public void breadthSearch(double colorCode) {
    // A Queue or a Stack, depending on the algorithm
    // this.worklist = new Deque<>();
    int colorC = Math.round((int) colorCode);
    Color colorbfs = new Color(0 + colorC, 255 - colorC, 0);

    while (bfsworklist.size() > 0) {
      Cell next = bfsworklist.poll();

      if (next.visited && next.color != Color.gray) {
        return;
      }
      else if (next == this.board.get(this.width - 1).get(this.length - 1)) {
        this.bfs = false;
        this.reconstruct(next.xy);
      }
      else {
        if (next.top != null) {
          bfsworklist.add(next.top);
          if (!next.top.visited) {
            cameFromEdge.put(next.top.xy, next.xy);
          }

        }
        if (next.bottom != null) {
          bfsworklist.add(next.bottom);
          if (!next.bottom.visited) {
            cameFromEdge.put(next.bottom.xy, next.xy);
          }
        }
        if (next.left != null) {
          bfsworklist.add(next.left);
          if (!next.left.visited) {
            cameFromEdge.put(next.left.xy, next.xy);
          }
        }
        if (next.right != null) {
          bfsworklist.add(next.right);
          if (!next.right.visited) {
            cameFromEdge.put(next.right.xy, next.xy);
          }
        }

      }

      next.color = colorbfs; // Color.blue;
      next.visited = true;
    }
  }

  // performs depth first search.
  public void depthSearch(double colorCode) {
    // A Queue or a Stack, depending on the algorithm
    // this.worklist = new Deque<>();

    int colorC = Math.round((int) colorCode);
    Color colordfs = new Color(0 + colorC, 0, 255 - colorC);

    while (this.dfsworklist.size() > 0) {
      Cell next = this.dfsworklist.pop();

      if (next.visited && next.color != Color.gray) {
        return;
      }
      else if (next == this.board.get(this.width - 1).get(this.length - 1)) {
        this.dfs = false;
        this.reconstruct(next.xy);
      }
      else {
        if (next.top != null) {
          dfsworklist.push(next.top);
          if (!next.top.visited) {
            cameFromEdge.put(next.top.xy, next.xy);
          }
        }
        if (next.bottom != null) {
          dfsworklist.push(next.bottom);
          if (!next.bottom.visited) {
            cameFromEdge.put(next.bottom.xy, next.xy);
          }
        }
        if (next.left != null) {
          dfsworklist.push(next.left);
          if (!next.left.visited) {
            cameFromEdge.put(next.left.xy, next.xy);
          }
        }
        if (next.right != null) {
          dfsworklist.push(next.right);
          if (!next.right.visited) {
            cameFromEdge.put(next.right.xy, next.xy);
          }
        }

      }

      next.color = colordfs;
      next.visited = true;
    }
  }

  // starts the reconstruction of the path to solve the maze
  public void reconstruct(Posn xy) {
    this.reconST = xy;
    this.reconstruct = true;

  }

  // dpending on what operation is true, performs a type of search on every tick
  public void onTick() {
    if (this.bfs) {
      if (colorCode > 254) {
        colorCode = 254;
      }
      this.colorCode += 0.25;
      this.breadthSearch(colorCode);
    }

    if (this.dfs) {
      if (colorCode > 254) {
        colorCode = 254;
      }
      this.colorCode += 0.25;
      this.depthSearch(colorCode);
    }

    if (this.reconstruct) {
      if (this.cameFromEdge.get(this.reconST) != null) {
        Cell current = this.board.get(this.reconST.x).get(this.reconST.y);
        current.color = Color.yellow;
        this.reconST = this.cameFromEdge.get(this.reconST);
      }
      else {
        this.reconstruct = false;
        this.board.get(0).get(0).color = Color.yellow;
      }
    }
  }
}

class ExamplesMaze {

  WorldScene scene;
  MazeWorld maze;
  MazeWorld maze0;
  MazeWorld maze1;
  Posn posn1;
  Posn posn0;

  void init() {
    scene = new WorldScene(0, 0);
    maze = new MazeWorld(1, 1);
    maze1 = new MazeWorld(1, 1);
    maze0 = new MazeWorld();
    posn1 = new Posn(1, 1);
    posn0 = new Posn(0, 0);
  }

  void testBigBang(Tester t) {
    init();
    MazeWorld world = new MazeWorld(60, 30);
    world.bigBang(world.width * 8 + 2, world.length * 8 + 2, 0.005);
  }

  // testing set scene method
  void testSetScene(Tester t) {
    init();
    this.maze.setBoard();
    this.maze1.setBoard();
    t.checkExpect(this.maze, this.maze1);
  }

  // testing the spanning tree method
  void testSpanningTree(Tester t) {
    init();
    this.maze.spanningTree();
    this.maze1.spanningTree();
    t.checkExpect(this.maze, this.maze1);
  }

  // testing the find method
  void testFind(Tester t) {
    init();
    t.checkExpect(this.maze.find(new Posn(0, 0)), this.posn0);
  }

  // testing the makeScene method
  void testMakeScene(Tester t) {
    init();
    t.checkExpect(this.maze.makeScene(), this.scene);
  }

  // testing the KeyEvent method
  void testonKeyEvent(Tester t) {
    init();
    this.maze.onKeyEvent("s");
    t.checkExpect(this.maze, this.maze1);
    this.maze.onKeyEvent("r");
    this.maze1.board = new ArrayList<>();
    this.maze1.edges = new ArrayList<>();
    this.maze1.representatives = new HashMap<>();
    this.maze1.setBoard();
    this.maze1.spanningTree();
    t.checkExpect(this.maze, this.maze1);
  }

  // testing the KeyEvent method
  void testonKeyEventDFSnBFS(Tester t) {
    init();
    this.maze.onKeyEvent("b");
    this.maze1.bfs = true;
    this.maze1.dfs = false;
    t.checkExpect(this.maze, this.maze1);
    this.maze.onKeyEvent("d");
    this.maze1.dfs = true;
    this.maze1.bfs = false;
    t.checkExpect(this.maze, this.maze1);
    t.checkExpect(this.maze1.bfs, false);
    t.checkExpect(this.maze1.dfs, true);
  }

  // testing the BFS method
  void testBFS(Tester t) {
    init();
    Cell nextTest = this.maze0.bfsworklist.poll();
    this.maze0.breadthSearch(0.0);
    this.maze.breadthSearch(0.0);
    t.checkExpect(this.maze.bfs, false);
    t.checkExpect(this.maze.dfs, false);
    t.checkExpect(this.maze.board.get(0).get(0).color, new Color(0, 255, 0));
    t.checkExpect(this.maze0.bfs, false);
    t.checkExpect(nextTest.color, new Color(128, 128, 128));
    t.checkExpect(nextTest.visited, false);
    t.checkExpect(this.maze0.dfs, false);
    t.checkExpect(this.maze0.board.get(0).get(0).color, new Color(128, 128, 128));
    t.checkExpect(this.maze0.board.get(2).get(4).color, new Color(128, 128, 128));
    t.checkExpect(this.maze0.board.get(10).get(10).color, new Color(128, 128, 128));
  }

  // testing the DFS method
  void testDFS(Tester t) {
    init();
    Cell nextTest = this.maze0.dfsworklist.pop();
    this.maze0.breadthSearch(0.0);
    this.maze.breadthSearch(0.0);
    t.checkExpect(this.maze.dfs, false);
    t.checkExpect(this.maze.bfs, false);
    t.checkExpect(this.maze.board.get(0).get(0).color, new Color(0, 255, 0));
    t.checkExpect(this.maze0.dfs, false);
    t.checkExpect(nextTest.color, new Color(0, 255, 0));
    t.checkExpect(nextTest.visited, true);
    t.checkExpect(this.maze0.bfs, false);
    t.checkExpect(this.maze0.board.get(0).get(0).color, new Color(0, 255, 0));

  }

  // testing the reconstruct method
  void testReconstruct(Tester t) {
    init();
    maze0.reconstruct(new Posn(10, 0));
    t.checkExpect(this.maze0.reconST, new Posn(10, 0));
    t.checkExpect(this.maze0.reconstruct, true);
    t.checkExpect(this.maze1.reconST, this.posn0);
    t.checkExpect(this.maze1.reconstruct, false);

  }

  // testing the OnTick method
  void testOnTick(Tester t) {
    init();
    this.maze0.onTick();
    t.checkExpect(this.maze0.cameFromEdge.get(this.maze0.reconST), null);
    t.checkExpect(this.maze0.bfs, false);
    t.checkExpect(this.maze0.dfs, false);
    t.checkExpect(this.maze0.reconstruct, false);
    t.checkExpect(this.maze0.board.get(0).get(0).color, new Color(128, 128, 128));
    this.maze1.onTick();
    t.checkExpect(this.maze1.cameFromEdge.get(this.maze0.reconST), null);
    t.checkExpect(this.maze1.bfs, false);
    t.checkExpect(this.maze1.dfs, false);
    this.maze1.bfs = true;
    this.maze1.colorCode = 260;
    this.maze1.onTick();
    t.checkExpect(this.maze1.colorCode, 254.25);
    this.maze0.dfs = true;
    this.maze0.colorCode = 260;
    this.maze0.onTick();
    t.checkExpect(this.maze0.colorCode, 254.25);
    this.maze0.cameFromEdge.put(this.posn0, this.posn0);
    t.checkExpect(this.maze0.cameFromEdge.get(this.maze0.reconST), this.posn0);
    t.checkExpect(this.maze0.reconstruct, false);
    t.checkExpect(this.maze0.board.get(0).get(0).color, new Color(254, 0, 1));

  }

}