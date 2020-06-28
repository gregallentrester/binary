package net.greg.examples.serverless;

import java.util.*;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;


public final class Handler
		implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	@Override
	public ApiGatewayResponse handleRequest(
			Map<String, Object> input, Context context) {

		Response responseBody =
			new Response("Current time " + new Date());

		Map<String, String> headers =
			new HashMap();

		headers.put("X-Powered-By", "AWS Lambda & Serverless");
		headers.put("Content-Type", "application/json");

		System.err.println("\n Received: " + input);

		return
			ApiGatewayResponse.
				builder().
				setStatusCode(200).
				setObjectBody(responseBody).
				setHeaders(headers).
				build();
	}
}
