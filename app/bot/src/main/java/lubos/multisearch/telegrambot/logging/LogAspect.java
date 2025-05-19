package lubos.multisearch.telegrambot.logging;

import lubos.multisearch.telegrambot.bot.commands.TelegramCommand;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.meta.api.objects.Update;

//@Aspect
@Component
public class LogAspect {


//    @Before("execution(void lubos.multisearch.telegrambot.bot.MultiSearchBot.consume(..)) && args(update)")
    public void logUpdate(Update update){
        System.out.println("KEEKEKEKEKEKEKEKEKEKEKEEK");
    }

//    @Before("execution(void lubos.multisearch.telegrambot.bot.MultiSearchBot.consume(*))")
    public void logUpdate2(){
        System.out.println("OLOLOLOLOLOLOLOLOLOL");
    }


//    @Before(value = "execution(void lubos.multisearch.telegrambot.bot.commands.TelegramCommand.handleCommand(..))")
//    public void logUpdate(){
//        System.out.println("1111111111111111111111111111111111111111111111");
//        System.out.println("1111111111111111111111111111111111111111111111");
//        System.out.println("1111111111111111111111111111111111111111111111");
//    }
//    @Before("execution(* lubos.multisearch.telegrambot.bot.commands.CommandHandler+.handleCommand(..))")
    public void logUpdate3(){
        System.out.println("22222222222222222222222222222222222222222222222");
        System.out.println("22222222222222222222222222222222222222222222222");
        System.out.println("22222222222222222222222222222222222222222222222");
    }


//    @Before("execution(* lubos.multisearch.telegrambot.bot.commands.ParametersExtractor+.extractParameters(..))")
    public void logUpdate4(){
        System.out.println("3333333333333333333333333333333333333333333333");
        System.out.println("3333333333333333333333333333333333333333333333");
        System.out.println("3333333333333333333333333333333333333333333333");

    }


}
