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

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.github.struts2.protobuf.utils.ProtobufUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.github.struts2.protobuf.protocol.EStatus;
import com.github.struts2.protobuf.protocol.FieldError;
import com.github.struts2.protobuf.protocol.ResponseError;
import com.github.struts2.protobuf.protocol.WireResponse;

/**
 * <p>Serializes validation and action errors into protobuf message. This interceptor does not
 * perform any validation itself, so it must follow the 'validation' interceptor on the stack.
 * </p>
 *
 * <p>This stack (defined in struts-default.xml) shows how to use this interceptor with the
 * 'validation' interceptor</p>
 * <pre>
 * &lt;interceptor-stack name="protobufValidationStack"&gt;
 *      &lt;interceptor-ref name="validation"&gt;
 *            &lt;param name="excludeMethods"&gt;input,back,cancel,browse&lt;/param&gt;
 *      &lt;/interceptor-ref&gt;
 *      &lt;interceptor-ref name="protobufValidation"/&gt;
 * &lt;/interceptor-stack&gt;
 * </pre>
 * <p>If 'validationFailedStatus' is set, it will be used as the response status
 * when validation fails.</p>
 *
 * <p>If the request has a parameter 'struts.validateOnly' execution will return after
 * validation (action won't be executed).</p>
 * 
 * @author Junahan - junahan@outlook.com 2018
 * @since 1.0.0
 */
public class ProtobufValidationInterceptor extends MethodFilterInterceptor {
  private static final long serialVersionUID = 4375382534489980975L;
  private static final Logger LOG = LogManager.getLogger(ProtobufValidationInterceptor.class);
  private static final String VALIDATE_ONLY_PARAM = "struts.validateOnly";

  private int validationFailedStatus = -1;

  /**
   * HTTP status that will be set in the response if validation fails
   * @param validationFailedStatus
   */
  public void setValidationFailedStatus(int validationFailedStatus) {
    this.validationFailedStatus = validationFailedStatus;
  }
  
  /* (non-Javadoc)
   * @see com.opensymphony.xwork2.interceptor.MethodFilterInterceptor#doIntercept(com.opensymphony.xwork2.ActionInvocation)
   */
  @Override
  protected String doIntercept(ActionInvocation invocation) throws Exception {
    HttpServletResponse response = ServletActionContext.getResponse();
    HttpServletRequest request = ServletActionContext.getRequest();

    Object action = invocation.getAction();
    if (action instanceof ProtobufResponseAware) {
        if (action instanceof ValidationAware) {
            // generate message.
            ValidationAware validationAware = (ValidationAware) action;
            if (validationAware.hasErrors()) {
                return generateAndSendMessage(action,response,validationAware);
            }
        }
        if (isValidateOnly(request)) {
          // there were no errors
        	WireResponse.Builder rm = WireResponse.newBuilder().setStatus(EStatus.SUCCEED);
          ProtobufUtil.writeResponse(response,rm.build());
          return Action.NONE;
        } else {
          return invocation.invoke();
        }
    } else {
      return invocation.invoke();
    }
  }
  
  private boolean isValidateOnly(HttpServletRequest request) {
    return "true".equals(request.getParameter(VALIDATE_ONLY_PARAM));
  }

  private String generateAndSendMessage(Object action, HttpServletResponse response, 
      ValidationAware validationAware) throws IOException {
    WireResponse.Builder builder = WireResponse.newBuilder();
    ResponseError.Builder errorBuilder = ResponseError.newBuilder();
    if (validationAware.hasErrors()) {
      builder.setStatus(EStatus.FAILED);
      // set the response status
      if (validationFailedStatus >= 0) {
        response.setStatus(validationFailedStatus);
      }
      
      if (validationAware.hasActionErrors()) {
        Collection<String> aes = validationAware.getActionErrors();
        errorBuilder.addAllActionErrors(aes);
      }
      if (validationAware.hasFieldErrors()) {
        Map<String,List<String>> fes = validationAware.getFieldErrors();
        Set<String> keys = fes.keySet();
        for (String key:keys) {
          List<String> ems = fes.get(key);
          FieldError.Builder mfe = FieldError.newBuilder();
          if (key != null) 
            mfe.setName(key);
          mfe.addAllMessages(ems).build();
          errorBuilder.addFieldErrors(mfe);
        }
      }
      builder.setError(errorBuilder.build());
      if (LOG.isDebugEnabled())
        LOG.debug(String.format("Validation errors: \n%s", builder.build().toString()));
    } else {
      builder.setStatus(EStatus.SUCCEED);
    }
    ProtobufUtil.writeResponse(response,builder.build());
    return Action.NONE;
  }
}
