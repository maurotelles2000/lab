package com.lab.jdbcclientsample.pedido.repository;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import tools.jackson.databind.ObjectMapper;

@Repository
public class PedidoRepository {

	private final JdbcClient jdbcClient;

	public PedidoRepository(JdbcClient jdbcClient, ObjectMapper objectMapper) {
		this.jdbcClient = jdbcClient;
	}

	public Long salvar(String pedido) {
		try {
	        jdbcClient.sql("CALL sp_inserir_pedido(:payload::jsonb)")
            .param("payload", pedido)
            .update();
			
			return 0L;
		} catch (Exception e) {
			throw new RuntimeException("Erro ao processar JSON do pedido para a procedure", e);
		}
	}

	public String listarPaginadoJson(Long ultimoId, int limit) {
		int safeLimit = Math.clamp(limit, 1, 100);
		String sql = "SELECT fn_listar_pedidos_keyset(:ultimoId, :limit)::text";
		return jdbcClient.sql(sql).param("ultimoId", ultimoId).param("limit", safeLimit).query(String.class).optional()
				.orElse("{\"content\":[],\"nextCursor\":null,\"hasNext\":false,\"limit\":" + safeLimit + "}");
	}

}