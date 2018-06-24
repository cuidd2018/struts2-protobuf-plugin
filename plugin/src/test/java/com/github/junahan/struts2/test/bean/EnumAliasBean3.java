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

import java.util.Objects;

public class EnumAliasBean3 {
	private EnumAllowingAlias aAllowingAliasEnum;

	public EnumAllowingAlias getaAllowingAliasEnum() {
		return aAllowingAliasEnum;
	}

	public void setaAllowingAliasEnum(EnumAllowingAlias aAllowingAliasEnum) {
		this.aAllowingAliasEnum = aAllowingAliasEnum;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		EnumAliasBean3 that = (EnumAliasBean3) o;
		return aAllowingAliasEnum == that.aAllowingAliasEnum;
	}

	@Override
	public int hashCode() {

		return Objects.hash(aAllowingAliasEnum);
	}

	@Override
	public String toString() {
		return "EnumAliasBean3{" +
				"aAllowingAliasEnum=" + aAllowingAliasEnum +
				'}';
	}
}
