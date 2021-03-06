/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.mica;

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.obiba.mica.config.Profiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.MetricFilterAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.MetricRepositoryAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;

@ComponentScan("org.obiba")
@EnableAutoConfiguration(exclude = { MetricFilterAutoConfiguration.class, MetricRepositoryAutoConfiguration.class,
  SecurityAutoConfiguration.class })
public class Application {

  private static final Logger log = LoggerFactory.getLogger(Application.class);

  @Inject
  private Environment env;

  /**
   * Initializes mica.
   * <p/>
   * Spring profiles can be configured with a program arguments --spring.profiles.active=your-active-profile
   * <p/>
   */
  @PostConstruct
  public void initApplication() {
    if(env.getActiveProfiles().length == 0) {
      log.warn("No Spring profile configured, running with default configuration");
    } else {
      log.info("Running with Spring profile(s) : {}", Arrays.toString(env.getActiveProfiles()));
    }
  }

  /**
   * Main method, used to run the application.
   * <p/>
   * To run the application with hot reload enabled, add the following arguments to your JVM:
   * "-javaagent:spring_loaded/springloaded-jhipster.jar -noverify -Dspringloaded=plugins=io.github.jhipster.loaded.instrument.JHipsterLoadtimeInstrumentationPlugin"
   */
  public static void main(String... args) throws InterruptedException {

    checkSystemProperty("MICA_HOME");

    SpringApplication app = new SpringApplication(Application.class);
    app.setShowBanner(false);

    SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);

    // Check if the selected profile has been set as argument.
    // if not the development profile will be added
    addDefaultProfile(app, source);

    app.run(args);
  }

  private static void checkSystemProperty(@NotNull String... properties) {
    for(String property : properties) {
      if(System.getProperty(property) == null) {
        throw new IllegalStateException("System property \"" + property + "\" must be defined.");
      }
    }
  }

  /**
   * Set a default profile if it has not been set
   */
  private static void addDefaultProfile(SpringApplication app, SimpleCommandLinePropertySource source) {
    if(!source.containsProperty("spring.profiles.active") && System.getProperty("spring.profiles.active") == null) {
      app.setAdditionalProfiles(Profiles.PROD);
    }
  }

}
