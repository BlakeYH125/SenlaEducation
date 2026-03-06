package org.kafka.producer.service;

import jakarta.annotation.PostConstruct;
import org.kafka.producer.entity.Account;
import org.kafka.producer.entity.Transfer;
import org.kafka.producer.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProducerService {

    private static final Logger log = LoggerFactory.getLogger(ProducerService.class);
    private static final String TOPIC = "transfers-topic";

    private final AccountRepository accountRepository;
    private final KafkaTemplate<String, Transfer> kafkaTemplate;

    private final Map<Long, Account> localAccounts = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public ProducerService(AccountRepository accountRepository, KafkaTemplate<String, Transfer> kafkaTemplate) {
        this.accountRepository = accountRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    public void initAccounts() {
        List<Account> dbAccounts = accountRepository.findAll();

        if (dbAccounts.isEmpty()) {
            log.info("Таблица счетов пуста. Генерируем 1000 новых счетов.");
            for (int i = 0; i < 1000; i++) {
                BigDecimal initialBalance = new BigDecimal(10000 + random.nextInt(90000));
                dbAccounts.add(new Account(initialBalance));
            }
            dbAccounts = accountRepository.saveAll(dbAccounts);
            log.info("1000 счетов успешно сохранены в БД.");
        } else {
            log.info("Найдено {} счетов в БД. Загружаем в память.", dbAccounts.size());
        }

        for (Account account : dbAccounts) {
            localAccounts.put(account.getId(), account);
        }
    }

    @Scheduled(fixedDelay = 200)
    public void generateAndSendTransfer() {
        if (localAccounts.size() < 2) return;

        List<Long> accountIds = new ArrayList<>(localAccounts.keySet());

        Long fromAccountId = accountIds.get(random.nextInt(accountIds.size()));
        Long toAccountId = accountIds.get(random.nextInt(accountIds.size()));
        while (fromAccountId.equals(toAccountId)) {
            toAccountId = accountIds.get(random.nextInt(accountIds.size()));
        }

        String transferId = UUID.randomUUID().toString();
        BigDecimal amount = new BigDecimal(random.nextInt(5000) + 1);

        Transfer transfer = new Transfer(transferId, fromAccountId, toAccountId, amount, "создан");

        log.info("Отправка в Kafka: Перевод {} на сумму {} руб. (Счет {} -> Счет {})",
                transferId, amount, fromAccountId, toAccountId);

        kafkaTemplate.send(TOPIC, transferId, transfer);
    }
}