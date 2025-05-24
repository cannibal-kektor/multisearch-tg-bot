package lubos.multisearch.telegrambot.logging;

import lubos.multisearch.telegrambot.bot.commands.processing.impl.DefaultCommandHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

import static lubos.multisearch.telegrambot.logging.LogHelper.*;
import static org.telegram.telegrambots.abilitybots.api.objects.Flag.MESSAGE;

@Aspect
@Component
public class LoggingAspects {

    final LogHelper logHelper;

    public LoggingAspects(LogHelper logHelper) {
        this.logHelper = logHelper;
    }


    @Pointcut("target(lubos.multisearch.telegrambot.bot.MultiSearchBotUpdateConsumer)")
    public void inTelegramBot() {
    }

    @Pointcut("inTelegramBot() && execution(void consume(..))")
    public void botConsumeMethod() {
    }

    @Pointcut("target(lubos.multisearch.telegrambot.bot.commands.processing.CommandHandler)")
    public void commandHandlerImpl() {
    }

    @Pointcut("commandHandlerImpl() && execution(void handleCommand(..))")
    public void commandHandlerImplMethod() {
    }


    @Order(1)
    @Around("botConsumeMethod() && args(update)")
    public void configureMDC(ProceedingJoinPoint pjp, Update update) throws Throwable {
        try {
            var ctxMap = MESSAGE.test(update) ?
                    Map.of(UPDATE_ID, update.getUpdateId().toString(),
                            CHAT_ID, update.getMessage().getChatId().toString(),
                            USERNAME, update.getMessage().getFrom().getUserName())
                    :
                    Map.of(UPDATE_ID, update.getUpdateId().toString(),
                            CHAT_ID, update.getCallbackQuery().getMessage().getChatId().toString(),
                            USERNAME, update.getCallbackQuery().getFrom().getUserName());
            MDC.setContextMap(ctxMap);
            pjp.proceed(pjp.getArgs());
        } finally {
            MDC.clear();
        }
    }

    @Order(2)
    @Around("botConsumeMethod() && args(update)")
    public void logUpdate(ProceedingJoinPoint pjp, Update update) throws Throwable {
        logHelper.logUpdateReceived(update);
        pjp.proceed(pjp.getArgs());
        logHelper.logUpdateProcessed(update);
    }

    @Order(1)
    @Around("commandHandlerImplMethod()  && args(..,contextId)")
    public void additionalMDC(ProceedingJoinPoint pjp, String contextId) throws Throwable {
        String command = pjp.getTarget() instanceof DefaultCommandHandler defaultImpl ?
                defaultImpl.getCommand().name() : "-";
        MDC.put(COMMAND, command);
        MDC.put(CONTEXT_ID, contextId);
        pjp.proceed(pjp.getArgs());
    }

    @Order(2)
    @Around("commandHandlerImplMethod()")
    public void logCommandProcessing(ProceedingJoinPoint pjp) throws Throwable {
        logHelper.logCommandHandlerStarted();
        pjp.proceed(pjp.getArgs());
        logHelper.logCommandHandlerFinished();
    }


}
