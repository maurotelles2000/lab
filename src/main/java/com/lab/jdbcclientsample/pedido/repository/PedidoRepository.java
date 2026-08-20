package com.lab.jdbcclientsample.pedido.repository;

import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.lab.jdbcclientsample.pedido.dto.PedidoDto;
import com.lab.jdbcclientsample.pedido.dto.PedidoRequest;

import tools.jackson.databind.ObjectMapper;

@Repository
public class PedidoRepository {

	private final JdbcClient jdbcClient;
	private final ObjectMapper objectMapper;

	public PedidoRepository(JdbcClient jdbcClient, ObjectMapper objectMapper) {
		this.jdbcClient = jdbcClient;
		this.objectMapper = objectMapper;
	}

	public void salvarPedido(PedidoRequest request) {
		try {
			String payloadJson = objectMapper.writeValueAsString(request);
			String sql = "CALL sp_inserir_pedido(CAST(:payload AS JSONB))";

			jdbcClient.sql(sql).param("payload", payloadJson).update();

		} catch (Exception e) {
			throw new RuntimeException("Erro ao processar JSON do pedido para a procedure", e);
		}
	}

	public void salvarPedido(String request) {
		try {
			String sql = "CALL sp_inserir_pedido(CAST(:payload AS JSONB))";

			jdbcClient.sql(sql).param("payload", request).update();

		} catch (Exception e) {
			throw new RuntimeException("Erro ao processar JSON do pedido para a procedure", e);
		}
	}

	public List<PedidoDto> listarPaginado(int page, int size) {
		int offset = page * size;
		String sql = "SELECT * FROM fn_listar_pedidos(:limit, :offset)";
		return jdbcClient.sql(sql).param("limit", size).param("offset", offset).query(PedidoDto.class).list();
	}

}