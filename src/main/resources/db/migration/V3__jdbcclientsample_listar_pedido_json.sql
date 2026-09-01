CREATE OR REPLACE FUNCTION fn_listar_pedidos_keyset(p_ultimo_id BIGINT, p_limit INT)
RETURNS JSONB AS $$
  WITH params AS (
    SELECT LEAST(GREATEST(COALESCE(p_limit, 20), 1), 100) AS lim
  ),
  dados AS (
    SELECT p.id, p.cliente_id, p.data_pedido
    FROM pedidos p, params
    WHERE p.id < COALESCE(p_ultimo_id, 9223372036854775807)
    ORDER BY p.id DESC
    LIMIT (SELECT lim + 1 FROM params)
  ),
  page AS (
    SELECT * FROM dados, params
    ORDER BY id DESC
    LIMIT (SELECT lim FROM params)
  )
  SELECT jsonb_build_object(
    'content', COALESCE(
      (SELECT jsonb_agg(jsonb_build_object('id', id, 'clienteId', cliente_id, 'dataPedido', data_pedido) ORDER BY id DESC) FROM page),
      '[]'::jsonb
    ),
    'nextCursor', (SELECT MIN(id) FROM page),
    'hasNext', (SELECT COUNT(*) FROM dados) > (SELECT lim FROM params),
    'limit', (SELECT lim FROM params)
  )
  FROM params;
$$ LANGUAGE sql STABLE PARALLEL SAFE;