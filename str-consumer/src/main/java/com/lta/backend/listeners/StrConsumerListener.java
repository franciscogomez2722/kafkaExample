package com.lta.backend.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

@Component
public class StrConsumerListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(StrConsumerListener.class);

    @KafkaListener(groupId = "group-1",
            topicPartitions = @TopicPartition(topic = "str-topic",partitions = {"0"})
            ,containerFactory = "validMessageContainerFactory")
    public void listener1(String message){
        LOGGER.info("LISTENER1 ::: Recibiendo un mensaje {}",message);
    }

    @KafkaListener(groupId = "group-1",
            topicPartitions = @TopicPartition(topic = "str-topic",partitions = {"1"})
            ,containerFactory = "validMessageContainerFactory")
    public void listener2(String message){
        LOGGER.info("LISTENER2 ::: Recibiendo un mensaje {}",message);
    }

    @KafkaListener(groupId = "group-2",topics = "str-topic",containerFactory = "validMessageContainerFactory")
    public void listener3(String message){
        LOGGER.info("LISTENER3 ::: Recibiendo un mensaje {}",message);
    }

}
