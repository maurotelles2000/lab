package com.lab.dto;

import jakarta.validation.constraints.NotBlank;

public record ClienteDTO(@NotBlank(message = "O nome é obrigatório") String nome) {
}