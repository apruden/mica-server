/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.mica.study.domain;

import javax.validation.constraints.NotNull;

import org.obiba.mica.core.domain.EntityState;
import org.obiba.mica.core.domain.LocalizedString;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class StudyState extends EntityState {

  private static final long serialVersionUID = -4271967393906681773L;

  @Override
  public String pathPrefix() {
    return "studies";
  }

  @NotNull
  private LocalizedString name;

  public LocalizedString getName() {
    return name;
  }

  public void setName(LocalizedString name) {
    this.name = name;
  }
}
