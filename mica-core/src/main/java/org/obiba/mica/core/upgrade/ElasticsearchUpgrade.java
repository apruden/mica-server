/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.mica.core.upgrade;

import javax.inject.Inject;

import org.obiba.mica.contact.event.IndexContactsEvent;
import org.obiba.mica.dataset.event.IndexDatasetsEvent;
import org.obiba.mica.file.event.IndexFilesEvent;
import org.obiba.mica.micaConfig.service.CacheService;
import org.obiba.mica.network.event.IndexNetworksEvent;
import org.obiba.mica.study.event.IndexStudiesEvent;
import org.obiba.runtime.Version;
import org.obiba.runtime.upgrade.UpgradeStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;

@Component
public class ElasticsearchUpgrade implements UpgradeStep {
  private static final Logger log = LoggerFactory.getLogger(ElasticsearchUpgrade.class);

  @Inject
  private CacheService cacheService;

  @Inject
  private EventBus eventBus;

  @Override
  public String getDescription() {
    return "Upgraded search engine.";
  }

  @Override
  public Version getAppliesTo() {
    return new Version("1.2");
  }

  @Override
  public void execute(Version version) {
    log.info("Rebuild elasticsearch indices.");
    cacheService.clearOpalTaxonomiesCache();
    eventBus.post(new IndexStudiesEvent());
    eventBus.post(new IndexFilesEvent());
    eventBus.post(new IndexContactsEvent());
    eventBus.post(new IndexNetworksEvent());
    eventBus.post(new IndexDatasetsEvent());
  }
}
