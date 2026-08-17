---
name: code-review
description: Review changes on branch for issues
allowed-tools:
  - read
  - grep
  - glob
  - exec
permissions:
  allow:
    - Exec(git diff)
    - Exec(git log)
---

Run `git diff main...HEAD` and review the changes for quality issues.

Evaluate:
1. **Correctness** — Any logic errors or edge cases?
2. **Security** — Any vulnerabilities introduced?
3. **Performance** — Any obvious inefficiencies?
4. **Style** — Consistent with the codebase?

Provide a summary with specific line references.