package lubos.multisearch.telegrambot.bot;

import jakarta.annotation.PostConstruct;
import lubos.multisearch.telegrambot.conf.BotInfo;
import lubos.multisearch.telegrambot.logging.LogHelper;
import lubos.multisearch.telegrambot.service.UserService;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.abilitybots.api.objects.Flag;
import org.telegram.telegrambots.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.abilitybots.api.toggle.BareboneToggle;
import org.telegram.telegrambots.abilitybots.api.util.AbilityExtension;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static lubos.multisearch.telegrambot.bot.utils.TelegramHelperUtils.userLocale;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getUser;


@Component
public class MultiSearchBotUpdateConsumer extends AbilityBot {

    public static final String USERNAME_IS_NULL = "check.username.is_null";

    final BotInfo botInfo;
    final UserService userService;
    final MessageSource messageSource;
    final LogHelper logHelper;

    public MultiSearchBotUpdateConsumer(TelegramClient telegramClient, UserService userService,
                                        List<AbilityExtension> extensions, BotInfo botInfo,
                                        MessageSource messageSource, LogHelper logHelper) {
        super(telegramClient, botInfo.username(), new DummyContext(), new BareboneToggle());
        addExtensions(extensions);
        this.userService = userService;
        this.botInfo = botInfo;
        this.messageSource = messageSource;
        this.logHelper = logHelper;
    }

    @PostConstruct
    public void initialize() {
        logHelper.logBotInitializing();
        onRegister();
        setMenuCommands();
        logHelper.logBotInitialized();
    }

    //TODO Can remove (aop)?
    @Override
    public void consume(Update update) {
        super.consume(update);
    }

    //    @AfterBotRegistration
    public void setMenuCommands() {
        List<BotCommand> commandsList = getAbilities()
                .values()
                .stream()
                .sorted(comparing(Ability::name))
                .filter(ability -> !ability.name().equals(DEFAULT))
                .map(ability -> (BotCommand) BotCommand.builder()
                        .command(format("/%s", ability.name()))
                        .description(messageSource.getMessage(ability.info(), null, Locale.ENGLISH))
                        .build())
                .toList();
        var command = new SetMyCommands(commandsList);
        getSilent().execute(command);
        logHelper.logMenuCommandsConfigured();
    }


    public Reply usernameIsNullReply() {
        return Reply.of((bot, upd) -> silent.send(messageSource.getMessage(USERNAME_IS_NULL, null, userLocale(upd)), upd.getMessage().getChatId()),
                usernameIsNull());
    }

    Predicate<Update> usernameIsNull() {
        return upd -> Objects.isNull(getUser(upd).getUserName());
    }

    @Override
    protected boolean checkGlobalFlags(Update update) {
        User user = getUser(update);
        return userService.checkUser(user)
                || Flag.TEXT.and(upd -> "/start".equals(upd.getMessage().getText()))
                .test(update);
    }

    @Override
    public Set<Long> blacklist() {
        return userService.getBannedIds();
    }

    @Override
    public Set<Long> admins() {
        return userService.getAdminsIds();
    }

    @Override
    public long creatorId() {
        return botInfo.creatorId();
    }

}