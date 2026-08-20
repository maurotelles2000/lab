package com.lab.jdbcclientsample.pedido.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.lab.jdbcclientsample.pedido.dto.ItemPedidoRequest;
import com.lab.jdbcclientsample.pedido.dto.PedidoRequest;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class PedidoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void deveCriarPedidoComSucessoViaEndpoint() throws Exception {
		var item01 = new ItemPedidoRequest(99L, 3, new BigDecimal("150.00"));
		var item02 = new ItemPedidoRequest(99L, 3, new BigDecimal("150.00"));
		var item03 = new ItemPedidoRequest(99L, 3, new BigDecimal("150.00"));
		var item04 = new ItemPedidoRequest(99L, 3, new BigDecimal("150.00"));
		var item05 = new ItemPedidoRequest(99L, 3, new BigDecimal("150.00"));
		var item06 = new ItemPedidoRequest(99L, 3, new BigDecimal("150.00"));
		var item07 = new ItemPedidoRequest(99L, 3, new BigDecimal("150.00"));
		var item08 = new ItemPedidoRequest(99L, 3, new BigDecimal("150.00"));
		var item09 = new ItemPedidoRequest(99L, 3, new BigDecimal("150.00"));
		var item10 = new ItemPedidoRequest(99L, 3, new BigDecimal("150.00"));
		var item11 = new ItemPedidoRequest(99L, 3, new BigDecimal("150.00"));
		var item12 = new ItemPedidoRequest(99L, 3, new BigDecimal("150.00"));
		var request = new PedidoRequest(10L, List.of(
				  item01
				, item02
				, item03
				, item04
				, item05
				, item06
				, item07
				, item08
				, item09
				, item10
				, item11
				, item12
				));

		mockMvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk());

	}
}
