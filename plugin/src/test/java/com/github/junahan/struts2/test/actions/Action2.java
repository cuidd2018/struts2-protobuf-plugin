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
package com.github.junahan.struts2.test.actions;

import java.util.Collection;

import com.github.junahan.struts2.test.protocol.EnumAllowingAlias;
import com.github.junahan.struts2.test.protocol.Foo;
import com.github.junahan.struts2.test.protocol.SampleMessage;
import com.github.junahan.struts2.test.protocol.ScalarValueTypeTestMessage;
import com.opensymphony.xwork2.Action;

public class Action2 extends TestAction {
	private static final long serialVersionUID = -4654776927954590190L;

	private ScalarValueTypeTestMessage scalValueTypeMessage;
	private Foo foo;
	private SampleMessage sampleMessage;
	private EnumAllowingAlias alias;
	private Collection<String> keywords;
	private String requestExt;
	
	@Override
	public String execute() throws Exception {
		return Action.SUCCESS;
	}

	public ScalarValueTypeTestMessage getScalValueTypeMessage() {
		return scalValueTypeMessage;
	}

	public void setScalValueTypeMessage(ScalarValueTypeTestMessage scalValueTypeMessage) {
		this.scalValueTypeMessage = scalValueTypeMessage;
	}

	public Foo getFoo() {
		return foo;
	}

	public void setFoo(Foo foo) {
		this.foo = foo;
	}

	public SampleMessage getSampleMessage() {
		return sampleMessage;
	}

	public void setSampleMessage(SampleMessage sampleMessage) {
		this.sampleMessage = sampleMessage;
	}

	public Collection<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Collection<String> keywords) {
		this.keywords = keywords;
	}

	public EnumAllowingAlias getAlias() {
		return alias;
	}

	public void setAlias(EnumAllowingAlias alias) {
		this.alias = alias;
	}

	public String getRequestExt() {
		return requestExt;
	}

	public void setRequestExt(String requestExt) {
		this.requestExt = requestExt;
	}

}
