CREATE OR REPLACE FUNCTION fn_contar_pedidos()
RETURNS BIGINT AS $$
DECLARE
    v_total BIGINT;
BEGIN
    SELECT reltuples::bigint INTO v_total
    FROM pg_class
    WHERE relname = 'pedidos';
    
    RETURN GREATEST(v_total, 0);
END;
$$ LANGUAGE plpgsql;