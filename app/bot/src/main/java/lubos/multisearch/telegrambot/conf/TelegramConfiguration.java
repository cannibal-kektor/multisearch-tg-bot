package lubos.multisearch.telegrambot.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

@Configuration
public class TelegramConfiguration {

    @Bean
    OkHttpTelegramClient telegramClient(BotInfo botInfo) {
        return new OkHttpTelegramClient(botInfo.token());
    }
}
