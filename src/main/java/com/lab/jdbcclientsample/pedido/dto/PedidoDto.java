package com.lab.jdbcclientsample.pedido.dto;

import java.time.LocalDateTime;

public record PedidoDto(
    Long id,
    Long clienteId,
    LocalDateTime dataPedido
) {}