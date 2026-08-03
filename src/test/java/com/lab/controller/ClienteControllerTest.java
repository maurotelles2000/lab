package com.lab.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.lab.dto.ClienteDTO;

public class ClienteControllerTest extends BaseIntegrationTest {

	@Test
	void deveCriarClienteComSucesso() throws Exception {
		ClienteDTO dto = new ClienteDTO("João da Silva");

		mockMvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.nome").value("João da Silva")).andExpect(jsonPath("$.id").exists());
	}

	@Test
	void deveRetornarErroQuandoNomeForNulo() throws Exception {
		String jsonNulo = "{\"nome\": null}";

		mockMvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON).content(jsonNulo))
				.andExpect(status().isBadRequest());
	}

	@Test
	void deveRetornarErroQuandoNomeForVazio() throws Exception {
		ClienteDTO dto = new ClienteDTO("");

		mockMvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].campo").value("nome"))
				.andExpect(jsonPath("$.errors[0].mensagem").value("O nome é obrigatório"))
				.andExpect(jsonPath("$.title").value("Erro de Validação")).andExpect(jsonPath("$.status").value(400));
	}

}