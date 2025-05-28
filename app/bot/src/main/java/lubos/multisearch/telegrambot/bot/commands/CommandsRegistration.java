package lubos.multisearch.telegrambot.bot.commands;

import lubos.multisearch.telegrambot.bot.commands.processing.impl.LoggingConfiguringCommandHandler;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.abilitybots.api.objects.Privacy;

import java.util.Map;

import static lubos.multisearch.telegrambot.bot.commands.Command.DEFAULT;
import static lubos.multisearch.telegrambot.bot.utils.TelegramHelperUtils.send;
import static lubos.multisearch.telegrambot.bot.utils.TelegramHelperUtils.userLocale;
import static org.telegram.telegrambots.abilitybots.api.objects.Flag.DOCUMENT;
import static org.telegram.telegrambots.abilitybots.api.objects.Flag.TEXT;

@Configuration
public class CommandsRegistration {

    final TelegramCommandBuilder commandBuilder;
    final MessageSource messageSource;


    public CommandsRegistration(TelegramCommandBuilder commandBuilder, MessageSource messageSource) {
        this.commandBuilder = commandBuilder;
        this.messageSource = messageSource;
    }

    @Bean
    TelegramCommand banUserCommand(TelegramCommandBuilder builder) {
        return builder
                .command(Command.BAN)
                .commandInfo(CommandsConstants.BAN_INFO)
                .privacy(Privacy.ADMIN)
                .inputPattern(CommandsConstants.BAN_USER_INPUT_PATTERN)
                .withReply(CommandsConstants.SPECIFY_USER_TO_BAN, TelegramCommandBuilder.HAS_INPUT, TEXT)
                .addNewCallback(CommandsConstants.BAN_USER_CALLBACK_PATTERN, CommandsConstants.BAN_USER_CALLBACK)
                .build();
    }


    @Bean
    TelegramCommand deleteDocumentCommand(TelegramCommandBuilder builder) {
        return builder
                .command(Command.DELETE)
                .commandInfo(CommandsConstants.DELETE_DOCUMENT_INFO)
                .privacy(Privacy.PUBLIC)
                .inputPattern(CommandsConstants.DOCUMENT_DELETE_INPUT_PATTERN)
                .withReply(CommandsConstants.SPECIFY_DOCUMENT_TO_DELETE, TelegramCommandBuilder.HAS_INPUT, TEXT)
                .addNewCallback(CommandsConstants.DOCUMENT_DELETE_CALLBACK_PATTERN, CommandsConstants.DOC_DELETE_CALLBACK)
                .withMenuCallback()
                .build();
    }

    @Bean
    TelegramCommand demoteAdminCommand(TelegramCommandBuilder builder) {
        return builder
                .command(Command.DEMOTE)
                .commandInfo(CommandsConstants.DEMOTE_INFO)
                .privacy(Privacy.CREATOR)
                .inputPattern(CommandsConstants.DEMOTE_ADMIN_INPUT_PATTERN)
                .withReply(CommandsConstants.SPECIFY_ADMIN_TO_DEMOTE, TelegramCommandBuilder.HAS_INPUT, TEXT)
                .build();
    }


    @Bean
    TelegramCommand documentsContentsCommand(TelegramCommandBuilder builder) {
        return builder
                .command(Command.CONTENTS)
                .commandInfo(CommandsConstants.DOC_CONTENTS_INFO)
                .privacy(Privacy.PUBLIC)
                .inputPattern(CommandsConstants.DOCUMENT_CONTENTS_INPUT_PATTERN)
                .withReply(CommandsConstants.SPECIFY_DOCUMENT_TO_LIST_CONTENTS, TelegramCommandBuilder.HAS_INPUT, TEXT)
                .pageable(CommandsConstants.DOCUMENT_CONTENTS_PAGING_CALLBACK_PATTERN)
                .withMenuCallback()
                .build();
    }

    @Bean
    TelegramCommand infoCommand(TelegramCommandBuilder builder) {
        return builder
                .command(Command.INFO)
                .commandInfo(CommandsConstants.INFO_INFO)
                .privacy(Privacy.PUBLIC)
                .build();
    }

    @Bean
    TelegramCommand languageCommand(TelegramCommandBuilder builder) {
        return builder
                .command(Command.LANGUAGE)
                .commandInfo(CommandsConstants.LANGUAGE_INFO)
                .privacy(Privacy.PUBLIC)
                .addNewCallback(CommandsConstants.LANGUAGE_CHANGE_CALLBACK_PATTERN, CommandsConstants.LANGUAGE_CALLBACK)
                .build();
    }

    @Bean
    TelegramCommand availableCommandsCommand(TelegramCommandBuilder builder) {
        return builder
                .command(Command.COMMANDS)
                .commandInfo(CommandsConstants.COMMANDS_INFO)
                .privacy(Privacy.PUBLIC)
                .withMenuCallback()
                .build();
    }

    @Bean
    TelegramCommand listDocumentsCommand(TelegramCommandBuilder builder) {
        return builder
                .command(Command.DOCUMENTS)
                .commandInfo(CommandsConstants.LIST_DOCUMENTS_INFO)
                .privacy(Privacy.PUBLIC)
                .pageable(CommandsConstants.LIST_DOCUMENTS_PAGEABLE_CALLBACK_PATTERN)
                .withMenuCallback()
                .build();
    }

    @Bean
    TelegramCommand listRegistrationRequestsCommand(TelegramCommandBuilder builder) {
        return builder
                .command(Command.REGISTRATION_REQUESTS)
                .commandInfo(CommandsConstants.REGISTRATION_REQUESTS_INFO)
                .privacy(Privacy.ADMIN)
                .pageable(CommandsConstants.REGISTRATION_REQUEST_PAGEABLE_CALLBACK_PATTERN)
                .withMenuCallback()
                .build();
    }

    @Bean
    TelegramCommand listUsersCommand(TelegramCommandBuilder builder) {
        return builder
                .command(Command.USERS)
                .commandInfo(CommandsConstants.LIST_USERS_INFO)
                .privacy(Privacy.ADMIN)
                .pageable(CommandsConstants.LIST_USERS_PAGEABLE_CALLBACK_PATTERN)
                .withMenuCallback()
                .build();
    }

    @Bean
    TelegramCommand getChapterCommand(TelegramCommandBuilder builder) {
        return builder
                .command(Command.CHAPTER)
                .commandInfo(CommandsConstants.CHAPTER_INFO)
                .privacy(Privacy.PUBLIC)
                .inputPattern(CommandsConstants.CHAPTER_INPUT_PATTERN)
                .withReply(CommandsConstants.SPECIFY_CHAPTER_ID, TelegramCommandBuilder.HAS_INPUT, TEXT)
                .pageable(CommandsConstants.CHAPTER_PAGEABLE_CALLBACK_PATTERN)
                .addNewCallback(CommandsConstants.EXPAND_CHAPTER_CALLBACK_PATTERN, CommandsConstants.CHAPTER_EXPAND_CALLBACK)
                .build();
    }


    @Bean
    TelegramCommand promoteUserCommand(TelegramCommandBuilder builder) {
        return builder
                .command(Command.PROMOTE)
                .commandInfo(CommandsConstants.PROMOTE_INFO)
                .privacy(Privacy.CREATOR)
                .inputPattern(CommandsConstants.PROMOTE_USER_INPUT_PATTERN)
                .withReply(CommandsConstants.SPECIFY_USER_TO_PROMOTE, TelegramCommandBuilder.HAS_INPUT, TEXT)
                .build();
    }


    @Bean
    TelegramCommand registerUserCommand(TelegramCommandBuilder builder) {
        return builder
                .command(Command.REGISTER)
                .commandInfo(CommandsConstants.REGISTER_INFO)
                .privacy(Privacy.ADMIN)
                .inputPattern(CommandsConstants.REGISTER_USER_INPUT_PATTERN)
                .withReply(CommandsConstants.SPECIFY_USER_TO_APPROVE_REGISTRATION, TelegramCommandBuilder.HAS_INPUT, TEXT)
                .addNewCallback(CommandsConstants.REGISTER_USER_CALLBACK_PATTERN, CommandsConstants.REGISTER_USER_CALLBACK)
                .build();
    }


    @Bean
    TelegramCommand searchCommand(TelegramCommandBuilder builder) {
        return builder
                .command(Command.SEARCH)
                .commandInfo(CommandsConstants.SEARCH_INFO)
                .privacy(Privacy.PUBLIC)
                .inputPattern(CommandsConstants.SEARCH_INPUT_PATTERN)
                .withReply(CommandsConstants.SPECIFY_SEARCH_STRING, TelegramCommandBuilder.HAS_INPUT, TEXT)
                .pageable(CommandsConstants.SEARCH_CALLBACK_PATTERN)
                .withMenuCallback()
                .build();
    }


    @Bean
    TelegramCommand startCommand(TelegramCommandBuilder builder) {
        return builder
                .command(Command.START)
                .commandInfo(CommandsConstants.START_INFO)
                .privacy(Privacy.PUBLIC)
                .build();
    }

    @Bean
    TelegramCommand unbanUserCommand(TelegramCommandBuilder builder) {
        return builder
                .command(Command.UNBAN)
                .commandInfo(CommandsConstants.UNBAN_INFO)
                .privacy(Privacy.ADMIN)
                .inputPattern(CommandsConstants.UNBAN_USER_INPUT_PATTERN)
                .withReply(CommandsConstants.SPECIFY_USER_TO_UNBAN, TelegramCommandBuilder.HAS_INPUT, TEXT)
                .addNewCallback(CommandsConstants.UNBAN_USER_CALLBACK_PATTERN, CommandsConstants.UNBAN_CALLBACK)
                .build();
    }


    @Bean
    TelegramCommand uploadDocumentCommand(TelegramCommandBuilder builder) {
        return builder
                .command(Command.UPLOAD)
                .commandInfo(CommandsConstants.UPLOAD_DOCUMENT_INFO)
                .privacy(Privacy.PUBLIC)
                .withReply(CommandsConstants.SEND_YOUR_DOCUMENT, TelegramCommandBuilder.ALWAYS_REPLY, DOCUMENT.or(TEXT))
                .parameterExtractor((upd, _) -> {
                    if (DOCUMENT.test(upd)) {
                        var document = upd.getMessage().getDocument();
                        return Map.of(CommandsConstants.FILE_NAME, document.getFileName(), CommandsConstants.FILE_ID, document.getFileId(),
                                CommandsConstants.FILE_SIZE, document.getFileSize().toString());
                    } else {
                        return Map.of(CommandsConstants.HTTP_LINK, upd.getMessage().getText());
                    }
                })
                .withMenuCallback()
                .build();
    }


    @Bean
    TelegramCommand purgeUserCommand(TelegramCommandBuilder builder) {
        return builder
                .command(Command.PURGE)
                .commandInfo(CommandsConstants.PURGE_INFO)
                .privacy(Privacy.CREATOR)
                .inputPattern(CommandsConstants.PURGE_USER_INPUT_PATTERN)
                .withReply(CommandsConstants.SPECIFY_USER_TO_PURGE, TelegramCommandBuilder.HAS_INPUT, TEXT)
                .addNewCallback(CommandsConstants.PURGE_USER_CALLBACK_PATTERN, CommandsConstants.PURGE_USER_CALLBACK)
                .build();
    }

    @Bean
    TelegramCommand configureLoggingCommand(TelegramCommandBuilder builder,
                                            LoggingConfiguringCommandHandler loggingHandler) {
        return builder
                .command(Command.LOGGING)
                .commandInfo(CommandsConstants.LOGGING_INFO)
                .privacy(Privacy.CREATOR)
                .addNewCallback(CommandsConstants.LOGGING_CHANGE_CALLBACK_PATTERN, CommandsConstants.LOGGING_CALLBACK)
                .commandHandler(loggingHandler)
                .build();
    }

    @Bean
    TelegramCommand unknownUserInputDefaultCommand(TelegramCommandBuilder builder) {
//      org.telegram.telegrambots.abilitybots.api.bot.BaseAbilityBot.getAbility
        return builder
                .command(DEFAULT)
                .commandInfo(CommandsConstants.DEFAULT)
                .privacy(Privacy.PUBLIC)
                .commandHandler((ctx, _, _) -> {
                            var locale = userLocale(ctx);
                            send(ctx, messageSource.getMessage(CommandsConstants.UNKNOWN_COMMAND_FALLBACK_MESSAGE, null, locale));
                        }
                )
                .build();
    }
}
