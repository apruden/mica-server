package org.obiba.mica.file.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.obiba.mica.core.domain.RevisionStatus;
import org.obiba.mica.file.Attachment;
import org.obiba.mica.file.FileService;
import org.obiba.mica.web.model.Mica;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

@Component
@Path("/draft")
@RequiresPermissions({ "/draft:EDIT" })
public class DraftFileSystemResource extends AbstractFileSystemResource {

  @Inject
  private FileService fileService;

  @Override
  protected boolean isPublished() {
    return false;
  }

  @GET
  @Path("/file-dl/{path:.*}")
  public Response downloadFile(@PathParam("path") String path, @QueryParam("version") String version) {
    Attachment attachment = doGetAttachment(path);
    return Response.ok(fileService.getFile(attachment.getId()))
      .header("Content-Disposition", "attachment; filename=\"" + attachment.getName() + "\"").build();
  }

  @GET
  @Path("/file/{path:.*}")
  public Mica.FileDto getFile(@PathParam("path") String path) {
    return doGetFile(path);
  }

  @DELETE
  @Path("/file/{path:.*}")
  public Response deleteFile(@PathParam("path") String path) {
    doDeleteFile(path);
    return Response.noContent().build();
  }

  @PUT
  @Path("/file/{path:.*}")
  public Response updateFile(@PathParam("path") String path, @QueryParam("status") String status,
    @QueryParam("publish") Boolean publish, @QueryParam("name") String newName, @QueryParam("move") String movePath,
    @QueryParam("copy") String copyPath) {

    if(!Strings.isNullOrEmpty(copyPath)) {
      if(!Strings.isNullOrEmpty(movePath))
        throw new IllegalArgumentException("Copy and move are mutually exclusive operations");
      if(!Strings.isNullOrEmpty(newName))
        throw new IllegalArgumentException("Copy and rename are mutually exclusive operations");
      doCopyFile(path, copyPath);
    } else if(!Strings.isNullOrEmpty(movePath)) {
      if(!Strings.isNullOrEmpty(newName))
        throw new IllegalArgumentException("Move and rename are mutually exclusive operations");
      doMoveFile(path, movePath);
    } else if(!Strings.isNullOrEmpty(newName)) doRenameFile(path, newName);

    if(publish != null) doPublishFile(path, publish);
    if (!Strings.isNullOrEmpty(status)) doUpdateStatus(path, RevisionStatus.valueOf(status.toUpperCase()));

    return Response.noContent().build();
  }

  @POST
  @Path("/files")
  public Response addFile(Mica.AttachmentDto attachmentDto, @Context UriInfo uriInfo) {
    doAddFile(attachmentDto);
    return Response
      .created(uriInfo.getBaseUriBuilder().path("draft").path("file").path(attachmentDto.getPath()).build()).build();
  }

  @GET
  @Path("/files-search/{path:.*}")
  public List<Mica.FileDto> searchFile(@PathParam("path") String path, @QueryParam("query") String query,
    @QueryParam("recursively") @DefaultValue("false") boolean recursively) {
    return doSearchFiles(path, query, recursively);
  }

}