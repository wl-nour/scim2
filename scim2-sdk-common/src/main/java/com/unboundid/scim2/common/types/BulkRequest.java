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
import com.unboundid.scim2.common.annotations.NotNull;
import com.unboundid.scim2.common.annotations.Nullable;
import com.unboundid.scim2.common.annotations.Schema;
import com.unboundid.scim2.common.messages.PatchOperation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


/**
 * This class represents a SCIM 2.0 Bulk request. A bulk request is a type of
 * operation that allows a client to perform multiple updates within a single
 * API call.
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
 * TODO: Test iterating over the object
 *
 * @see BulkOperation
 */
@Schema(id="urn:ietf:params:scim:schemas:core:2.0:BulkRequest",
        name="Bulk Request", description = "SCIM 2.0 bulk request operation")
public class BulkRequest
    extends BaseScimResource
    implements Iterable<BulkOperation>
{
  // An integer specifying the number of errors that the SCIM service provider
  // will accept before the operation is terminated and an error response is
  // returned. This value is optional in a request, and should never be returned
  // in a response.
  @Nullable
  private Integer failureCount;

  @NotNull
  private final ArrayList<BulkOperation> operations;


  public BulkRequest(@Nullable final List<BulkOperation> operations,
                     final Integer failureCount)
  {
    this.operations = (operations == null) ? List.of() : operations;
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

  public void setFailureCount(Integer failureCount)
  {
    if (failureCount == null)
    {
      this.failureCount = null;
      return;
    }

    this.failureCount = Math.max(failureCount, 0);
  }

  /**
   * Retrieves all the individual operations in this patch request.
   *
   * @return The individual operations in this patch request.
   */
  @NotNull
  public List<BulkOperation> getOperations()
  {
    return operations;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @NotNull
  public Iterator<BulkOperation> iterator()
  {
    return getOperations().iterator();
  }

  public void addOperation(@Nullable BulkOperation... operations)
  {
    if (operations == null)
    {
      return;
    }

    for (var op : operations)
    {
      if (op != null)
      {
        this.operations.add(op);
      }
    }
  }

  public void apply()
  {

  }

  @Override
  @NotNull
  public String toString()
  {
    // Temporarily clear the failure count to print
    Integer originalCount = failureCount;
    failureCount = null;
    String returnString = super.toString();
    failureCount = originalCount;
    return returnString;
  }

  @NotNull
  public String toStringWithFailureCount()
  {
    return super.toString();
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(failureCount, operations);
  }
}
