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
package com.github.struts2.protobuf.test.actions;

import com.github.struts2.protobuf.test.ProtocolTest2.TestEnum2;
import com.opensymphony.xwork2.Action;

public class Action4 extends TestAction {
	private static final long serialVersionUID = 4382920673939820128L;
	private Integer int32Field;
	private TestEnum2 enumField;
	private String stringField;
	
	@Override
	public String execute() throws Exception {
		return Action.SUCCESS;
	}
	
	public Integer getInt32Field() {
		return int32Field;
	}
	public void setInt32Field(Integer int32Field) {
		this.int32Field = int32Field;
	}
	public TestEnum2 getEnumField() {
		return enumField;
	}
	public void setEnumField(TestEnum2 enumField) {
		this.enumField = enumField;
	}
	public String getStringField() {
		return stringField;
	}
	public void setStringField(String stringField) {
		this.stringField = stringField;
	}
	
}
