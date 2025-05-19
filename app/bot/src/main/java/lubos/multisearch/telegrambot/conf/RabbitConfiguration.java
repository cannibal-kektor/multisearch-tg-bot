package lubos.multisearch.telegrambot.conf;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.SimplePropertyValueConnectionNameStrategy;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfiguration {

    @Value("${app.rabbit.tgActionExchangeName}")
    private String tgActionExchangeName;

    @Bean
    public CustomExchange tgActionExchange() {
        return new ExchangeBuilder(tgActionExchangeName, "x-modulus-hash")
                .durable(true)
                .build();
    }

    @Bean
    public Jackson2JsonMessageConverter jsonConverter() {
        //можно убрать TypeId header, тк он тип сообщений статичен
        // Converter.setClassMapper( DefaultClassMapper().setDefaultType(ActionMessage));
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimplePropertyValueConnectionNameStrategy cns() {
        return new SimplePropertyValueConnectionNameStrategy("spring.application.name");
    }

//    @Bean
//    RabbitTemplateCustomizer rabbitTemplateCustomizer() {
//        return rabbitTemplate -> {
//            rabbitTemplate.setMessageConverter(jsonConverter());
//            rabbitTemplate.setRecoveryCallback(
//                new RecoveryCallback<Object>() {
//
//                    @Override
//                    public Object recover(RetryContext context) throws Exception {
//                        return null;
//                    }
//                }
//        );
//        };
//    }
//    @Bean
//    RabbitTemplateCustomizer rabbitTemplateCustomizer() {
//        return rabbitTemplate -> {
//            rabbitTemplate.setBeforePublishPostProcessors(new GZipPostProcessor());
//        };
//    }


}
