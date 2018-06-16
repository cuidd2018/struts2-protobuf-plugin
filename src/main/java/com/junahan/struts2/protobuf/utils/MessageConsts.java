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

/**
 * TODO - add document
 * 
 * @author Junahan - junahan@outlook.com 2018
 * @since 1.0.0
 */
public interface MessageConsts {

  /**
   * code for "succeed". 
   */
  String SUCCEED = "succeed";

  /**
   * code for "error".
   */
  String ERROR = "error";
  
  /**
   * The code for unsupported media type message.
   */
  String UNSUPPORTED_MEDIA_TYPE = "unsupported.media.type";
  
  /**
   * Mime type of Protobuf message
   */
  String MIME_PROTOBUF = "application/x-protobuffer";
  
  /**
   * Mime type of JSON
   */
  String MIME_JSON = "application/json";
  
  /**
   * Charset of UTF_8.
   */
  String UTF_8 = "UTF-8";

}
