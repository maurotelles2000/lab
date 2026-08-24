package com.lab.jdbcclientsample.pedido.repository;

import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.lab.jdbcclientsample.pedido.dto.PedidoDto;
import com.lab.jdbcclientsample.pedido.dto.PedidoPaginadoResponseDto;
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

	public PedidoPaginadoResponseDto listarPaginado(int page, int size) {
		int offset = page * size;

		// 1. Busca o total de registros usando a função de contagem
		String sqlCount = "SELECT fn_contar_pedidos()";
		Long totalRegistros = jdbcClient.sql(sqlCount)
				.query(Long.class)
				.single();

		// 2. Busca os dados paginados utilizando o mapeamento automático do JdbcClient
		String sqlList = "SELECT * FROM fn_listar_pedidos(:limit, :offset)";
		List<PedidoDto> itens = jdbcClient.sql(sqlList)
				.param("limit", size)
				.param("offset", offset)
				.query(PedidoDto.class)
				.list();

		// 3. Retorna o DTO estruturado com a paginação completa
		return new PedidoPaginadoResponseDto(itens, totalRegistros != null ? totalRegistros : 0L, page, size);
	}

	
	
	
}