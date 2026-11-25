-- 阶段1 RBAC：roles / permissions / user_roles / role_permissions + 种子数据

CREATE TABLE IF NOT EXISTS roles (
    id   BIGSERIAL PRIMARY KEY,
    code VARCHAR(64)  NOT NULL UNIQUE,
    name VARCHAR(128) NOT NULL
);

CREATE TABLE IF NOT EXISTS permissions (
    id   BIGSERIAL PRIMARY KEY,
    code VARCHAR(64)  NOT NULL UNIQUE,
    name VARCHAR(128)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL REFERENCES users (id),
    role_id BIGINT NOT NULL REFERENCES roles (id),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id       BIGINT NOT NULL REFERENCES roles (id),
    permission_id BIGINT NOT NULL REFERENCES permissions (id),
    PRIMARY KEY (role_id, permission_id)
);

-- 种子角色
INSERT INTO roles (code, name) VALUES
    ('ADMIN', '管理员'),
    ('ONCALL', '值班工程师'),
    ('VIEWER', '只读观察员')
ON CONFLICT (code) DO NOTHING;

-- 种子权限
INSERT INTO permissions (code, name) VALUES
    ('alert:read', '查看告警'),
    ('alert:ack', '确认告警'),
    ('incident:read', '查看工单'),
    ('incident:write', '操作工单'),
    ('tool:approve', '审批自动化操作')
ON CONFLICT (code) DO NOTHING;

-- 给 ADMIN 角色赋予全部权限
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id FROM roles r, permissions p
WHERE r.code = 'ADMIN'
ON CONFLICT DO NOTHING;
