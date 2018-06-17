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
package com.junahan.struts2.protobuf.demo.actions;

import com.google.protobuf.Message;
import com.junahan.struts2.protobuf.ProtobufResponseAware;
import com.junahan.struts2.protobuf.demo.DemoResponse;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

public class EchoAction extends ActionSupport implements ProtobufResponseAware {
	private static final long serialVersionUID = -2144322110047021579L;
	private String echoMessage;
	private DemoResponse responseMessage;
	
	@Override
	public String execute() throws Exception {
		if (echoMessage == null) echoMessage = " ";
		DemoResponse.Builder drb = DemoResponse.newBuilder();
		drb.setEchoMessage(echoMessage);
		responseMessage = drb.build();
		return Action.SUCCESS;
	}

	public String getEchoMessage() {
		return echoMessage;
	}

	public void setEchoMessage(String echoMessage) {
		this.echoMessage = echoMessage;
	}

	@Override
	public Message getResponseMessage() {
		return responseMessage;
	}
}
