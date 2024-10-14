package lubos.multisearch.telegrambot.conf;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.SimplePropertyValueConnectionNameStrategy;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.CachingConnectionFactoryConfigurer;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class RabbitConfiguration {

    @Value("${app-config.rabbit.tgActionRoutingKey}")
    private String tgActionRoutingKey;

    @Value("${app-config.rabbit.tgActionQueueName}")
    private String tgActionQueueName;

    @Value("${app-config.rabbit.tgActionExchangeName}")
    private String tgActionExchangeName;


    @Bean
    Queue tgActionQueue() {
        return QueueBuilder.nonDurable(tgActionQueueName)
                .expires(30000)
                .ttl(20000)
                .maxLength(200L)
                .overflow(QueueBuilder.Overflow.rejectPublish)
                .leaderLocator(QueueBuilder.LeaderLocator.minLeaders)
//                .singleActiveConsumer() ?
//                .quorum()
                .build();
    }

    @Bean
    public CustomExchange tgActionExchange() {
        return new ExchangeBuilder(tgActionExchangeName, "x-modulus-hash")
                .durable(true)
                .build();
    }

    @Bean
    public Binding tgActionBinding() {
        return BindingBuilder.bind(tgActionQueue())
                .to(tgActionExchange())
                .with("")
                .noargs();
    }


    @Bean
    public Jackson2JsonMessageConverter jsonConverter() {
        var converter = new Jackson2JsonMessageConverter();
        converter.setCreateMessageIds(true);
        return converter;
    }

    //альтернатива конфигурации CachingConnectionFactory- RabbitConnectionFactoryBean and Configuring SSL стр217
    @Bean
    CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer(RabbitConnectionDetails connectionDetails,
                                                                         RabbitProperties properties, ThreadPoolTaskExecutor taskExecutor) {
        CachingConnectionFactoryConfigurer configurer = new CachingConnectionFactoryConfigurer(properties,
                connectionDetails) {
            @Override
            public void configure(CachingConnectionFactory connectionFactory, RabbitProperties rabbitProperties) {
                super.configure(connectionFactory, rabbitProperties);
                connectionFactory.setExecutor(taskExecutor);
            }
        };
        configurer.setConnectionNameStrategy(cns());
        return configurer;
    }


    @Bean
    public SimplePropertyValueConnectionNameStrategy cns() {
        return new SimplePropertyValueConnectionNameStrategy("spring.application.name");
    }


//    @Bean
//    RabbitTemplateCustomizer rabbitTemplateCustomizer() {
//        return rabbitTemplate -> {
////            rabbitTemplate.setMessageConverter(jsonConverter());
//        rabbitTemplate.setRecoveryCallback(
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



}
