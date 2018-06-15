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
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.struts2.StrutsJUnit4TestCase;
import org.junit.Test;

import com.github.struts2.protobuf.ProtobufValidationInterceptor;
import com.github.struts2.protobuf.test.actions.ActionTestWithActionError;
import com.github.struts2.protobuf.test.actions.ActionTestWithFieldError;
import com.github.struts2.protobuf.utils.MessageConsts;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionProxy;
import com.github.struts2.protobuf.protocol.EStatus;
import com.github.struts2.protobuf.protocol.FieldError;
import com.github.struts2.protobuf.protocol.ResponseError;
import com.github.struts2.protobuf.protocol.WireResponse;

public class ProtobufValidationInterceptorTest extends StrutsJUnit4TestCase<ProtobufValidationInterceptorTest> {
	
	@Test
	public void withFieldErrorTest() throws Exception {
		// request
		ActionProxy proxy = getActionProxy("/fieldErrorTest");
		ActionTestWithFieldError action = (ActionTestWithFieldError)proxy.getAction();
		
		// action execution
		action.validate();
		String result = action.execute();
		assertEquals(Action.ERROR, result);
		Map<String, List<String>> fieldErrors = action.getFieldErrors();
		assertEquals(2,fieldErrors.size());
		
		// interceptor 
		ProtobufValidationInterceptor interceptor = new ProtobufValidationInterceptor();
		interceptor.intercept(proxy.getInvocation());
		
		// compare
		String contentType = response.getContentType();
		byte[] content = response.getContentAsByteArray();
		assertEquals(MessageConsts.MIME_PROTOBUF,contentType);
		assertTrue(content.length > 0);
		WireResponse wr = WireResponse.parseFrom(content);
		assertNotNull(wr);
		assertEquals(EStatus.FAILED, wr.getStatus());
		ResponseError re = wr.getError();
		assertNotNull(re);
		List<FieldError> errors = re.getFieldErrorsList();
		assertEquals(2,errors.size());
		for (FieldError error:errors) {
			String name = error.getName();
			String message = error.getMessages(0);
			assertTrue(fieldErrors.containsKey(name));
			assertEquals(fieldErrors.get(name).get(0), message);
		}
	}
	
	@Test
	public void withValidationOnlyOptionTest() throws Exception {
		// request
		request.setParameter("struts.validationOnly", "true");
		ActionProxy proxy = getActionProxy("/fieldErrorTest");
		ActionTestWithFieldError action = (ActionTestWithFieldError)proxy.getAction();
		
		// action execution
		action.validate();
		Map<String, List<String>> fieldErrors = action.getFieldErrors();
		assertEquals(2,fieldErrors.size());
		
		// interceptor 
		ProtobufValidationInterceptor interceptor = new ProtobufValidationInterceptor();
		interceptor.intercept(proxy.getInvocation());
		
		// compare
		String contentType = response.getContentType();
		byte[] content = response.getContentAsByteArray();
		assertEquals(MessageConsts.MIME_PROTOBUF,contentType);
		assertTrue(content.length > 0);
		WireResponse wr = WireResponse.parseFrom(content);
		assertNotNull(wr);
		assertEquals(EStatus.FAILED, wr.getStatus());
		ResponseError re = wr.getError();
		assertNotNull(re);
		List<FieldError> errors = re.getFieldErrorsList();
		assertEquals(2,errors.size());
		for (FieldError error:errors) {
			String name = error.getName();
			String message = error.getMessages(0);
			assertTrue(fieldErrors.containsKey(name));
			assertEquals(fieldErrors.get(name).get(0), message);
		}
	}

	@Test
	public void withActionErrorTest() throws Exception {
		// request
		ActionProxy proxy = getActionProxy("/actionErrorTest");
		ActionTestWithActionError action = (ActionTestWithActionError)proxy.getAction();
		
		// action execution
		action.validate();
		Collection<String> actionErrors = action.getActionErrors();
		assertEquals(1,actionErrors.size());
		
		// interceptor 
		ProtobufValidationInterceptor interceptor = new ProtobufValidationInterceptor();
		interceptor.intercept(proxy.getInvocation());
		
		// compare
		String contentType = response.getContentType();
		byte[] content = response.getContentAsByteArray();
		assertEquals(MessageConsts.MIME_PROTOBUF,contentType);
		assertTrue(content.length > 0);
		WireResponse wr = WireResponse.parseFrom(content);
		assertNotNull(wr);
		assertEquals(EStatus.FAILED, wr.getStatus());
		ResponseError re = wr.getError();
		assertNotNull(re);
		assertEquals(1, re.getActionErrorsCount());
		assertEquals(actionErrors.toArray()[0], re.getActionErrors(0));
	}
	
	@Override
	protected String executeAction(String uri) throws ServletException, UnsupportedEncodingException {
		return super.executeAction(uri);
	}

	@Override
	protected String getConfigPath() {
		return "struts-plugin.xml, struts-test.xml";
	}
	
}
