package az.lsim.proxy.http;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class ApacheHttpClientProxy implements ApacheHttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(ApacheHttpClientProxy.class.getName());


    private String host;
    private int port;
    private ApacheHttpClient apacheHttpClient = new ApacheHttpClientDirectly();


    public ApacheHttpClientProxy(String host,int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public String sendRequest(String url, ApacheHttpClientDirectly.HttpRequestMethod method) throws Exception {
        return apacheHttpClient.sendRequest(url,method);
    }


    @Override
    public String sendPost(String url, String parameters) throws Exception {
        url = EncodeUtil.encodeUrl(url);
        LOG.info("sendPost url=" + url);
        LOG.info("sendPost parameters=" + parameters);

        try (CloseableHttpClient httpClient = getInsecureHttpClient()) {
            HttpHost proxy = new HttpHost(this.host, this.port, "http");
            RequestConfig config = RequestConfig.custom().setProxy(proxy).build();

            HttpPost method = new HttpPost(url);
            method.setConfig(config);

            method = postParamSplitter(parameters, method);

            // Deal with the response.
            // Use caution: ensure correct character encoding and is not binary data
            String response = executeMethod(httpClient, method);
            LOG.info("url apache client post=" + url + ";\n" + "resp apache client get=" + response);
            return response;
        }
    }

    @Override
    public String sendGet(String url) throws Exception {
        try (CloseableHttpClient httpClient = getInsecureHttpClient()){
            HttpHost proxy = new HttpHost(this.host, this.port, "http");
            RequestConfig config = RequestConfig.custom().setProxy(proxy).build();
            HttpGet request = new HttpGet(url);
            request.setConfig(config);
            String responseBodyStr = executeMethod(httpClient,request);
            LOG.info("sendGet url=" + url + ";\n" + "sendGet response=" + responseBodyStr);
            return responseBodyStr;
        }
    }

    @Override
    public String executeMethod(CloseableHttpClient httpClient, HttpUriRequest method) throws IOException, Exception {
        return apacheHttpClient.executeMethod(httpClient,method);
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
