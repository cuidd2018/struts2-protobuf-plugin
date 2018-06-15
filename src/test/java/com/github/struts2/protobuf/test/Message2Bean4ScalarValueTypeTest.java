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
package com.github.struts2.protobuf.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.struts2.protobuf.ProtobufPopulator;
import com.github.struts2.protobuf.test.ScalarValueTypeTestMessage;
import com.github.struts2.protobuf.test.bean.ScalarValueTypeBean;

public class Message2Bean4ScalarValueTypeTest {
	private ProtobufPopulator populator;
		
	@Before
	public void setup() {
		populator = new ProtobufPopulator();
	}
	
	@After
	public void teardown() {
		//populator = null;
	}
	
	@Test
	public void populateMessageToBeanWithScalarValueTypeTest() throws Exception {
		// create the test bean
		ScalarValueTypeBean testBean = TestUtil.generateScalarValueTypeBean();
		// build the test message.
		ScalarValueTypeTestMessage.Builder svtmb = TestUtil.generateScalarValueTypeTestMessageFromBean(testBean);
		
		// populate message to resultBean.
		ScalarValueTypeBean resultBean = new ScalarValueTypeBean();
		populator.populate(resultBean, svtmb.build());
		
		// check result.
		assertEquals(testBean, resultBean);
	}
	
	@Test
	public void uint32TypeWithMaxValueTest() throws Exception {
		// create the test bean
		ScalarValueTypeBean testBean = TestUtil.generateScalarValueTypeBean();

		// test max value.
		testBean.setaUint32(Integer.MAX_VALUE);
		// build the test message.
		ScalarValueTypeTestMessage.Builder svtmb = TestUtil.generateScalarValueTypeTestMessageFromBean(testBean);
		ScalarValueTypeBean resultBean = new ScalarValueTypeBean();
		populator.populate(resultBean, svtmb.build());
		assertEquals(testBean, resultBean);
	}
	
	@Test
	public void uint32TypeWithMinValueTest() throws Exception {
		// create the test bean
		ScalarValueTypeBean testBean = TestUtil.generateScalarValueTypeBean();

		// test min value.
		testBean.setaUint32(Integer.MIN_VALUE);
		// build the test message.
		ScalarValueTypeTestMessage.Builder svtmb = TestUtil.generateScalarValueTypeTestMessageFromBean(testBean);
		ScalarValueTypeBean resultBean = new ScalarValueTypeBean();
		populator.populate(resultBean, svtmb.build());
		assertEquals(testBean, resultBean);
	}
	
	@Test
	public void uint64TypeMaxValueTest() throws Exception {
		// create the test bean
		ScalarValueTypeBean testBean = TestUtil.generateScalarValueTypeBean();

		// test max value.
		testBean.setaUint64(Long.MAX_VALUE);
		// build the test message.
		ScalarValueTypeTestMessage.Builder svtmb = TestUtil.generateScalarValueTypeTestMessageFromBean(testBean);
		ScalarValueTypeBean resultBean = new ScalarValueTypeBean();
		populator.populate(resultBean, svtmb.build());
		assertEquals(testBean, resultBean);
	}
	
	@Test
	public void uint64TypeMinValueTest() throws Exception {
		// create the test bean
		ScalarValueTypeBean testBean = TestUtil.generateScalarValueTypeBean();

		// test min value.
		testBean.setaUint64(Long.MIN_VALUE);
		// build the test message.
		ScalarValueTypeTestMessage.Builder svtmb = TestUtil.generateScalarValueTypeTestMessageFromBean(testBean);
		ScalarValueTypeBean resultBean = new ScalarValueTypeBean();
		populator.populate(resultBean, svtmb.build());
		assertEquals(testBean, resultBean);		
	}
	
	@Test
	public void fixed32TypeMaxValueTest() throws Exception {
		// create the test bean
		ScalarValueTypeBean testBean = TestUtil.generateScalarValueTypeBean();

		// test max value.
		testBean.setaFixed32(Integer.MAX_VALUE);
		// build the test message.
		ScalarValueTypeTestMessage.Builder svtmb = TestUtil.generateScalarValueTypeTestMessageFromBean(testBean);
		ScalarValueTypeBean resultBean = new ScalarValueTypeBean();
		populator.populate(resultBean, svtmb.build());
		assertEquals(testBean, resultBean);			
	}
	
	@Test
	public void fixed32TypeMinValueTest() throws Exception {
		// create the test bean
		ScalarValueTypeBean testBean = TestUtil.generateScalarValueTypeBean();

		// test min value.
		testBean.setaFixed32(Integer.MIN_VALUE);
		// build the test message.
		ScalarValueTypeTestMessage.Builder svtmb = TestUtil.generateScalarValueTypeTestMessageFromBean(testBean);
		ScalarValueTypeBean resultBean = new ScalarValueTypeBean();
		populator.populate(resultBean, svtmb.build());
		assertEquals(testBean, resultBean);			
	}
	
	@Test
	public void fixed64TypeMaxValueTest() throws Exception {
		// create the test bean
		ScalarValueTypeBean testBean = TestUtil.generateScalarValueTypeBean();

		// test max value.
		testBean.setaFixed64(Long.MAX_VALUE);
		// build the test message.
		ScalarValueTypeTestMessage.Builder svtmb = TestUtil.generateScalarValueTypeTestMessageFromBean(testBean);
		ScalarValueTypeBean resultBean = new ScalarValueTypeBean();
		populator.populate(resultBean, svtmb.build());
		assertEquals(testBean, resultBean);			
	}
	
	@Test
	public void fixed64TypeMinValueTest() throws Exception {
		// create the test bean
		ScalarValueTypeBean testBean = TestUtil.generateScalarValueTypeBean();

		// test min value.
		testBean.setaFixed64(Long.MIN_VALUE);
		// build the test message.
		ScalarValueTypeTestMessage.Builder svtmb = TestUtil.generateScalarValueTypeTestMessageFromBean(testBean);
		ScalarValueTypeBean resultBean = new ScalarValueTypeBean();
		populator.populate(resultBean, svtmb.build());
		assertEquals(testBean, resultBean);			
	}
	
	@Test
	public void populateEmptyByteStringTest() throws Exception {
		ScalarValueTypeTestMessage.Builder svtmb = TestUtil.generateScalarValueTypeTestMessage();
		svtmb.clearABytes();
		ScalarValueTypeBean resultBean = new ScalarValueTypeBean();
		populator.populate(resultBean, svtmb.build());
		assertNotNull(resultBean);
		assertNull(resultBean.getaBytes());
	}
	
	@Test
	public void populateEmptyStringTest() throws Exception {
		ScalarValueTypeTestMessage.Builder svtmb = TestUtil.generateScalarValueTypeTestMessage();
		svtmb.clearAString();
		ScalarValueTypeBean resultBean = new ScalarValueTypeBean();
		populator.populate(resultBean, svtmb.build());
		assertNotNull(resultBean);
		assertNull(resultBean.getaString());		
	}
}
