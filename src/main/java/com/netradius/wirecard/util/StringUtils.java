package com.netradius.wirecard.util;

/**
 * Provides some simple string utility functions.
 *
 * @author Erik R. Jensen
 */
public class StringUtils {

  public static boolean hasLength(CharSequence seq) {
    return seq != null && seq.length() > 0;
  }

  public static boolean hasText(CharSequence seq) {
    if (!hasLength(seq)) {
      return false;
    }
    int len = seq.length();
    for (int i = len - 1; i >= 0; i--) {
      if (!Character.isWhitespace(seq.charAt(i))) {
        return true;
      }
    }
    return false;
  }
}
