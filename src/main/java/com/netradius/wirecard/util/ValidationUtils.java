package com.netradius.wirecard.util;

/**
 * Utility methods for argument validation.
 *
 * @author Erik R. Jensen
 */
public class ValidationUtils {

  /**
   * Checks a string to ensure it contains no more characters than the max length.
   *
   * @param s         the string to check
   * @param maxLength the maximum number of characters allowed
   */
  public static void checkMaxLength(String s, int maxLength) {
    if (s != null) {
      if (s.length() > maxLength) {
        throw new IllegalArgumentException("String length must be less than " + maxLength);
      }
    }
  }

  /**
   * Checks the string to validate it matches a specific length.
   *
   * @param s       the string to check
   * @param lengths the allowed lengths
   */
  public static void checkLengthEquals(String s, int... lengths) {
    if (s != null) {
      int slen = s.length();
      for (int len : lengths) {
        if (slen == len) {
          return;
        }
      }
      throw new IllegalArgumentException("String length does not match");
    }
  }

  /**
   * Checks to make sure an object is not null.
   *
   * @param o the object to check
   */
  public static void checkForNull(Object o) {
    if (o == null) {
      throw new NullPointerException("Object may not be null");
    }
  }

  /**
   * Checks to make sure a string is not empty.
   *
   * @param s the string to check
   */
  public static void checkForEmpty(String s) {
    if (isEmpty(s)) {
      throw new IllegalArgumentException("String may not be null or empty");
    }
  }

  /**
   * Checks if a string is empty. An string is considered empty by this method when it is null,
   * contains no characters or only contains whitespace characters.
   *
   * @param str the string to check
   * @return true of the string is empty, false if otherwise
   */
  public static boolean isEmpty(String str) {
    return str == null || trimWhitespace(str).isEmpty();
  }

  /**
   * Trims off any leading or trailing whitespace from the given string. This method will handle
   * null or empty strings.
   *
   * @param str the string to trim
   * @return the trimmed string
   */
  public static String trimWhitespace(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }
    return trimLeadingWhitespace(trimTrailingWhitespace(str));
  }

  /**
   * Trims off any leading whitespace from the given string. This method will handle null and
   * empty strings.
   *
   * @param str the string to trim
   * @return the trimmed string
   */
  public static String trimLeadingWhitespace(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }
    for (int i = 0; i < str.length(); i++) {
      if (!Character.isWhitespace(str.charAt(i))) {
        return str.substring(i, str.length());
      }
    }
    return "";
  }

  /**
   * Trims off any training whitespace from the given string. This method will handle null and
   * empty strings.
   *
   * @param str the string to trim
   * @return the trimmed string
   */
  public static String trimTrailingWhitespace(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }
    for (int i = str.length() - 1; i >= 0; i--) {
      if (!Character.isWhitespace(str.charAt(i))) {
        return str.substring(0, i + 1);
      }
    }
    return "";
  }

}
