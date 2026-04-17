package com.eralp.ecommerce.messaging;

import com.eralp.ecommerce.config.RabbitMqConfig;
import com.eralp.ecommerce.dto.event.OrderCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderCreatedConsumer {

    @RabbitListener(queues = RabbitMqConfig.ORDER_QUEUE)
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info(
                "Received order created event. orderId={}, userId={}, email={}, total={}",
                event.getOrderId(),
                event.getUserId(),
                event.getUserEmail(),
                event.getTotalAmount()
        );
        log.info("Sending confirmation email to {}", event.getUserEmail());
    }
}
