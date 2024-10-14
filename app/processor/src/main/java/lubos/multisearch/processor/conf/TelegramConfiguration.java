package lubos.multisearch.processor.conf;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

@Configuration
@ConfigurationPropertiesScan("lubos.multisearch.processor.conf")
public class TelegramConfiguration {
    @Bean
    OkHttpTelegramClient telegramClient(BotInfo botInfo) {
        return new OkHttpTelegramClient(botInfo.token());
    }
}
