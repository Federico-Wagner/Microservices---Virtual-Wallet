package com.billeteraVirtual.transacciones;

import com.billeteraVirtual.transacciones.config.RabbitConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public NotificationPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNotification(String email, String subject, String message) {
        NotificationEvent event = new NotificationEvent(LocalDateTime.now().toString(), "TRANSACTION", email, subject, message);
        rabbitTemplate.convertAndSend(RabbitConfig.NOTIFICATIONS_QUEUE, event);
    }

    @Data
    @AllArgsConstructor
    public static class NotificationEvent {
        private String createdAt;
        private String type;
        private String to;
        private String subject;
        private String message;
    }
}
