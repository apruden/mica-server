/*
 * Copyright (c) 2015 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.obiba.mica.web.model;

import javax.validation.constraints.NotNull;

import org.obiba.mica.core.domain.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentDtos {

  @NotNull
  Mica.CommentDto asDto(@NotNull Comment comment) {
    return Mica.CommentDto.newBuilder() //
      .setId(comment.getId()) //
      .setAuthor(comment.getAuthor()) //
      .setMessage(comment.getMessage()) //
      .setClassId(comment.getClassId()) //
      .setTimestamps(TimestampsDtos.asDto(comment)) //
      .build(); //
  }

  @NotNull
  Comment fromDto(@NotNull Mica.CommentDtoOrBuilder dto) {
    Comment comment = Comment.newBuilder() //
      .id(dto.getId()) //
      .author(dto.getAuthor()) //
      .message(dto.getMessage()) //
      .classId(dto.getClassId()) //
      .build();

    if(dto.hasTimestamps()) TimestampsDtos.fromDto(dto.getTimestamps(), comment);

    return comment;
  }
}
