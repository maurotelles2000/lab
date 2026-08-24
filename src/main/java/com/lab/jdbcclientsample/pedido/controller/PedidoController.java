package com.lab.jdbcclientsample.pedido.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lab.jdbcclientsample.pedido.dto.PedidoPaginadoResponseDto;
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

	@GetMapping("/pesquisa")
	public ResponseEntity<PedidoPaginadoResponseDto> pesquisar(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "10") int size) {
	    PedidoPaginadoResponseDto resultado = pedidoService.pesquisar(page, size);
	    return ResponseEntity.ok(resultado);
	}
	@GetMapping("/memoria")
	public String verificarMemoria() {
		Runtime runtime = Runtime.getRuntime();
		long memoriaUsadaMB = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
		long memoriaTotalMB = runtime.totalMemory() / (1024 * 1024);

		return String.format("JVM usando atualmente: %d MB (Total alocada: %d MB)", memoriaUsadaMB, memoriaTotalMB);
	}
}