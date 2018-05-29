package az.lsim.proxy.http;

import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.io.InputStream;

public class ApacheHttpClientSplitter implements ApacheHttpClient {


    private ApacheHttpClient apacheHttpClient;


    public ApacheHttpClientSplitter(String host, int port) {
        if ((host != null && !host.equals("")) && port != 0) {
            apacheHttpClient = new ApacheHttpClientProxy(host, port);
            return;
        }
        apacheHttpClient = new ApacheHttpClientDirectly();
    }
    public ApacheHttpClientSplitter(){
        apacheHttpClient = new ApacheHttpClientDirectly();
    }


    @Override
    public String sendRequest(String url, ApacheHttpClientDirectly.HttpRequestMethod method) throws Exception {
        return null;
    }

    @Override
    public String sendPost(String url, String parameters) throws Exception {
        return apacheHttpClient.sendPost(url, parameters);
    }

    @Override
    public String sendGet(String url) throws Exception {
        return apacheHttpClient.sendGet(url);
    }

    @Override
    public String executeMethod(CloseableHttpClient httpClient, HttpUriRequest method) throws IOException, Exception {
        return apacheHttpClient.executeMethod(httpClient, method);
    }

    @Override
    public CloseableHttpClient getInsecureHttpClient() throws Exception {
        return apacheHttpClient.getInsecureHttpClient();
    }

    @Override
    public String getContentAsStringFromStream(InputStream stream) throws IOException {
        return apacheHttpClient.getContentAsStringFromStream(stream);
    }
}
