package com.github.junahan.struts2.demo;

import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.github.junahan.struts2.demo.protocol.DemoRequest;
import com.github.junahan.struts2.protocol.WireRequest;
import com.github.junahan.struts2.protocol.WireResponse;
import com.github.junahan.struts2.util.MessageConsts;

public class EchoClient {
	private static final Log LOG = LogFactory.getLog(EchoClient.class);
	private static final CloseableHttpClient httpclient = HttpClients.createDefault();
	
	public String doEcho(String message) throws Exception {
		String echoMessage = "";
		HttpPost httpPost = new HttpPost("http://localhost:8080/echo");
		DemoRequest demoRequest = DemoRequest.newBuilder().setEchoMessage(message).build();
		WireRequest.Builder request = WireRequest.newBuilder();
		request.setPayloadType(DemoRequest.getDescriptor().getFullName());
		request.setPayload(demoRequest.toByteString());
		
		ByteArrayEntity requestEntity = new ByteArrayEntity(request.build().toByteArray(), 
				ContentType.create(MessageConsts.MIME_PROTOBUF));
		httpPost.addHeader(requestEntity.getContentType());
		httpPost.addHeader(requestEntity.getContentEncoding());
		httpPost.setEntity(requestEntity);

		CloseableHttpResponse response = httpclient.execute(httpPost);
		try {
		    StatusLine statusLine = response.getStatusLine();
        if (statusLine.getStatusCode() != HttpStatus.SC_OK) 
          throw new HttpResponseException(statusLine.getStatusCode(), statusLine.toString());

		    HttpEntity responseEntity = response.getEntity();
        // check the entity
        if (responseEntity == null) {
            throw new ClientProtocolException("Response contains no content");
        }
        
        // check the entity type
        Header contentType = responseEntity.getContentType();
        if (!contentType.getValue().contains(MessageConsts.MIME_PROTOBUF)) {
        	throw new ClientProtocolException(String.format("Invalid content type - {}, expected {}.",
        			contentType.getValue().toString(), MessageConsts.MIME_PROTOBUF));
        }
        
        byte[] payload = EntityUtils.toByteArray(responseEntity);
        WireResponse wr = WireResponse.parseFrom(payload);
        DemoRequest dr = DemoRequest.parseFrom(wr.getPayload());
        echoMessage = dr.getEchoMessage();
		    EntityUtils.consume(responseEntity);
		} finally {
		    response.close();
		}
		return echoMessage;
	}
	
	public static void main(String[] args) {
		EchoClient client = new EchoClient();
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		System.out.println("> Try any message and input '!q' to quit.");
		while(true) {
			System.out.print("> ");
			String message = scanner.nextLine();
			if (message.trim().equals("!q")) {
				break;
			}
			try {
				String echoMessage = client.doEcho(message);
				System.out.println(echoMessage);
			} catch (Exception e) {
				System.out.println(e.getMessage());
				LOG.debug(e);
			}
		}
		try {
			httpclient.close();
		} catch (IOException e) {
			// do nothing
		}
	}
}
