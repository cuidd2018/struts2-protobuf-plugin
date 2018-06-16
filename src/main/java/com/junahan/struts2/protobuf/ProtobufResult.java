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
package com.junahan.struts2.protobuf;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsStatics;
import org.apache.struts2.result.StrutsResultSupport;

import com.google.protobuf.Message;
import com.junahan.struts2.protobuf.protocol.EStatus;
import com.junahan.struts2.protobuf.protocol.WireResponse;
import com.junahan.struts2.protobuf.utils.MessageConsts;
import com.junahan.struts2.protobuf.utils.ProtobufUtil;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.inject.Inject;

/**
 * <p/>
 * This result serializes an action result into protobuf message.
 * <p/>
 * <p/>
 * <b>Example:</b>
 * <p/>
 * <p/>
 * 
 * <pre>
 * &lt;!-- START SNIPPET: example --&gt;
 * &lt;result name=&quot;success&quot; type=&quot;protobuf&quot; /&gt;
 * &lt;!-- END SNIPPET: example --&gt;
 * </pre>
 * 
 * @author Junahan - junahan@outlook.com 2018
 * @since 1.0.0
 */
public class ProtobufResult extends StrutsResultSupport {
  private static final long serialVersionUID = -978101742299823998L;
  private static Logger LOG = LogManager.getLogger(ProtobufResult.class);
  protected String defaultCharset = MessageConsts.UTF_8;
  protected boolean allowCaching = false;

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.apache.struts2.dispatcher.StrutsResultSupport#doExecute(java.lang.String
   * , com.opensymphony.xwork2.ActionInvocation)
   */
  @Override
  protected void doExecute(String finalLocation, ActionInvocation invocation)
      throws Exception {
    ActionContext actionContext = invocation.getInvocationContext();
    HttpServletResponse response = (HttpServletResponse) actionContext
        .get(StrutsStatics.HTTP_RESPONSE);
    
    Message rMessage = null;
    Object action = invocation.getAction();
    if (action instanceof ProtobufResponseAware) {
      rMessage = ((ProtobufResponseAware)action).getResponseMessage();
    }
    
    WireResponse.Builder wrb = WireResponse.newBuilder();
    wrb.setStatus(EStatus.SUCCEED);
    
    if (rMessage != null) {
      //wrb.setPayloadType(rMessage.getDescriptorForType().getName());
    	wrb.setPayloadType(rMessage.getDescriptorForType().getFullName());
      wrb.setPayload(rMessage.toByteString());
    } else {
      LOG.warn(String.format("No response message of the action %s", actionContext.getName()));
    }
    ProtobufUtil.writeResponse(response, wrb.build(), allowCaching);
  }

  /**
   * @return the defaultCharset
   */
  public String getDefaultCharset() {
    return defaultCharset;
  }

  /**
   * @param defaultCharset
   *          the defaultCharset to set
   */
  @Inject(StrutsConstants.STRUTS_I18N_ENCODING)
  public void setDefaultCharset(String defaultCharset) {
    this.defaultCharset = defaultCharset;
  }

  /**
   * @return the allowCache
   */
  public boolean isAllowCaching() {
    return allowCaching;
  }

  /**
   * @param allowCache
   *          the allowCache to set
   */
  public void setAllowCaching(boolean allowCaching) {
    this.allowCaching = allowCaching;
  }

}
