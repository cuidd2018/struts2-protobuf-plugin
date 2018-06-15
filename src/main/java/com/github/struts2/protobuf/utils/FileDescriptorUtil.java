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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.DescriptorProtos.FileOptions;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.DescriptorValidationException;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.Message;

/**
 * TODO - add document
 * 
 * @author Junhana - junahan@outlook.com 2018
 * @since 1.0.0
 *
 */
public class FileDescriptorUtil {
  private static final Logger LOG = LogManager.getLogger(FileDescriptorUtil.class);
  
  /**
   * 
   * @param fd
   * @return
   * @throws ClassNotFoundException
   * @throws NoSuchMethodException
   * @throws SecurityException
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   * @throws InvocationTargetException
   */
  public static Message getDefaultInstanceOfMessageField(FieldDescriptor fd) 
    throws ClassNotFoundException, NoSuchMethodException, 
    SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    if (fd == null) throw new IllegalArgumentException("Argument fd is required.");
    JavaType jType = fd.getJavaType();
    if (jType != JavaType.MESSAGE) {
      throw new IllegalArgumentException("The field type should be MESSAGE.");
    }
    String className = getClassNameOfMessageField(fd);
    Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
    Method method = clazz.getDeclaredMethod("getDefaultInstance");
    Message dm = (Message)method.invoke(null);
    return dm;
  }
  
  /**
   * 
   * 
   * @param fieldDesc
   * @return
   */
  public static String getClassNameOfMessageField(FieldDescriptor fieldDesc) {
    if (fieldDesc == null) throw new IllegalArgumentException("Argument fieldDesc is required.");
    JavaType jType = fieldDesc.getJavaType();
    if (jType == JavaType.MESSAGE || jType == JavaType.ENUM) {
      // for type - MESSAGE, ENUM
      StringBuilder sb = new StringBuilder();
      FileDescriptorProto fdp = fieldDesc.getFile().toProto();
      Descriptor descriptor = fieldDesc.getMessageType();
      FileOptions fos = fdp.getOptions();
      sb.append(fos.getJavaPackage()).append(".");
      if (!fos.getJavaMultipleFiles()) {
        if (fos.hasJavaOuterClassname()) {
          sb.append(fos.getJavaOuterClassname()).append("$");
        } else {
          String name = getFileNameWithoutPathAndExtension(fdp.getName());
          name = name.replaceFirst(name.substring(0, 1), name.substring(0,1).toUpperCase());
          sb.append(name).append("$");
        }
      }
      sb.append(descriptor.getName());
      return sb.toString();
    } else {
      throw new IllegalArgumentException("The field type should be MESSAGE or ENUM.");
    }
  }

  /**
   * Get the java output class name of the specified {@code FileDescriptor} object.
   *  
   * @param fileDesc - the specified file descriptor object.
   * @return - the class name.
   */
  public static String getJavaOutputClassName(FileDescriptor fileDesc) {
    if (fileDesc == null) throw new IllegalArgumentException("Argument fileDesc is required.");
    StringBuilder sb = new StringBuilder();
    FileDescriptorProto fdp = fileDesc.toProto();
    FileOptions fos = fdp.getOptions();
    sb.append(fos.getJavaPackage()).append(".");
    if (fos.hasJavaOuterClassname()) {
      sb.append(fos.getJavaOuterClassname());
    } else {
      String name = getFileNameWithoutPathAndExtension(fileDesc.getName());
      sb.append(name.replaceFirst(name.substring(0, 1), name.substring(0,1).toUpperCase()));
    }
    return sb.toString();
  }
  
  private static String getFileNameWithoutPathAndExtension(String fullName) {
  	String regex = "/";
  	if (!File.separator.equals("/")) regex = "\\";
    String[] subs = fullName.split(regex);
    String last = subs[subs.length - 1];
    int endIndex = last.indexOf(".");
    return last.substring(0,endIndex);
  }
  
  /**
   * Find message type in file descriptors by full name or unqualified name. 
   * Notice: Doesn't find the nested type.
   * 
   * @param fileDescriptors - file descriptors for look up the message.
   * @param name - the full name or unqualified name of the message to look up.
   * @return - the message type's descriptor or null if not found.
   */
  public static Descriptor findMessageTypeByName(Collection<FileDescriptor> fileDescriptors, String name) {
    if (name == null || name.isEmpty() || fileDescriptors == null || fileDescriptors.size() == 0) return null;
    if (LOG.isDebugEnabled()) LOG.debug("name = " + name);
    for (FileDescriptor fd:fileDescriptors) {
      String lookupName = name;
      if (name.startsWith(fd.getPackage())) {
        lookupName = name.replace(fd.getPackage() + ".", "");
      }
      if (LOG.isDebugEnabled()) LOG.debug("lookup name = " + lookupName);
      Descriptor desc = fd.findMessageTypeByName(lookupName);
      if (desc != null) return desc;
    }
    return null;
  }
  
  /**
   * Find all extensions (includes top-level or nested extensions definition) of the specified container type 
   * against certain file descriptor set.
   * 
   * @param fileDescriptors - 
   * @param containerType
   * @return
   */
  public static List<FieldDescriptor> findExtensionsByType(Collection<FileDescriptor> fileDescriptors, 
      Descriptor containerType) {
    if (containerType == null) throw new IllegalArgumentException("The argument containerType is required.");
    List<FieldDescriptor> fieldDescs = new ArrayList<FieldDescriptor>();
    if (fileDescriptors != null) {
      for (FileDescriptor fd:fileDescriptors) {
        // for top-level definition extensions.
        List<FieldDescriptor> exts = fd.getExtensions();
        for (FieldDescriptor ext:exts) {
          if (ext.getContainingType().getFullName().equals(containerType.getFullName())) {
            if (LOG.isDebugEnabled()) LOG.debug("find extension - " + ext.getFullName());
            fieldDescs.add(ext);
          }
        }
        List<Descriptor> mTypes = fd.getMessageTypes();
        for (Descriptor mType:mTypes) {
          // for nested level definition extensions.
          List<FieldDescriptor> nestedExts = mType.getExtensions();
          for (FieldDescriptor ext:nestedExts) {
            if (ext.getContainingType().getFullName().equals(containerType.getFullName())) {
              if (LOG.isDebugEnabled()) LOG.debug("find extension - " + ext.getFullName());
              fieldDescs.add(ext);
            }
          }
        }
      }
    }
    return fieldDescs;
  }
  
  /**
   * register all extensions 
   *  
   * @param fileDescriptors - file descriptor set.
   * @param extensionRegistry - the {@code ExtensionRegistry} object.
   * @throws ClassNotFoundException 
   * @throws SecurityException 
   * @throws NoSuchMethodException 
   * @throws InvocationTargetException 
   * @throws IllegalArgumentException 
   * @throws IllegalAccessException 
   */
  public static void registerAllExtensions(Collection<FileDescriptor> fileDescriptors,
      ExtensionRegistry extensionRegistry) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    if (extensionRegistry == null) 
      throw new IllegalArgumentException("The argument extensionRegistry is required.");
    if (fileDescriptors != null) {
      for (FileDescriptor fd:fileDescriptors) {
        String className = getJavaOutputClassName(fd);
        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
        Method method = clazz.getDeclaredMethod("registerAllExtensions",ExtensionRegistry.class);
        method.invoke(null,extensionRegistry);
      }
    }
  }
  
  @SuppressWarnings("unused")
  private static void registerAllExtensions2(Collection<FileDescriptor> fileDescriptors,
      ExtensionRegistry extensionRegistry) throws Exception {
    if (extensionRegistry == null) 
      throw new IllegalArgumentException("The argument extensionRegistry is required.");
    if (fileDescriptors != null) {
      List<FieldDescriptor> exts = new ArrayList<FieldDescriptor>();
      for (FileDescriptor fd:fileDescriptors) {
        // for top-level definition extensions.
        exts.addAll(fd.getExtensions());
        List<Descriptor> mTypes = fd.getMessageTypes();
        for (Descriptor mType:mTypes) {
          // for nested level definition extensions.
          exts.addAll(mType.getExtensions());
        }
      }
      for (FieldDescriptor ext:exts) {
        JavaType jType = ext.getJavaType();
        if (jType == JavaType.MESSAGE) {
          if (LOG.isDebugEnabled()) LOG.debug("register extension - " + ext.getFullName());
          extensionRegistry.add(ext, getDefaultInstanceOfMessageField(ext));
        } else {
          if (LOG.isDebugEnabled()) LOG.debug("register extension - " + ext.getFullName());
          extensionRegistry.add(ext);
        }
      }
    }
  }
  
  /**
   * Load field descriptors from java output classes.
   * 
   * @param names
   * @return
   * @throws Exception
   */
  public static Collection<FileDescriptor> loadByClasses(Set<String> names) throws Exception {
    List<FileDescriptor> fds = new ArrayList<FileDescriptor>();
    if (names != null) {
      for (String name:names) {
        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(name);
        Method method = clazz.getDeclaredMethod("getDescriptor");
        Object result = method.invoke(null);
        if (result != null && result instanceof FileDescriptor) {
          fds.add((FileDescriptor)result);
        }
      }
    }
    return fds;
  }
  
  /*
   * Build file descriptors from the file descriptor set without external dependencies.  
   * It automatically builds the internal dependencies. 
   * 
   * @param InputStream is - 
   * @throws IOException - 
   * @throws DescriptorValidationException - 
   */
  @SuppressWarnings("unused")
  private static Collection<FileDescriptor> buildFrom(InputStream is) 
    throws IOException, DescriptorValidationException {
    FileDescriptorSet fdset = FileDescriptorSet.parseFrom(is);
    Map<String,FileDescriptorProto> fdpMap = new HashMap<String,FileDescriptorProto>();
    Map<String,FileDescriptor> fds = new HashMap<String,FileDescriptor>();
    for (FileDescriptorProto fdp:fdset.getFileList()) {
      fdpMap.put(fdp.getName(), fdp);
    }
    for (FileDescriptorProto fdp:fdset.getFileList()) {
      buildFileDescriptor(fdpMap,fds,fdp);
    }
    return fds.values();
  }
  
  /*
   * Recursive enable method to build the file descriptor against the {@code FileDescriptorProto} objects,
   * and additional dependencies.
   * It looks up the dependencies and try to build the dependencies file descriptor if possible.
   * 
   * @param Map<String,FileDescriptorProto> fdps - file descriptor set.
   * @param Map<String,FileDescriptor> fds - dependencies.
   * @param FileDescriptorProto fdp - the {@code FileDescriptorProto} object to be build.
   */
  private static void buildFileDescriptor(
      Map<String,FileDescriptorProto> fdps,
      Map<String,FileDescriptor> fds,
      FileDescriptorProto fdp) throws DescriptorValidationException {
    List<String> dependencies = fdp.getDependencyList();
    List<FileDescriptor> depFds = new ArrayList<FileDescriptor>();
    // check and build dependencies.
    if (dependencies.size() > 0) {
      for(String name:dependencies) {
        if (!fdps.containsKey(name)) throw new RuntimeException("Missing dependency [" + name + "].");
        if (fds.containsKey(name)) {
          if (!depFds.contains(fds.get(name))) {
            depFds.add(fds.get(name));
          }
        } else {
          // recursive call
          buildFileDescriptor(fdps,fds,fdps.get(name));
        }
      }
    }

    // build the fileDescriptor with dependencies.
    FileDescriptor[] deps = new FileDescriptor[0];
    deps = depFds.toArray(deps);
    FileDescriptor fd = FileDescriptor.buildFrom(fdp, deps);
    fds.put(fdp.getName(), fd);
  }
}
