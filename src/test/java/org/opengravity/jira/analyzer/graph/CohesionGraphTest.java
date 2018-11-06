package org.opengravity.jira.analyzer.graph;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CohesionGraphTest extends IssueHolder {

  @Before
  public void setUp() {
    super.setUp();
  }

  @After
  public void tearDown() {
    super.tearDown();
  }

  @Test
  public void weightBetweenLinkedVertexes() {
    issue("issue 1").withComponents("component 1", "component 2");
    buildGraph();
    final double weight = weight("component 1", "component 2");
    Assert.assertEquals(1, weight, 0.0001);
  }

  @Test(expected = EdgeNotExistsException.class)
  public void weightBetweenUnlinkedVertexes() {
    issue("issue 1").withComponents("component 1", "component 2");
    issue("issue 2").withComponents("component 3", "component 4");
    buildGraph();
    final double weight = weight("component 1", "component 3");
    Assert.assertEquals(0, weight, 0.0001);
  }

  @Test
  public void weightBetweenMultilinkedVertexes() {
    issue("issue 1").withComponents("component 1", "component 2");
    issue("issue 2").withComponents("component 1", "component 2", "component 3");
    issue("issue 3").withComponents("component 2", "component 3");
    buildGraph();
    final double weight = weight("component 1", "component 3");
    Assert.assertEquals(0.5, weight, 0.0001);
  }

}