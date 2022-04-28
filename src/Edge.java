import java.util.Random;
import javalib.worldimages.Posn;

// represents an undirected edge in a graph, a connection between two
// cells
class Edge {

  // Coordinates of the first cell in the edge
  Posn from;

  // Coordinates of the second cell in the edge
  Posn to;

  // the weight of the edge between these two cells
  int weight;
  Random rand;

  Edge(Posn from, Posn to) {
    this.from = from;
    this.to = to;
    this.rand = new Random();
    this.weight = this.rand.nextInt(100); // generates a random weight
  }

}