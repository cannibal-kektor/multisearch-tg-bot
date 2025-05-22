package lubos.multisearch.telegrambot.logging;

//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.objects.Update;

@Aspect
@Component
public class LoggingAspects {


    public static final String RECEIVED_TG_MESSAGE = "logging.receive.message";
    public static final String RECEIVED_TG_CALLBACK = "logging.receive.callback";
    public static final String STARTING_PREPROCESSING_INPUT_MESSAGE = "logging.preprocess.message.start";
    public static final String STARTING_PREPROCESSING_INPUT_CALLBACK = "logging.preprocess.callback.start";
    public static final String BOT_INITIALIZED = "logging.bot.initialized";
    public static final String MENU_COMMANDS_SET = "logging.menu.commands.set";


    @Autowired
    LogHelper logHelper;


    @Pointcut("target(lubos.multisearch.telegrambot.bot.MultiSearchBotUpdateConsumer)")
    public void inTelegramBot() {}

//    @Around("inTelegramBot() && execution(void initialize())")
//    public void BotInitialized(ProceedingJoinPoint joinPoint) throws Throwable {
//        logHelper.logBotInitializing();
//        joinPoint.proceed();
//        logHelper.logBotInitialized();
//    }

    @Around("inTelegramBot() && execution(void consume(..)) && args(update)")
    public void logUpdate(ProceedingJoinPoint pjp, Update update) throws Throwable {
        logHelper.logUpdateReceived(update);
        pjp.proceed(pjp.getArgs());
        logHelper.logUpdateProcessed(update);
    }

    @Pointcut("target(lubos.multisearch.telegrambot.bot.commands.processing.CommandHandler)")
    public void commandHandlerImpl() {}

    @Around("commandHandlerImpl() && execution(void handleCommand(..)) && args(messageContext,..,contextId)")
    public void logCommandProcessing(ProceedingJoinPoint pjp, MessageContext messageContext, String contextId) throws Throwable {
        logHelper.logCommandHandlerStarted(messageContext,contextId);
        pjp.proceed(pjp.getArgs());
        logHelper.logCommandHandlerFinished(messageContext,contextId);
    }


//    @Before(value = "execution(void lubos.multisearch.telegrambot.bot.commands.TelegramCommand.handleCommand(..))")
//    public void logUpdate(){
//        System.out.println("1111111111111111111111111111111111111111111111");
//        System.out.println("1111111111111111111111111111111111111111111111");
//        System.out.println("1111111111111111111111111111111111111111111111");
//    }
//    @Before("execution(* lubos.multisearch.telegrambot.bot.commands.processing.CommandHandler+.handleCommand(..))")
    public void logUpdate3(){
        System.out.println("22222222222222222222222222222222222222222222222");
        System.out.println("22222222222222222222222222222222222222222222222");
        System.out.println("22222222222222222222222222222222222222222222222");
    }


//    @Before("execution(* lubos.multisearch.telegrambot.bot.commands.processing.ParametersExtractor+.extractParameters(..))")
    public void logUpdate4(){
        System.out.println("3333333333333333333333333333333333333333333333");
        System.out.println("3333333333333333333333333333333333333333333333");
        System.out.println("3333333333333333333333333333333333333333333333");

    }


}
