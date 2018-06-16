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
package com.junahan.struts2.protobuf.test.bean;

import java.util.ArrayList;
import java.util.Collection;

public class RepeatedFieldBean {
	private Collection<Integer> ids;
	private Collection<EnumAliasBean> testMessages;

	public RepeatedFieldBean() {
		ids = new ArrayList<Integer>();
		testMessages = new ArrayList<EnumAliasBean>();
	}

	public Collection<Integer> getIds() {
		return ids;
	}

	public void setIds(Collection<Integer> ids) {
		this.ids = ids;
	}

	public Collection<EnumAliasBean> getTestMessages() {
		return testMessages;
	}

	public void addId(Integer id) {
		this.ids.add(id);
	}
	
	public void addAllIds(Collection<Integer> ids) {
		this.ids.addAll(ids);
	}
	
	public void setTestMessages(Collection<EnumAliasBean> testMessages) {
		this.testMessages = testMessages;
	}
	
	public void addTestMessage(EnumAliasBean testMessage) {
		this.testMessages.add(testMessage);
	}
	
	public void addAllTestMessage(Collection<EnumAliasBean> testMessages) {
		this.testMessages.addAll(testMessages);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ids == null) ? 0 : ids.hashCode());
		result = prime * result + ((testMessages == null) ? 0 : testMessages.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RepeatedFieldBean)) {
			return false;
		}
		RepeatedFieldBean other = (RepeatedFieldBean) obj;
		if (ids == null) {
			if (other.ids != null) {
				return false;
			}
		} else if (!ids.equals(other.ids)) {
			return false;
		}
		if (testMessages == null) {
			if (other.testMessages != null) {
				return false;
			}
		} else if (!testMessages.equals(other.testMessages)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "RepeatedFieldBean [ids=" + ids + ", testMessages=" + testMessages + "]";
	}
	
}
