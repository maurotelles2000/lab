package com.lab.jdbcclientsample.pedido.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lab.jdbcclientsample.pedido.repository.PedidoRepository;

@Service
public class PedidoService {

	private final PedidoRepository pedidoRepository;

	public PedidoService(PedidoRepository pedidoRepository) {
		this.pedidoRepository = pedidoRepository;
	}

	@Transactional
	public Long salvar(String pedido) {
		return pedidoRepository.salvar(pedido);
	}

	public String pesquisar(Long ultimoId, int limit) {
		String jsonPaginado = pedidoRepository.listarPaginadoJson(ultimoId, limit);
		return jsonPaginado;
	}
}