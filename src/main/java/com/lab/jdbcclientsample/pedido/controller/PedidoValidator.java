package com.lab.jdbcclientsample.pedido.controller;

import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class PedidoValidator {

    /**
     * Valida o JsonNode do pedido.
     * Lança IllegalArgumentException com todos os erros encontrados, se houver.
     */
    public static void validar(JsonNode pedido) {
        List<String> erros = new ArrayList<>();

        if (pedido == null || pedido.isNull() || pedido.isMissingNode()) {
            throw new IllegalArgumentException("Corpo da requisição vazio ou inválido.");
        }

        if (!pedido.isObject()) {
            throw new IllegalArgumentException("O corpo do pedido deve ser um objeto JSON.");
        }

        // ---- clienteId ----
        JsonNode clienteId = pedido.get("clienteId");
        if (clienteId == null || clienteId.isNull()) {
            erros.add("Campo 'clienteId' é obrigatório.");
        } else if (!clienteId.isIntegralNumber()) {
            erros.add("Campo 'clienteId' deve ser um número inteiro.");
        } else if (clienteId.asLong() <= 0) {
            erros.add("Campo 'clienteId' deve ser maior que zero.");
        }

        // ---- itens ----
        JsonNode itens = pedido.get("itens");
        if (itens == null || itens.isNull()) {
            erros.add("Campo 'itens' é obrigatório.");
        } else if (!itens.isArray()) {
            erros.add("Campo 'itens' deve ser uma lista.");
        } else if (itens.isEmpty()) {
            erros.add("Campo 'itens' não pode ser vazio.");
        } else {
            for (int i = 0; i < itens.size(); i++) {
                JsonNode item = itens.get(i);
                String prefixo = "itens[" + i + "]";

                if (item == null || !item.isObject()) {
                    erros.add(prefixo + " deve ser um objeto.");
                    continue;
                }

                // produtoId
                JsonNode produtoId = item.get("produtoId");
                if (produtoId == null || produtoId.isNull()) {
                    erros.add(prefixo + ".produtoId é obrigatório.");
                } else if (!produtoId.isIntegralNumber()) {
                    erros.add(prefixo + ".produtoId deve ser um número inteiro.");
                } else if (produtoId.asLong() <= 0) {
                    erros.add(prefixo + ".produtoId deve ser maior que zero.");
                }

                // quantidade
                JsonNode quantidade = item.get("quantidade");
                if (quantidade == null || quantidade.isNull()) {
                    erros.add(prefixo + ".quantidade é obrigatório.");
                } else if (!quantidade.isIntegralNumber()) {
                    erros.add(prefixo + ".quantidade deve ser um número inteiro.");
                } else if (quantidade.asLong() <= 0) {
                    erros.add(prefixo + ".quantidade deve ser maior que zero.");
                }

                // precoUnitario
                JsonNode precoUnitario = item.get("precoUnitario");
                if (precoUnitario == null || precoUnitario.isNull()) {
                    erros.add(prefixo + ".precoUnitario é obrigatório.");
                } else if (!precoUnitario.isNumber()) {
                    erros.add(prefixo + ".precoUnitario deve ser numérico.");
                } else if (precoUnitario.asDouble() < 0) {
                    erros.add(prefixo + ".precoUnitario não pode ser negativo.");
                }
            }
        }

        if (!erros.isEmpty()) {
            throw new IllegalArgumentException("Pedido inválido: " + String.join(" | ", erros));
        }
    }
}