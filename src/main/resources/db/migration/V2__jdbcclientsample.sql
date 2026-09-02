CREATE TABLE IF NOT EXISTS pedidos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    data_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS itens_pedido (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT REFERENCES pedidos(id),
    produto_id BIGINT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario NUMERIC(10,2) NOT NULL
);


CREATE OR REPLACE PROCEDURE sp_inserir_pedido(p_payload JSONB)
LANGUAGE sql AS $$
  WITH novo_pedido AS (
    INSERT INTO pedidos (cliente_id) 
    VALUES ((p_payload->>'clienteId')::BIGINT)
    RETURNING id
  )
  INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, preco_unitario)
  SELECT (SELECT id FROM novo_pedido), "produtoId", quantidade, "precoUnitario"
  FROM jsonb_to_recordset(p_payload->'itens') 
  AS x("produtoId" BIGINT, quantidade INT, "precoUnitario" NUMERIC);
$$;