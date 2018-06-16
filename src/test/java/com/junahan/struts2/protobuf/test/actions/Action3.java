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
package com.junahan.struts2.protobuf.test.actions;

import java.util.Collection;

import com.junahan.struts2.protobuf.test.EnumAllowingAlias;
import com.junahan.struts2.protobuf.test.bean.FooBean;
import com.junahan.struts2.protobuf.test.bean.SampleMessageBean;
import com.junahan.struts2.protobuf.test.bean.ScalarValueTypeBean;
import com.opensymphony.xwork2.Action;

public class Action3 extends TestAction {
	private static final long serialVersionUID = -1807482863408461776L;
	private ScalarValueTypeBean scalValueTypeMessage;
	private FooBean foo;
	private SampleMessageBean sampleMessage;
	private Collection<String> keywords;
	private String requestExt;
	private EnumAllowingAlias alias;

	@Override
	public String execute() throws Exception {
		return Action.SUCCESS;
	}	
	
	public ScalarValueTypeBean getScalValueTypeMessage() {
		return scalValueTypeMessage;
	}
	
	public void setScalValueTypeMessage(ScalarValueTypeBean scalValueTypeMessage) {
		this.scalValueTypeMessage = scalValueTypeMessage;
	}
	
	public FooBean getFoo() {
		return foo;
	}
	
	public void setFoo(FooBean foo) {
		this.foo = foo;
	}
	
	public SampleMessageBean getSampleMessage() {
		return sampleMessage;
	}
	
	public void setSampleMessage(SampleMessageBean sampleMessage) {
		this.sampleMessage = sampleMessage;
	}
	
	public Collection<String> getKeywords() {
		return keywords;
	}
	
	public void setKeywords(Collection<String> keywords) {
		this.keywords = keywords;
	}
	
	public String getRequestExt() {
		return requestExt;
	}
	
	public void setRequestExt(String requestExt) {
		this.requestExt = requestExt;
	}
	
	public EnumAllowingAlias getAlias() {
		return alias;
	}
	
	public void setAlias(EnumAllowingAlias alias) {
		this.alias = alias;
	}
	
}
