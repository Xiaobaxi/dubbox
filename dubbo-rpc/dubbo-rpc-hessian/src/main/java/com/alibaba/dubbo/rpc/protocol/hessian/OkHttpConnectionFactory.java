package com.alibaba.dubbo.rpc.protocol.hessian;

import com.alibaba.dubbo.common.Constants;
import com.caucho.hessian.client.HessianConnection;
import com.caucho.hessian.client.HessianConnectionFactory;
import com.caucho.hessian.client.HessianProxyFactory;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author fangzhibin
 *
 */
public class OkHttpConnectionFactory implements HessianConnectionFactory {

    private final OkHttpClient client;

    public OkHttpConnectionFactory(com.alibaba.dubbo.common.URL url) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.readTimeout(url.getParameter(Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT), TimeUnit.MILLISECONDS);
        builder.connectTimeout(url.getParameter(Constants.TIMEOUT_KEY, Constants.DEFAULT_TIMEOUT), TimeUnit.MILLISECONDS);
        client = builder.build();
    }

    @Override
    public void setHessianProxyFactory(HessianProxyFactory hessianProxyFactory) {
    }

    @Override
    public HessianConnection open(URL url) throws IOException {
        return new OkHttpConnection(client, url);
    }
}
