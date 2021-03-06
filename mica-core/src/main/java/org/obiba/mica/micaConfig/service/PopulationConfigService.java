/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.mica.micaConfig.service;

import java.io.IOException;
import java.util.Scanner;

import javax.inject.Inject;

import org.obiba.mica.micaConfig.domain.PopulationConfig;
import org.obiba.mica.micaConfig.repository.PopulationConfigRepository;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class PopulationConfigService extends EntityConfigService<PopulationConfig> {

  @Inject
  PopulationConfigRepository populationConfigRepository;

  @Override
  protected PopulationConfigRepository getRepository() {
    return populationConfigRepository;
  }

  @Override
  protected String getDefaultId() {
    return "default";
  }

  @Override
  protected PopulationConfig createDefaultForm() {
    return createDefaultPopulationForm();
  }

  private PopulationConfig createDefaultPopulationForm() {
    PopulationConfig form = new PopulationConfig();
    form.setDefinition(getDefaultPopulationFormResourceAsString("definition.json"));
    form.setSchema(getDefaultPopulationFormResourceAsString("schema.json"));
    return form;
  }

  private Resource getDefaultPopulationFormResource(String name) {
    return new DefaultResourceLoader().getResource("classpath:config/population-form/" + name);
  }

  private String getDefaultPopulationFormResourceAsString(String name) {
    try(Scanner s = new Scanner(getDefaultPopulationFormResource(name).getInputStream())) {
      return s.useDelimiter("\\A").hasNext() ? s.next() : "";
    } catch(IOException e) {
      return "";
    }
  }
}
