-- 告警服务表结构

CREATE TABLE IF NOT EXISTS alerts (
    id          BIGSERIAL PRIMARY KEY,
    fingerprint VARCHAR(255) NOT NULL,
    alert_name  VARCHAR(255) NOT NULL,
    severity    VARCHAR(32)  NOT NULL,
    status      VARCHAR(32)  NOT NULL,
    source      VARCHAR(128),
    namespace   VARCHAR(128),
    service     VARCHAR(128),
    instance    VARCHAR(255),
    labels      JSONB,
    annotations JSONB,
    starts_at   TIMESTAMPTZ,
    ends_at     TIMESTAMPTZ,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_alerts_status ON alerts (status);
CREATE INDEX IF NOT EXISTS idx_alerts_severity ON alerts (severity);
CREATE INDEX IF NOT EXISTS idx_alerts_fingerprint ON alerts (fingerprint);
