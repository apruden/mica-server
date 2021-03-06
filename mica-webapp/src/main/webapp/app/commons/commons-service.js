/*
 * Copyright (c) 2016 OBiBa. All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

'use strict';

mica.commons

  .factory('CommentsResource', ['$resource',
    function ($resource) {
      return $resource('/ws/draft/:type/:id/comments', {}, {
        'save': {
          method: 'POST',
          params: {type: '@type', id: '@id'},
          headers : {'Content-Type' : 'text/plain' },
          errorHandler: true
        },
        'get': {method: 'GET', params: {type: '@type', id: '@id'}, errorHandler: true}
      });
    }])

  .factory('CommentResource', ['$resource',
    function ($resource) {
      return $resource('/ws/draft/:type/:id/comment/:commentId', {}, {
        'delete': {
          method: 'DELETE',
          params: {type: '@type', id: '@id', commentId: '@commentId'},
          errorHandler: true
        },
        'update': {
          method: 'PUT',
          params: {type: '@type', id: '@id', commentId: '@commentId'},
          headers : {'Content-Type' : 'text/plain' },
          errorHandler: true
        }
      });
    }])

  .factory('DocumentPermissionsService',
    function () {
      var factory = {};

      factory.state = function(value) {
        this.permissions = value.permissions;
        this.status = value.revisionStatus;
        return this;
      };

      factory.canView = function() {
        return this.permissions ? this.permissions.view : false;
      };

      factory.canEdit = function() {
        return this.permissions ? this.permissions.edit && this.status === 'DRAFT': false;
      };

      factory.canDelete = function() {
        return this.permissions ? this.permissions.delete && this.status === 'DELETED' : false;
      };

      factory.canPublish = function() {
        return this.permissions ? this.permissions.publish && this.status === 'UNDER_REVIEW': false;
      };

      return factory;
    });
