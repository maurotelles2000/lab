package com.lab.jdbcclientsample.pedido.config;



import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String FILA_PEDIDOS = "pedidos.criados";

    @Bean
    public Queue queue() {
        return new Queue(FILA_PEDIDOS, true); 
    }
}