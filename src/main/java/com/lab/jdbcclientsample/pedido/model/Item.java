package com.lab.jdbcclientsample.pedido.model;

import java.math.BigDecimal;

public record Item(long produtoId, int quantidade, BigDecimal precoUnitario) {
}