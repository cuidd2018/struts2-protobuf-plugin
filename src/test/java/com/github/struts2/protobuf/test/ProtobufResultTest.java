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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.StrutsJUnit4TestCase;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.junit.Test;

import com.github.struts2.protobuf.test.TestRequest;
import com.github.struts2.protobuf.test.TestResponse;
import com.github.struts2.protobuf.utils.MessageConsts;
import com.github.struts2.protobuf.protocol.ResponseError;
import com.github.struts2.protobuf.protocol.WireRequest;
import com.github.struts2.protobuf.protocol.WireResponse;

public class ProtobufResultTest extends StrutsJUnit4TestCase<ProtobufResultTest>{
	
	@Test
	public void echoTest() throws Exception {
		TestRequest.Builder trb = TestRequest.newBuilder();
		trb.setEchoMessage("ping pang");
		WireRequest.Builder wrb = WireRequest.newBuilder();
		wrb.setPayloadType(TestRequest.getDescriptor().getFullName());
		wrb.setPayload(trb.build().toByteString());
		request.setContent(wrb.build().toByteArray());
		request.addHeader("Content-Type", MessageConsts.MIME_PROTOBUF);
//		ActionProxy proxy = getActionProxy("/echo");
//		String result = proxy.execute();
		String result = executeAction("/echo");
		
    // compare
    assertNotNull(result);
    assertEquals(HttpServletResponse.SC_OK, response.getStatus());
    assertEquals(MessageConsts.MIME_PROTOBUF, response.getContentType());
    assertTrue(response.getContentLength() > 0);
    WireResponse wr = WireResponse.parseFrom(response.getContentAsByteArray());
    assertEquals(TestResponse.getDescriptor().getFullName(), wr.getPayloadType().toString());
    TestResponse tr = TestResponse.parseFrom(wr.getPayload());
    assertEquals(trb.getEchoMessage(), tr.getEchoMessage());
	}
	
	@Test
	public void withException() throws Exception {
		String result = executeAction("/actionWithExceptionTest");
		
    // compare
    assertNotNull(result);
    assertTrue(response.getStatus() != HttpServletResponse.SC_OK);
    assertEquals(MessageConsts.MIME_PROTOBUF, response.getContentType());
    WireResponse wr = WireResponse.parseFrom(response.getContentAsByteArray());
    assertTrue(wr.hasError());
    assertNotNull(wr.getError().getException());
	}
	
	@Test
	public void withFieldError() throws Exception {
		String result = executeAction("/fieldErrorTest");
		
    // compare
    assertNotNull(result);
    assertTrue(response.getStatus() != HttpServletResponse.SC_OK);
    assertEquals(MessageConsts.MIME_PROTOBUF, response.getContentType());
    WireResponse wr = WireResponse.parseFrom(response.getContentAsByteArray());
    assertTrue(wr.hasError());
    ResponseError re = wr.getError();
    assertNotNull(re);
    assertEquals(2, re.getFieldErrorsCount());
	}
	
	@Test
	public void withActionError() throws Exception {
		String result = executeAction("/actionErrorTest");
		
    // compare
    assertNotNull(result);
    assertTrue(response.getStatus() != HttpServletResponse.SC_OK);
    assertEquals(MessageConsts.MIME_PROTOBUF, response.getContentType());
    WireResponse wr = WireResponse.parseFrom(response.getContentAsByteArray());
    assertTrue(wr.hasError());
    ResponseError re = wr.getError();
    assertNotNull(re);
    assertEquals(1, re.getActionErrorsCount());
	}

  @Override
	protected String executeAction(String uri) throws ServletException, UnsupportedEncodingException {
    request.setRequestURI(uri);
  	ActionMapping mapping = getActionMapping(request);

    assertNotNull(mapping);
    Dispatcher.getInstance().serviceAction(request, response, mapping);

    return response.getContentAsString();	
	}
	
	@Override
	protected String getConfigPath() {
		return "struts-plugin.xml,struts-test.xml";
	}
	
}
