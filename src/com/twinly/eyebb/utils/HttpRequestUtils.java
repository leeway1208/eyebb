package com.twinly.eyebb.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

import com.twinly.eyebb.constant.HttpConstants;

public class HttpRequestUtils {
	static HttpClient httpClient;

	public HttpRequestUtils() {
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		// The default value is zero, that means the timeout is not used.
		int timeoutConnection = 3000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT)
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = 3000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		httpClient = new DefaultHttpClient(httpParameters);
	}

	private static String TAG = "HttpRequestUtils";

	private static String getParameters(Map<String, String> map) {
		if (map == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			sb.append(entry.getKey()).append("=").append(entry.getValue());
			sb.append("&");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	private static List<NameValuePair> postParameters(Map<String, String> map) {
		if (map == null) {
			return null;
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		return params;
	}

	private static String getResponse(HttpEntity entity) {
		if (entity == null) {
			System.out.println("entity is null");
			return HttpConstants.HTTP_POST_RESPONSE_EXCEPTION;
		}
		try {
			// receive data
			BufferedReader in = new BufferedReader(new InputStreamReader(
					entity.getContent(), HTTP.UTF_8));
			StringBuilder response = new StringBuilder();
			String inputLine;

			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);

			in.close();
			return response.toString();
		} catch (IOException e) {
			System.out.println("error = " + e.getMessage());
			Log.e(TAG, e.getMessage());
			return HttpConstants.HTTP_POST_RESPONSE_EXCEPTION;
		}
	}

	public static String get(String action, Map<String, String> map) {
		String url = HttpConstants.SERVER_URL + action + "?"
				+ getParameters(map);

		System.out.println("url = " + url);

		/*
		 * AuthScope as = new AuthScope("158.182.246.221", 8089);
		 * UsernamePasswordCredentials upc = new UsernamePasswordCredentials(
		 * "master", "controller"); ((AbstractHttpClient)
		 * httpClient).getCredentialsProvider() .setCredentials(as, upc);
		 * BasicHttpContext localContext = new BasicHttpContext(); BasicScheme
		 * basicAuth = new BasicScheme();
		 * localContext.setAttribute("preemptive-auth", basicAuth); HttpHost
		 * targetHost = new HttpHost("158.182.246.221", 8089, "http");
		 */

		HttpGet get = new HttpGet(url);
		get.setHeader("Content-Type", "application/json");
		try {
			/*
			 * HttpResponse httpResponse = httpClient.execute(targetHost, get,
			 * localContext);
			 */
			HttpResponse httpResponse = httpClient.execute(get);
			return getResponse(httpResponse.getEntity());
		} catch (Exception e) {
			System.out.println("error = " + e.getMessage());
			return HttpConstants.HTTP_POST_RESPONSE_EXCEPTION;
		}
	}

	public static String post(String action, Map<String, String> map) {
		String url = HttpConstants.SERVER_URL + action;
		System.out.println("url = " + url);
		HttpPost post = new HttpPost(url);
		try {
			post.setEntity(new UrlEncodedFormEntity(postParameters(map),
					HTTP.UTF_8));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			HttpResponse httpResponse = httpClient.execute(post);
			return getResponse(httpResponse.getEntity());
		} catch (Exception e) {
			System.out.println("error = " + e.getMessage());
			return HttpConstants.HTTP_POST_RESPONSE_EXCEPTION;
		}
	}

}
