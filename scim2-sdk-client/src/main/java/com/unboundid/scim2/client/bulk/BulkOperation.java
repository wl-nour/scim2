/*
 * Copyright 2023 Ping Identity Corporation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (GPLv2 only)
 * or the terms of the GNU Lesser General Public License (LGPLv2.1 only)
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 */

package com.unboundid.scim2.client.bulk;

import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.types.ETagConfig;
import jakarta.ws.rs.HttpMethod;

import java.net.http.HttpRequest;


/**
 * This class represents a single operation that can be included in a bulk
 * request. A bulk request is composed of one or more BulkOperations.
 * <br><br>
 * A bulk operation contains the following pieces of data:
 * <ul>
 *   <li> {@code method}: The HTTP method (e.g., POST, PUT) that should be used
 *        for the request.
 *   <li> {@code path}: The endpoint that should be targeted by the request
 *        (e.g., {@code /Users}, {@code /Groups}).
 *   <li> {@code version}: The SCIM ETag. For more information about ETags, see
 *        the documentation in {@link ETagConfig}.
 *   <li> {@code data}: The JSON body of the HTTP request.
 * </ul>
 *
 * The following is an example of an individual bulk operation that will create
 * a single user.
 * <pre>
 * {
 *   "method": "POST",
 *   "path": "/Users"
 *   "version": "W\/\"e180ee84f0671b1\""
 *   "data": {
 *     "schemas": "urn:ietf:params:scim:schemas:core:2.0:User",
 *     "userName": "Kratos"
 *   }
 * }
 * </pre>
 *
 * @see BulkRequest
 */
@Schema(id="urn:ietf:params:scim:schemas:core:2.0:BulkOperation",
        name="Bulk Operation", description="A SCIM 2 bulk operation")
public class BulkOperation extends BaseScimResource
{
  @NotNull
  public static final String BULK_ID_PREFIX = "bulkId:";

  // The HTTP operation type.
  @NotNull
  private final HttpMethod method;

  // The attribute path corresponding to the endpoint that should receive the
  // request. For example, to create a new user, the "path" parameter should
  // point to the "/Users" endpoint.
  @NotNull
  private final String path;

  // Bulk operations can have an optional "bulk ID" field so that it may be
  // referenced by other operations in the same BulkRequest. This is useful to
  // reference data that is within the bulk request and has not been created
  // in the database yet.
  //
  // For example, consider a request that creates a user and a new group that
  // contains the user. The bulk operation for the user add can include a bulkId
  // of "qwerty", and the bulk operation for the group can include a
  // "bulkId:qwerty" value in its list of members. This will indicate that the
  // previous user resource created within the bulk request should be added to
  // the group. For more information, see RFC 7644 Section 3.7.2.
  @Nullable
  private final String bulkId;

  // The optional SCIM ETag value, which can be used if the SCIM service
  // provider supports ETag versioning.
  @Nullable
  private final String version;

  // The JSON body of the request.
  @NotNull
  private final ScimResource data;

  @Nullable
  private String getBulkId()
  {
    return bulkId;
  }

  @Nullable
  private String getVersion()
  {
    return version;
  }

  @NotNull
  private ScimResource getData()
  {
    return data;
  }


//  /**
//   * Represents the HTTP operation type. The GET operation type is not included
//   * because it cannot be used for bulk operations that are designed to modify
//   * SCIM resources.
//   */
//  public enum OperationType
//  {
//    /**
//     * The HTTP POST operation type.
//     */
//    POST,
//
//    /**
//     * The HTTP PUT operation type.
//     */
//    PUT,
//
//    /**
//     * The HTTP PATCH operation type.
//     */
//    PATCH,
//
//    /**
//     * The HTTP DELETE operation type.
//     */
//    DELETE,
//  }


  /**
   * Constructs a SCIM 2 bulk operation.
   * <br><br>
   * Bulk operations can be created by using methods in this class such as
   * {@link #delete(String, String)}, as this provides a more concise way
   * of instantiating a bulk operation.
   *
   * @param opType    The type of operation (e.g., POST)
   * @param path      The path to the SCIM resource.
   * @param bulkId    The unique identifier for the resource within the bulk
   *                  operation.
   * @param version   The SCIM ETag of the resource.
   * @param data      The input data for the API request.
   */
  private BulkOperation(@NotNull final HttpMethod opType,
                        @NotNull final String path,
                        @Nullable final String bulkId,
                        @Nullable final String version,
                        @NotNull final ScimResource data)
  {
    this.method = opType;
    this.path = path;
    this.bulkId = bulkId;
    this.version = version;
    this.data = data;
  }

//  /**
//   * Creates a GET bulk operation.
//   *
//   * @param path      The path to the SCIM resource.
//   * @param bulkId    The unique identifier for the resource within the bulk
//   *                  operation.
//   * @param version   The SCIM {@code version} of the resource (similar to an ETag).
//   *
//   * @return  A new bulk operation.
//   */
//  @NotNull
//  public static BulkOperation get(@NotNull final String path,
//                                  @Nullable final String bulkId,
//                                  @NotNull final String version)
//  {
//    return new BulkOperation(OperationType.GET, path, bulkId, version, null);
//  }
//
//  /**
//   * Creates a minimal GET bulk operation.
//   *
//   * @param path  The path to the SCIM resource.
//   * @return      A new bulk operation.
//   */
//  @NotNull
//  public static BulkOperation get(@NotNull final String path)
//  {
//    return new BulkOperation(OperationType.GET, path, null, null, null);
//  }

  /**
   * Creates a POST bulk operation.
   *
   * @param path      The path to the SCIM resource.
   * @param bulkId    The unique identifier for the resource within the bulk
   *                  operation.
   * @param version   The SCIM {@code version} of the resource (similar to an ETag).
   * @param data      The JSON input data with details about the data
   *                  modification.
   *
   * @return  A new bulk operation.
   */
  @NotNull
  public static BulkOperation post(@NotNull final String path,
                                   @Nullable final String bulkId,
                                   @NotNull final String version,
                                   @NotNull final ScimResource data)
  {
    return new BulkOperation(HttpMethod.POST, path, bulkId, version, data);
  }

  /**
   * Creates a POST bulk operation without a {@code version} field.
   *
   * @param path      The path to the SCIM resource.
   * @param bulkId    The unique identifier for the resource within the bulk
   *                  operation.
   * @param data      The JSON input data with details about the data
   *                  modification.
   *
   * @return  A new bulk operation.
   */
  @NotNull
  public static BulkOperation post(@NotNull final String path,
                                   @Nullable final String bulkId,
                                   @NotNull final ScimResource data)
  {
    return new BulkOperation(OperationType.POST, path, bulkId, null, data);
  }

  /**
   * Creates a PUT bulk operation.
   *
   * @param path      The path to the SCIM resource.
   * @param bulkId    The unique identifier for the resource within the bulk
   *                  operation.
   * @param version   The SCIM {@code version} of the resource (similar to an ETag).
   * @param data      The JSON input data with details about the data
   *                  modification.
   *
   * @return  A new bulk operation.
   */
  @NotNull
  public static BulkOperation put(@NotNull final String path,
                                  @Nullable final String bulkId,
                                  @NotNull final String version,
                                  @NotNull final ScimResource data)
  {
    return new BulkOperation(OperationType.PUT, path, bulkId, version, data);
  }

  /**
   * Creates a PUT bulk operation without a {@code version} field.
   *
   * @param path      The path to the SCIM resource.
   * @param bulkId    The unique identifier for the resource within the bulk
   *                  operation.
   * @param data      The JSON input data with details about the data
   *                  modification.
   *
   * @return  A new bulk operation.
   */
  @NotNull
  public static BulkOperation put(@NotNull final String path,
                                  @Nullable final String bulkId,
                                  @NotNull final ScimResource data)
  {
    return new BulkOperation(OperationType.PUT, path, bulkId, null, data);
  }

  /**
   * Creates a PATCH bulk operation.
   *
   * @param path      The path to the SCIM resource.
   * @param bulkId    The unique identifier for the resource within the bulk
   *                  operation.
   * @param version   The SCIM {@code version} of the resource (similar to an ETag).
   * @param data      The JSON input data with details about the data
   *                  modification.
   *
   * @return  A new bulk operation.
   */
  @NotNull
  public static BulkOperation patch(@NotNull final String path,
                                    @Nullable final String bulkId,
                                    @NotNull final String version,
                                    @NotNull final ScimResource data)
  {
    return new BulkOperation(OperationType.PATCH, path, bulkId, version, data);
  }

  /**
   * Creates a PATCH bulk operation without a {@code version} field.
   *
   * @param path      The path to the SCIM resource.
   * @param bulkId    The unique identifier for the resource within the bulk
   *                  operation.
   * @param data      The JSON input data with details about the data
   *                  modification.
   *
   * @return  A new bulk operation.
   */
  @NotNull
  public static BulkOperation patch(@NotNull final String path,
                                    @Nullable final String bulkId,
                                    @NotNull final ScimResource data)
  {
    return new BulkOperation(OperationType.PATCH, path, bulkId, null, data);
  }

  /**
   * Creates a DELETE bulk operation.
   *
   * @param path      The path to the SCIM resource.
   * @param bulkId    The unique identifier for the resource within the bulk
   *                  operation.
   * @param version   The SCIM {@code version} of the resource (similar to an ETag).
   *
   * @return  A new bulk operation.
   */
  @NotNull
  public static BulkOperation delete(@NotNull final String path,
                                     @Nullable final String bulkId,
                                     @Nullable final String version)
  {
    return new BulkOperation(OperationType.DELETE, path, bulkId, version, null);
  }

  /**
   * Creates a DELETE operation without a {@code version} field.
   *
   * @param path      The path to the SCIM resource.
   * @param bulkId    The unique identifier for the resource within the bulk
   *                  operation.
   *
   * @return  A new bulk operation.
   */
  @NotNull
  public static BulkOperation delete(@NotNull final String path,
                                     @Nullable final String bulkId)

  {
    return new BulkOperation(OperationType.DELETE, path, bulkId, null, null);
  }
}
