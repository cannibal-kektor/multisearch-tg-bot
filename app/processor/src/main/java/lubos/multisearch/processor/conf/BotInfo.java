package lubos.multisearch.processor.conf;


import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("bot")
public record BotInfo(String token,
                      String username,
                      long creatorId) {
}
