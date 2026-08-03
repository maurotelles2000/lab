package com.lab.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErrorDTO(String title, int status, String detail, LocalDateTime timestamp, List<ErroDTO> errors) {
}