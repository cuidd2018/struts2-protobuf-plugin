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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.Test;

import com.github.junahan.struts2.ProtobufPopulator;
import com.github.junahan.struts2.test.bean.BazBean;
import com.github.junahan.struts2.test.bean.BazBean3;
import com.github.junahan.struts2.test.bean.EnumAliasBean;
import com.github.junahan.struts2.test.bean.FooBean;
import com.github.junahan.struts2.test.bean.FooBean3;
import com.github.junahan.struts2.test.bean.OuterBean;
import com.github.junahan.struts2.test.bean.RepeatedFieldBean;
import com.github.junahan.struts2.test.bean.RepeatedFieldBean2;
import com.github.junahan.struts2.test.bean.SampleMessageBean;
import com.github.junahan.struts2.test.bean.SubMessageBean;
import com.github.junahan.struts2.test.protocol.Baz2;
import com.github.junahan.struts2.test.protocol.Baz3;
import com.github.junahan.struts2.test.protocol.EnumAliasTestMessage;
import com.github.junahan.struts2.test.protocol.EnumAllowingAlias;
import com.github.junahan.struts2.test.protocol.Foo;
import com.github.junahan.struts2.test.protocol.Foo3;
import com.github.junahan.struts2.test.protocol.Outer;
import com.github.junahan.struts2.test.protocol.ProtocolTest;
import com.github.junahan.struts2.test.protocol.RepeatedField;
import com.github.junahan.struts2.test.protocol.SampleMessage;
import com.github.junahan.struts2.test.protocol.SubMessage;

public class Message2BeanTest extends BasicTestCase {
	
	@Test
	public void allowingAliasEnumTest() throws Exception {
		EnumAliasBean testBean = new EnumAliasBean();
		testBean.setaField(1024);
		testBean.setaAllowingAliasEnum(EnumAllowingAlias.RUNNING);
		
		EnumAliasTestMessage.Builder eatmb = EnumAliasTestMessage.newBuilder();
		eatmb.setAField(testBean.getaField());
		eatmb.setAAllowingAliasEnum(EnumAllowingAlias.STARTED);
		
		EnumAliasBean testBean2 = new EnumAliasBean();
		ProtobufPopulator populator = new ProtobufPopulator();
		populator.populate(testBean2, eatmb.build());
		
		assertEquals(testBean, testBean2);
	}
	
	@Test
	public void repeatedFieldTest() throws Exception {
		RepeatedFieldBean testBean = new RepeatedFieldBean();
		testBean.addAllIds(generateIds(10));
		testBean.addAllTestMessage(generateTestBeans(10));
		// generate the message by the testBean.
		RepeatedField.Builder rfb = copyFrom(testBean);
		
		// do populate
		RepeatedFieldBean targetBean = new RepeatedFieldBean();
		ProtobufPopulator populator = new ProtobufPopulator();
		populator.populate(targetBean, rfb.build());
		
		// check result
		assertEquals(testBean, targetBean);
	}

	@Test
	public void populateToArrayTest() throws Exception {
		RepeatedFieldBean2 rfb2 = new RepeatedFieldBean2();
		Integer[] ids = new Integer[10];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = random.nextInt();
		}
		rfb2.setIds(ids);
		
		RepeatedField.Builder rfb = RepeatedField.newBuilder();
		for (Integer element:ids) {
			rfb.addIds(element);
		}
		
		RepeatedFieldBean2 result = new RepeatedFieldBean2();
		ProtobufPopulator populator = new ProtobufPopulator();
		populator.populate(result, rfb.build());
		
		assertEquals(rfb2, result);
	}
	
	@Test
	public void nestedTypeTest() throws Exception {
		OuterBean ob = new OuterBean();
		ob.setIval(2048);
		ob.setBooly(false);
		
		// generate the message by the testBean.
		Outer.MiddleAA.Inner.Builder omaib = Outer.MiddleAA.Inner.newBuilder();
		omaib.setIval(ob.getIval());
		omaib.setBooly(ob.isBooly());
		OuterBean result1 = new OuterBean();
		ProtobufPopulator populator = new ProtobufPopulator();
		populator.populate(result1, omaib.build());
		
		Outer.MiddleBB.Inner.Builder ombib = Outer.MiddleBB.Inner.newBuilder();
		ombib.setIval(ob.getIval());
		ombib.setBooly(ob.isBooly());
		OuterBean result2 = new OuterBean();
		populator.populate(result2, ombib.build());
		
		assertEquals(ob, result1);
		assertEquals(ob, result2);
	}
	
	@Test
	public void extensionMessageTest() throws Exception {
		FooBean fb = new FooBean();
		fb.setName("Wold Cup");
		fb.setBar(32);
		fb.setMisc(64);
		
		Foo.Builder fooBuilder = Foo.newBuilder();
		fooBuilder.setName(fb.getName())
			.setExtension(ProtocolTest.bar, fb.getBar())
			.setExtension(ProtocolTest.misc, fb.getMisc());
		
		FooBean result = new FooBean();
		ProtobufPopulator populator = new ProtobufPopulator();
		populator.populate(result, fooBuilder.build());
		
		// check result
		assertEquals(fb, result);
	}
	
	@Test
	public void extensionNestedMessageTest() throws Exception {
		FooBean fb = new FooBean();
		BazBean baz = new BazBean();
		baz.setMisc(2^10);
		fb.setName(UUID.randomUUID().toString());
		fb.setBar(random.nextInt());
		fb.setMisc(random.nextInt());
		fb.setFooExt(baz);
		
		Foo.Builder fooBuilder = Foo.newBuilder();
		Baz2.Builder bazBuilder = Baz2.newBuilder();
		bazBuilder.setMisc(baz.getMisc());
		fooBuilder.setName(fb.getName())
			.setExtension(ProtocolTest.bar, fb.getBar())
			.setExtension(ProtocolTest.misc, fb.getMisc())
			.setExtension(Baz2.fooExt, bazBuilder.build());
		
		FooBean result = new FooBean();
		ProtobufPopulator populator = new ProtobufPopulator();
		populator.populate(result, fooBuilder.build());
		
		// check result
		assertEquals(fb, result);
	}
	
	@Test
	public void oneOfMessageTest() throws Exception {
		SubMessageBean sub = new SubMessageBean();
		sub.setSubName("sub");
		SampleMessageBean sampleBean = new SampleMessageBean();
		sampleBean.setName("sample");
		sampleBean.setSubMessage(sub);
		
		SampleMessage.Builder smb = SampleMessage.newBuilder();
		smb.setName(sampleBean.getName());
		
		SampleMessageBean result = new SampleMessageBean();
		ProtobufPopulator populator = new ProtobufPopulator();
		populator.populate(result, smb.build());
		assertEquals(sampleBean.getName(), result.getName());
		assertNull(result.getSubMessage());
		
		SubMessage.Builder subBuilder = SubMessage.newBuilder();
		smb.setSubMessage(subBuilder.setSubName(sampleBean.getSubMessage().getSubName()).build());
		SampleMessageBean result2 = new SampleMessageBean();
		populator.populate(result2, smb.build());
		assertEquals(sampleBean.getSubMessage(), result2.getSubMessage());
		assertNull(result2.getName());
	}
	
	@Test(expected=com.github.junahan.struts2.ProtobufException.class)
	public void populateMapField() throws Exception {
		FooBean3 fb3 = new FooBean3();
		Map<String, BazBean3> bazs = new HashMap<String, BazBean3>();
		for (int i = 0; i < 10; i++) {
			BazBean3 bean = new BazBean3();
			bean.setName(UUID.randomUUID().toString());
			bazs.put(UUID.randomUUID().toString(), bean);
		}
		fb3.setBazs(bazs);
		
		Foo3.Builder f3b = Foo3.newBuilder();
		for (String key:bazs.keySet()) {
			BazBean3 value = bazs.get(key);
			f3b.putBazs(key, Baz3.newBuilder().setName(value.getName()).build());
		}
		
		FooBean3 result = new FooBean3();
		ProtobufPopulator populator = new ProtobufPopulator();
		populator.populate(result, f3b.build());
		
		//assertEquals(result, fb3);
	}
	
	private RepeatedField.Builder copyFrom(RepeatedFieldBean bean) {
		RepeatedField.Builder rfb = RepeatedField.newBuilder();
		if (bean != null) {
			Collection<Integer> ids = bean.getIds();
			rfb.addAllIds(ids);
			Collection<EnumAliasBean> messages = bean.getTestMessages();
			for (EnumAliasBean message:messages) {
				rfb.addTestMessages(copyFrom(message).build());
			}
		}
		return rfb;
	}
	
	private EnumAliasTestMessage.Builder copyFrom(EnumAliasBean bean) {
		EnumAliasTestMessage.Builder etmb = EnumAliasTestMessage.newBuilder();
		if (bean != null) {
			etmb.setAField(bean.getaField());
			etmb.setAAllowingAliasEnum(bean.getaAllowingAliasEnum());
		}
		return etmb;
	}
	
	private Collection<Integer> generateIds(int number) {
		Collection<Integer> ids = new ArrayList<Integer>();
		for (int i = 0; i < number; i++) {
			ids.add(i);
		}
		return ids;
	}
	
	private Collection<EnumAliasBean> generateTestBeans(int number) {
		Collection<EnumAliasBean> testBeans = new ArrayList<EnumAliasBean>();
		for (int i = 0; i < number; i++) {
			EnumAliasBean testBean = new EnumAliasBean();
			testBean.setaField(random.nextInt());
			testBean.setaAllowingAliasEnum(EnumAllowingAlias.forNumber(random.nextInt(2)));
			testBeans.add(testBean);
		}
		return testBeans;
	}
}
