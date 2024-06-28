package com.unboundid.scim2.common;

import com.unboundid.scim2.common.types.BulkOperation;
import org.testng.annotations.Test;

public class BulkOperationTest
{
  @Test
  public void testDelete()
  {
    BulkOperation operation =
        BulkOperation.delete("/Users/resource", "resource");

    BulkOperation operation2 =
        BulkOperation.delete("/Users/resource", "resource").isBulkId();

  }

  @Test
  public void testPost()
  {
    BulkOperation operation =
        BulkOperation.post("/Users/resource", new GenericScimResource());

    BulkOperation operation2 =
        BulkOperation.post("/Users/resource", new GenericScimResource());
    operation2.setBulkId("qwerty");

  }

  @Test
  public void testPut()
  {
    BulkOperation operation = BulkOperation.put("/Users/resource",
        "resource", new GenericScimResource());

    BulkOperation operation2 = BulkOperation.put("/Users/resource",
        "resource", new GenericScimResource()).isBulkId();

  }

  @Test
  public void testPatch()
  {
    BulkOperation operation = BulkOperation.patch("/Users/resource",
        "resource", new GenericScimResource());

    BulkOperation operation2 = BulkOperation.patch("/Users/resource",
        "resource", new GenericScimResource()).isBulkId();

  }

  // Test toString()
  // Test serializing
  // Test all modifications for UserResource, GroupResource, and a custom
  //    SCIM resource that is not natively bundled with the SCIM SDK
}
