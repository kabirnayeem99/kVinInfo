# kVinInfo Agent Guidelines

You are an expert Kotlin Multiplatform (KMP) engineer. Follow these rules strictly for every task.

## Mandatory Workflow

1.  **Research & Consult**:
    *   Read `architecture.md` to understand the system design before any changes.
    *   Use `lean-ctx` tools (`ctx_read`, `ctx_search`, `ctx_overview`) for efficient discovery.
2.  **Strategy**:
    *   Formulate a plan that maintains architectural integrity.
3.  **Execution (Iterative)**:
    *   **Act**: Apply surgical code changes.
    *   **Format**: Run `./gradlew ktlintFormat` after edits. Formatting **must** be perfect.
    *   **Test**: ALWAYS add new tests for new features or bug fixes.
    *   **Verify**: Run `./gradlew ktlintCheck` and `./gradlew :kvininfo:allTests`. Both **must** pass.
4.  **Finalize**:
    *   Update `architecture.md` if the system structure, API, or metrics changed.
    *   Log key decisions in `MEMORY.md` if they are personal/local-specific.

## Technical Standards

*   **Strict Formatting**: We use `ktlint`. Never submit code that fails `./gradlew ktlintCheck`.
*   **Testing**: 100% success rate is the only acceptable state. Add tests to the appropriate `commonTest` file.
*   **Architecture First**: If a change violates the design in `architecture.md`, justify it or change the plan.
*   **Context Efficiency**: Use `LEAN-CTX.md` rules. Minimize tokens by using targeted reads and compressed shell output.

## Tooling Cheat Sheet

*   **Format**: `./gradlew ktlintFormat`
*   **Check Style**: `./gradlew ktlintCheck`
*   **Run Tests**: `./gradlew :kvininfo:allTests` (for KMP) or `./gradlew jvmTest` (faster for JVM-only).
*   **Architecture**: `architecture.md` (read/update)
*   **Guidelines**: `agents.md` (this file)

<!-- lean-ctx -->
## lean-ctx

Prefer lean-ctx MCP tools over native equivalents for token savings:
`ctx_read` > Read/cat, `ctx_search` > Grep/rg, `ctx_shell` > bash, `ctx_tree` > ls/find.
Native Edit/Write/Glob stay as-is; use `ctx_edit` only when Edit needs an unavailable Read.
Full rules: LEAN-CTX.md (open on demand — do not auto-load).
<!-- /lean-ctx -->
