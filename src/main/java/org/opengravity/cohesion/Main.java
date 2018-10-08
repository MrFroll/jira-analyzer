package org.opengravity.cohesion;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphMLExporter;
import org.opengravity.cohesion.extractor.Issue;
import org.opengravity.cohesion.extractor.IssuesExtractor;
import org.opengravity.cohesion.extractor.JiraException;
import org.opengravity.cohesion.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  private static Map<Integer, List<Pair>> indexes = new HashMap<>();

  public static void main(String[] args)
      throws JiraException, IOException, URISyntaxException, ExportException {

    if (args.length != 0) {
      throw new IllegalArgumentException("Property file expected as an argument");
    }
    Configuration config = readConfigurations();
    List<Issue> issues = extractIssues(config);
    Graph<String, DefaultWeightedEdge> cohesionGraph = createCohesionGraph(issues);
    exportToGraphML(config, cohesionGraph);
  }

  private static void exportToGraphML(Configuration config,
      Graph<String, DefaultWeightedEdge> cohesionGraph) throws IOException, ExportException {
    GraphMLExporter<String, DefaultWeightedEdge> exporter = new GraphMLExporter<>();
    exporter.setVertexLabelProvider(s -> s);
    exporter.setVertexLabelAttributeName("label");
    exporter.setExportEdgeWeights(true);
    exporter.setEdgeWeightAttributeName("weight");
    exporter.exportGraph(cohesionGraph, new FileWriter(config.getGraphMl()));
  }

  private static List<Issue> extractIssues(Configuration config) throws IOException, JiraException {
    List<Issue> jiraIssues = readIssuesFromDump(config);
    if (jiraIssues.isEmpty()) {
      jiraIssues = readIssuesFromJira(config);
      log.info("Got from jira {} issues", jiraIssues.size());
      dumpIssues(jiraIssues, config.getDump());
    } else {
      log.info("Got from file {} issues", jiraIssues.size());
    }
    return jiraIssues;
  }

  private static List<Issue> readIssuesFromJira(Configuration configuration) throws JiraException {
    List<Issue> jiraIssues;
    IssuesExtractor extractor = new IssuesExtractor(configuration);
    jiraIssues = extractor.extract();
    return jiraIssues;
  }

  private static List<Issue> readIssuesFromDump(Configuration configuration) throws IOException {
    final File file = new File(configuration.getDump());
    if (file.exists() && !file.isDirectory()) {
      final String data = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
      final Type type = new TypeToken<List<Issue>>() {
      }.getType();
      return new Gson().fromJson(data, type);
    }
    return new ArrayList<>();
  }

  private static Configuration readConfigurations() throws IOException, URISyntaxException {
    final Properties properties = new Properties();
    properties.load(ClassLoader.class.getResourceAsStream("/cohesion-visualizer.properties"));
    return new Configuration(properties);
  }

  private static Graph<String, DefaultWeightedEdge> createCohesionGraph(List<Issue> jiraIssues) {
    SimpleWeightedGraph<String, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(
        DefaultWeightedEdge.class);
    Set<String> components = new HashSet<>();
    jiraIssues.forEach(item -> components.addAll(item.getComponents()));
    components.forEach(graph::addVertex);

    for (Issue jiraIssue : jiraIssues) {
      final List<String> jiraComponents = jiraIssue.getComponents();
      final List<Pair> pairs = indexes
          .computeIfAbsent(jiraComponents.size(), Main::calculate);
      pairs.forEach(
          p -> addEdge(graph, p, jiraComponents));
    }
    return graph;
  }

  private static void addEdge(SimpleWeightedGraph<String, DefaultWeightedEdge> graph, Pair p,
      List<String> jiraComponents) {
    final String firstVertex = jiraComponents.get(p.getFirst());
    final String secondVertex = jiraComponents.get(p.getSecond());
    DefaultWeightedEdge edge = graph.getEdge(firstVertex, secondVertex);
    if (edge != null) {
      double weight = graph.getEdgeWeight(edge);
      graph.setEdgeWeight(edge, weight + 1);
    } else {
      edge = graph.addEdge(firstVertex, secondVertex);
      graph.setEdgeWeight(edge, 1);
    }
  }

  private static List<Pair> calculate(Integer size) {
    List<Pair> pairs = new ArrayList<>();
    for (int i = 0; i < size; i++) {
      for (int j = i + 1; j < size; j++) {
        pairs.add(new Pair(i, j));
      }
    }
    return pairs;
  }

  private static void dumpIssues(List<Issue> issues, String dumpFile) throws IOException {
    final File file = new File(dumpFile);
    final String data = new Gson().toJson(issues);
    FileUtils.writeStringToFile(file, data, StandardCharsets.UTF_8);
  }
}
