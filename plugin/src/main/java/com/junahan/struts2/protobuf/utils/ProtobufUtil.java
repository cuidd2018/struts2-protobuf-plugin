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

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.junahan.struts2.protobuf.protocol.EStatus;
import com.junahan.struts2.protobuf.protocol.ExceptionMessage;
import com.junahan.struts2.protobuf.protocol.ResponseError;
import com.junahan.struts2.protobuf.protocol.WireResponse;

/** 
 * TODO - add document
 * 
 * @author Junahan - junahan@outlook.com since 2018
 * @since 1.0.0
 */
public final class ProtobufUtil {
  private static final Logger LOG = LogManager.getLogger(ProtobufUtil.class);
  //private static Collection<FileDescriptor> fds;
  
  /**
   * Get the {@PropertyDescriptor} by the given name.
   * This method support to convert the given name (maybe a protobuf message field name using "_" as separator) to 
   * Java Property Style name as an alias.
   *  
   * @param bean - object of the bean 
   * @param name - the given name
   * @throws NoSuchMethodException 
   * @throws InvocationTargetException 
   * @throws IllegalAccessException 
   */
  public static PropertyDescriptor getPropertyDescriptor(Object bean, String name) 
    throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
    Set<String> alias = getAlias(name);
    PropertyDescriptor pd = null;
    for (String alia:alias) {
      pd = PropertyUtils.getPropertyDescriptor(bean, alia);
      if (pd != null) break;
    }
    return pd;
  }
  
  public static void writeResponse(HttpServletResponse response, WireResponse responseMessage) throws IOException {
    writeResponse(response,responseMessage,false);
  }
  
  public static void writeResponse(HttpServletResponse response, WireResponse responseMessage, boolean allowCaching) throws IOException {
    if (responseMessage == null) throw new IllegalArgumentException("The responseMessage argument is required.");
    byte[] content = responseMessage.toByteArray();
    if (response.getStatus() <= 0) response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MessageConsts.MIME_PROTOBUF);
    response.setContentLength(content.length);
    response.setCharacterEncoding(MessageConsts.UTF_8);
    // Set the cache control headers if neccessary
    if (!allowCaching) {
      response.addHeader("Pragma", "no-cache");
      response.addHeader("Cache-Control", "no-cache");
    }
    LOG.debug("Protobuf message result metadata - [content-type=[" + response.getContentType() + 
    		"] length=[" + content.length +
    		"] charset=[" + response.getCharacterEncoding() + "]");
    ServletOutputStream os = null;
    try {
      os = response.getOutputStream();
      os.write(content);
      os.flush();
    } finally {
      if (os != null) os.close();
    }
  }
  
  public static void writeException(Object action, HttpServletResponse response, 
      Exception exception, boolean stackTraceEnabled) throws IOException {
    if (action == null || response == null || exception == null) 
      throw new IllegalArgumentException("The arguments action, response and exception are required.");
    WireResponse.Builder wrb = WireResponse.newBuilder();
    ResponseError.Builder errorBuilder = ResponseError.newBuilder();
    ExceptionMessage.Builder emb = ExceptionMessage.newBuilder();
    
    // check exception.
    if (exception != null) {
    	if (exception.getMessage() != null) 
    		emb.setMessage(exception.getMessage());
    }
    
    if (stackTraceEnabled) {
    	for (StackTraceElement element : exception.getStackTrace()) {
    		emb.addStackTrace(element.toString());
    	}
    }
    errorBuilder.setException(emb.build());
    wrb.setStatus(EStatus.FAILED);
    wrb.setError(errorBuilder.build());
    writeResponse(response,wrb.build());
  }
  
  /*
   * Convert the proto name to alias:
   * proto field name and standard java property name.
   * 
   * @param name - the given name.
   * @return property alias as {@Set<String>} type object.
   * 
   */
  private static Set<String> getAlias(String name) {
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
