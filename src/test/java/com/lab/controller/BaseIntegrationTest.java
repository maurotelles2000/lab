package com.lab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import com.lab.config.IntegrationTest;

import tools.jackson.databind.ObjectMapper;

@IntegrationTest
public abstract class BaseIntegrationTest {
	
	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;


}
