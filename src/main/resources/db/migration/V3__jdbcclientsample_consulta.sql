CREATE OR REPLACE FUNCTION fn_listar_pedidos(p_limit INT, p_offset INT)
RETURNS TABLE (
    id BIGINT,
    cliente_id BIGINT,
    data_pedido TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT p.id, p.cliente_id, p.data_pedido
    FROM pedidos p
    ORDER BY p.id DESC
    LIMIT p_limit OFFSET p_offset;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION fn_contar_pedidos()
RETURNS BIGINT AS $$
DECLARE
    v_total BIGINT;
BEGIN
    SELECT COUNT(*) INTO v_total FROM pedidos;
    RETURN v_total;
END;
$$ LANGUAGE plpgsql;