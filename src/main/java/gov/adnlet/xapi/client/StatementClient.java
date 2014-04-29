package gov.adnlet.xapi.client;

import java.net.URI;
import java.net.URISyntaxException;

import com.google.gson.*;

import java.io.*;

import org.apache.http.*;
import org.apache.http.client.protocol.*;
import org.apache.http.impl.auth.*;
import org.apache.http.client.*;
import org.apache.http.impl.client.*;

import gov.adlnet.xapi.model.*;
import gov.adlnet.xapi.model.adapters.ActorAdapter;
import gov.adlnet.xapi.model.adapters.StatenentObjectAdapter;

import org.apache.http.client.methods.*;
import org.apache.http.auth.*;
import org.apache.http.entity.StringEntity;

public class StatementClient {
	private HttpClient _client = HttpClients.createDefault();
	private HttpClientContext _context;
	private HttpHost _host;

	public StatementClient(String uri, String user, String password)
			throws URISyntaxException {
		init(new URI(uri), user, password);
	}

	public StatementClient(URI uri, String user, String password) {
		init(uri, user, password);
	}

	private void init(URI uri, String user, String password) {
		this._host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
		CredentialsProvider credsProvider = new BasicCredentialsProvider();
		credsProvider.setCredentials(new AuthScope(this._host.getHostName(),
				this._host.getPort()), new UsernamePasswordCredentials(user,
				password));

		// Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		// Generate BASIC scheme object and add it to the local auth cache
		BasicScheme basicAuth = new BasicScheme();
		authCache.put(_host, basicAuth);

		// Add AuthCache to the execution context
		this._context = HttpClientContext.create();
		this._context.setCredentialsProvider(credsProvider);
		this._context.setAuthCache(authCache);
	}

	private Gson getDecoder() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Actor.class, new ActorAdapter());
		builder.registerTypeAdapter(IStatementObject.class,
				new StatenentObjectAdapter());
		return builder.create();
	}

	public boolean publishStatement(Statement statement)
			throws java.io.UnsupportedEncodingException, java.io.IOException {
		
		HttpPost post = new HttpPost("/xapi/statements");
		Gson gson = getDecoder();		
		String json = gson.toJson(statement);
		System.out.println("Got Here");
		System.out.println(json);
		post.setEntity(new StringEntity(json));
		post.addHeader("X-Experience-API-Version", "1.0");
		HttpResponse resp = this._client.execute(this._host, post,
				this._context);
		HttpEntity entity = resp.getEntity();
		InputStreamReader in = new InputStreamReader(entity.getContent());
		BufferedReader reader = new BufferedReader(in);
		System.out.println(resp.getStatusLine());
		try {
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			System.out.println(sb);
			return true;
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			return false;
		} finally {
			in.close();
			reader.close();
		}
	}

	public StatementCollection getStatements() throws java.io.IOException {
		HttpGet method = new HttpGet("/xapi/statements");
		method.addHeader("X-Experience-API-Version", "1.0");
		HttpResponse resp = this._client.execute(this._host, method,
				this._context);
		HttpEntity entity = resp.getEntity();
		Gson gson = this.getDecoder();
		InputStreamReader in = new InputStreamReader(entity.getContent());
		BufferedReader reader = new BufferedReader(in);
		try {
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			return gson.fromJson(sb.toString(), StatementCollection.class);
		} finally {
			in.close();
			reader.close();
		}

	}
}
