package lubos.multisearch.telegrambot.bot.commands;

import lombok.extern.slf4j.Slf4j;
import lubos.multisearch.telegrambot.bot.commands.processing.CommandHandler;
import lubos.multisearch.telegrambot.bot.commands.processing.ParametersExtractor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.abilitybots.api.objects.ReplyCollection;
import org.telegram.telegrambots.abilitybots.api.util.AbilityExtension;

import static lombok.AccessLevel.PRIVATE;


@Setter
@Getter
@Accessors(fluent = true, chain = true)
@FieldDefaults(level = PRIVATE)
public class TelegramCommand implements AbilityExtension {

    Command command;
    Ability ability;
    ReplyCollection replyCollection;
    CommandHandler commandHandler;
    ParametersExtractor parametersExtractor;

}
