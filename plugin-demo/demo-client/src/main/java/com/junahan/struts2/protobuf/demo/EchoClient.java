package com.junahan.struts2.protobuf.demo;

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
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.junahan.struts2.protobuf.protocol.WireRequest;
import com.junahan.struts2.protobuf.protocol.WireResponse;
import com.junahan.struts2.protobuf.utils.MessageConsts;

public class EchoClient {
	private static final Log LOG = LogFactory.getLog(EchoClient.class);
	private static final CloseableHttpClient httpclient = HttpClients.createDefault();
	
	public String doEcho(String message) throws Exception {
		String echoMessage = "";
		HttpPost httpPost = new HttpPost("http://localhost:8080/echo");
		httpPost.addHeader("content-type", MessageConsts.MIME_PROTOBUF);
		//httpPost.addHeader("content-type", "text/plain; charset=UTF-8");
		DemoRequest demoRequest = DemoRequest.newBuilder().setEchoMessage(message).build();
		WireRequest.Builder request = WireRequest.newBuilder();
		request.setPayloadType(DemoRequest.getDescriptor().getFullName());
		request.setPayload(demoRequest.toByteString());
		
		ByteArrayEntity requestEntity = new ByteArrayEntity(request.build().toByteArray());
		httpPost.setEntity(requestEntity);
		CloseableHttpResponse response = httpclient.execute(httpPost);
    LOG.debug(String.format("statusline: %s",response.getStatusLine()));
    LOG.debug(String.format("entity: %s", EntityUtils.toString(response.getEntity())));
		try {
		    StatusLine statusLine = response.getStatusLine();
        HttpEntity responseEntity = response.getEntity();
        if (statusLine.getStatusCode() != HttpStatus.SC_OK
        		&& statusLine.getStatusCode() != HttpStatus.SC_EXPECTATION_FAILED) {
            throw new HttpResponseException(statusLine.getStatusCode(), statusLine.toString());
        }
        // check the entity
        if (responseEntity == null) {
            throw new ClientProtocolException("Response contains no content");
        }
        Header contentType = responseEntity.getContentType();
        if (!contentType.getValue().equals(MessageConsts.MIME_PROTOBUF)) {
          throw new ClientProtocolException(String.format("Invalid contentType %s, expected %s.",
              contentType.getValue(), MessageConsts.MIME_PROTOBUF));
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
	}
}
