/*
 * Copyright 2024 Ping Identity Corporation
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

package com.unboundid.scim2.common.types;

import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.ScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;

import java.util.Objects;

import static com.unboundid.scim2.common.types.BulkOperation.OperationType.*;


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
        name="Bulk Operation", description = "SCIM 2.0 Bulk Operation")
public class BulkOperation extends BaseScimResource
{
  @NotNull
  public static final String BULK_ID_PREFIX = "bulkId:";

  // The HTTP operation type.
  @NotNull
  private final OperationType method;

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
  private String bulkId = null;

  // The optional SCIM ETag value, which can be used if the SCIM service
  // provider supports ETag versioning.
  @Nullable
  private String version;

  // The JSON body of the request. May be null if this is a bulk delete
  // operation.
  @Nullable
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

  @Nullable
  private ScimResource getData()
  {
    return data;
  }


  /**
   * Represents the HTTP operation type. The GET operation type is not included
   * because it cannot be used for bulk operations that are designed to modify
   * SCIM resources.
   */
  public enum OperationType
  {
    /**
     * The HTTP POST operation type.
     */
    POST,

    /**
     * The HTTP PUT operation type.
     */
    PUT,

    /**
     * The HTTP PATCH operation type.
     */
    PATCH,

    /**
     * The HTTP DELETE operation type.
     */
    DELETE,
  }


  /**
   * Constructs a SCIM 2.0 Bulk Operation.
   * <br><br>
   * Bulk operations can be created by using methods in this class such as
   * {@link #delete(String, String)}, as this provides a more concise way
   * of instantiating a bulk operation.
   *
   * @param opType    The type of operation (e.g., POST)
   * @param path      The path to the SCIM resource.
   * @param id        The unique identifier for the resource within the bulk
   *                  operation.
   * @param data      The input data for the API request.
   */
  private BulkOperation(@NotNull final OperationType opType,
                        @NotNull final String path,
                        @Nullable final String id,
                        @Nullable final ScimResource data)
  {
    this.method = opType;
    this.path = path;
    this.data = data;

    super.setId(id);
  }

  /**
   * Creates a POST bulk operation without a {@code version} field.
   * <br><br>
   * POST operations do not take in an {@code id} field because the resource
   * ID will be generated by the SCIM service provider and cannot be determined
   * beforehand. If you wish to reference the created resource with a bulk ID,
   * then use the {@link #setBulkId} method, e.g.:
   * <pre>
   *   BulkOperation op =
   *       BulkOperation.post("path", getData()).setBulkId("asdf");
   * </pre>
   *
   * @param path      The path to the SCIM resource.
   * @param data      The JSON input data with details about the data
   *                  modification.
   *
   * @return  A new bulk operation.
   */
  @NotNull
  public static BulkOperation post(@NotNull final String path,
                                   @NotNull final ScimResource data)
  {
    Objects.requireNonNull(data,
        "The 'data' field should not be null for a POST operation.");
    return new BulkOperation(OperationType.POST, path, null, data);
  }

  /**
   * This should only need to be used for POST operations.
   */
  @NotNull
  public BulkOperation setBulkId(@Nullable String bulkId)
  {
    if (bulkId == null)
    {
      return this;
    }

    // Set the provided bulk ID. Ensure that the 'id' field is null.
    this.bulkId = BULK_ID_PREFIX + bulkId;
    setId(null);

    return this;
  }

  /**
   * Creates a PUT bulk operation without a {@code version} field.
   *
   * @param path      The path to the SCIM resource.
   * @param id        The unique identifier for the resource within the bulk
   *                  operation. This may be a bulk ID. If it is, call the
   *                  {@link #isBulkId()} method after this bulk operation is
   *                  created.
   * @param data      The JSON input data with details about the data
   *                  modification.
   *
   * @return  A new bulk operation.
   */
  @NotNull
  public static BulkOperation put(@NotNull final String path,
                                  @NotNull final String id,
                                  @NotNull final ScimResource data)
  {
    Objects.requireNonNull(data,
        "The 'data' field should not be null for a PUT operation.");
    return new BulkOperation(OperationType.PUT, path, id,  data);
  }

  /**
   * Creates a PATCH bulk operation without a {@code version} field.
   *
   * @param path      The path to the SCIM resource.
   * @param id        The unique identifier for the resource within the bulk
   *                  operation. This may be a bulk ID. If it is, call the
   *                  {@link #isBulkId()} method after this bulk operation is
   *                  created.
   * @param data      The JSON input data with details about the data
   *                  modification.
   *
   * @return  A new bulk operation.
   */
  @NotNull
  public static BulkOperation patch(@NotNull final String path,
                                    @NotNull final String id,
                                    @NotNull final ScimResource data)
  {
    Objects.requireNonNull(data,
        "The 'data' field should not be null for a PATCH operation.");
    return new BulkOperation(OperationType.PATCH, path, id, data);
  }

  /**
   * Creates a DELETE operation without a {@code version} field.
   *
   * @param path      The path to the SCIM resource.
   * @param id        The unique identifier for the resource within the bulk
   *                  operation. This may be a bulk ID. If it is, call the
   *                  {@link #isBulkId()} method after this bulk operation is
   *                  created.
   *
   * @return  A new bulk operation.
   */
  @NotNull
  public static BulkOperation delete(@NotNull final String path,
                                     @Nullable final String id)
  {
    return new BulkOperation(DELETE, path, id, null);
  }

  /**
   * Indicates that the provided ID is a bulk ID.
   *
   * @return  This bulk operation.
   */
  @NotNull
  public BulkOperation isBulkId()
  {
    // The ID that was previously provided should be moved to the 'bulkId'
    // field.
    bulkId = BULK_ID_PREFIX + getId();
    setId(null);

    return this;
  }

  @NotNull
  public BulkOperation setVersion(@Nullable String version)
  {
    this.version = version;
    return this;
  }

  @Override
  public String getId()
  {
    String id = super.getId();
    if (id != null)
    {
      return id;
    }

    return bulkId;
  }

  @Override
  @NotNull
  public String toString()
  {
    // The 'id' field is a helper resource that is not actually part of the
    // operation's JSON representation, so it should not be printed.
    String originalID = super.getId();
    setId(null);
    String returnString = super.toString();
    setId(originalID);

    return returnString;
  }
}
