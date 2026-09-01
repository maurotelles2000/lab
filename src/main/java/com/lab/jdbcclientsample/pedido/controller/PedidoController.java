package com.lab.jdbcclientsample.pedido.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lab.jdbcclientsample.pedido.dto.PedidoRequest;
import com.lab.jdbcclientsample.pedido.service.PedidoService;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

	private final PedidoService pedidoService;

	public PedidoController(PedidoService pedidoService) {
		this.pedidoService = pedidoService;
	}

	@PostMapping
	public ResponseEntity<Void> criar(@RequestBody PedidoRequest request) {

		pedidoService.cadastrar(request);
		return ResponseEntity.ok().build();
	}

	@GetMapping(value = "/pesquisa", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> pesquisar(@RequestParam(required = false) Long ultimoId,
			@RequestParam(defaultValue = "20") int limit) {
		String json = pedidoService.pesquisar(ultimoId, limit);
		return ResponseEntity.ok(json);
	}

}