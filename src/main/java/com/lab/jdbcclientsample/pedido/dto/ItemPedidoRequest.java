package com.lab.jdbcclientsample.pedido.dto;

import java.math.BigDecimal;

public record ItemPedidoRequest(Long produtoId, Integer quantidade, BigDecimal precoUnitario) {
}