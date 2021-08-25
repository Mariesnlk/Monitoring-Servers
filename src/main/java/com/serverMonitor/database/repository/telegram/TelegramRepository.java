package com.serverMonitor.database.repository.telegram;

import com.serverMonitor.database.enteties.telegram.TelegramInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TelegramRepository extends CrudRepository<TelegramInfo, Long> {

    List<TelegramInfo> findAll();

    TelegramInfo findByChartId(Long chatId);
}
