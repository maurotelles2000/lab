package com.lab.jdbcclientsample.pedido.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lab.jdbcclientsample.pedido.service.PedidoService;

import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

	private final PedidoService pedidoService;

	public PedidoController(PedidoService pedidoService) {
		this.pedidoService = pedidoService;
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Long> criar(@RequestBody JsonNode pedido) {
		PedidoValidator.validar(pedido);
		Long id = pedidoService.salvar(pedido.toString());
		//return ResponseEntity.status(HttpStatus.CREATED).body(id);
		return ResponseEntity.ok().build();
		
	}

	@GetMapping(value = "/pesquisa", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> pesquisar(@RequestParam(required = false) Long ultimoId,
			@RequestParam(defaultValue = "20") int limit) {
		String json = pedidoService.pesquisar(ultimoId, limit);
		return ResponseEntity.ok(json);
	}

	public void validar(JsonNode pedido) {
		if (!pedido.hasNonNull("clienteId"))
			throw new IllegalArgumentException("clienteId obrigatório");

		if (!pedido.hasNonNull("itens") || !pedido.get("itens").isArray() || pedido.get("itens").isEmpty())
			throw new IllegalArgumentException("itens obrigatório e não pode ser vazio");

		for (JsonNode item : pedido.get("itens")) {
			if (!item.hasNonNull("produtoId") || !item.hasNonNull("quantidade"))
				throw new IllegalArgumentException("cada item precisa de produtoId e quantidade");
			if (item.get("quantidade").asInt() <= 0)
				throw new IllegalArgumentException("quantidade tem que ser > 0");
		}
	}

}