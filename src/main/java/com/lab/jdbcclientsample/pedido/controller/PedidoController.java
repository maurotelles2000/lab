package com.lab.jdbcclientsample.pedido.controller;

import java.util.UUID;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lab.jdbcclientsample.pedido.config.RabbitMQConfig;
import com.lab.jdbcclientsample.pedido.service.PedidoService;
import com.lab.jdbcclientsample.pedido.service.RabbitMQProducerAssincrono;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

	private final PedidoService pedidoService;
	private RabbitTemplate rabbitTemplate;
	private RabbitMQProducerAssincrono producerAssincrono;

	public PedidoController(PedidoService pedidoService, RabbitTemplate rabbitTemplate, RabbitMQProducerAssincrono producerAssincrono) {
		this.pedidoService = pedidoService;
		this.rabbitTemplate = rabbitTemplate;
		this.producerAssincrono = producerAssincrono;
	}

//	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<String> criar2(@RequestBody JsonNode pedido) {
//		PedidoValidator.validar(pedido);
//		//Long id = pedidoService.salvar(pedido.toString());
//		String uuidPedido = UUID.randomUUID().toString();
//		rabbitTemplate.convertAndSend(RabbitMQConfig.FILA_PEDIDOS, pedido.toString());
//		
//		//return ResponseEntity.status(HttpStatus.CREATED).body(id);
//		return ResponseEntity.status(HttpStatus.CREATED).body(uuidPedido);
//		
//	}

	
	
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> criar(@RequestBody JsonNode pedidoJson) {
        return Mono.fromRunnable(() -> {
            // 1. Validação rápida em memória
            PedidoValidator.validar(pedidoJson);
            
            // 2. Geração do UUID
            String uuidPedido = UUID.randomUUID().toString();
            if (pedidoJson instanceof ObjectNode) {
                ((ObjectNode) pedidoJson).put("uuid", uuidPedido);
            }

            // 3. Envio assíncrono via buffer (não trava a resposta HTTP)
            String mensagem = String.format("{\"uuid\": \"%s\", \"dados\": %s}", uuidPedido, pedidoJson.toString());
            producerAssincrono.enviar(mensagem);
            
        })
        .thenReturn(ResponseEntity.status(201).build());
    }
	
	
	
	
	
	@PostMapping(value="/sincrono", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Void>> sincrono(@RequestBody JsonNode pedidoJson) {
        
        return Mono.fromRunnable(() -> {
            // 1. Valida o JSON
            PedidoValidator.validar(pedidoJson);
            
            // 2. Gera o UUID único
            String uuidPedido = UUID.randomUUID().toString();
            
            if (pedidoJson instanceof ObjectNode) {
                ((ObjectNode) pedidoJson).put("uuid", uuidPedido);
            }

            // 3. Envia direto para a fila do RabbitMQ
            String mensagem = String.format("{\"uuid\": \"%s\", \"dados\": %s}", uuidPedido, pedidoJson.toString());
            rabbitTemplate.convertAndSend(RabbitMQConfig.FILA_PEDIDOS, mensagem);
            
        })
        .subscribeOn(Schedulers.boundedElastic()) // Mantém a operação de envio bloqueante isolada do Event Loop do Netty
        .thenReturn(ResponseEntity.status(201).build()); // Retorna 201 Created sem corpo (ou com o que preferir)
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