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

import org.obiba.mica.micaConfig.domain.NetworkConfig;
import org.obiba.mica.micaConfig.repository.NetworkConfigRepository;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class NetworkConfigService extends EntityConfigService<NetworkConfig> {

  @Inject
  NetworkConfigRepository networkConfigRepository;

  @Override
  protected NetworkConfigRepository getRepository() {
    return networkConfigRepository;
  }

  @Override
  protected String getDefaultId() {
    return "default";
  }

  @Override
  protected NetworkConfig createDefaultForm() {
    return createDefaultNetworkForm();
  }

  private NetworkConfig createDefaultNetworkForm() {
    NetworkConfig form = new NetworkConfig();
    form.setDefinition(getDefaultNetworkFormResourceAsString("definition.json"));
    form.setSchema(getDefaultNetworkFormResourceAsString("schema.json"));
    return form;
  }

  private Resource getDefaultNetworkFormResource(String name) {
    return new DefaultResourceLoader().getResource("classpath:config/network-form/" + name);
  }

  private String getDefaultNetworkFormResourceAsString(String name) {
    try(Scanner s = new Scanner(getDefaultNetworkFormResource(name).getInputStream())) {
      return s.useDelimiter("\\A").hasNext() ? s.next() : "";
    } catch(IOException e) {
      return "";
    }
  }
}
