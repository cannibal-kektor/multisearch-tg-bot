open module botProcessor {

    requires spring.context;
    requires spring.beans;
    requires spring.core;
    requires spring.boot;
    requires spring.tx;
    requires spring.boot.autoconfigure;
    requires spring.aop;

    requires spring.data.commons;
    requires spring.data.mongodb;
    requires org.mongodb.driver.core;

    requires spring.data.elasticsearch;

    requires spring.amqp;
    requires spring.rabbit;
    requires spring.messaging;

    requires telegrambots.client;
    requires telegrambots.meta;

    requires jakarta.annotation;
    requires jakarta.validation;
    requires org.hibernate.validator;

    requires org.jsoup;
    requires org.apache.pdfbox;
    requires org.apache.poi.ooxml;

    requires lombok;
    requires org.mapstruct;
    requires com.google.common;
    requires spring.retry;
    requires com.rabbitmq.client;
    requires org.aspectj.weaver;
    requires org.slf4j;


}