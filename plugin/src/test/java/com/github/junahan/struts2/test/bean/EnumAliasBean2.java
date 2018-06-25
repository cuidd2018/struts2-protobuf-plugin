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
package com.github.junahan.struts2.test.bean;

import com.github.junahan.struts2.test.protocol.EnumAllowingAlias;

public class EnumAliasBean2 {
	private int aField;
	private EnumAllowingAlias aAllowingAliasEnum;
	
	public void setaField(int aField) {
		this.aField = aField;
	}

	public EnumAllowingAlias getaAllowingAliasEnum() {
		return aAllowingAliasEnum;
	}

	public void setaAllowingAliasEnum(EnumAllowingAlias aAllowingAliasEnum) {
		this.aAllowingAliasEnum = aAllowingAliasEnum;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aAllowingAliasEnum == null) ? 0 : aAllowingAliasEnum.hashCode());
		result = prime * result + aField;
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
		if (!(obj instanceof EnumAliasBean2)) {
			return false;
		}
		EnumAliasBean2 other = (EnumAliasBean2) obj;
		if (aAllowingAliasEnum != other.aAllowingAliasEnum) {
			return false;
		}
		if (aField != other.aField) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "EnumAliasTestBean [aField=" + aField + ", aAllowingAliasEnum=" + aAllowingAliasEnum + "]";
	}
	
}
