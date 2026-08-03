package com.lab.exception;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.lab.dto.ApiErrorDTO;
import com.lab.dto.ErroDTO;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
		List<ErroDTO> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
				.map(erro -> new ErroDTO(erro.getField(), erro.getDefaultMessage())).toList();
		ApiErrorDTO errorResponse = new ApiErrorDTO("Erro de Validação", 400,
				"Um ou mais campos estão com valores inválidos", LocalDateTime.now(), fieldErrors);

		return ResponseEntity.badRequest().body(errorResponse);
	}

	@ExceptionHandler(NoResourceFoundException.class)
	public ResponseEntity<ApiErrorDTO> handleNotFound(NoResourceFoundException ex) {
		ApiErrorDTO errorResponse = new ApiErrorDTO("Recurso não encontrado", 404, "A URL solicitada não existe",
				LocalDateTime.now(), null);
		return ResponseEntity.status(404).body(errorResponse);
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ApiErrorDTO> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
		var errorResponse = new ApiErrorDTO("Método Não Permitido", 405,
				"O método " + ex.getMethod() + " não é suportado para esta rota", LocalDateTime.now(), null);
		return ResponseEntity.status(405).body(errorResponse);
	}

}