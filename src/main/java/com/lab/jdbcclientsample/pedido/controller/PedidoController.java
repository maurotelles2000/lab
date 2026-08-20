package com.lab.jdbcclientsample.pedido.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.lab.jdbcclientsample.pedido.dto.PedidoDto;
import com.lab.jdbcclientsample.pedido.dto.PedidoRequest;
import com.lab.jdbcclientsample.pedido.service.PedidoService;

import jakarta.servlet.http.HttpServletRequest;

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
	public List<PedidoDto> pesquisar(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		return pedidoService.pesquisar(page, size);
	}

	@GetMapping("/memoria")
	public String verificarMemoria() {
		Runtime runtime = Runtime.getRuntime();
		long memoriaUsadaMB = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
		long memoriaTotalMB = runtime.totalMemory() / (1024 * 1024);

		return String.format("JVM usando atualmente: %d MB (Total alocada: %d MB)", memoriaUsadaMB, memoriaTotalMB);
	}
}