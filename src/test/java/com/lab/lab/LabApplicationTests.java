package com.lab.lab;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

import com.lab.controller.BaseIntegrationTest;

class LabApplicationTests extends BaseIntegrationTest {

	@Test
	void deveRetornar404QuandoUrlNaoExistir() throws Exception {
		mockMvc.perform(get("/api/clientes/url-que-nao-existe")).andExpect(status().isNotFound());
	}

	@Test
	void deveRetornar405QuandoMetodoNaoPermitido() throws Exception {
		mockMvc.perform(get("/api/clientes")).andExpect(status().isMethodNotAllowed());
	}

}
