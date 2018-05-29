package az.lsim.proxy.http;

public class Test {

    public static void main(String[] args) {
    try {
//      ApacheHttpClient apacheHttpClient = new ApacheHttpClientSplitter();
        ApacheHttpClient apacheHttpClient = new ApacheHttpClientSplitter("207.148.68.19",8080);
        String result = apacheHttpClient.sendGet("http://ipecho.net/plain");
        System.out.println(result);
    }catch (Exception ex) {
        ex.printStackTrace();
    }
    }
}
