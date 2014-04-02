package org.smg.server.util;

import com.google.appengine.api.utils.SystemProperty;

public class NamespaceUtil {
  public static String VERSION = SystemProperty.applicationVersion.get().substring(0, 1);
}