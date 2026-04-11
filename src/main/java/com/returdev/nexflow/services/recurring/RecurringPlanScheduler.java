package com.returdev.nexflow.services.recurring;

import com.returdev.nexflow.model.entities.RecurringPlanEntity;
import com.returdev.nexflow.model.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
@RequiredArgsConstructor
public class RecurringPlanScheduler {

    private final RecurringPlanService planService;


    @Scheduled(cron = "0 5 * * * *")
    @SchedulerLock(name = "recurringPlanLock", lockAtLeastFor = "PT5M", lockAtMostFor = "PT15M")
    public void runScheduledPlans() {

        int pageSize = 50;
        LocalDateTime date = LocalDateTime.now();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        boolean hasMorePlans = true;

        log.info("Starting scheduled execution of recurring plans...");

        while (hasMorePlans) {

            Pageable pageable = PageRequest.of(0, pageSize);
            Page<RecurringPlanEntity> page = planService.getPlansToExecute(date, pageable);

            if (page.isEmpty()) {
                hasMorePlans = false;
                continue;
            }

            page.forEach(recurringPlan -> {
                try {

                    planService.executePlan(recurringPlan);

                    successCount.incrementAndGet();
                } catch (BusinessException ex) {
                    log.error("Business failure for plan ID {}: {}", recurringPlan.getId(), ex.getCode());
                    ;
                    failureCount.decrementAndGet();
                } catch (Exception ex) {
                    log.error("Unexpected technical failure for plan ID {}: {}", recurringPlan.getId(), ex.getMessage());
                    ;
                    failureCount.decrementAndGet();
                }
            });

            try {
                Thread.sleep(200);
            } catch (InterruptedException ignored) {
            }

        }

        log.info("Execution summary: {} success, {} failures", successCount.get(), failureCount.get());
        ;

    }

}
