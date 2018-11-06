package org.opengravity.jira.analyzer.graph;

import java.util.Objects;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class Vertex {

  private final Graph<Vertex, DefaultWeightedEdge> graph;
  private final String name;
  private int weight;

  public Vertex(String name, Graph<Vertex, DefaultWeightedEdge> graph) {
    this.name = name;
    this.graph = graph;
  }

  public int getWeight() {
    return weight;
  }

  public String getName() {
    return name;
  }

  public void increment() {
    weight++;
  }

  public void link(Vertex anohter) {
    if (graph.getEdge(this, anohter) == null) {
      graph.addEdge(this, anohter);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Vertex vertex = (Vertex) o;
    return Objects.equals(name, vertex.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public String toString() {
    return "V" + name + "/[" + weight + "]";
  }
}
