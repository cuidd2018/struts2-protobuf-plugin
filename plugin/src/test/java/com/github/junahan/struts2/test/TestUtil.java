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
package com.github.junahan.struts2.test;

import com.github.junahan.struts2.protocol.EStatus;
import com.github.junahan.struts2.test.bean.ScalarValueTypeBean;
import com.github.junahan.struts2.test.protocol.ScalarValueTypeTestMessage;
import com.google.protobuf.ByteString;

public class TestUtil {
	
	public static ScalarValueTypeBean generateScalarValueTypeBean() {
		ScalarValueTypeBean testBean = new ScalarValueTypeBean();
		testBean.setaBool(true);
		testBean.setaBytes(ByteString.copyFrom("Dawin".getBytes()));
		testBean.setaDouble(64d);
		testBean.setaFixed32(323232);
		testBean.setaFixed64(646464l);
		testBean.setaFloat(64f);
		testBean.setaInt32(32);
		testBean.setaInt64(64l);
		testBean.setaSFixed32(3232);
		testBean.setaSFixed64(6464l);
		testBean.setaSmall32(0);
		testBean.setaSmall64(0l);
		testBean.setaUint32(100);
		testBean.setaUint64(100l);
		testBean.setaString("Seven Dwarfs");
		testBean.setaStatus(EStatus.SUCCEED);
		return testBean;
	}
	
	public static ScalarValueTypeTestMessage.Builder generateScalarValueTypeTestMessage() {
		ScalarValueTypeTestMessage.Builder svtmb = ScalarValueTypeTestMessage.newBuilder();
		
		svtmb.setABool(true);
		svtmb.setABytes(ByteString.copyFrom("Jobs".getBytes()))
			.setADouble(63d)
			.setAFixed32(313131)
			.setAFixed64(636363l)
			.setAFloat(63f)
			.setAInt32(31)
			.setAInt64(63)
			.setASFixed32(3131)
			.setASFixed64(6363l)
			.setASmall32(31)
			.setASmall64(63l)
			.setAUint32(303030)
			.setAUint64(606060l)
			.setAString("Snow White")
			.setAStatus(EStatus.SUCCEED);
		
		return svtmb;
	}	
	
	public static ScalarValueTypeTestMessage.Builder generateScalarValueTypeTestMessageFromBean(ScalarValueTypeBean testBean) {
		ScalarValueTypeTestMessage.Builder svtmb = ScalarValueTypeTestMessage.newBuilder();
		svtmb.setABool(testBean.isaBool())
			.setABytes(testBean.getaBytes())
			.setADouble(testBean.getaDouble())
			.setAFixed32(testBean.getaFixed32())
			.setAFixed64(testBean.getaFixed64())
			.setAFloat(testBean.getaFloat())
			.setAInt32(testBean.getaInt32())
			.setAInt64(testBean.getaInt64())
			.setASFixed32(testBean.getaSFixed32())
			.setASFixed64(testBean.getaSFixed64())
			.setASmall32(testBean.getaSmall32())
			.setASmall64(testBean.getaSmall64())
			.setAUint32(testBean.getaUint32())
			.setAUint64(testBean.getaUint64())
			.setAString(testBean.getaString())
			.setAStatus(testBean.getaStatus());
		
		return svtmb;
	}
}
