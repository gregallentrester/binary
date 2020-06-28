package net.greg.examples.serverless;

import java.nio.charset.StandardCharsets;

import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ApiGatewayResponse {

	private final int statusCode;
	private final String body;
	private final Map<String, String> headers;
	private final boolean isBase64Encoded;

	public ApiGatewayResponse(
			int statusCodeVal,
			String bodyVal,
			Map<String, String> headersVal,
			boolean isBase64EncodedVal) {

		statusCode = statusCodeVal;
		body = bodyVal;
		headers = headersVal;
		isBase64Encoded = isBase64EncodedVal;
	}

	// API Gateway expects named property
	public boolean isIsBase64Encoded() { return isBase64Encoded; }
	public int getStatusCode() { return statusCode; }
	public String getBody() { return body; }
	public Map<String, String> getHeaders() { return headers; }


	public static Builder builder() { return new Builder(); }


	public static class Builder {

		private static final ObjectMapper mapper =
			new ObjectMapper();

		private int statusCode = 200;

		private Map<String, String> headers =
			Collections.emptyMap();

		private String rawBody;
		private Object objectBody;
		private byte[] binaryBody;
		private boolean base64Encoded;

		public Builder setStatusCode(int value) {

			statusCode = value;
			return this;
		}

		public Builder setHeaders(Map<String, String> value) {

			headers = value;
			return this;
		}

		/**
		 * Builds the {@link ApiGatewayResponse} using the passed raw body string.
		 */
		public Builder setRawBody(String value) {

			rawBody = value;
			return this;
		}

		/**
		 * Builds the {@link ApiGatewayResponse} using the passed object body
		 * converted to JSON.
		 */
		public Builder setObjectBody(Object value) {

			objectBody = value;
			return this;
		}

		/**
		 * Builds the {@link ApiGatewayResponse} using the passed binary body
		 * encoded as base64. {@link #setBase64Encoded(boolean)
		 * setBase64Encoded(true)} will be in invoked automatically.
		 */
		public Builder setBinaryBody(byte[] value) {

			binaryBody = value;
			setBase64Encoded(true);
			return this;
		}

		/**
		 * A base64encoded response requires:
		 * <ol>
		 * <li> "Binary Media Types" to be configured in API Gateway
		 * <li> "Accept" header set to one of the "Binary Media Types"
		 * </ol>
		 */
		public Builder setBase64Encoded(boolean value) {

			base64Encoded = value;
			return this;
		}

		public ApiGatewayResponse build() {

			String body = null;

			if (null != rawBody) {

				body = rawBody;
			}
			else if (objectBody != null) {

				try {

					body =
						mapper.writeValueAsString(objectBody);
				}
				catch (JsonProcessingException e) {

					System.err.println(
						"JsonProcessingException - failed object serialization ");

					throw new RuntimeException(e);
				}
			}
			else if (null != binaryBody) {

				body =
				  new String(
					  Base64.getEncoder().
						  encode(binaryBody), StandardCharsets.UTF_8);
			}

			return
				new ApiGatewayResponse(
					statusCode, body, headers, base64Encoded);
		}
	}
}
