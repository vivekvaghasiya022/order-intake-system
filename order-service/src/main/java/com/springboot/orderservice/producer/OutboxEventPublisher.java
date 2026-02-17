package com.springboot.orderservice.producer;


import com.springboot.orderservice.service.OutboxEventProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventPublisher {

    private final OutboxEventProcessService outboxEventProcessService;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {
        log.info("Scheduled task started: Publishing pending outbox events");
        outboxEventProcessService.processBatch();
        log.info("Scheduled task completed: Finished processing pending outbox events");
    }
}
