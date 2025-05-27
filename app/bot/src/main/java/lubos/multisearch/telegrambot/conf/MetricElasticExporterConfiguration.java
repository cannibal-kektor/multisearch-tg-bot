package lubos.multisearch.telegrambot.conf;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.util.IOUtils;
import io.micrometer.core.ipc.http.HttpSender;
import io.micrometer.elastic.ElasticConfig;
import io.micrometer.elastic.ElasticMeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.export.elastic.ElasticProperties;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

@Configuration
public class MetricElasticExporterConfiguration {
    @Bean
    public ElasticMeterRegistry elasticMeterRegistry(ElasticConfig elasticConfig, Clock clock, SecureHttpSender httpsSender) {
        return ElasticMeterRegistry.builder(elasticConfig)
                .clock(clock)
                .httpClient(httpsSender)
                .build();
//        HttpsURLConnection.setDefaultHostnameVerifier()
    }

    @Bean
    public SecureHttpSender secureHttpSender(ElasticProperties properties, SslBundles sslBundles) {
        SslBundle sslBundle = sslBundles.getBundle("metric-elastic");
        SSLContext sslContext = sslBundle.createSslContext();
        return new SecureHttpSender(properties, sslContext.getSocketFactory());
    }

    public static class SecureHttpSender implements HttpSender {

        final int connectTimeout;
        final int readTimeout;
        final SSLSocketFactory sslSocketFactory;

        public SecureHttpSender(ElasticProperties properties, SSLSocketFactory sslSocketFactory) {
            connectTimeout = (int) properties.getConnectTimeout().toMillis();
            readTimeout = (int) properties.getReadTimeout().toMillis();
            this.sslSocketFactory = sslSocketFactory;
        }

        @Override
        public Response send(Request request) throws IOException {
            HttpURLConnection httpsCon = null;
            try {
                httpsCon = (HttpURLConnection) request.getUrl().openConnection();

                // if the connection is an instance of the HttpsURLConnection class, the ssl configuration will always been applied.
                if (httpsCon instanceof HttpsURLConnection httpsConnection) {
                    httpsConnection.setSSLSocketFactory(sslSocketFactory);
                }

                httpsCon.setConnectTimeout(connectTimeout);
                httpsCon.setReadTimeout(readTimeout);
                Method method = request.getMethod();
                httpsCon.setRequestMethod(method.name());

                for (var header : request.getRequestHeaders().entrySet()) {
                    httpsCon.setRequestProperty(header.getKey(), header.getValue());
                }

                if (method != Method.GET) {
                    httpsCon.setDoOutput(true);
                    try (OutputStream os = httpsCon.getOutputStream()) {
                        os.write(request.getEntity());
                        os.flush();
                    }
                }

                int status = httpsCon.getResponseCode();

                String body = null;
                try {
                    if (httpsCon.getErrorStream() != null) {
                        body = IOUtils.toString(httpsCon.getErrorStream());
                    } else if (httpsCon.getInputStream() != null) {
                        body = IOUtils.toString(httpsCon.getInputStream());
                    }
                } catch (IOException ignored) {
                }

                return new Response(status, body);

            } finally {
                try {
                    if (httpsCon != null) {
                        httpsCon.disconnect();
                    }
                } catch (Exception ignore) {
                }
            }
        }
    }
}
