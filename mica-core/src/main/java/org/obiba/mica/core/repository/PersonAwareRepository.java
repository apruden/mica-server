/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.mica.core.repository;

import java.util.List;

import org.obiba.mica.contact.event.PersonUpdatedEvent;
import org.obiba.mica.core.domain.PersonAware;
import org.obiba.mica.core.domain.Person;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;

public interface PersonAwareRepository<T extends PersonAware> {

  PersonRepository getPersonRepository();

  EventBus getEventBus();

  default void saveContacts(T obj) {
    updateRemovedContacts(obj);
    obj.getAllMemberships().forEach(c -> obj.addToPerson(c));
    List<Person> persons = obj.getAllPersons();
    getPersonRepository().save(persons);
    persons.forEach(p -> getEventBus().post(new PersonUpdatedEvent(p)));
  }

  default void deleteContacts(T obj) {
    obj.getAllPersons().forEach(c -> obj.removeFromPerson(c));
    getPersonRepository().save(obj.getAllPersons());
  }

  default void updateRemovedContacts(T obj) {
    List<Person> persons = findAllPersonsByParent(obj);
    Sets.SetView<Person> removedPersons = Sets
      .difference(Sets.newHashSet(persons), Sets.newHashSet(obj.getAllPersons()));
    removedPersons.forEach(c -> obj.removeFromPerson(c));
    getPersonRepository().save(removedPersons);
    removedPersons.forEach(p -> getEventBus().post(new PersonUpdatedEvent(p)));
  }

  List<Person> findAllPersonsByParent(T obj);
}
