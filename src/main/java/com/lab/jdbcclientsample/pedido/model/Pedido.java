package com.lab.jdbcclientsample.pedido.model;

import java.util.List;

public record Pedido(
	    long clienteId,
	    List<Item> itens
	) {}
