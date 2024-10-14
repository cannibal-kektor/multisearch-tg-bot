package lubos.multisearch.telegrambot.model;

public record TelegramUser(
        Long telegramId,
        String username,
        String languageCode) {
}
