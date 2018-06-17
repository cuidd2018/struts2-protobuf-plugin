/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.junahan.struts2.protobuf.utils;

import java.util.UUID;

/**
 * Utility class for String.
 * 
 * @author Junahan - junahan@outlook.com 2018
 * @since 1.0.0
 */
public class Strings {
  public static final String htmlSpaceRegrex = "[\\s|\u00a0|\u3000]";
  
  /**
   * Check to determine the text is empty or not.
   * 
   * @param text - the text object.
   * @return true if the {@code text} argument is {@code null} or length less than 1, 
   *         otherwise false.
   */
  public static boolean isEmpty(String text) {
    return text == null || trimHtmlText(text).length() <= 0;
  }

  public static boolean isNotBlank(String text) {
    return text != null && trimHtmlText(text).length() > 0;
  }

  /**
   * Convenience method for adding quotes. The specified
   * object is converted to a string representation by
   * calling its <code>toString()</code> method.
   * 
   * @param aValue an object to quote
   * @return a quoted string
   */
  public static String quote(final Object aValue) {
      if (aValue != null) {
          return "\"" + aValue + "\"";
      }
      return "\"\"";
  }

  /**
   * Convenience method for removing surrounding quotes
   * from a string value.
   * 
   * @param aValue a string to remove quotes from
   * @return an un-quoted string
   */
  public static String unquote(final String aValue) {
      if (aValue != null && aValue.startsWith("\"") && aValue.endsWith("\"")) {
          return aValue.substring(0, aValue.length() - 1).substring(1);
      }
      return aValue;
  }
  
  /**
   * 
   *  
   * @param origin - the {@code origin} string object.
   * @param length - the shortage {@code length}.
   * @return the shortage summary string.
   */
  public static String summary(String origin, int length) {
    if (origin == null) return null;
    if (length < 0) return "";
    if ((length + 3) >= origin.length()) return origin;
    return origin.substring(0,length) + "...";
  }
  
  /**
   * Trim the {@code htmlText} string to remove the whitespace.
   * 
   * @param htmlText - the string object of the htmlText.
   * @return replaced string
   */
  public static String trimHtmlText(String htmlText) {
    if (htmlText == null) return null;
    return htmlText.replaceAll("^[\\u00a0|\\u3000|\\s]+","").replaceAll("[\\u00a0|\\u3000|\\s]+$", "");
  }
  
  /**
   * 
   * @param object source object
   * @param length target string length limit
   * @return truncated string.
   */
  public static String toString(Object object, int length) {
    if (object == null) return null;
    return summary(object.toString(),length);
  }
  
  /**
   * Compares two strings a and b. Returns true if 
   * 
   * <ul>
   * <li> both a and b are null.</li> 
   * <li> a != null and a equals b.</li>
   * <li> b != null and b equals a.</li>
   * </ul>
   * 
   * @param a string a
   * @param b string
   * @return compare result.
   */
  public static boolean isSame(String a, String b) {
    if (a == null && b == null) return true;
    if (a != null) return a.equals(b);
    if (b != null) return b.equals(a); // This line may be removed.
    return false;
  }
  
  public final static String uuid() {
    return UUID.randomUUID().toString().replace("-","");
  }
  
  /**
   * Convert the byte[] data to hex string.
   * 
   * @param data origin byte array data
   * @return the hex represent of the data.
   */
  public static String toHexString(final byte[] data) {
    StringBuilder hex = new StringBuilder(data.length * 2);
    for (byte b : data) {
        if ((b & 0xFF) < 0x10) hex.append("0");
        hex.append(Integer.toHexString(b & 0xFF));
    }
    return hex.toString();    
  }  
  
  private Strings() {}
}
