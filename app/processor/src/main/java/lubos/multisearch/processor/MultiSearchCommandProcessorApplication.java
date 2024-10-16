package lubos.multisearch.processor;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
//@ConfigurationPropertiesScan("lubos.multisearch.processor.conf")
public class MultiSearchCommandProcessorApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(MultiSearchCommandProcessorApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }

//// ELASTIC - УЖЕ ЕСТЬ КАКОЙ-ТО ВСТРОЕННЫЙ КОННЕКШЕН ПУЛ ДЛЯ КЛИЕНТА (НО НЕ НАСТРАИВАЕТСЯ ЧЕРЕЗ application.yml)
//    @Bean
//    RestClientBuilderCustomizer restClientBuilderCustomizer(){
//        return builder -> {
//            builder.setHttpClientConfigCallback(
//                    httpClientBuilder -> {
//                        httpClientBuilder.setMaxConnTotal();
//                        httpClientBuilder.setMaxConnPerRoute();
//                        httpClientBuilder.setConnectionManager()
//                    }
//            )
//        }
//    }

}
