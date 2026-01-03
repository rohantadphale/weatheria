## 2026-01-03 - Accessible Labels in Embedded React
**Learning:** In "embedded" React apps (no build step, using Babel standalone), accessibility props like `aria-label` are easily missed because there's no ESLint plugin-jsx-a11y running.
**Action:** Always manually check inputs and buttons for accessible labels when working in legacy or simple no-build environments. Use `inputProps` for `TextField` in MUI to pass aria attributes to the underlying input.
