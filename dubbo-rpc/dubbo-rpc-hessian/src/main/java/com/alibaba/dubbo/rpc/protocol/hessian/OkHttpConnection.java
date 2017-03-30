package com.alibaba.dubbo.rpc.protocol.hessian;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import com.caucho.hessian.client.AbstractHessianConnection;


/**
 * 
 * @author fangzhibin
 *
 */
public class OkHttpConnection extends AbstractHessianConnection {

    private final OkHttpClient okHttpClient;
    
    private final ByteArrayOutputStream output;

    private final Request.Builder builder;

    private volatile Response response;

    public OkHttpConnection(OkHttpClient client, URL url) {
        this.okHttpClient = client;
        this.output = new ByteArrayOutputStream();
        this.builder = new Request.Builder().url(url);
    }

    @Override
    public void addHeader(String key, String value) {
        builder.addHeader(key, value);
    }
    
    public OutputStream getOutputStream() throws IOException {
        return output;
    }

    @Override
    public void sendRequest() throws IOException {
        Request request = builder.post(RequestBody.create(null, output.toByteArray())).build();
        this.response = okHttpClient.newCall(request).execute();
    }

    @Override
    public int getStatusCode() {
        return response == null ? 0 : response.code();
    }

    @Override
    public String getStatusMessage() {
        return response == null ? null : response.message();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return response == null || response.body() == null ? null : response.body().byteStream();
    }

	@Override
	public void destroy() throws IOException {
	}
}
