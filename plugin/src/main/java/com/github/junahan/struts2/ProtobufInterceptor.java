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
package com.github.junahan.struts2;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.apache.struts2.ServletActionContext;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import com.github.junahan.struts2.ProtobufException;
import com.github.junahan.struts2.ProtobufInterceptor;
import com.github.junahan.struts2.ProtobufPopulator;
import com.github.junahan.struts2.protocol.WireRequest;
import com.github.junahan.struts2.util.FileDescriptorUtil;
import com.github.junahan.struts2.util.MessageConsts;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.util.TextParseUtil;

/**
 * <p>Populate an action from the protobuf message.
 * </p>
 *
 * <p>This stack (defined in struts-default.xml) shows how to use this interceptor with the
 * 'protobuf' interceptor</p>
 * <pre>
 * &lt;interceptor-stack name="protobufDefaultStack"&gt;
 *      &lt;interceptor-ref name="basicStack"/&gt;
 *      &lt;interceptor-ref name="protobuf"&gt;
 *            &lt;param name="fileDescriptorClasses"&gt;com.junahan.struts2.protocol.Protocol&lt;/param&gt;
 *            &lt;param name="customFileDescriptorClasses"&gt;&lt;/param&gt;
 *      &lt;/interceptor-ref&gt;
 * &lt;/interceptor-stack&gt;
 * </pre>
 * <p>The param 'customFileDescriptorClasses' is used to configure the generated protocol class - 
 * which provide FileDescriptor. The populator relay on the FileDescriptor to parse the protobuf message on flying.</p>
 *
 * @author Junahan - junahan@outlook.com 2018
 * @since 1.0.0
 */
public class ProtobufInterceptor extends AbstractInterceptor {
  private static final long serialVersionUID = -644662110094857648L;
  private static final Logger LOG = LogManager.getLogger(ProtobufInterceptor.class);
  protected final ProtobufPopulator populator = new ProtobufPopulator();
  protected Set<String> fileDescriptorClasses;
  protected Set<String> customFileDescriptorClasses;
  
  /*
   * (non-Javadoc)
   * 
   * @see com.opensymphony.xwork2.interceptor.AbstractInterceptor#intercept(com.
   * opensymphony.xwork2.ActionInvocation)
   */
  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
    HttpServletRequest request = ServletActionContext.getRequest();
    //HttpServletResponse response = ServletActionContext.getResponse();
    
    // get the content type.
    String contentType = request.getContentType();
    if (contentType != null) {
      int iSemicolonIdx;
      if ((iSemicolonIdx = contentType.indexOf(";")) != -1)
        contentType = contentType.substring(0, iSemicolonIdx);
    }

    if (contentType != null
        && MessageConsts.MIME_PROTOBUF.equalsIgnoreCase(contentType)) {

      // limit the request size.
      int length = request.getContentLength();
      if (length > 4 * 1024 * 1024)
        throw new ProtobufException(
            "The message is too larger (expected less than 4M bytes).");
      try {
	      WireRequest wr = WireRequest.parseFrom(request.getInputStream());
	      if (wr == null) throw new ProtobufException("Failed to load the message from request.");
	      if (LOG.isTraceEnabled())
	        LOG.trace(String.format("wireRequest: %s",wr.toString()));
	      Collection<FileDescriptor> fds = FileDescriptorUtil.loadByClasses(fileDescriptorClasses);
	      fds.addAll(FileDescriptorUtil.loadByClasses(customFileDescriptorClasses));
	
	      // parse request message.
	      Message message = parseFrom(wr,fds);
	      // populate the protobuf message.
	      Object rootObject = invocation.getStack().peek();
	      populator.populate(rootObject, message);
      } catch (ProtobufException pe) {
      	throw pe;
      } catch (Exception e) {
      	throw new ProtobufException("Failed to populate message to action rootObjecty.", e);
      }
    } else {
      // ignoring - for non-protobuf content type request. 
      if (LOG.isDebugEnabled()) {
        LOG.debug("Ignoring process the non protobuf content type request - content type " 
            + contentType);
      }
    }
    
    return invocation.invoke();
  }
  
  private Message parseFrom(WireRequest request, Collection<FileDescriptor> fileDescriptors) 
    throws ClassNotFoundException, NoSuchMethodException, 
    SecurityException, IllegalAccessException, IllegalArgumentException, 
    InvocationTargetException, InvalidProtocolBufferException, ProtobufException {
    String name = request.getPayloadType();
    if (!Strings.isEmpty(name)) {
	    Descriptor dt = FileDescriptorUtil.findMessageTypeByName(fileDescriptors,name);
	    if (dt == null) {
	    	String error = String.format("Failed to find the file descriptor of the message %s. "
	  				+ "Please check the struts2-protobuf-plugin configue parameter - customFileDescriptorClasses.", name);
	    	throw new ProtobufException(error);
	    }
	    ExtensionRegistry er = ExtensionRegistry.newInstance();
	    FileDescriptorUtil.registerAllExtensions(fileDescriptors, er);
	    //Message message = FileDescriptorUtils.getDefaultInstanceOfMessageField(dt);
	    DynamicMessage dm = DynamicMessage.parseFrom(dt, request.getPayload(),er);
	    return dm;
    } else {
    	LOG.warn("The request message payload type is empty.");
    	return null;
    }
  }
  
  /**
   * @return the set of the fileDescriptorClasses
   */
  public Set<String> getFileDescriptorClasses() {
    return fileDescriptorClasses;
  }

  /**
   * inject the fileDescriptorClasses parameter
   * 
   * @param fileDescriptorClasses - the fileDescriptorClasses to set
   */
  public void setFileDescriptorClasses(String fileDescriptorClasses) {
  	this.fileDescriptorClasses = TextParseUtil.commaDelimitedStringToSet(fileDescriptorClasses);
  }

  /**
   * @return the set of the customFileDescriptorClasses
   */
	public Set<String> getCustomFileDescriptorClasses() {
		return customFileDescriptorClasses;
	}
	
	/**
	 * Inject the customFileDescriptorClasses parameter. 
	 * 
	 * @param customFileDescriptorClasses
	 */
	public void setCustomFileDescriptorClasses(String customFileDescriptorClasses) {
		this.customFileDescriptorClasses = TextParseUtil.commaDelimitedStringToSet(customFileDescriptorClasses);
	}
  
}
