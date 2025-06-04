package lubos.multisearch.telegrambot.bot.commands;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

@UtilityClass
public class CommandsConstants {

   public final String MESSAGE = "MESSAGE";
   public final String REPLY_MESSAGE = "REPLY_MESSAGE";
   public final String MENU_CALLBACK = "MENU_CALLBACK";
   public final String PAGING_CALLBACK = "PAGING_CALLBACK";

   public final String BAN_INFO = "command.ban.info";
   public final String SPECIFY_USER_TO_BAN = "command.ban.reply.specify";
   public final Pattern BAN_USER_INPUT_PATTERN = Pattern.compile("^(?:/ban )?(?<username>@?\\w+)$");
   public final Pattern BAN_USER_CALLBACK_PATTERN = Pattern.compile("^ban (?<username>\\w+) users (?<usersPage>\\d{1,4}|-1)$");
   public final String BAN_USER_CALLBACK = "BAN_USER_CALLBACK";

   public final String DELETE_DOCUMENT_INFO = "command.delete.info";
   public final String SPECIFY_DOCUMENT_TO_DELETE = "command.delete.reply.specify";
   public final Pattern DOCUMENT_DELETE_INPUT_PATTERN = Pattern.compile("^(?:/delete )?(?<inputArg>.+)$");
   public final Pattern DOCUMENT_DELETE_CALLBACK_PATTERN = Pattern.compile("^doc_delete (?<docId>.[^ ]+)(?: back_docs (?<backDocs>\\d{1,4}))?$");
   public final String DOC_DELETE_CALLBACK = "DOC_DELETE_CALLBACK";

   public final String DEMOTE_INFO = "command.demote.info";
   public final String SPECIFY_ADMIN_TO_DEMOTE = "command.demote.reply.specify";
   public final Pattern DEMOTE_ADMIN_INPUT_PATTERN = Pattern.compile("^(?:/demote )?(?<username>@?\\w+)$");

   public final String DOC_CONTENTS_INFO = "command.contents.info";
   public final String SPECIFY_DOCUMENT_TO_LIST_CONTENTS = "command.contents.reply.specify";
   public final Pattern DOCUMENT_CONTENTS_INPUT_PATTERN = Pattern.compile("^(?:/contents )?(?<inputArg>.+)$");
   public final Pattern DOCUMENT_CONTENTS_PAGING_CALLBACK_PATTERN = Pattern.compile("^doc_contents (?<contentsPage>\\d{1,4}) (?<docId>.[^ ]*) chapter (?<chapterPage>\\d{1,4}|-1) docs (?<docsPage>\\d{1,4}|-1)$");

   public final String INFO_INFO = "command.info.info";

   public final String LANGUAGE_INFO = "command.language.info";
   public final Pattern LANGUAGE_CHANGE_CALLBACK_PATTERN = Pattern.compile("^lang (?<lang>en|ru)$");
   public final String LANGUAGE_CALLBACK = "LANGUAGE_CALLBACK";

   public final String COMMANDS_INFO = "command.commands.info";

   public final String LIST_DOCUMENTS_INFO = "command.documents.info";
   public final Pattern LIST_DOCUMENTS_PAGEABLE_CALLBACK_PATTERN = Pattern.compile("^doc_page (?<docsPage>\\d{1,4})$");

   public final String REGISTRATION_REQUESTS_INFO = "command.registration_requests.info";
   public final Pattern REGISTRATION_REQUEST_PAGEABLE_CALLBACK_PATTERN = Pattern.compile("^reg_page (?<regPage>\\d{1,4})$");

   public final String LIST_USERS_INFO = "command.users.info";
   public final Pattern LIST_USERS_PAGEABLE_CALLBACK_PATTERN = Pattern.compile("^users_page (?<usersPage>\\d{1,4})$");

   public final String CHAPTER_INFO = "command.chapter.info";
   public final String SPECIFY_CHAPTER_ID = "command.chapter.reply.specify";
   public final Pattern CHAPTER_INPUT_PATTERN = Pattern.compile("^(?:/chapter )?(?<chapterId>.[^ ]+)$");
   public final Pattern CHAPTER_PAGEABLE_CALLBACK_PATTERN = Pattern.compile("^chapter_num (?<chapterPage>\\d{1,4}) (?<docId>.[^ ]+)$");
   public final Pattern EXPAND_CHAPTER_CALLBACK_PATTERN = Pattern.compile("^chapter (?<chapterId>.[^ ]+)$");
   public final String CHAPTER_EXPAND_CALLBACK = "CHAPTER_EXPAND_CALLBACK";

   public final String PROMOTE_INFO = "command.promote.info";
   public final String SPECIFY_USER_TO_PROMOTE = "command.promote.reply.specify";
   public final Pattern PROMOTE_USER_INPUT_PATTERN = Pattern.compile("^(?:/promote )?(?<username>@?\\w+)$");

   public final String REGISTER_INFO = "command.register.info";
   public final String SPECIFY_USER_TO_APPROVE_REGISTRATION = "command.register.reply.specify";
   public final Pattern REGISTER_USER_INPUT_PATTERN = Pattern.compile("^(?:/register )?(?<username>@?\\w+)$");
   public final Pattern REGISTER_USER_CALLBACK_PATTERN = Pattern.compile("^reg (?<username>\\w*) regPage (?<regPage>\\d{1,4}|-1)$");
   public final String REGISTER_USER_CALLBACK = "REGISTER_USER_CALLBACK";

   public final String SEARCH_INFO = "command.search.info";
   public final String SPECIFY_SEARCH_STRING = "command.search.reply.specify";
   public final Pattern SEARCH_INPUT_PATTERN = Pattern.compile("^(?:/search )?(?<searchStr>.+)");
   public final Pattern SEARCH_CALLBACK_PATTERN = Pattern.compile("^search (?<searchPage>\\d{1,4})");

   public final String START_INFO = "command.start.info";

   public final String UNBAN_INFO = "command.unban.info";
   public final String SPECIFY_USER_TO_UNBAN = "command.unban.reply.specify";
   public final Pattern UNBAN_USER_INPUT_PATTERN = Pattern.compile("^(?:/unban )?(?<username>@?\\w+)$");
   public final Pattern UNBAN_USER_CALLBACK_PATTERN = Pattern.compile("^unban (?<username>\\w+) users (?<usersPage>\\d{1,4}|-1)$");
   public final String UNBAN_CALLBACK = "UNBAN_CALLBACK";

   public final String UPLOAD_DOCUMENT_INFO = "command.upload.info";
   public final String SEND_YOUR_DOCUMENT = "command.upload.reply";
   public final Pattern INPUT_ARGUMENT_PATTERN = Pattern.compile("(?<htmlLink>.*)?");
   public final String FILE_NAME = "FILE_NAME";
   public final String FILE_ID = "FILE_ID";
   public final String FILE_SIZE = "FILE_SIZE";
   public final String HTTP_LINK = "HTTP_LINK";

   public final String PURGE_INFO = "command.purge.info";
   public final String SPECIFY_USER_TO_PURGE = "command.purge.reply.specify";
   public final Pattern PURGE_USER_INPUT_PATTERN = Pattern.compile("^(?:/purge )?(?<username>@?\\w+)$");
   public final Pattern PURGE_USER_CALLBACK_PATTERN = Pattern.compile("^purge (?<username>\\w+) users (?<usersPage>\\d{1,4}|-1)$");
   public final String PURGE_USER_CALLBACK = "PURGE_USER_CALLBACK";

   public final String DEFAULT = "default";
   public final String UNKNOWN_COMMAND_FALLBACK_MESSAGE = "command.default.fallback_message";

   public final String LOGGING_INFO = "command.logging.info";
   public final Pattern LOGGING_CHANGE_CALLBACK_PATTERN = Pattern.compile("^logging (?<level>TRACE|DEBUG|INFO|WARN|ERROR|FATAL|OFF)$");
   public final String LOGGING_CALLBACK = "LOGGING_CALLBACK";
}

