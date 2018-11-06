package org.opengravity.jira.analyzer.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;
import org.opengravity.jira.analyzer.Configuration;

public class Common {

  private Common() {
  }

  public static Configuration readConfigurations() throws CannotReadPropertiesException {
    try {
      final Properties properties = new Properties();
      properties.load(ClassLoader.class.getResourceAsStream("/cohesion-visualizer.properties"));
      return new Configuration(properties);
    } catch (IOException | URISyntaxException e) {
      throw new CannotReadPropertiesException(e);
    }
  }

}
