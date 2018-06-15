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
package com.github.struts2.protobuf.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * TODO - add document
 * 
 * @author Junahan - junahan@outlook.com 2018
 * @since 1.0.0
 *
 */
public class BeanPropertyUtil {

  /**
   * 
   * @param bean
   * @param name
   * @throws NoSuchMethodException 
   * @throws InvocationTargetException 
   * @throws IllegalAccessException 
   */
  public static PropertyDescriptor getPropertyDescriptor(Object bean, String name) 
    throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    return getPropertyDescriptor(bean,name,null);
  }
  
  /**
   * Get the {@code PropertyDescriptor} of the specified bean with the name and the alias.
   * 
   * @param bean
   * @param name
   * @param alias
   * @return
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchMethodException
   */
  public static PropertyDescriptor getPropertyDescriptor(Object bean, String name, Map<String,String> alias) 
    throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    if (name == null || name.isEmpty() || bean == null) 
      throw new IllegalArgumentException("The arguments bean and name are required.");
    Set<String> nameAlias = getAlias(name);
    PropertyDescriptor pd = null;
    for (String alia:nameAlias) {
      pd = PropertyUtils.getPropertyDescriptor(bean, alia);
      if (pd == null && alias != null && alias.size() > 0 && alias.get(alia) != null) {
        pd = PropertyUtils.getPropertyDescriptor(bean, alias.get(alia));
      }
      if (pd != null) break;
    }
    return pd;
  }
  
  /**
   * Convert the proto name to alias:
   * proto field name and standard java property name.
   * 
   */
  public static Set<String> getAlias(String name) {
    Set<String> alias = new HashSet<String>();
    alias.add(name);
    String[] subs = name.split("_");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < subs.length; i++) {
      if (i == 0) {
        sb.append(subs[i]);
      } else {
        sb.append(Character.toUpperCase(subs[i].charAt(0)));
        sb.append(subs[i].substring(1));
      }
    }
    alias.add(sb.toString());
    return alias;
  }
}
