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

public class OuterBean {
	private int ival;
	private boolean booly;
	private String misc;
	
	public int getIval() {
		return ival;
	}
	public void setIval(int ival) {
		this.ival = ival;
	}
	public boolean isBooly() {
		return booly;
	}
	public void setBooly(boolean booly) {
		this.booly = booly;
	}
	public String getMisc() {
		return misc;
	}
	public void setMisc(String misc) {
		this.misc = misc;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (booly ? 1231 : 1237);
		result = prime * result + (int) (ival ^ (ival >>> 32));
		result = prime * result + ((misc == null) ? 0 : misc.hashCode());
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
		if (!(obj instanceof OuterBean)) {
			return false;
		}
		OuterBean other = (OuterBean) obj;
		if (booly != other.booly) {
			return false;
		}
		if (ival != other.ival) {
			return false;
		}
		if (misc == null) {
			if (other.misc != null) {
				return false;
			}
		} else if (!misc.equals(other.misc)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "OuterBean [ival=" + ival + ", booly=" + booly + ", misc=" + misc + "]";
	}

}
