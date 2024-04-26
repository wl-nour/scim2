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

package com.unboundid.scim2.common.types;

import com.unboundid.scim2.common.BaseScimResource;
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * This class represents a SCIM 2 Bulk request. A bulk request is a type of
 * operation that allows a client to perform multiple updates at once.
 * <p>
 * The following is an example of a bulk request in JSON form:
 * <pre>
 * {
 *   "schemas": "urn:ietf:params:scim:schemas:core:2.0:BulkRequest",
 *   "failureCount": 0
 *   "Operations": {
 *     [bulk operation 1],
 *     [bulk operation 2],
 *     ...
 *   }
 * }
 * </pre>
 *
 * TODO: Add tests serializing bulk requests and bulk operations.
 *
 * @see BulkOperation
 */
@Schema(id="urn:ietf:params:scim:schemas:core:2.0:BulkRequest",
        name="Bulk Request", description = "SCIM 2.0 bulk request operation")
public class BulkRequest extends BaseScimResource
{
  // An integer specifying the number of errors that the SCIM service provider
  // will accept before the operation is terminated and an error response is
  // returned. This value is optional in a request, and should never be returned
  // in a response.
  private int failureCount;

  @NotNull
  private final List<BulkOperation> operations;


  public BulkRequest(@Nullable final List<BulkOperation> operations,
                     final int failureCount)
  {
    this.operations =
        (operations == null) ? List.of() : new ArrayList<>(operations);
    setFailureCount(failureCount);
  }

//  public BulkRequest(final int failureCount,
//                     @NotNull final BulkOperation... operations)
//  {
//    this(List.of(operations), failureCount);
//  }

  public BulkRequest(@Nullable final List<BulkOperation> operations)
  {
    this(operations, 0);
  }

  public BulkRequest(@Nullable final BulkOperation... operations)
  {
    this((operations == null) ? null : List.of(operations), 0);
  }


  public void setFailureCount(int failureCount)
  {
    failureCount = Math.max(failureCount, 0);
  }
}
