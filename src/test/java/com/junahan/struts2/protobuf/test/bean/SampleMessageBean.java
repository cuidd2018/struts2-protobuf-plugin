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

public class SampleMessageBean {
	private String name;
	private SubMessageBean subMessage;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public SubMessageBean getSubMessage() {
		return subMessage;
	}
	public void setSubMessage(SubMessageBean subMessage) {
		this.subMessage = subMessage;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((subMessage == null) ? 0 : subMessage.hashCode());
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
		if (!(obj instanceof SampleMessageBean)) {
			return false;
		}
		SampleMessageBean other = (SampleMessageBean) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (subMessage == null) {
			if (other.subMessage != null) {
				return false;
			}
		} else if (!subMessage.equals(other.subMessage)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "SampleMessageBean [name=" + name + ", subMessage=" + subMessage + "]";
	}

}
