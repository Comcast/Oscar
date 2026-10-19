/*
 * Copyright 2026 Comcast Cable Communications Management, LLC
 * SPDX-License-Identifier: Apache-2.0
 */
package com.comcast.oscar.dictionary;

import java.util.List;

/** Immutable symbol metadata. Configuration values belong to separate JSON instances. */
public record TlvDefinition(
    List<Integer> path,
    String name,
    String codec,
    int lengthMin,
    int lengthMax,
    int lengthBytes,
    String supportedVersion,
    int maxInstances,
    String description) {
  public TlvDefinition {
    path = List.copyOf(path);
  }

  public int type() {
    return path.get(path.size() - 1);
  }

  public List<Integer> parent() {
    return path.subList(0, path.size() - 1);
  }
}
