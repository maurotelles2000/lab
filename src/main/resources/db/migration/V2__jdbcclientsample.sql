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
LANGUAGE plpgsql AS $$
DECLARE
    v_cliente_id BIGINT;
    v_pedido_id BIGINT;
BEGIN
    v_cliente_id := (p_payload->>'clienteId')::BIGINT;
    
    INSERT INTO pedidos (cliente_id) VALUES (v_cliente_id)
    RETURNING id INTO v_pedido_id;

    INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, preco_unitario)
    SELECT 
        v_pedido_id,
        (item->>'produtoId')::BIGINT,
        (item->>'quantidade')::INTEGER,
        (item->>'precoUnitario')::NUMERIC
    FROM jsonb_array_elements(p_payload->'itens') AS item;
END;
$$;


CREATE INDEX IF NOT EXISTS idx_pedidos_cliente_id_id_desc 
ON pedidos (cliente_id, id DESC);
