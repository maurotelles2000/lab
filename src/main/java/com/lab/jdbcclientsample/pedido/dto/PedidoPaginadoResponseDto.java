package com.lab.jdbcclientsample.pedido.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoPaginadoResponseDto {

	private List<PedidoDto> itens;
	private long totalRegistros;
	private int pagina;
	private int tamanho;
	private int totalPaginas;

	public PedidoPaginadoResponseDto(List<PedidoDto> itens, long totalRegistros, int pagina, int tamanho) {
		this.itens = itens;
		this.totalRegistros = totalRegistros;
		this.pagina = pagina;
		this.tamanho = tamanho;
		this.totalPaginas = tamanho > 0 ? (int) Math.ceil((double) totalRegistros / tamanho) : 0;
	}
}