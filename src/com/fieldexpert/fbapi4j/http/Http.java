package com.fieldexpert.fbapi4j.http;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import com.fieldexpert.fbapi4j.common.Attachment;

public class Http {
	public static final String GET = "GET";
	public static final String POST = "POST";

	public static InputStream get(URL url, Map<String, String> parameters) {
		return req(GET, url, parameters, null, null);
	}

	public static InputStream post(URL url, Map<String, String> parameters) {
		return req(POST, url, parameters, null, null);
	}

	public static InputStream post(URL url, Map<String, String> parameters, List<Attachment> attachments) {
		return req(POST, url, parameters, null, null, attachments);
	}

	public static InputStream req(String method, URL url, Map<String, String> parameters, Integer connectTimeOut, Integer readTimeOut) {
		return req(method, url, parameters, connectTimeOut, readTimeOut, null);
	}

	public static InputStream req(String method, URL url, Map<String, String> parameters, Integer connectTimeOut, Integer readTimeOut, List<Attachment> attachments) {
		boolean hasAttachments = attachments != null && !attachments.isEmpty();

		if (hasAttachments && !method.equals(POST)) {
			throw new IllegalArgumentException("Must use a POST request if sending attachments.");
		}

		InputStream is = null;

		try {
			HttpRequest request = null;
			if (GET.equals(method)) {
				request = new GetRequest(url, parameters, connectTimeOut, readTimeOut);
			} else if (POST.equals(method) && !hasAttachments) {
				request = new PostRequest(url, parameters, connectTimeOut, readTimeOut);
			} else if (POST.equals(method) && hasAttachments) {
				request = new MultiPartPostRequest(url, parameters, connectTimeOut, readTimeOut, attachments);
			} else {
				throw new IllegalStateException("Couldn't resolve HTTP method.");
			}

			is = request.execute();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return is;
	}
}
