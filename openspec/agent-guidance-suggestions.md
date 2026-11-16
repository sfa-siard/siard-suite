## Harness Suggestions

Suggestion: [fix-postgres-import-memory] in [openspec/config.yaml]
Reason: The specs rule requiring 3 scenario types per requirement (happy path, error/edge case, credential/input-security) exists in config.yaml but was not enforced: all 3 requirements in the postgres-archive-import delta shipped with only 1–2 scenarios and no error/edge or credential scenario, and neither openspec validate nor openspec-verify-change flagged the gap before archive.
