package org.obiba.mica.access.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.obiba.mica.core.domain.AbstractAuditableDocument;
import org.obiba.mica.core.domain.AttachmentAware;
import org.obiba.mica.file.Attachment;
import org.obiba.mica.file.PersistableWithAttachments;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 *
 */
public class DataAccessRequest extends AbstractAuditableDocument
  implements AttachmentAware, PersistableWithAttachments {

  private static final long serialVersionUID = -6728220507676973832L;

  /**
   * User name of the user making the request.
   */
  @NotNull
  private String applicant;

  /**
   * Json string containing the request data.
   */
  private String content;

  private Status status = Status.OPENED;

  private List<Attachment> attachments;

  private List<StatusChange> statusChangeHistory;

  //
  // Accessors
  //

  public String getApplicant() {
    return applicant;
  }

  public void setApplicant(String applicant) {
    this.applicant = applicant;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public boolean hasContent() {
    return !Strings.isNullOrEmpty(content);
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  //
  // Attachments
  //

  @Override
  @NotNull
  public List<Attachment> getAttachments() {
    if(attachments == null) attachments = new ArrayList<>();
    return attachments;
  }

  @Override
  public boolean hasAttachments() {
    return attachments != null && !attachments.isEmpty();
  }

  @Override
  public void addAttachment(@NotNull Attachment attachment) {
    getAttachments().add(attachment);
  }

  @Override
  public void setAttachments(List<Attachment> attachments) {
    this.attachments = attachments;
  }

  @JsonIgnore
  @Override
  public Iterable<Attachment> getAllAttachments() {
    return () -> getAttachments().stream().filter(a -> a != null).iterator();
  }

  @Override
  public Attachment findAttachmentById(String attachmentId) {
    return getAttachments().stream().filter(a -> a != null && a.getId().equals(attachmentId)).findAny().orElse(null);
  }

  public boolean hasStatusChangeHistory() {
    return statusChangeHistory != null && !statusChangeHistory.isEmpty();
  }

  public List<StatusChange> getStatusChangeHistory() {
    if (statusChangeHistory == null) statusChangeHistory = Lists.newArrayList();
    return statusChangeHistory;
  }

  public void addAttachment(@NotNull StatusChange statusChange) {
    getStatusChangeHistory().add(statusChange);
  }

  public void setStatusChangeHistory(List<StatusChange> statusChangeHistory) {
    this.statusChangeHistory = statusChangeHistory;
  }

  @Override
  public String pathPrefix() {
    return null;
  }

  @Override
  public Map<String, Serializable> parts() {
    return null;
  }

  //
  // Inner classes and enums
  //

  public enum Status {
    OPENED,     // request is being edited by the applicant
    SUBMITTED, // request is submitted by the applicant, ready for review
    REVIEWED,  // request is being reviewed
    APPROVED,  // request was reviewed and approved
    REJECTED   // request was reviewed and rejected
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static class Builder {
    private DataAccessRequest request;

    public Builder() {
      request = new DataAccessRequest();
    }

    public Builder applicant(String applicant) {
      request.applicant = applicant;
      return this;
    }

    public Builder status(String status) {
      request.status = Status.valueOf(status.toUpperCase());
      return this;
    }

    public Builder content(String content) {
      request.content = content;
      return this;
    }

    public DataAccessRequest build() {
      return request;
    }
  }

}