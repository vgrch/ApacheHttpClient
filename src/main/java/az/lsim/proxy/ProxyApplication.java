package az.lsim.proxy;

import az.lsim.proxy.http.ApacheHttpClient;
import az.lsim.proxy.http.ApacheHttpClientSplitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class ProxyApplication {

    private static final Logger logger = LoggerFactory.getLogger(ProxyApplication.class);

    private ApacheHttpClient apacheHttpClient;

    @Value(value = "${url:ipecho.net/plain}")
    private String url;

    @Value("${host:}")
    private String host;

    @Value("${port:}")
    private String port;

    public static void main(String[] args) {
        SpringApplication.run(ProxyApplication.class, args);
    }


    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        try {
            int port = 0;
            logger.info("Application started");
            if (!url.equals("") && (!url.toLowerCase().startsWith("http://") || !url.toLowerCase().startsWith("https://"))) {
                url = "http://" + url;
            }
            try {
                port = Integer.parseInt(this.port);
            } catch (Exception ex) {
                port = 0;
            }
            logger.info("url : " + url);
            logger.info("host : " + host);
            logger.info("port : " + port);
            apacheHttpClient = new ApacheHttpClientSplitter(host, port);
            logger.info(apacheHttpClient.sendGet(url));
        } catch (Exception ex) {
            logger.error("", ex);
        }
    }


}
