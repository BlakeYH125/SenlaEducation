package org.kafka.consumer.service;

import org.kafka.consumer.entity.Account;
import org.kafka.consumer.entity.Transfer;
import org.kafka.consumer.repository.AccountRepository;
import org.kafka.consumer.repository.TransferRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankService {

    private static final Logger log = LoggerFactory.getLogger(BankService.class);

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;

    public BankService(AccountRepository accountRepository, TransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public void processTransfer(Transfer transfer) {
        log.info("Начало обработки перевода {}", transfer.getId());

        Account fromAccount = accountRepository.findById(transfer.getFromAccountId()).orElse(null);
        Account toAccount = accountRepository.findById(transfer.getToAccountId()).orElse(null);

        if (fromAccount == null || toAccount == null) {
            log.error("Ошибка валидации: Счета не найдены для перевода {}", transfer.getId());
            throw new RuntimeException("Счета не найдены");
        }

        if (fromAccount.getBalance().compareTo(transfer.getAmount()) < 0) {
            log.error("Ошибка валидации: Недостаточно средств на счете {} для перевода {}",
                    fromAccount.getId(), transfer.getId());
            throw new RuntimeException("Недостаточно средств");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(transfer.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(transfer.getAmount()));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        transfer.setStatus("готово");
        transferRepository.save(transfer);

        log.info("Успех: Перевод {} успешно выполнен", transfer.getId());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailedTransfer(Transfer transfer) {
        transfer.setStatus("завершилось с ошибкой");
        transferRepository.save(transfer);
        log.info("Перевод {} сохранен в БД со статусом 'завершилось с ошибкой'", transfer.getId());
    }
}