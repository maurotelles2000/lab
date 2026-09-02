package com.lab.service;

import org.springframework.stereotype.Service;

import com.lab.dto.ClienteDTO;
import com.lab.entity.Cliente;

//import jakarta.transaction.Transactional;

@Service
public class ClienteService {

	//private final ClienteRepository repository;

//	public ClienteService(ClienteRepository repository) {
//		this.repository = repository;
//	}

	//@Transactional	
	public Cliente salvarCliente(ClienteDTO dto) {
//		Cliente cliente = Cliente.builder().nome(dto.nome()).build();
		//return repository.save(cliente);
		return null; 
	}
}