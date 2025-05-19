package lubos.multisearch.processor.conf;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.SimplePropertyValueConnectionNameStrategy;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;


@Configuration
public class RabbitConfiguration implements RabbitListenerConfigurer {

    private final LocalValidatorFactoryBean validator;

    @Value("${app.rabbit.tgActionExchangeName}")
    private String tgActionExchangeName;


    public RabbitConfiguration(LocalValidatorFactoryBean validator) {
        this.validator = validator;
    }

//    @Bean
//    Queue tgActionQueue() {
//        return QueueBuilder.nonDurable(tgActionQueueName)
//                .exclusive()
//                .expires(30000)
//                .ttl(20000)
//                .maxLength(200L)
//                .overflow(QueueBuilder.Overflow.rejectPublish)
//                .leaderLocator(QueueBuilder.LeaderLocator.minLeaders)

    /// /                .singleActiveConsumer() ?
    /// /                .quorum()
//                .build();
//    }
    @Bean
    public Queue tgActionQueue() {
        return QueueBuilder
                .nonDurable()
                .exclusive()
                .autoDelete()
//                .expires(30000)
                .ttl(20000)
                .maxLength(200L)
                .overflow(QueueBuilder.Overflow.rejectPublish)
//                .leaderLocator(QueueBuilder.LeaderLocator.minLeaders)
                .leaderLocator(QueueBuilder.LeaderLocator.clientLocal)
//                .singleActiveConsumer() ?
//                .quorum() no sense for exclusive queue
                .build();
    }

    @Bean
    CustomExchange tgActionExchange() {
        return new ExchangeBuilder(tgActionExchangeName, "x-modulus-hash")
                .durable(true)
                .build();
    }

    @Bean
    Binding tgActionBinding() {
        return BindingBuilder.bind(tgActionQueue())
                .to(tgActionExchange())
                .with("")
                .noargs();
    }

    @Bean
    ContainerCustomizer<DirectMessageListenerContainer> configure(ThreadPoolTaskExecutor taskExecutor) {
        return container -> {
            container.setMismatchedQueuesFatal(true);
            container.setTaskExecutor(taskExecutor);
//            container.setGlobalQos(true); //see globalQos in consp
//            container.setTaskScheduler();
        };
    }


    @Bean
    public Jackson2JsonMessageConverter jsonConverter() {
        return new Jackson2JsonMessageConverter();
    }


    //альтернатива конфигурации CachingConnectionFactory- RabbitConnectionFactoryBean and Configuring SSL стр217

    @Bean
    CachingConnectionFactoryConfigurer rabbitConnectionFactoryConfigurer(RabbitConnectionDetails connectionDetails, RabbitProperties properties, ThreadPoolTaskExecutor taskExecutor) {
        return new CachingConnectionFactoryConfigurer(properties, connectionDetails) {
            @Override
            public void configure(CachingConnectionFactory connectionFactory, RabbitProperties rabbitProperties) {
                super.configure(connectionFactory, rabbitProperties);
                connectionFactory.setExecutor(taskExecutor);
                connectionFactory.setConnectionNameStrategy(cns());
            }
        };
    }

    @Bean
    public SimplePropertyValueConnectionNameStrategy cns() {
        return new SimplePropertyValueConnectionNameStrategy("spring.application.name");
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        registrar.setValidator(validator);
    }

//    @Bean
//    MessageRecoverer messageRecoverer() {
//        return new RejectAndDontRequeueRecoverer();
//    }

//    @Bean
//    RabbitListenerErrorHandler rabbitErrorHandler() {
//        return (amqpMessage, message, ex) -> {
//            var cause = ex.getCause();
//            var wrapped = cause instanceof ApplicationException e ? e : new ServerProcessingException(cause);
//            wrapped.setActionMessage((ActionMessage) message.getPayload());
//            throw new ListenerExecutionFailedException(cause.getMessage(), wrapped, amqpMessage);
//        };
//    }
//
//    @Bean
//    MessageRecoverer messageRecoverer(TelegramSender sender, TelegramKeyboard keyboard, MessageSource messageSource) {
//        return (message, cause) -> {
//            var appException = (ApplicationException) cause.getCause();
//            var actionMessage = appException.getActionMessage();
//            var locale = userLocale(actionMessage);
//            var msg = appException.getLocalizedMessage(messageSource, locale);
//            sender.send(actionMessage.chatId(), msg, keyboard.commandsKeyboard(actionMessage.user().getId(), locale));
//            throw new AmqpRejectAndDontRequeueException(appException);
//        };
//    }
//    @Bean
//    RetryOperationsInterceptor interceptor() {
//        return RetryInterceptorBuilder.stateless()
//                .maxAttempts(5)
//                .retryPolicy()
//                .recoverer(new RepublishMessageRecoverer(amqpTemplate(), "something", "somethingelse"))
//                .build();
//    }

//    @Bean
//    public StatefulRetryOperationsInterceptor interceptor() {
//        return RetryInterceptorBuilder.stateful()
//                .maxAttempts(2)
//                .backOffOptions(1000, 2.0, 10000) // initialInterval, multiplier, maxInterval
//                .recoverer((message, cause) -> {
//                    System.out.println("MESSAGE RECOVERER");
//                    throw new AmqpRejectAndDontRequeueException(cause);
//                })
//                .build();
//    }


//    @Bean
//    RestClientBuilderCustomizer elasticClientBuilderCustomizer(){
//        return builder -> {
//            builder.setRequestConfigCallback(
//                    requestConfigBuilder -> {
//                        requestConfigBuilder.
//                    }
//            )
//          builder.setHttpClientConfigCallback(
//                  new RestClientBuilder.HttpClientConfigCallback() {
//                      @Override
//                      public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
//                          return httpClientBuilder.setSSLStrategy(new SSLIOSessionStrategy())
//                      }
//                  }
//          )
//        };
//    }

//    @Bean
//    RestClientOptions restClientOptions(){
//        return  new RestClientOptions(RequestOptions.DEFAULT).with(
//                builder ->{
//                    builder.
//                }
//        );
//    }

//    @Bean //нужно вручную inject - походу только для reply case
//    RecoveryCallback<TgActionDTO> recoveryCallback(){
//        return context -> {

    /// /            context.
//            System.out.println("RECOVERY CALLBACK");
//            throw new RuntimeException();
//        };
//    }


//    RabbitRetryTemplateCustomizer rabbitRetryTemplateCustomizer(){
//        return (target, retryTemplate) -> {
//            retryTemplate.
//        }

//    }


//
//    @Bean
//    RabbitTemplateCustomizer rabbitTemplateCustomizer() {
//        return rabbitTemplate -> {
//            rabbitTemplate.setMessageConverter(jsonConverter());
//        };
//    }


//        @Bean
//        RabbitTemplateCustomizer rabbitTemplateCustomizer() {
//        return rabbitTemplate -> {
//            rabbitTemplate.setAfterReceivePostProcessors(new GUnzipPostProcessor());
//        };
//    }


//    @Bean Имя для консюмера
//    ConsumerTagStrategy consumerTagStrategy()

}