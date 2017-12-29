package com.netradius.wirecard.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for StringUtils.
 *
 * @author Erik R. Jensen
 */
public class StringUtilsTest {

  @Test
  public void testHasText() {
    assertFalse(StringUtils.hasText(null));
    assertFalse(StringUtils.hasText(""));
    assertFalse(StringUtils.hasText(" "));
    assertTrue(StringUtils.hasText("a "));
    assertTrue(StringUtils.hasText(" a"));
    assertTrue(StringUtils.hasText("a"));
  }
}
