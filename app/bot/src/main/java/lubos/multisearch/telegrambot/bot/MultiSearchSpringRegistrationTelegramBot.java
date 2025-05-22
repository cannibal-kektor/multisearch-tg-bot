package lubos.multisearch.telegrambot.bot;

import lubos.multisearch.telegrambot.conf.BotInfo;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class MultiSearchSpringRegistrationTelegramBot implements SpringLongPollingBot, LongPollingUpdateConsumer{

    final BotInfo botInfo;
    final ThreadPoolTaskExecutor taskExecutor;
    final MultiSearchBotUpdateConsumer updateConsumer;

    public MultiSearchSpringRegistrationTelegramBot(BotInfo botInfo, ThreadPoolTaskExecutor taskExecutor, MultiSearchBotUpdateConsumer updateConsumer) {
        this.botInfo = botInfo;
        this.taskExecutor = taskExecutor;
        this.updateConsumer = updateConsumer;
    }

    @Override
    public void consume(List<Update> updates) {
        updates.forEach(update -> taskExecutor.execute(() -> updateConsumer.consume(update)));
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public String getBotToken() {
        return botInfo.token();
    }
}
