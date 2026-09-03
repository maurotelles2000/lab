package com.lab.jdbcclientsample.pedido.service;

	
import java.nio.charset.StandardCharsets;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.lab.jdbcclientsample.pedido.config.RabbitMQConfig;

import jakarta.annotation.PostConstruct;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Service
public class RabbitMQProducerAssincrono {

    private final RabbitTemplate rabbitTemplate;

    // Sink multithread para alta vazão (em memória)
    private final Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();

    public RabbitMQProducerAssincrono(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostConstruct
    public void init() {
        sink.asFlux()
            .publishOn(Schedulers.boundedElastic())
            .subscribe(mensagem -> {
                try {
                    rabbitTemplate.convertAndSend(RabbitMQConfig.FILA_PEDIDOS, mensagem.getBytes(StandardCharsets.UTF_8));
                } catch (Exception e) {
                }
            });
    }

    public void enviar(String mensagem) {
        sink.tryEmitNext(mensagem);
    }
}