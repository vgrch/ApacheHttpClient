package az.lsim.proxy.http;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public interface ApacheHttpClient {

    public String sendRequest(String url, ApacheHttpClientDirectly.HttpRequestMethod method) throws Exception;
    public String sendPost(String url, String parameters) throws Exception;
    public String sendGet(String url) throws Exception;
    public String executeMethod(CloseableHttpClient httpClient, HttpUriRequest method) throws IOException, Exception;
    public CloseableHttpClient getInsecureHttpClient() throws Exception;
    public String getContentAsStringFromStream(InputStream stream) throws IOException;

    default public HttpPost postParamSplitter(String parameters,HttpPost method) throws UnsupportedEncodingException {
        if (parameters != null) {
            ArrayList<BasicNameValuePair> postParameters = new ArrayList<BasicNameValuePair>();
            String[] params = parameters.split("&");
            for (String param : params) {
                String[] parts = param.split("=");
                postParameters.add(new BasicNameValuePair(parts[0], parts[1]));
            }
            method.setEntity(new UrlEncodedFormEntity(postParameters));
        }
        return method;
    }
}
