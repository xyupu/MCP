-- 开发库重建脚本：当 Flyway 校验失败（checksum 不匹配）时使用。
-- 在 Navicat 中对 aicops 库执行本脚本，然后重启 auth-service，
-- Flyway 会从 V1__init.sql 开始重新迁移。
-- 警告：会清空 organizations/users/roles/permissions 及 Flyway 元数据！

DROP TABLE IF EXISTS role_permissions CASCADE;
DROP TABLE IF EXISTS user_roles CASCADE;
DROP TABLE IF EXISTS permissions CASCADE;
DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS organizations CASCADE;
DROP TABLE IF EXISTS flyway_schema_history CASCADE;
