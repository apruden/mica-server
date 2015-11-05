package org.obiba.mica.web.model;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.obiba.mica.dataset.domain.HarmonizationDataset;
import org.obiba.mica.dataset.domain.StudyDataset;
import org.obiba.mica.file.AttachmentState;
import org.obiba.mica.network.domain.Network;
import org.obiba.mica.security.service.SubjectAclService;
import org.obiba.mica.study.domain.Study;
import org.obiba.mica.study.domain.StudyState;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

@Component
class PermissionsDtos {

  @Inject
  private SubjectAclService subjectAclService;

  private PermissionsDtos() {}

  public Mica.PermissionsDto asDto(@NotNull  Network network) {
    return asDto("/draft/network", network.getId());
  }

  public Mica.PermissionsDto asDto(@NotNull Study study) {
    return asDto("/draft/study", study.getId());
  }

  public Mica.PermissionsDto asDto(StudyState studyState) {
    return asDto("/draft/study", studyState.getId());
  }

  public Mica.PermissionsDto asDto(StudyDataset  dataset) {
    return asDto("/draft/study-dataset", dataset.getId());
  }

  public Mica.PermissionsDto asDto(HarmonizationDataset dataset) {
    return asDto("/draft/harmonization-dataset", dataset.getId());
  }

  public Mica.PermissionsDto asDto(@NotNull AttachmentState state) {
    return asDto("/draft/file", state.getFullPath());
  }

  public Mica.PermissionsDto asDto(@NotNull String resource, @Nullable String instance) {
    Mica.PermissionsDto.Builder builder = Mica.PermissionsDto.newBuilder();

    if (Strings.isNullOrEmpty(instance)) {
      builder.setAdd(subjectAclService.isPermitted(resource, "ADD"));
    } else {
      builder.setView(subjectAclService.isPermitted(resource, "VIEW", instance)) //
        .setEdit(subjectAclService.isPermitted(resource, "EDIT", instance)) //
        .setDelete(subjectAclService.isPermitted(resource, "DELETE", instance)) //
        .setPublish(subjectAclService.isPermitted(resource, "PUBLISH", instance));
    }

    return builder.build();
  }
}