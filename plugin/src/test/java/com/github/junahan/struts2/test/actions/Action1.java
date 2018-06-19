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

import com.github.junahan.struts2.protocol.EStatus;
import com.google.protobuf.ByteString;
import com.opensymphony.xwork2.Action;

public class Action1 extends TestAction {
	private static final long serialVersionUID = -1940722585717113635L;
  private int aInt32;
  private long aInt64;
  private float aFloat;
  private double aDouble;
  private int aUint32;
  private long aUint64;
  private int aSmall32;
  private long aSmall64;
  private int aFixed32;
  private long aFixed64;
  private int aSFixed32;
  private long aSFixed64;
  private boolean aBool;
  private String aString;
  private ByteString aBytes;
  private EStatus aStatus;
 
	@Override
	public String execute() throws Exception {
		return Action.SUCCESS;
	}

	public int getaInt32() {
		return aInt32;
	}

	public void setaInt32(int aInt32) {
		this.aInt32 = aInt32;
	}

	public long getaInt64() {
		return aInt64;
	}

	public void setaInt64(long aInt64) {
		this.aInt64 = aInt64;
	}

	public float getaFloat() {
		return aFloat;
	}

	public void setaFloat(float aFloat) {
		this.aFloat = aFloat;
	}

	public double getaDouble() {
		return aDouble;
	}

	public void setaDouble(double aDouble) {
		this.aDouble = aDouble;
	}

	public int getaUint32() {
		return aUint32;
	}

	public void setaUint32(int aUint32) {
		this.aUint32 = aUint32;
	}

	public long getaUint64() {
		return aUint64;
	}

	public void setaUint64(long aUint64) {
		this.aUint64 = aUint64;
	}

	public int getaSmall32() {
		return aSmall32;
	}

	public void setaSmall32(int aSmall32) {
		this.aSmall32 = aSmall32;
	}

	public long getaSmall64() {
		return aSmall64;
	}

	public void setaSmall64(long aSmall64) {
		this.aSmall64 = aSmall64;
	}

	public int getaFixed32() {
		return aFixed32;
	}

	public void setaFixed32(int aFixed32) {
		this.aFixed32 = aFixed32;
	}

	public long getaFixed64() {
		return aFixed64;
	}

	public void setaFixed64(long aFixed64) {
		this.aFixed64 = aFixed64;
	}

	public int getaSFixed32() {
		return aSFixed32;
	}

	public void setaSFixed32(int aSFixed32) {
		this.aSFixed32 = aSFixed32;
	}

	public long getaSFixed64() {
		return aSFixed64;
	}

	public void setaSFixed64(long aSFixed64) {
		this.aSFixed64 = aSFixed64;
	}

	public boolean isaBool() {
		return aBool;
	}

	public void setaBool(boolean aBool) {
		this.aBool = aBool;
	}

	public String getaString() {
		return aString;
	}

	public void setaString(String aString) {
		this.aString = aString;
	}

	public ByteString getaBytes() {
		return aBytes;
	}

	public void setaBytes(ByteString aBytes) {
		this.aBytes = aBytes;
	}

	public EStatus getaStatus() {
		return aStatus;
	}

	public void setaStatus(EStatus aStatus) {
		this.aStatus = aStatus;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
