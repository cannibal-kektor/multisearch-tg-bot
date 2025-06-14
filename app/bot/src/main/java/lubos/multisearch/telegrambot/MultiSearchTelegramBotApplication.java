package lubos.multisearch.telegrambot;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan("lubos.multisearch.telegrambot.conf")
//@EnableLoadTimeWeaving(aspectjWeaving = EnableLoadTimeWeaving.AspectJWeaving.ENABLED)
public class MultiSearchTelegramBotApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(MultiSearchTelegramBotApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
    }


}
