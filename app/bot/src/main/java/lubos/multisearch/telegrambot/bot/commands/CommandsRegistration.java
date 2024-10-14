package lubos.multisearch.telegrambot.bot.commands;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.abilitybots.api.objects.Privacy;

import java.util.Map;

import static org.telegram.telegrambots.abilitybots.api.objects.Flag.DOCUMENT;
import static org.telegram.telegrambots.abilitybots.api.objects.Flag.TEXT;

@Configuration
public class CommandsRegistration {

    final TelegramCommandBuilder commandBuilder;

    public CommandsRegistration(TelegramCommandBuilder commandBuilder) {
        this.commandBuilder = commandBuilder;
    }

    @Bean
    TelegramCommand banUserCommand() {
        return commandBuilder.newCommand()
                .command(Command.BAN)
                .commandInfo(CommandsConstants.BAN_INFO)
                .privacy(Privacy.ADMIN)
                .inputPattern(CommandsConstants.BAN_USER_INPUT_PATTERN)
                .withReply(CommandsConstants.SPECIFY_USER_TO_BAN, TelegramCommandBuilder.HAS_INPUT, TEXT)
                .addCallbackReply(CommandsConstants.BAN_USER_CALLBACK_PATTERN, CommandsConstants.BAN_USER_CALLBACK)
                .build();
    }


    @Bean
    TelegramCommand deleteDocumentCommand() {
        return commandBuilder.newCommand()
                .command(Command.DELETE)
                .commandInfo(CommandsConstants.DELETE_DOCUMENT_INFO)
                .privacy(Privacy.PUBLIC)
                .inputPattern(CommandsConstants.DOCUMENT_DELETE_INPUT_PATTERN)
                .withReply(CommandsConstants.SPECIFY_DOCUMENT_TO_DELETE, TelegramCommandBuilder.HAS_INPUT, TEXT)
                .addCallbackReply(CommandsConstants.DOCUMENT_DELETE_CALLBACK_PATTERN, CommandsConstants.DOC_DELETE_CALLBACK)
                .withMenuCallback()
                .build();
    }

    @Bean
    TelegramCommand demoteAdminCommand() {
        return commandBuilder.newCommand()
                .command(Command.DEMOTE)
                .commandInfo(CommandsConstants.DEMOTE_INFO)
                .privacy(Privacy.CREATOR)
                .inputPattern(CommandsConstants.DEMOTE_ADMIN_INPUT_PATTERN)
                .withReply(CommandsConstants.SPECIFY_ADMIN_TO_DEMOTE, TelegramCommandBuilder.HAS_INPUT, TEXT)
                .build();
    }


    @Bean
    TelegramCommand documentsContentsCommand() {
        return commandBuilder.newCommand()
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
    TelegramCommand infoCommand() {
        return commandBuilder.newCommand()
                .command(Command.INFO)
                .commandInfo(CommandsConstants.INFO_INFO)
                .privacy(Privacy.PUBLIC)
                .build();
    }

    @Bean
    TelegramCommand languageCommand() {
        return commandBuilder.newCommand()
                .command(Command.LANGUAGE)
                .commandInfo(CommandsConstants.LANGUAGE_INFO)
                .privacy(Privacy.PUBLIC)
                .addCallbackReply(CommandsConstants.LANGUAGE_CHANGE_CALLBACK_PATTERN, CommandsConstants.LANGUAGE_CALLBACK)
                .build();
    }

    @Bean
    TelegramCommand availableCommandsCommand() {
        return commandBuilder.newCommand()
                .command(Command.COMMANDS)
                .commandInfo(CommandsConstants.COMMANDS_INFO)
                .privacy(Privacy.PUBLIC)
                .withMenuCallback()
                .build();
    }

    @Bean
    TelegramCommand listDocumentsCommand() {
        return commandBuilder.newCommand()
                .command(Command.DOCUMENTS)
                .commandInfo(CommandsConstants.LIST_DOCUMENTS_INFO)
                .privacy(Privacy.PUBLIC)
                .pageable(CommandsConstants.LIST_DOCUMENTS_PAGEABLE_CALLBACK_PATTERN)
                .withMenuCallback()
                .build();
    }

    @Bean
    TelegramCommand listRegistrationRequestsCommand() {
        return commandBuilder.newCommand()
                .command(Command.REGISTRATION_REQUESTS)
                .commandInfo(CommandsConstants.REGISTRATION_REQUESTS_INFO)
                .privacy(Privacy.ADMIN)
                .pageable(CommandsConstants.REGISTRATION_REQUEST_PAGEABLE_CALLBACK_PATTERN)
                .withMenuCallback()
                .build();
    }

    @Bean
    TelegramCommand listUsersCommand() {
        return commandBuilder.newCommand()
                .command(Command.USERS)
                .commandInfo(CommandsConstants.LIST_USERS_INFO)
                .privacy(Privacy.ADMIN)
                .pageable(CommandsConstants.LIST_USERS_PAGEABLE_CALLBACK_PATTERN)
                .withMenuCallback()
                .build();
    }

    @Bean
    TelegramCommand getChapterCommand() {
        return commandBuilder.newCommand()
                .command(Command.CHAPTER)
                .commandInfo(CommandsConstants.CHAPTER_INFO)
                .privacy(Privacy.PUBLIC)
                .inputPattern(CommandsConstants.CHAPTER_INPUT_PATTERN)
                .withReply(CommandsConstants.SPECIFY_CHAPTER_ID, TelegramCommandBuilder.HAS_INPUT, TEXT)
                .pageable(CommandsConstants.CHAPTER_PAGEABLE_CALLBACK_PATTERN)
                .addCallbackReply(CommandsConstants.EXPAND_CHAPTER_CALLBACK_PATTERN, CommandsConstants.CHAPTER_EXPAND_CALLBACK)
                .build();
    }


    @Bean
    TelegramCommand promoteUserCommand() {
        return commandBuilder.newCommand()
                .command(Command.PROMOTE)
                .commandInfo(CommandsConstants.PROMOTE_INFO)
                .privacy(Privacy.CREATOR)
                .inputPattern(CommandsConstants.PROMOTE_USER_INPUT_PATTERN)
                .withReply(CommandsConstants.SPECIFY_USER_TO_PROMOTE, TelegramCommandBuilder.HAS_INPUT, TEXT)
                .build();
    }


    @Bean
    TelegramCommand registerUserCommand() {
        return commandBuilder.newCommand()
                .command(Command.REGISTER)
                .commandInfo(CommandsConstants.REGISTER_INFO)
                .privacy(Privacy.ADMIN)
                .inputPattern(CommandsConstants.REGISTER_USER_INPUT_PATTERN)
                .withReply(CommandsConstants.SPECIFY_USER_TO_APPROVE_REGISTRATION, TelegramCommandBuilder.HAS_INPUT, TEXT)
                .addCallbackReply(CommandsConstants.REGISTER_USER_CALLBACK_PATTERN, CommandsConstants.REGISTER_USER_CALLBACK)
                .build();
    }


    @Bean
    TelegramCommand searchCommand() {
        return commandBuilder.newCommand()
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
    TelegramCommand startCommand() {
        return commandBuilder.newCommand()
                .command(Command.START)
                .commandInfo(CommandsConstants.START_INFO)
                .privacy(Privacy.PUBLIC)
                .build();
    }

    @Bean
    TelegramCommand unbanUserCommand() {
        return commandBuilder.newCommand()
                .command(Command.UNBAN)
                .commandInfo(CommandsConstants.UNBAN_INFO)
                .privacy(Privacy.ADMIN)
                .inputPattern(CommandsConstants.UNBAN_USER_INPUT_PATTERN)
                .withReply(CommandsConstants.SPECIFY_USER_TO_UNBAN, TelegramCommandBuilder.HAS_INPUT, TEXT)
                .addCallbackReply(CommandsConstants.UNBAN_USER_CALLBACK_PATTERN, CommandsConstants.UNBAN_CALLBACK)
                .build();
    }


    @Bean
    TelegramCommand uploadDocumentCommand() {
        return commandBuilder.newCommand()
                .command(Command.UPLOAD)
                .commandInfo(CommandsConstants.UPLOAD_DOCUMENT_INFO)
                .privacy(Privacy.PUBLIC)
                .withReply(CommandsConstants.SEND_YOUR_DOCUMENT, TelegramCommandBuilder.ALWAYS_REPLY, DOCUMENT.or(TEXT))
                .parameterExtractor((upd, pattern) -> {
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
    TelegramCommand purgeUserCommand() {
        return commandBuilder.newCommand()
                .command(Command.PURGE)
                .commandInfo(CommandsConstants.PURGE_INFO)
                .privacy(Privacy.CREATOR)
                .inputPattern(CommandsConstants.PURGE_USER_INPUT_PATTERN)
                .withReply(CommandsConstants.SPECIFY_USER_TO_PURGE, TelegramCommandBuilder.HAS_INPUT, TEXT)
                .addCallbackReply(CommandsConstants.PURGE_USER_CALLBACK_PATTERN, CommandsConstants.PURGE_USER_CALLBACK)
                .build();
    }


}
