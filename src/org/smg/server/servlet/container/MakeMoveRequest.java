
package org.smg.server.servlet.container;

import java.util.List;
import java.util.Map;

public class MakeMoveRequest {
  private String accessSignature;
  private List<Map<String, Object>> operations;

  @Override
  public String toString() {
    return operations.toString();
  }

  public final String getAccessSignature() {
    return accessSignature;
  }

  public final void setAccessSignature(String accessSignature) {
    this.accessSignature = accessSignature;
  }

  public final List<Map<String, Object>> getOperations() {
    return operations;
  }

  public final void setOperations(List<Map<String, Object>> operations) {
    this.operations = operations;
  }
}
