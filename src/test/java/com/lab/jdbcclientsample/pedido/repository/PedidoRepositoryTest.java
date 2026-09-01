package com.lab.jdbcclientsample.pedido.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.jdbc.core.simple.JdbcClient;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class PedidoRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setUp() {
        jdbcClient.sql("BEGIN").update();
    }

    @AfterEach
    void tearDown() {
        jdbcClient.sql("ROLLBACK").update();
    }

    @Test
    void deveListarPedidosComPaginacaoKeyset() {
        String json = jdbcClient.sql("SELECT fn_listar_pedidos_keyset(NULL, 5)::text")
                .query(String.class)
                .single();

        assertThat(json).contains("\"hasNext\"");
    }
}