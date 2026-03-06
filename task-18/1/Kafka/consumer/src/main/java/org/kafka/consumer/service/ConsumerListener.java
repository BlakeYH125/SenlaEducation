package org.kafka.consumer.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.kafka.consumer.entity.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsumerListener {

    private static final Logger log = LoggerFactory.getLogger(ConsumerListener.class);
    private final BankService bankService;

    public ConsumerListener(BankService bankService) {
        this.bankService = bankService;
    }

    @KafkaListener(topics = "transfers-topic", groupId = "bank-consumer-group")
    public void listenTransfers(List<ConsumerRecord<String, Transfer>> records, Acknowledgment ack) {
        log.info("Получена пачка из {} сообщений", records.size());

        for (ConsumerRecord<String, Transfer> record : records) {
            Transfer transfer = record.value();
            try {
                bankService.processTransfer(transfer);
            } catch (Exception e) {
                log.error("Транзакция прервана для перевода {}: {}", transfer.getId(), e.getMessage());
                bankService.saveFailedTransfer(transfer);
            }
        }
        ack.acknowledge();
        log.info("Пачка успешно обработана и подтверждена");
    }
}