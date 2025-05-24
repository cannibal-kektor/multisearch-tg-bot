package lubos.multisearch.processor.logging;


import lubos.multisearch.processor.entrypoint.CommandActionContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

import static lubos.multisearch.processor.logging.LogHelper.*;

@Aspect
@Component
public class LoggingAspects {

    final LogHelper logHelper;

    public LoggingAspects(LogHelper logHelper) {
        this.logHelper = logHelper;
    }

    @Pointcut("target(lubos.multisearch.processor.entrypoint.TelegramCommandListener)")
    public void commandListenerClass() {
    }

    @Pointcut("commandListenerClass() && execution(void consume(..))")
    public void commandConsumerMethod() {
    }

    @Pointcut("target(lubos.multisearch.processor.bot.commands.CommandProcessor)")
    public void commandProcessorImplClass() {
    }


    @Order(1)
    @Around("commandConsumerMethod() && args(context)")
    public void configureRequestMDC(ProceedingJoinPoint pjp, CommandActionContext context) throws Throwable {
        try {
            var ctxMap = Map.of(UPDATE_ID, context.updateId().toString(),
                    CHAT_ID, context.chatId().toString(),
                    USERNAME, context.user().getUserName(),
                    COMMAND, context.command().name(),
                    CONTEXT_ID, context.contextId());
            MDC.setContextMap(ctxMap);
            pjp.proceed(pjp.getArgs());
        } finally {
            MDC.clear();
        }
    }

    @Order(2)
    @Around("commandConsumerMethod()")
    public void logUpdate(ProceedingJoinPoint pjp) throws Throwable {
        logHelper.logCommandReceived();
        pjp.proceed(pjp.getArgs());
        logHelper.logCommandProcessed();
    }

    @Around("commandProcessorImplClass() && execution(void process(..))")
    public void logCommandProcessing(ProceedingJoinPoint pjp) throws Throwable {
        String commandProcessorImpl = pjp.getTarget().getClass().getSimpleName();
        MDC.put(COMMAND_PROCESSOR, commandProcessorImpl);
        logHelper.logCommandProcessorStarted(commandProcessorImpl);
        pjp.proceed(pjp.getArgs());
        logHelper.logCommandProcessorFinished(commandProcessorImpl);
    }


}
