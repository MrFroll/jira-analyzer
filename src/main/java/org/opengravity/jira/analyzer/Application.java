package org.opengravity.jira.analyzer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.opengravity.jira.analyzer.core.CannotAnalyzeException;
import org.opengravity.jira.analyzer.cohesion.CohesionAnalyzer;
import org.opengravity.jira.analyzer.cohesion.CohesionInjector;
import org.opengravity.jira.analyzer.cohesion.Mode;
import org.opengravity.jira.analyzer.utils.CannotReadPropertiesException;
import org.opengravity.jira.analyzer.utils.Common;

public class Application {

  public static void main(String[] args)
      throws CannotAnalyzeException, CannotReadPropertiesException {
    Configuration config = Common.readConfigurations();
    final Injector injector = Guice.createInjector(new CohesionInjector(Mode.PROD, config));
    final CohesionAnalyzer analyzer = injector.getInstance(CohesionAnalyzer.class);
    analyzer.analyze(config);
  }
}
