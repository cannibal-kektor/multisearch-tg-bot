package lubos.multisearch.telegrambot.bot.commands;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class CommandsConstants {

    final String MESSAGE = "MESSAGE";
    final String REPLY_MESSAGE = "REPLY_MESSAGE";
    final String MENU_CALLBACK = "MENU_CALLBACK";
    final String PAGING_CALLBACK = "PAGING_CALLBACK";

    final String BAN_INFO = "command.ban.info";
    final String SPECIFY_USER_TO_BAN = "command.ban.reply.specify";
    final Pattern BAN_USER_INPUT_PATTERN = Pattern.compile("^(?:/ban )?(?<username>@?\\w+)$");
    final Pattern BAN_USER_CALLBACK_PATTERN = Pattern.compile("^ban (?<username>\\w+) users (?<usersPage>\\d{1,4}|-1)$");
    final String BAN_USER_CALLBACK = "BAN_USER_CALLBACK";

    final String DELETE_DOCUMENT_INFO = "command.delete.info";
    final String SPECIFY_DOCUMENT_TO_DELETE = "command.delete.reply.specify";
    final Pattern DOCUMENT_DELETE_INPUT_PATTERN = Pattern.compile("^(?:/delete )?(?<inputArg>.+)$");
    final Pattern DOCUMENT_DELETE_CALLBACK_PATTERN = Pattern.compile("^doc_delete (?<docId>.[^ ]+)(?: back_docs (?<backDocs>\\d{1,4}))?$");
    final String DOC_DELETE_CALLBACK = "DOC_DELETE_CALLBACK";

    final String DEMOTE_INFO = "command.demote.info";
    final String SPECIFY_ADMIN_TO_DEMOTE = "command.demote.reply.specify";
    final Pattern DEMOTE_ADMIN_INPUT_PATTERN = Pattern.compile("^(?:/demote )?(?<username>@?\\w+)$");

    final String DOC_CONTENTS_INFO = "command.contents.info";
    final String SPECIFY_DOCUMENT_TO_LIST_CONTENTS = "command.contents.reply.specify";
    final Pattern DOCUMENT_CONTENTS_INPUT_PATTERN = Pattern.compile("^(?:/contents )?(?<inputArg>.+)$");
    final Pattern DOCUMENT_CONTENTS_PAGING_CALLBACK_PATTERN = Pattern.compile("^doc_contents (?<contentsPage>\\d{1,4}) (?<docId>.[^ ]*) chapter (?<chapterPage>\\d{1,4}|-1) docs (?<docsPage>\\d{1,4}|-1)$");

    final String INFO_INFO = "command.info.info";

    final String LANGUAGE_INFO = "command.language.info";
    final Pattern LANGUAGE_CHANGE_CALLBACK_PATTERN = Pattern.compile("^lang (?<lang>en|ru)$");
    final String LANGUAGE_CALLBACK = "LANGUAGE_CALLBACK";

    final String COMMANDS_INFO = "command.commands.info";

    final String LIST_DOCUMENTS_INFO = "command.documents.info";
    final Pattern LIST_DOCUMENTS_PAGEABLE_CALLBACK_PATTERN = Pattern.compile("^doc_page (?<docsPage>\\d{1,4})$");

    final String REGISTRATION_REQUESTS_INFO = "command.registration_requests.info";
    final Pattern REGISTRATION_REQUEST_PAGEABLE_CALLBACK_PATTERN = Pattern.compile("^reg_page (?<regPage>\\d{1,4})$");

    final String LIST_USERS_INFO = "command.users.info";
    final Pattern LIST_USERS_PAGEABLE_CALLBACK_PATTERN = Pattern.compile("^users_page (?<usersPage>\\d{1,4})$");

    final String CHAPTER_INFO = "command.chapter.info";
    final String SPECIFY_CHAPTER_ID = "command.chapter.reply.specify";
    final Pattern CHAPTER_INPUT_PATTERN = Pattern.compile("^(?:/chapter )?(?<chapterId>.[^ ]+)$");
    final Pattern CHAPTER_PAGEABLE_CALLBACK_PATTERN = Pattern.compile("^chapter_num (?<chapterPage>\\d{1,4}) (?<docId>.[^ ]+)$");
    final Pattern EXPAND_CHAPTER_CALLBACK_PATTERN = Pattern.compile("^chapter (?<chapterId>.[^ ]+)$");
    final String CHAPTER_EXPAND_CALLBACK = "CHAPTER_EXPAND_CALLBACK";

    final String PROMOTE_INFO = "command.promote.info";
    final String SPECIFY_USER_TO_PROMOTE = "command.promote.reply.specify";
    final Pattern PROMOTE_USER_INPUT_PATTERN = Pattern.compile("^(?:/promote )?(?<username>@?\\w+)$");

    final String REGISTER_INFO = "command.register.info";
    final String SPECIFY_USER_TO_APPROVE_REGISTRATION = "command.register.reply.specify";
    final Pattern REGISTER_USER_INPUT_PATTERN = Pattern.compile("^(?:/register )?(?<username>@?\\w+)$");
    final Pattern REGISTER_USER_CALLBACK_PATTERN = Pattern.compile("^reg (?<username>\\w*) regPage (?<regPage>\\d{1,4}|-1)$");
    final String REGISTER_USER_CALLBACK = "REGISTER_USER_CALLBACK";

    final String SEARCH_INFO = "command.search.info";
    final String SPECIFY_SEARCH_STRING = "command.search.reply.specify";
    final Pattern SEARCH_INPUT_PATTERN = Pattern.compile("^(?:/search )?(?<searchStr>.+)");
    final Pattern SEARCH_CALLBACK_PATTERN = Pattern.compile("^search (?<searchPage>\\d{1,4})");

    final String START_INFO = "command.start.info";

    final String UNBAN_INFO = "command.unban.info";
    final String SPECIFY_USER_TO_UNBAN = "command.unban.reply.specify";
    final Pattern UNBAN_USER_INPUT_PATTERN = Pattern.compile("^(?:/unban )?(?<username>@?\\w+)$");
    final Pattern UNBAN_USER_CALLBACK_PATTERN = Pattern.compile("^unban (?<username>\\w+) users (?<usersPage>\\d{1,4}|-1)$");
    final String UNBAN_CALLBACK = "UNBAN_CALLBACK";

    final String UPLOAD_DOCUMENT_INFO = "command.upload.info";
    final String SEND_YOUR_DOCUMENT = "command.upload.reply";
    final Pattern INPUT_ARGUMENT_PATTERN = Pattern.compile("(?<htmlLink>.*)?");
    final String FILE_NAME = "FILE_NAME";
    final String FILE_ID = "FILE_ID";
    final String FILE_SIZE = "FILE_SIZE";
    final String HTTP_LINK = "HTTP_LINK";

    final String PURGE_INFO = "command.purge.info";
    final String SPECIFY_USER_TO_PURGE = "command.purge.reply.specify";
    final Pattern PURGE_USER_INPUT_PATTERN = Pattern.compile("^(?:/purge )?(?<username>@?\\w+)$");
    final Pattern PURGE_USER_CALLBACK_PATTERN = Pattern.compile("^purge (?<username>\\w+) users (?<usersPage>\\d{1,4}|-1)$");
    final String PURGE_USER_CALLBACK = "PURGE_USER_CALLBACK";

    final String DEFAULT = "default";
    final String UNKNOWN_COMMAND_FALLBACK_MESSAGE = "command.default.fallback_message";
}

