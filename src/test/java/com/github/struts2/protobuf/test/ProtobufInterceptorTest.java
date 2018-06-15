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
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.UUID;

import org.apache.struts2.StrutsJUnit4TestCase;
import org.apache.struts2.StrutsStatics;
import org.junit.Test;

import com.github.struts2.protobuf.ProtobufException;
import com.github.struts2.protobuf.ProtobufInterceptor;
import com.github.struts2.protobuf.test.EnumAllowingAlias;
import com.github.struts2.protobuf.test.Foo;
import com.github.struts2.protobuf.test.ProtocolTest;
import com.github.struts2.protobuf.test.ProtocolTest2;
import com.github.struts2.protobuf.test.ScalarValueTypeTestMessage;
import com.github.struts2.protobuf.test.TestRequest;
import com.github.struts2.protobuf.test.ProtocolTest2.TestEnum2;
import com.github.struts2.protobuf.test.ProtocolTest2.TestMessageA;
import com.github.struts2.protobuf.test.actions.Action1;
import com.github.struts2.protobuf.test.actions.Action2;
import com.github.struts2.protobuf.test.actions.Action3;
import com.github.struts2.protobuf.test.actions.Action4;
import com.github.struts2.protobuf.test.bean.FooBean;
import com.github.struts2.protobuf.test.bean.ScalarValueTypeBean;
import com.github.struts2.protobuf.utils.MessageConsts;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.mock.MockActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;
import com.github.struts2.protobuf.protocol.Protocol;
import com.github.struts2.protobuf.protocol.WireRequest;

public class ProtobufInterceptorTest extends StrutsJUnit4TestCase<ProtobufInterceptorTest> {
    private MockActionInvocationEx invocation;
    
    @Test(expected=ProtobufException.class)
    public void testMessageWithInvalidType() throws Exception {
    	WireRequest.Builder wrb = WireRequest.newBuilder();
    	Foo.Builder fb = Foo.newBuilder();
    	fb.setName("Snow Man");
    	wrb.setPayloadType(UUID.randomUUID().toString());
    	wrb.setPayload(fb.build().toByteString());
    	testBadProtobuf(wrb.build().toByteString());
    }
    
    private void testBadProtobuf(ByteString bytes) throws Exception {
      // construct a bad request
      request.setContent(bytes.toByteArray());
      request.addHeader("Content-Type", MessageConsts.MIME_PROTOBUF);
      
    	// interceptor
      ProtobufInterceptor interceptor = new ProtobufInterceptor();
      interceptor.setFileDescriptorClasses(Protocol.class.getName());
      interceptor.setCustomFileDescriptorClasses(ProtocolTest.class.getName());
      
    	Action1 action = new Action1();

    	this.invocation.setAction(action);
    	this.invocation.getStack().push(action);
    	
    	// do intercept 
    	interceptor.intercept(this.invocation);
    }

    @Test
    public void testScalarValuePopulating() throws Exception {
    	// request
    	ScalarValueTypeTestMessage.Builder svtmb = TestUtil.generateScalarValueTypeTestMessage();
    	generateProtobufRequest(svtmb.build());
    	
    	// interceptor
      ProtobufInterceptor interceptor = new ProtobufInterceptor();
      interceptor.setFileDescriptorClasses(Protocol.class.getName());
      interceptor.setCustomFileDescriptorClasses(ProtocolTest.class.getName());
      
    	Action1 action = new Action1();

    	this.invocation.setAction(action);
    	this.invocation.getStack().push(action);
    	
    	// do intercept 
    	interceptor.intercept(this.invocation);
    	assertTrue(invocation.isInvoked());
    	
    	// compare
    	assertEquals(svtmb.getABool(), action.isaBool());
    	//assertEquals(svtmb.getADouble(), action.getaDouble());
    	assertEquals(svtmb.getAFixed32(), action.getaFixed32());
    	assertEquals(svtmb.getAFixed64(), action.getaFixed64());
    	//assertEquals(svtmb.getAFloat(), action.getaFloat());
    	assertEquals(svtmb.getAInt32(), action.getaInt32());
    	assertEquals(svtmb.getAInt64(), action.getaInt64());
    	assertEquals(svtmb.getASFixed32(), action.getaSFixed32());
    	assertEquals(svtmb.getASFixed64(), action.getaSFixed64());
    	assertEquals(svtmb.getASmall32(), action.getaSmall32());
    	assertEquals(svtmb.getASmall64(), action.getaSmall64());
    	assertEquals(svtmb.getAString(), action.getaString());
    	assertEquals(svtmb.getAUint32(), action.getaUint32());
    	assertEquals(svtmb.getAUint64(), action.getaUint64());
    	assertEquals(svtmb.getABytes(), action.getaBytes());
    	assertEquals(svtmb.getAStatus(), action.getaStatus());
    	
    }
    
    // when action contains message type.
    @Test
    public void testMessage2MessagePopulating() throws Exception {
    	// request
    	TestRequest.Builder trb = TestRequest.newBuilder();
    	ScalarValueTypeTestMessage.Builder svtmb = TestUtil.generateScalarValueTypeTestMessage();
    	trb.setScalValueTypeMessage(svtmb.build());
    	Foo.Builder fb = Foo.newBuilder();
    	fb.setName("Nick");
    	fb.setExtension(ProtocolTest.bar, 10000);
    	trb.setFoo(fb.build());
    	trb.addKeywords("zoo");
    	trb.setAlias(EnumAllowingAlias.STARTED);
    	trb.setExtension(ProtocolTest.requestExt, "lighting");
    	generateProtobufRequest(trb.build());
    	
    	// interceptor
      ProtobufInterceptor interceptor = new ProtobufInterceptor();
      interceptor.setFileDescriptorClasses(Protocol.class.getName());
      interceptor.setCustomFileDescriptorClasses(ProtocolTest.class.getName());
      
    	Action2 action = new Action2();

    	this.invocation.setAction(action);
    	this.invocation.getStack().push(action);
    	
    	// do intercept 
    	interceptor.intercept(this.invocation);
    	assertTrue(invocation.isInvoked());
    	
    	// compare
    	assertNull(action.getSampleMessage());
    	assertEquals(fb.build(), action.getFoo());
    	assertEquals(svtmb.build(), action.getScalValueTypeMessage());
    	assertEquals(trb.getKeywordsCount(), action.getKeywords().size());
    	assertEquals(trb.getExtension(ProtocolTest.requestExt), action.getRequestExt());
    	assertEquals(trb.getAlias(), action.getAlias());
    }
    
    @Test
    public void testMessage2BeanPopulating() throws Exception {
    	ScalarValueTypeBean svb = TestUtil.generateScalarValueTypeBean();
    	TestRequest.Builder trb = TestRequest.newBuilder();
    	ScalarValueTypeTestMessage.Builder svtmb = TestUtil.generateScalarValueTypeTestMessageFromBean(svb);
    	trb.setScalValueTypeMessage(svtmb.build());
    	Foo.Builder fb = Foo.newBuilder();
    	fb.setName("a small cat");
    	fb.setExtension(ProtocolTest.bar, 120);
    	trb.setFoo(fb.build());
    	trb.addKeywords("pig");
    	trb.setAlias(EnumAllowingAlias.UNKNOWN);
    	trb.setExtension(ProtocolTest.requestExt, "dog");
    	generateProtobufRequest(trb.build());
    	
    	// interceptor
      ProtobufInterceptor interceptor = new ProtobufInterceptor();
      interceptor.setFileDescriptorClasses(Protocol.class.getName());
      interceptor.setCustomFileDescriptorClasses(ProtocolTest.class.getName());
      
    	Action3 action = new Action3();

    	this.invocation.setAction(action);
    	this.invocation.getStack().push(action);
    	
    	// do intercept 
    	interceptor.intercept(this.invocation);
    	assertTrue(invocation.isInvoked());
    	
    	// compare
    	assertNotNull(action.getScalValueTypeMessage());
    	assertEquals(svb, action.getScalValueTypeMessage());
    	FooBean foo = action.getFoo();
    	assertNotNull(action.getFoo());
    	assertEquals(fb.getName(),foo.getName());
    	assertEquals(fb.getExtension(ProtocolTest.bar).intValue(), foo.getBar());
    	assertNull(foo.getFooExt());
    	assertNotNull(action.getAlias());
    	assertEquals(trb.getAlias(), action.getAlias());
    	assertEquals(trb.getExtension(ProtocolTest.requestExt), action.getRequestExt());
    	Collection<String> keywords = action.getKeywords();
    	assertTrue(keywords.size() == 1);
    	assertEquals(trb.getKeywords(0), keywords.toArray()[0]);
    }
    
    @Test
    public void testWithSingleJavaFileOption() throws Exception {
    	// request
    	TestMessageA.Builder tmab = TestMessageA.newBuilder();
    	tmab.setEnumField(TestEnum2.ENUM_VALUE1);
    	tmab.setInt32Field(128);
    	tmab.setExtension(ProtocolTest2.stringField, "eating a word");
    	generateProtobufRequest(tmab.build());
    	
    	// interceptor
      ProtobufInterceptor interceptor = new ProtobufInterceptor();
      interceptor.setFileDescriptorClasses(Protocol.class.getName());
      StringBuilder sb = new StringBuilder();
      sb.append(ProtocolTest.class.getName()).append(",");
      sb.append(ProtocolTest2.class.getName());
      interceptor.setCustomFileDescriptorClasses(sb.toString());
      
    	Action4 action = new Action4();

    	this.invocation.setAction(action);
    	this.invocation.getStack().push(action);
    	
    	// do intercept 
    	interceptor.intercept(this.invocation);
    	assertTrue(invocation.isInvoked());
    	
    	// compare
    	assertNotNull(action.getInt32Field());
    	assertEquals(tmab.getInt32Field(), action.getInt32Field().intValue());
    	assertNotNull(action.getEnumField());
    	assertEquals(tmab.getEnumField(), action.getEnumField());
    	assertNotNull(action.getStringField());
    	assertEquals(tmab.getExtension(ProtocolTest2.stringField), action.getStringField());
    }
    
    private void generateProtobufRequest(Message message) throws Exception {
    	WireRequest.Builder wrb = WireRequest.newBuilder();
    	if(message != null) {
    		wrb.setPayloadType(message.getDescriptorForType().getFullName());
    		wrb.setPayload(message.toByteString());
    	}
      request.setContent(wrb.build().toByteArray());
      request.addHeader("Content-Type", MessageConsts.MIME_PROTOBUF);
    }
    
    @Override
		public void setUp() throws Exception {
        super.setUp();
        
        ActionContext context = ActionContext.getContext();
        ValueStack stack = context.getValueStack();

        ActionContext.setContext(context);
        context.put(StrutsStatics.HTTP_REQUEST, request);
        context.put(StrutsStatics.HTTP_RESPONSE, response);
        context.put(StrutsStatics.SERVLET_CONTEXT, servletContext);
        this.invocation = new MockActionInvocationEx();
        this.invocation.setInvocationContext(context);
        this.invocation.setStack(stack);
    }

		@Override
		protected String getConfigPath() {
			return "struts-plugin.xml, struts-test.xml";
		}
}

class MockActionInvocationEx extends MockActionInvocation {
    private boolean invoked;

    @Override
    public String invoke() throws Exception {
        this.invoked = true;
        return super.invoke();
    }

    public boolean isInvoked() {
        return this.invoked;
    }

    public void setInvoked(boolean invoked) {
        this.invoked = invoked;
    }
}
