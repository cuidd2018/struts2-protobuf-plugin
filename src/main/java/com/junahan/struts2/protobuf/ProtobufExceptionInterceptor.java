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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.junahan.struts2.protobuf.utils.ProtobufUtil;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

/**
 * <!-- START SNIPPET: description -->
 * 
 * Serialize the exception as protobuffer message response in case of the action
 * is <code>ProtobufResponseAware</code>.
 * 
 * <b>Note:</b> The interceptor only has effect if the action is
 * <code>ProtobufResponseAware</code>. It will not have any effect if this interceptor is not
 * in the interceptor stack for your actions. It is recommended that you make
 * this interceptor the first interceptor on the stack, ensuring that it has
 * full access to catch any exception, even those caused by other interceptors.
 * 
 * <!-- END SNIPPET: description -->
 * 
 * <p/>
 * <u>Interceptor parameters:</u>
 * 
 * <!-- START SNIPPET: parameters -->
 * 
 * <ul>
 * 
 * <li>logEnabled (optional) - Should exceptions also be logged? (boolean
 * true|false)</li>
 * 
 * <li>logLevel (optional) - what log level should we use (
 * <code>trace, debug, info, warn, error, fatal</code>)? - defaut is
 * <code>debug</code></li>
 * 
 * <li>logCategory (optional) - If provided we would use this category (eg.
 * <code>com.mycompany.app</code>). Default is to use
 * <code>com.junahan.struts2.protobuf.ProtobutExceptionInterceptor</code>.</li>
 * 
 * </ul>
 * 
 * The parameters above enables us to log all thrown exceptions with stacktace
 * in our own logfile, and present a friendly message (with no stacktrace) to
 * the end user.
 * 
 * <!-- END SNIPPET: parameters -->
 * 
 * 
 * <p/>
 * <u>Example code:</u>
 * 
 * <pre>
 * <!-- START SNIPPET: example -->
 * &lt;struts&gt;
 *     &lt;package name="default" extends="protobuf-default"&gt;
 *         &lt;action name="test"&gt;
 *             &lt;interceptor-ref name="protobufException"/&gt;
 *             &lt;result type="protobuf"/&gt;;
 *         &lt;/action&gt;
 *     &lt;/package&gt;
 * &lt;/struts&gt;
 * <!-- END SNIPPET: example -->
 * </pre>
 * 
 * @author Junahan - junahan@outlook.com 2018
 * @since 1.0.0
 */
public class ProtobufExceptionInterceptor extends AbstractInterceptor {
  private static final long serialVersionUID = -34928214664769425L;
  protected static final Logger LOG = LogManager.getLogger(ProtobufExceptionInterceptor.class);

  protected Logger categoryLogger;
  protected boolean logEnabled = false;
  protected boolean stackTraceEnabled = false;
  protected String logCategory;
  protected String logLevel;
  
  /**
   * @return the stackTraceEnabled
   */
  public boolean isStackTraceEnabled() {
    return stackTraceEnabled;
  }

  /**
   * @param stackTraceEnabled the stackTraceEnabled to set
   */
  public void setStackTraceEnabled(boolean stackTraceEnabled) {
    this.stackTraceEnabled = stackTraceEnabled;
  }

  public boolean isLogEnabled() {
    return logEnabled;
  }

  public void setLogEnabled(boolean logEnabled) {
    this.logEnabled = logEnabled;
  }

  public String getLogCategory() {
    return logCategory;
  }

  public void setLogCategory(String logCatgory) {
    this.logCategory = logCatgory;
  }

  public String getLogLevel() {
    return logLevel;
  }

  public void setLogLevel(String logLevel) {
    this.logLevel = logLevel;
  }

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {
    HttpServletRequest request = ServletActionContext.getRequest();
    HttpServletResponse response = ServletActionContext.getResponse();
    Object action = invocation.getAction();
    if (action instanceof ProtobufResponseAware) {
      try {
        return invocation.invoke();
      } catch (Exception exception) {
        if (isLogEnabled()) {
          handleLogging(exception);
        }
        // write the result of the exception.
        return sendExceptionMessage(request,response,exception,stackTraceEnabled);
      }
    } else {
      return invocation.invoke();
    }
  }
  
  /**
   * Serialize the intercepter exception as protobuf message response and 
   * send it directly by servlet response.
   * 
   * @param response - the response
   * @param exception - the catched exception form the interceptor 
   * @throws Exception
   */
  protected String sendExceptionMessage(Object action, HttpServletResponse response, Exception exception, 
      boolean stackTraceEnabled) throws Exception {
    ProtobufUtil.writeException(action, response, exception, stackTraceEnabled);
    return Action.NONE;
  }

  /**
   * Handles the logging of the exception.
   * 
   * @param e
   *          the exception to log.
   */
  protected void handleLogging(Exception e) {
    if (logCategory != null) {
      if (categoryLogger == null) {
        // init category logger
        categoryLogger = LogManager.getLogger(logCategory);
      }
      doLog(categoryLogger, e);
    } else {
      doLog(LOG, e);
    }
  }

  /**
   * Performs the actual logging.
   * 
   * @param logger
   *          the provided logger to use.
   * @param e
   *          the exception to log.
   */
  protected void doLog(Logger logger, Exception e) {
    if (logLevel == null) {
      logger.debug(e.getMessage(), e);
      return;
    }

    if ("trace".equalsIgnoreCase(logLevel)) {
      logger.trace(e.getMessage(), e);
    } else if ("debug".equalsIgnoreCase(logLevel)) {
      logger.debug(e.getMessage(), e);
    } else if ("info".equalsIgnoreCase(logLevel)) {
      logger.info(e.getMessage(), e);
    } else if ("warn".equalsIgnoreCase(logLevel)) {
      logger.warn(e.getMessage(), e);
    } else if ("error".equalsIgnoreCase(logLevel)) {
      logger.error(e.getMessage(), e);
    } else if ("fatal".equalsIgnoreCase(logLevel)) {
      logger.fatal(e.getMessage(), e);
    } else {
      throw new IllegalArgumentException("LogLevel [" + logLevel
          + "] is not supported");
    }
  }
}
