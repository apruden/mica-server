package org.obiba.mica.service;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import javax.inject.Inject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.obiba.git.command.GitCommandHandler;
import org.obiba.mica.domain.Study;
import org.obiba.mica.domain.StudyState;
import org.obiba.mica.event.StudyUpdatedEvent;
import org.obiba.mica.repository.StudyStateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.google.common.eventbus.EventBus;
import com.google.common.io.Files;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.obiba.mica.assertj.Assertions.assertThat;
import static org.obiba.mica.domain.LocalizedString.en;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
@ContextConfiguration(classes = StudyServiceTest.Config.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StudyServiceTest {

  private static final Logger log = LoggerFactory.getLogger(StudyServiceTest.class);


  @Inject
  private StudyService studyService;

  @Inject
  private StudyStateRepository studyStateRepository;

  @Inject
  private EventBus eventBus;

  @Inject
  private MongoTemplate mongoTemplate;

  @BeforeClass
  public static void init() {
    SecurityUtils.setSecurityManager(new DefaultSecurityManager());
  }

  @Before
  public void clearDatabase() {
    mongoTemplate.getDb().dropDatabase();
  }

  @Test
  public void test_create_and_load_new_study() throws Exception {

    Study study = new Study();
    study.setName(en("name en").forFr("name fr"));
    studyService.save(study);

    List<StudyState> studyStates = studyStateRepository.findAll();
    log.info(">>> studyStates: {}", studyStates);
    assertThat(studyStates).hasSize(1);

    StudyState studyState = studyStates.get(0);
    assertThat(studyState.getId()) //
        .isNotEmpty() //
        .isEqualTo(study.getId());
    assertThat(studyState.getName()).isEqualTo(study.getName());

    verify(eventBus).post(any(StudyUpdatedEvent.class));
    reset(eventBus);

    Study retrievedStudy = studyService.findDraftStudy(study.getId());
    assertThat(retrievedStudy).areFieldsEqualToEachOther(study);
  }

  @Test
  public void test_update_study() throws Exception {

    Study study = new Study();
    study.setName(en("name en to update").forFr("name fr to update"));
    studyService.save(study);

    study.setName(en("new name en").forFr("new name fr"));
    studyService.save(study);

    List<StudyState> studyStates = studyStateRepository.findAll();
    assertThat(studyStates).hasSize(1);

    StudyState studyState = studyStates.get(0);
    assertThat(studyState.getId()) //
        .isNotEmpty() //
        .isEqualTo(study.getId());
    assertThat(studyState.getName()).isEqualTo(study.getName());

    verify(eventBus, times(2)).post(any(StudyUpdatedEvent.class));
    reset(eventBus);

    Study retrievedStudy = studyService.findDraftStudy(study.getId());
    assertThat(retrievedStudy).areFieldsEqualToEachOther(study);
  }

  @Test
  public void test_publish_current() throws Exception {

    Study study = new Study();
    study.setName(en("name en to update").forFr("name fr to update"));
    studyService.save(study);

    log.debug("all: {}", studyService.findAllStates());
    log.debug("publish: {}", studyService.findPublishedStates());

    assertThat(studyService.findAllStates()).hasSize(1);
    assertThat(studyService.findPublishedStates()).isEmpty();

    studyService.publish(study.getId());
    List<StudyState> publishedStates = studyService.findPublishedStates();
    assertThat(publishedStates).hasSize(1);
    StudyState publishedState = publishedStates.get(0);
    assertThat(publishedState.getId()).isEqualTo(study.getId());
    assertThat(publishedState.getPublishedTag()).isEqualTo("1");

    assertThat(studyService.findPublishedStudy(study.getId())).areFieldsEqualToEachOther(study);

  }

  @Configuration
  @EnableMongoRepositories("org.obiba.mica.repository")
  static class Config extends AbstractMongoConfiguration {

    static final File BASE_REPO = Files.createTempDir();

    static {
      BASE_REPO.deleteOnExit();
    }



    @Bean
    public PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
      return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public StudyService studyService() {
      return new StudyService();
    }

    @Bean
    public GitService gitService() throws IOException {
      GitService gitService = new GitService();
      gitService.setRepositoriesRoot(BASE_REPO);
      return gitService;
    }

    @Bean
    public GitCommandHandler gitCommandHandler() throws IOException {
      return new GitCommandHandler();
    }

    @Bean
    public EventBus eventBus() {
      return mock(EventBus.class);
    }

    @Override
    protected String getDatabaseName() {
      return "mica-test";
    }

    @Override
    public Mongo mongo() throws UnknownHostException {
      return new MongoClient("localhost:27018");
    }

    @Override
    protected String getMappingBasePackage() {
      return "org.obiba.mica.domain";
    }

  }

}
