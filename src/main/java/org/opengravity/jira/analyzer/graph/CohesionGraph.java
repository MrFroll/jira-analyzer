package org.opengravity.jira.analyzer.graph;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphMLExporter;
import org.opengravity.jira.analyzer.Configuration;
import org.opengravity.jira.analyzer.domain.Issue;

public class CohesionGraph {

  private SimpleWeightedGraph<Vertex, DefaultWeightedEdge> graph;
  private ArrayList<Issue> issues;
  private Map<String, Vertex> name2Vertex;
  private List<String> components;

  public void exportToGraphML(Configuration config) throws CannotExportGraphException {
    GraphMLExporter<Vertex, DefaultWeightedEdge> exporter = new GraphMLExporter<>();
    exporter.setVertexLabelProvider(Vertex::getName);
    exporter.setVertexLabelAttributeName("label");
    exporter.setExportEdgeWeights(true);
    exporter.setEdgeWeightAttributeName("weight");
    try {
      exporter.exportGraph(graph, new FileWriter(config.getGraphMl()));
    } catch (IOException | ExportException e) {
      throw new CannotExportGraphException(e);
    }
  }

  public void build(List<Issue> issues) {
    initializeObjectFields(issues);
    createVertexes();
    createEdges();
    weightGraph();
    normalize();
  }

  private void normalize() {
    for (DefaultWeightedEdge edge : graph.edgeSet()) {
      final Vertex source = graph.getEdgeSource(edge);
      final Vertex target = graph.getEdgeTarget(edge);
      final double weight = graph.getEdgeWeight(edge);

      final double normalizedWeight = 2 * weight / (source.getWeight() + target.getWeight());
      graph.setEdgeWeight(edge, normalizedWeight);
    }
  }

  private void weightGraph() {
    skipEdgeWeightsToZero();
    issues.forEach(issue -> weightIssue(issue.getComponents()));
  }

  private void skipEdgeWeightsToZero() {
    graph.edgeSet().forEach(edge -> graph.setEdgeWeight(edge, 0));
  }

  private void weightIssue(Set<String> components) {
    List<Vertex> vertexes = components
        .stream()
        .map(name2Vertex::get)
        .peek(Vertex::increment)
        .collect(Collectors.toList());
    for (int i = 0; i < vertexes.size(); i++) {
      for (int j = i + 1; j < vertexes.size(); j++) {
        DefaultWeightedEdge edge = graph.getEdge(vertexes.get(i), vertexes.get(j));
        graph.setEdgeWeight(edge, graph.getEdgeWeight(edge) + 1);
      }
    }
  }

  private void initializeObjectFields(List<Issue> issues) {
    this.issues = new ArrayList<>(issues);
    this.components = new ArrayList<>();
    this.graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
    this.name2Vertex = new HashMap<>();
    issues.forEach(item -> this.components.addAll(item.getComponents()));
  }

  private void createEdges() {
    issues.forEach(issue -> linkComponents(issue.getComponents()));
  }

  private void linkComponents(Set<String> components) {
    List<Vertex> vertexes = components
        .stream()
        .map(name2Vertex::get)
        .collect(Collectors.toList());
    for (int i = 0; i < vertexes.size(); i++) {
      for (int j = i + 1; j < vertexes.size(); j++) {
        vertexes.get(i).link(vertexes.get(j));
      }
    }
  }

  private void createVertexes() {
    components.stream().map(this::createVertex).forEach(graph::addVertex);
  }

  private Vertex createVertex(String name) {
    final Vertex vertex = name2Vertex.computeIfAbsent(name, key -> new Vertex(key, graph));
    graph.addVertex(vertex);
    return vertex;
  }

  public double weight(String left, String right) {
    Vertex leftVertex = name2Vertex.get(left);
    Vertex rightVertex = name2Vertex.get(right);
    DefaultWeightedEdge edge = graph.getEdge(leftVertex, rightVertex);
    if (edge == null) {
      throw new EdgeNotExistsException(leftVertex + "-" + rightVertex);
    }
    return graph.getEdgeWeight(edge);
  }
}
