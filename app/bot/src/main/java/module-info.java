open module telegramBot {
    requires spring.core;
    requires spring.aop;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.beans;
    requires spring.context;
    requires spring.tx;

    requires spring.data.mongodb;
    requires org.mongodb.driver.core;

    requires spring.rabbit;
    requires spring.amqp;

    requires telegrambots.meta;
    requires telegrambots.abilities;
    requires telegrambots.longpolling;
    requires telegrambots.springboot.longpolling.starter;
    requires telegrambots.client;

    requires lombok;
    requires com.google.common;

    requires jakarta.annotation;
}