<template>
  <router-view />
</template>

<script setup lang="ts">
</script>

<style>
/* ---- Tokens: ink ramp + one signal accent ---- */
:root {
  --ink-deepest: #07080d;
  --ink-page: #0b0d14;
  --ink-card: #11141c;
  --ink-raised: #1a1e28;
  --ink-sunken: #0e1017;
  --ink-text: #eef0f5;
  --ink-text-2: #a3a9b8;
  --ink-text-3: #7a8296;
  --ink-hairline: rgba(238, 240, 245, 0.08);
  --ink-hairline-strong: rgba(238, 240, 245, 0.14);

  --accent: #d99a5b;
  --positive: #4cc38a;
  --negative: #e5484d;
  --warning: #d9a35a;

  --font-ui: 'Inter', 'IBM Plex Sans', system-ui, -apple-system, 'Segoe UI',
    'PingFang SC', 'Microsoft YaHei', sans-serif;
  --font-mono: 'JetBrains Mono', 'IBM Plex Mono', ui-monospace, 'Cascadia Code',
    Consolas, monospace;

  --ease: cubic-bezier(0.22, 1, 0.36, 1);
  --dur-fast: 140ms;
  --dur-mid: 200ms;
}

/* ---- Element Plus → ink ramp ---- */
html.dark {
  --el-bg-color: var(--ink-card);
  --el-bg-color-page: var(--ink-page);
  --el-bg-color-overlay: var(--ink-raised);
  --el-text-color-primary: var(--ink-text);
  --el-text-color-regular: var(--ink-text-2);
  --el-text-color-secondary: var(--ink-text-3);
  --el-text-color-placeholder: var(--ink-text-3);
  --el-text-color-disabled: #5a6070;
  --el-border-color: var(--ink-hairline-strong);
  --el-border-color-light: var(--ink-hairline);
  --el-border-color-lighter: rgba(238, 240, 245, 0.06);
  --el-border-color-extra-light: rgba(238, 240, 245, 0.04);
  --el-fill-color: var(--ink-raised);
  --el-fill-color-light: #171a22;
  --el-fill-color-lighter: #14171f;
  --el-fill-color-extra-light: #12151d;
  --el-fill-color-blank: var(--ink-card);
  --el-color-primary: var(--accent);
  --el-color-primary-light-3: #b9824e;
  --el-color-primary-light-5: #a5764a;
  --el-color-primary-light-7: #7d5c3e;
  --el-color-primary-light-8: #5f4834;
  --el-color-primary-light-9: #2a2119;
  --el-color-primary-dark-2: #e8aa6a;
  --el-box-shadow: 0 8px 24px rgba(0, 0, 0, 0.35);
  --el-box-shadow-light: 0 2px 8px rgba(0, 0, 0, 0.25);
  --el-mask-color: rgba(7, 8, 13, 0.7);
}

/* ---- Base ---- */
html,
body,
#app {
  margin: 0;
  height: 100%;
}

body {
  background: var(--ink-page);
  color: var(--ink-text);
  font-family: var(--font-ui);
  font-size: 14px;
  line-height: 1.5;
  -webkit-font-smoothing: antialiased;
  text-rendering: optimizeLegibility;
}

h1,
h2,
h3,
p {
  margin: 0;
}

/* Primary actions read as a quiet off-white control, not a colored block */
html.dark .el-button--primary {
  --el-button-bg-color: var(--ink-text);
  --el-button-border-color: var(--ink-text);
  --el-button-text-color: var(--ink-page);
  --el-button-hover-bg-color: #ffffff;
  --el-button-hover-border-color: #ffffff;
  --el-button-hover-text-color: var(--ink-page);
  --el-button-active-bg-color: #d7d9de;
  --el-button-active-border-color: #d7d9de;
}

html.dark .el-input__wrapper {
  background-color: var(--ink-sunken);
  box-shadow: 0 0 0 1px var(--ink-hairline-strong) inset;
  border-radius: 8px;
  transition: box-shadow var(--dur-fast) var(--ease);
}

html.dark .el-input__wrapper.is-focus {
  box-shadow: 0 0 0 1px var(--accent) inset;
}

/* ---- Shared app primitives ---- */
.app-page {
  max-width: 1440px;
  margin: 0 auto;
}

.tabular {
  font-variant-numeric: tabular-nums;
}

.mono {
  font-family: var(--font-mono);
}

.muted {
  color: var(--ink-text-3);
}

.page-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--ink-hairline);
  margin-bottom: 24px;
}

.page-title {
  font-size: 28px;
  font-weight: 500;
  line-height: 1.15;
  letter-spacing: -0.01em;
}

.page-sub {
  margin-top: 4px;
  font-size: 13px;
  color: var(--ink-text-3);
}

.page-meta {
  font-family: var(--font-mono);
  font-size: 12px;
  color: var(--ink-text-3);
  padding-bottom: 2px;
}

/* Status dot — the one signature detail, only for live state */
.dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  flex: none;
}

.dot-live {
  background: var(--accent);
}

.dot-negative {
  background: var(--negative);
}

.dot-positive {
  background: var(--positive);
}

.dot-warning {
  background: var(--warning);
}

.dot-muted {
  background: var(--ink-text-3);
}

/* Panels */
.panel {
  background: var(--ink-card);
  border: 1px solid var(--ink-hairline);
  border-radius: 14px;
}

.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 16px 20px;
}

.panel-title {
  font-size: 14px;
  font-weight: 500;
}

.panel-meta {
  font-family: var(--font-mono);
  font-size: 12px;
  color: var(--ink-text-3);
}

.empty-line {
  padding: 40px 20px;
  text-align: center;
  font-size: 13px;
  color: var(--ink-text-3);
}
</style>
