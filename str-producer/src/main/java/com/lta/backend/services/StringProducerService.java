package com.lta.backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class StringProducerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StringProducerService.class);

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    public void sendMessage(String message){
        kafkaTemplate.send("str-topic",message).whenComplete((result,ex) -> {
           if(ex != null){
               LOGGER.error("Error, al enviar el mensaje: {}",ex.getMessage());
           }
           LOGGER.info("Mensaje enviado con éxito: {}",result.getProducerRecord().value());
           LOGGER.info("Particion {}, Offset {}", result.getRecordMetadata().partition(),result.getRecordMetadata().offset());
        });
    }
}
