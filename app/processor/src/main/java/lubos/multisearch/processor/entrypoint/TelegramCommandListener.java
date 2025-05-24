package lubos.multisearch.processor.entrypoint;

import jakarta.validation.Valid;
import lubos.multisearch.processor.bot.commands.Command;
import lubos.multisearch.processor.bot.commands.CommandProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;


@Component
public class TelegramCommandListener {

    private final EnumMap<Command, CommandProcessor> commandsProcessorsMap = new EnumMap<>(Command.class);

    public TelegramCommandListener(List<CommandProcessor> commandProcessors) {
        commandProcessors.forEach(commandProcessor ->
                commandsProcessorsMap.put(commandProcessor.getCommand(), commandProcessor));
    }

    @RabbitListener(queues = "#{tgActionQueue}", errorHandler = "rabbitErrorHandler")
    void consume(@Payload @Valid CommandActionContext context) {
        var commandProcessor = commandsProcessorsMap.get(context.command());
        commandProcessor.process(context);
    }

}