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
package com.github.struts2.protobuf;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.github.struts2.protobuf.utils.BeanPropertyUtil;
import com.github.struts2.protobuf.utils.FileDescriptorUtil;
import com.github.struts2.protobuf.utils.Strings;
import com.google.protobuf.Message;

/**
 * Populates protobuf message to java bean object or conversely.
 * 
 * @author Junahan - junahan@outlook.com since 2018
 * @since 1.0.0
 * 
 */
public class ProtobufPopulator {
  private static final Logger LOG = LogManager.getLogger(ProtobufPopulator.class);
  
  /**
   * Populates the protobuf message to object.
   * 
   * @see #populate(Object, Message, Map) 
   * @param object 
   * 				the target object.
   * @param message 
   * 				the source message which fields (has value) will be populated 
   *        to the target object.
   * @throws Exception
   */
  public void populate(final Object object, final Message message) throws Exception {
    populate(object, message, null);
  }
  
  /**
   * Populates the protobuf message fields to object properties by matching the name 
   * (following the java bean standard convention or using the name mapping).
   * 
   * NOTICE: 
   * <ul>
   *  <li>auto converts the message name to standard java bean name.</li> 
   *  <li>only copies the NOT NULL value field to target object - 
   *      that is, keep the target object property original value if no value to 
   *      be copied.
   *  </li>
   *  <li>if the field of the target object is not primary or enum and the field instance 
   *      is existing, this implementation just uses it rather than creates new one.
   *  </li>
   *  <li>if the field of the target object is a collection or array, this implementation creates 
   *      new collection or array and override it with new values. 
   *  </li>
   * </ul>
   * 
   * @param object 
   * 				the target object.
   * @param message 
   * 				the source message which fields (has value) will be populated 
   *        to the target object.
   * @throws Exception
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void populate(final Object object, final Message message, 
      final Map<String,String> alias) throws Exception {
    if (object == null || message == null) {
      //throw new IllegalArgumentException("The arguments object and message are required.");
    	LOG.warn("Ignore to populate - message or target object is empty.");
    	return;
    }

    if (LOG.isTraceEnabled()) {
      LOG.trace("Populate object {} with message {}",object.getClass().getName(),message.toString());
    } else {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Populate object {} with message {}", object.getClass().getName(), 
            message.getClass().getName());
      }
    }
    
    Map<FieldDescriptor, Object> allFields = message.getAllFields();
    for (FieldDescriptor fd : allFields.keySet()) {
      PropertyDescriptor pd = BeanPropertyUtil.getPropertyDescriptor(object, fd.getName(), alias);
      // ignore the non writable properties.
      Method wMethod = null;
      if (pd != null) wMethod = pd.getWriteMethod();
      if (pd == null || wMethod == null
          || !Modifier.isPublic(wMethod.getModifiers())) {
        if (LOG.isDebugEnabled())
          LOG.debug("Ignore to set the field [{}] - unmatch setter method.",
              fd.getName());
        continue;
      }
      Class[] paramTypes = wMethod.getParameterTypes();
      Type[] genericTypes = wMethod.getGenericParameterTypes();

      if (paramTypes.length == 1) {
        Class clazz = paramTypes[0];
        Type type = genericTypes[0];
        if (LOG.isDebugEnabled()) {
          LOG.debug("target clazz = " + clazz.getName());
          LOG.debug("target parameter type = " + type.getClass().getName());
        }
        JavaType jType = fd.getJavaType();
        if (jType == JavaType.ENUM) {
          if (fd.isRepeated()) {
            List<EnumValueDescriptor> eValues = (List<EnumValueDescriptor>) message
                .getField(fd);
            Object convertedValue = convertRepeatedEnum(eValues,clazz,type);
            BeanUtils.setProperty(object, pd.getName(), convertedValue);
          } else {
            EnumValueDescriptor ev = (EnumValueDescriptor) message.getField(fd);
            Object convertedValue = convertEnum(ev,clazz);
            BeanUtils.setProperty(object, pd.getName(), convertedValue);
          }
        } else if (jType == JavaType.MESSAGE) {
          if (fd.isRepeated()) {
            List<Message> values = (List<Message>) message.getField(fd);
            Object convertedValue = convertRepeatedMessage(values, clazz, type,alias);
            PropertyUtils.setProperty(object, pd.getName(), convertedValue);
          } else {
            Message value = (Message) message.getField(fd);
            if (LOG.isTraceEnabled()) {
              LOG.trace("message field value = " + value.toString());
            } else {
              if (LOG.isDebugEnabled())
                LOG.debug("message field value = " + Strings.summary(value.toString(), 100));
            }
            Object propertyObject = null;
            try {
              propertyObject = PropertyUtils.getProperty(object,pd.getName());
            } catch (Exception e) {
              // do nothing.
            }
            Object convertedValue = convertMessage(value, propertyObject, clazz,alias);
            PropertyUtils.setProperty(object, pd.getName(), convertedValue);
          }
        } else {
          // value = message.getField(fd);
          BeanUtils.setProperty(object, pd.getName(), message.getField(fd));
        }
      } else {
        if (LOG.isWarnEnabled()) {
          LOG.warn("Ignore to set the field [{}] - unmatch the setter method parameter size.", pd.getName());
        }
      }
    }
  }

  /**
   * Copies object properties (NOT NULL value) to message fields by matching the 
   * name (following the standard java bean convention or using the specified name mapping). 
   * 
   * NOTICE: 
   * <ul>
   *  <li>only copies the NOT NULL value to target message.</li>
   *  <li>only support the message extensions which defined in the same 
   *      protocolbuf file with message.</li>
   *  <li>If the field type of the target object is a message and the field has value, 
   *      this implementation uses the exiting message rather than create new one.</li>
   *  <li>If the field type of the target object is a collection or array, this 
   *      implementation always create new one.</li>
   * </ul>
   *  
   * @param messageBuilder - the message builder
   * @param bean - the object
   * @param alias - Map<key, value> 
   * 								- the key is the Message.Builder's fileDescriptor name.
   * 				        - the value is a Map<String, String> for object property name -> message field name.
   * @throws Exception - 
   */
  public void populateToMessage(final Message.Builder messageBuilder, final Object bean, 
      final Map<String,Map<String,String>> alias, List<String> exclusion) throws Exception {
    if (messageBuilder == null || bean == null) {
      throw new IllegalArgumentException(
          String.format("Illegal arguments: dest(Message)-> %s, orig(Object) -> %s.",
              messageBuilder,bean));
    }
    if (exclusion == null) exclusion = new ArrayList<String>();
    Descriptor descriptor = messageBuilder.getDescriptorForType();
    FileDescriptor fileDescriptor = descriptor.getFile();
    Collection<FileDescriptor> fileDescriptors = new ArrayList<FileDescriptor>();
    fileDescriptors.add(fileDescriptor);
    List<FieldDescriptor> fds = new ArrayList<FieldDescriptor>(); 
    fds.addAll(descriptor.getFields());
    fds.addAll(FileDescriptorUtil.findExtensionsByType(fileDescriptors, descriptor));
    //fds.addAll(descriptor.getExtensions());
    //LOG.debug("Field descriptors = " + fds);
    Map<String,String> mapper = null;
    if (alias != null)
      mapper = alias.get(messageBuilder.getDescriptorForType().getName());
    for (FieldDescriptor fd : fds) {
      String fName = fd.getName();
      PropertyDescriptor pd = BeanPropertyUtil.getPropertyDescriptor(bean, fName, mapper);
      if (pd == null) {
        if (LOG.isInfoEnabled()) 
          LOG.info(String.format("Ignore the field %s - doesn't find related property in %s.",
              fName, bean.getClass().getName()));
        continue;
      }
      String pName = pd.getName();
      if (!PropertyUtils.isReadable(bean, pName)) {
        if (LOG.isInfoEnabled()) 
          LOG.info(String.format("Ignroe to copy the field %s - is not readable in %s.",
              pName, bean.getClass().getName()));
        continue;
      }
      LOG.debug(String.format("fName -> %s, pName -> %s ",fName,pName));
      
      if (!(exclusion.contains(fName) || exclusion.contains(pName))) {
        Object value = PropertyUtils.getProperty(bean,pName);
        LOG.debug(String.format("try to copy field %s to %s with value -> %s",pName,fName,value));
        if (value != null) {
          FieldDescriptor.Type type = fd.getType();
          if (type == FieldDescriptor.Type.ENUM) {
            Object convertedValue = null;
            if (fd.isRepeated()) {
              convertedValue = convertToRepeatedEnum(value,fd.getEnumType());
            } else {
              convertedValue = convertToEnum(value,fd.getEnumType());
            }
            messageBuilder.setField(fd, convertedValue);
          } else if (type == FieldDescriptor.Type.MESSAGE) {
            LOG.debug(String.format("Embedded message type -> %s",fd.getMessageType()));
            Object convertedValue = null;
            if (fd.isRepeated()) {
              Message.Builder fieldBuilder = messageBuilder.newBuilderForField(fd);
              convertedValue = convertToRepeatedMessage(value,fieldBuilder,alias);
            } else {
              Message.Builder fieldBuilder = ((Message)messageBuilder.getField(fd)).toBuilder();                
              convertedValue = convertToMessage(value,fieldBuilder,alias);
            }
            messageBuilder.setField(fd, convertedValue);
          } else if (type == FieldDescriptor.Type.GROUP) {
            throw new ProtobufException("Unsupported to convert to GROUP type.");
          } else {
            Object convertedValue = null;
            Class<?> vType = fd.getDefaultValue().getClass();
            if (fd.isRepeated()) {
              convertedValue = convertToRepeatedProtoPrimitive(value,vType);
            } else {
              convertedValue = ConvertUtils.convert(value, vType);
            }
            messageBuilder.setField(fd,convertedValue);
          }
        } else {
          //messageBuilder.clearField(fd);
          LOG.info(String.format("Ignroe to copy field %s -> %s - the value is null.",
              pName,fName));
        }
      } else {
        LOG.info(String.format("Ignore to copy field %s -> %s - exclusion field.",
            pName,fName));
      }
    }
  }  

  @SuppressWarnings({ "rawtypes" })
  private Object convertToRepeatedProtoPrimitive(Object value, Class clazz) throws Exception {
    Object targets = null;
    if (value != null) {
      Class valueClazz = value.getClass();
      if (Collection.class.isAssignableFrom(valueClazz)) {
        targets = ConvertUtils.convert(value,clazz);
      } else if (valueClazz.isArray()) {
        List<Object> values = asList(value);
        if (LOG.isDebugEnabled()) LOG.debug("size of the values = " + values.size());
        targets = ConvertUtils.convert(values, clazz);
      } else {
        throw new ProtobufException(
            String.format("Incompatible type %s, expectted %s.",
                valueClazz.getName(),List.class.getName()));
      }
    }
    return targets;
  }
  
  @SuppressWarnings({ "rawtypes" })
  private Object convertToEnum(Object value, EnumDescriptor enumDescriptor) throws Exception {
    Class vClazz = value.getClass();
    if (vClazz.isEnum()) {
      return enumDescriptor.findValueByName(((Enum)value).name());
    } else if (value instanceof String) {
      return enumDescriptor.findValueByName((String)value);
    } else {
      throw new ProtobufException(
          String.format("Incompitable type %s, expectted %s.",
              value.getClass().getName(),enumDescriptor.getName()));
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private Object convertToRepeatedEnum(Object value, EnumDescriptor enumDescriptor) throws Exception {
    List messageEnums = new ArrayList();
    if (value != null) {
      Class valueClazz = value.getClass();
      if (Collection.class.isAssignableFrom(valueClazz)) {
        // for collection.
        Collection<Object> values = (Collection<Object>) value;
        for (Object object:values) {
          Object convertedValue = convertToEnum(object,enumDescriptor);
          if (convertedValue != null) messageEnums.add(convertedValue);
        }
      } else if (valueClazz.isArray()) {
        // for array.
        List<Object> values = asList(value);
        for (Object object:values) {
          Object convertedValue = convertToEnum(object,enumDescriptor);
          if (convertedValue != null) messageEnums.add(convertedValue);
        }
      } else {
        throw new ProtobufException(
            String.format("Incompatible type %s, expectted %s.",
                valueClazz.getName(),List.class.getName()));
      }
    }
    return messageEnums;
  }
  
  @SuppressWarnings("rawtypes")
  private Message convertToMessage(Object value, Message.Builder fieldBuilder, 
      final Map<String,Map<String,String>> mapping) throws Exception {
    if (value == null) return null;
    Class valueClazz = value.getClass();
    if (isIncompatibleTypeToMessage(value)) {
      throw new ProtobufException(
          String.format("Incompatible type %s, expectted %s.",
              valueClazz.getName(),
              "object or message type of " + fieldBuilder.getDescriptorForType().getName()));
    } else if (Message.class.isAssignableFrom(valueClazz)) {
      if (((Message)value).getDescriptorForType() == fieldBuilder.getDescriptorForType()) {
        //return (Message)value;
        return fieldBuilder.mergeFrom((Message)value).build();
      } else {
        throw new ProtobufException(
            String.format("Incompatible type %s, expectted %s.",
                ((Message)value).getDescriptorForType().getName(),
                fieldBuilder.getDescriptorForType().getName()));
      }
    } else {
      populateToMessage(fieldBuilder,value,mapping,null);
      return fieldBuilder.build();
    }
  }
  
  @SuppressWarnings("rawtypes")
  private boolean isIncompatibleTypeToMessage(Object value) {
    Class valueClazz = value.getClass();
    return (valueClazz.isPrimitive() || valueClazz.isEnum() || valueClazz.isArray()
        || Character.class.isAssignableFrom(valueClazz) || Number.class.isAssignableFrom(valueClazz)
        || Collection.class.isAssignableFrom(valueClazz) || Map.class.isAssignableFrom(valueClazz));
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private List<Message> convertToRepeatedMessage(Object value, Message.Builder fieldBuilder,
      final Map<String,Map<String,String>> mapping) throws Exception {
    List<Message> messages = new ArrayList<Message>();
    if (value != null) {
      Class valueClazz = value.getClass();
      if (Collection.class.isAssignableFrom(valueClazz)) {
        // for collection.
        Collection<Object> values = (Collection<Object>)value;
        for (Object object:values) {
          Message message = convertToMessage(object, fieldBuilder,mapping);
          if (message != null) messages.add(message);
          fieldBuilder.clear();
        }
      } else if (valueClazz.isArray()) {
        // for arrays.
        List<Object> values = asList(value);
        for (Object object:values) {
          Message message = convertToMessage(object,fieldBuilder,mapping);
          if (message != null) messages.add(message);
          fieldBuilder.clear();
        }
      } else {
        throw new ProtobufException(
            String.format("Incompatible type %s, expected type %s.",
                value.getClass().getName(),Collection.class.getName()));
      }
    }
    return messages;
  }
  
  /*
   * Converts enum to String or compatible enum type.
   * 
   * @param value
   * @param clazz
   * @throws Exception in case of finding incompatible type.
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private Object convertEnum(EnumValueDescriptor value, Class clazz) throws Exception {
    if (clazz.isEnum()) {
      return Enum.valueOf(clazz, value.getName());
    } else if (String.class.isAssignableFrom(clazz)) {
      return value.getName();
    } else {
      throw new ProtobufException(String.format(
          "Incompatible type - expectted %s, but was %s.", clazz.getName(),
          value.getType().getName()));
    }
  }

  
  /*
   * Converts repeated enum fields to Collection<String> or collection of compatible enum type.
   * 
   * @param values
   * @param clazz
   * @param type
   * @return 
   * @throws Exception 
   */
  @SuppressWarnings({ "unchecked", "rawtypes"})
  private Object convertRepeatedEnum(List<EnumValueDescriptor> values, Class clazz, Type type) 
    throws Exception {
    if (Collection.class.isAssignableFrom(clazz)) {
      Class itemClass = getClassOfType(type);
      if (itemClass.isEnum()) {
        Collection newCollection = newCollection(clazz);
        for (EnumValueDescriptor ev:values) {
          Object value = convertEnum(ev,itemClass);
          newCollection.add(value);
        }
        return newCollection;
      } else {
        throw new ProtobufException(String.format(
            "Incompatible type - expectted collection of %s, but was %s.", itemClass.getName(),
            values.get(0).getType().getName()));
      }
    } else {
      // TODO - may need to support convert to map.
      throw new ProtobufException(String.format(
          "Incompatible type - expectted %s, but was %s.", clazz.getName(),
          List.class.getName()));
    }
  }
  
  /*
   * Converts Message type field to target object (applying the names mapping).
   * 
   * @param value
   * @param target
   * @param clazz
   * @param alias
   * @return
   * @throws Exception
   */
  @SuppressWarnings({ "rawtypes"})
  private Object convertMessage(Message value, Object target, Class clazz, Map<String,String> alias) throws Exception {
    // TODO - support dynamic message to target.
    if (LOG.isDebugEnabled()) {
      LOG.debug("Message name = " + value.getDescriptorForType().getName());
      LOG.debug("target object type = " + clazz.getName());
    }
    if (target != null) {
      // if the target is a message, it must be same message type.
      if (Message.class.isAssignableFrom(clazz)) {
        Message targetMessage = (Message)target;
        return targetMessage.toBuilder().mergeFrom(value).build();
      }
      // populate property to target object.
      populate(target,value,alias);
      return target;
    } else {
      if (Message.class.isAssignableFrom(clazz)) {
        Message targetMessage = newDefaultMessage(clazz);
        return targetMessage.toBuilder().mergeFrom(value).build();
      } else {
        // create new instance of the target.
        // TODO - may be the clazz is a map, array or collection?
        target = clazz.newInstance();
        populate(target, value, alias);
        return target;
      }
    }
  }

  /*
   * Gets raw class of the parameterized type.
   *  
   * @param type the parameterized type.
   * @return
   */
  @SuppressWarnings("rawtypes")
  private Class getClassOfType(Type type) {
    Class itemClass = Object.class;
    Type itemType = null;
    if ((type != null) && (type instanceof ParameterizedType)) {
      ParameterizedType ptype = (ParameterizedType) type;
      itemType = ptype.getActualTypeArguments()[0];
      if (itemType.getClass().equals(Class.class)) {
        itemClass = (Class) itemType;
      } else {
        itemClass = (Class) ((ParameterizedType) itemType).getRawType();
      }
    }
    return itemClass;
  }
  
  /*
   * Create a new collection for the specified class type.
   * 
   * @param clazz
   * @return
   * @throws IllegalAccessException
   */
  @SuppressWarnings("rawtypes")
  private Collection newCollection(Class clazz) throws IllegalAccessException {
    Collection newCollection = null;
    if (Collection.class.isAssignableFrom(clazz)) {
      try {
        newCollection = (Collection) clazz.newInstance();
      } catch (InstantiationException ex) {
        // fallback if clazz represents an interface or abstract class
        if (Set.class.isAssignableFrom(clazz)) {
          newCollection = new HashSet();
        } else {
          newCollection = new ArrayList();
        }
      }
    }
    return newCollection;
  }
  
  /*  
   * Converts repeated message field to target object.
   * 
   * @param values
   * @param clazz
   * @param type
   * @param alias
   * @return
   * @throws Exception
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private Object convertRepeatedMessage(List<Message> values, Class clazz,
      Type type, Map<String,String> alias) throws Exception {
    if (Collection.class.isAssignableFrom(clazz)) {
      Class itemClass = getClassOfType(type);
      Collection newCollection = newCollection(clazz);

      // for each message.
      for (Message msg:values) {
        Object object = convertMessage(msg, null, itemClass, alias);
        if (object != null) {
          newCollection.add(object);
        } else {
          if (LOG.isWarnEnabled()) 
            LOG.warn(String.format("Failed to convert message %s to target class %s.",
                msg,itemClass.getName()));
        }
      }
      return newCollection;
    } else {
      throw new ProtobufException(String.format(
          "Incompatible type - expectted %s, but was %s.", clazz.getName(),
          List.class.getName()));
    }
  }

  /*
   * Convert array object to list. 
   *  
   * @param arrayObject
   * @return
   */
  private List<Object> asList(Object arrayObject) {
    if (!arrayObject.getClass().isArray()) 
      throw new IllegalArgumentException("Invalid type of arrayObject - expected an array object.");
    List<Object> values = new ArrayList<Object>();
    //Class arrayType = valueClazz.getComponentType();
    int length = Array.getLength(arrayObject);
    for (int i = 0; i < length; i++) {
      values.add(Array.get(arrayObject, i));
    }
    return values;
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private Message newDefaultMessage(Class clazz) 
    throws NoSuchMethodException, SecurityException, 
    IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    if (!Message.class.isAssignableFrom(clazz)) {
      throw new IllegalArgumentException(String.format("The clazz %s is not Message type.",
          clazz.getName()));
    }
    Method method = clazz.getDeclaredMethod("getDefaultInstance");
    Message instance = (Message)method.invoke(null);
    return instance;
  }
}
