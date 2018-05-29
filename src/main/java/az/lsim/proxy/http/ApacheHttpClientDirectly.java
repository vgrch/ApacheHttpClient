package az.lsim.proxy.http;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApacheHttpClientDirectly implements ApacheHttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(ApacheHttpClientDirectly.class.getName());

    public enum HttpRequestMethod {
        GET,
        POST
    };

    public String sendRequest(String url, HttpRequestMethod method) throws Exception {
        if (url == null || url.trim().isEmpty()) {
            throw new Exception("url is empty or null");
        }
        url = url.trim();
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        if (method == HttpRequestMethod.GET) {
            return sendGet(url);
        } else if (method == HttpRequestMethod.POST) {
            return sendPost(url, null);
        }
        return null;
    }

    public String sendPost(String url, String parameters) throws Exception {
        url = EncodeUtil.encodeUrl(url);
        LOG.info("sendPost url=" + url);
        LOG.info("sendPost parameters=" + parameters);

        try (CloseableHttpClient httpClient = getInsecureHttpClient()) {
            HttpPost method = new HttpPost(url);

            method = postParamSplitter(parameters, method);

            // Deal with the response.
            // Use caution: ensure correct character encoding and is not binary data
            String response = executeMethod(httpClient, method);
            LOG.info("url apache client post=" + url + ";\n" + "resp apache client get=" + response);
            return response;
        }
    }
    public String sendGet(String url) throws Exception {
        url = EncodeUtil.encodeUrl(url);
        LOG.info("sendGet url=" + url);
        try (CloseableHttpClient httpClient = getInsecureHttpClient()) {
            // Create an instance of HttpClient.
            HttpGet get = new HttpGet(url);
            // Read the response body.
            String responseBodyStr = executeMethod(httpClient, get);
            LOG.info("sendGet url=" + url + ";\n" + "sendGet response=" + responseBodyStr);
            return responseBodyStr;
        }
    }

    public String executeMethod(CloseableHttpClient httpClient, HttpUriRequest method) throws IOException, Exception {
        CloseableHttpResponse response = httpClient.execute(method);
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_ACCEPTED) {
            LOG.info("Method failed: " + response.getStatusLine());
            throw new Exception("Method failed: " + response.getStatusLine());
        }

        // Read the response body.
        String responseBodyStr = getContentAsStringFromStream(response.getEntity().getContent());

        return responseBodyStr;
    }

    public String sendPostCoreJava(String url, String json) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            wr.writeBytes(json);
            wr.flush();
        }
        int responseCode = con.getResponseCode();
        StringBuilder response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        LOG.info("url apache client post=" + url + ";\n" + "resp apache client post=" + response);

        return response.toString();
    }

    public CloseableHttpClient getInsecureHttpClient() throws Exception {
        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                builder.build(), SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(
                sslsf).build();
        return httpClient;
    }

    public String getContentAsStringFromStream(InputStream stream) throws IOException {
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(stream));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

}
