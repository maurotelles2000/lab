package com.lab.jdbcclientsample.pedido.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lab.jdbcclientsample.pedido.dto.PedidoDto;
import com.lab.jdbcclientsample.pedido.dto.PedidoRequest;
import com.lab.jdbcclientsample.pedido.repository.PedidoRepository;

@Service
public class PedidoService {

	private final PedidoRepository pedidoRepository;

	public PedidoService(PedidoRepository pedidoRepository) {
		this.pedidoRepository = pedidoRepository;
	}

	@Transactional
	public void cadastrar(PedidoRequest request) {
		pedidoRepository.salvarPedido(request);
	}

	@Transactional
	public void cadastrar(String request) {
		pedidoRepository.salvarPedido(request);
	}

	public List<PedidoDto> pesquisar(int page, int size) {
		return pedidoRepository.listarPaginado(page, size);
	}
}