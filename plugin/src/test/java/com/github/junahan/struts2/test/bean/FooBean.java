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

public class FooBean {
	private String name;
	private int bar;
	private int misc;
	private BazBean fooExt;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBar() {
		return bar;
	}
	
	public void setBar(int bar) {
		this.bar = bar;
	}
	
	public int getMisc() {
		return misc;
	}
	
	public void setMisc(int misc) {
		this.misc = misc;
	}
	
	public BazBean getFooExt() {
		return fooExt;
	}
	
	public void setFooExt(BazBean fooExt) {
		this.fooExt = fooExt;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bar;
		result = prime * result + ((fooExt == null) ? 0 : fooExt.hashCode());
		result = prime * result + misc;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		if (!(obj instanceof FooBean)) {
			return false;
		}
		FooBean other = (FooBean) obj;
		if (bar != other.bar) {
			return false;
		}
		if (fooExt == null) {
			if (other.fooExt != null) {
				return false;
			}
		} else if (!fooExt.equals(other.fooExt)) {
			return false;
		}
		if (misc != other.misc) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return "Foo [name=" + name + ", bar=" + bar + ", misc=" + misc + ", fooExt=" + fooExt + "]";
	}
	
	
}
