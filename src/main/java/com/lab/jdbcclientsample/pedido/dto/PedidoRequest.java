package com.lab.jdbcclientsample.pedido.dto;

import java.util.List;

public record PedidoRequest(Long clienteId, List<ItemPedidoRequest> itens) {
}