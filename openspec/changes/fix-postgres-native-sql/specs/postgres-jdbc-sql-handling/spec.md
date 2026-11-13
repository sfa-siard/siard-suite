# Spec Delta

## Purpose

Defines how the PostgreSQL JDBC wrapper treats SQL text: caller-supplied statements reach the pgjdbc driver without wrapper-level `nativeSQL` pre-conversion, preserving `?` parameter markers, literal question marks, and JDBC escape syntax for the driver to process.

## ADDED Requirements

### Requirement: Statement SQL is passed to the driver without wrapper conversion

The PostgreSQL JDBC wrapper SHALL hand SQL text for `Statement.executeQuery`, all `Statement.execute` overloads, `Connection.prepareStatement` overloads, and `Connection.prepareCall` overloads to the underlying driver unchanged. It SHALL NOT rewrite `?` characters or expand JDBC escapes before delegation. `Statement.executeUpdate` is exempt because it additionally performs large-object unlinking for DELETE statements.

#### Scenario: Query executes with caller's SQL

- **WHEN** code calls `executeQuery` on a PostgreSQL statement with a SELECT string
- **THEN** the driver executes that exact SQL text and returns the result rows

#### Scenario: Literal question mark survives to the server

- **WHEN** statement SQL contains `?` used as the PostgreSQL jsonb existence operator (e.g. `WHERE jdata ? 'key'`)
- **THEN** the query executes successfully and returns the matching rows instead of failing with a server-side parameter error

#### Scenario: Malformed SQL is rejected by the driver

- **WHEN** statement SQL is syntactically invalid
- **THEN** the `SQLException` raised by the driver propagates to the caller with its SQLState intact

### Requirement: JDBC escape syntax in statement SQL is processed by the driver

The PostgreSQL JDBC wrapper SHALL rely on the driver's own escape processing for `{fn …}`, `{d …}`, `{t …}`, `{ts …}`, `{oj …}`, and `{escape …}` constructs appearing in statement SQL.

#### Scenario: Scalar function escape evaluates

- **WHEN** statement SQL contains `{fn now()}`
- **THEN** the driver expands the escape and the query returns the current timestamp

#### Scenario: Date literal escape evaluates

- **WHEN** statement SQL contains `{d '2001-01-01'}`
- **THEN** the driver expands the escape and the query treats it as the DATE literal `2001-01-01`

#### Scenario: Escape-looking text inside a quoted literal is not processed

- **WHEN** statement SQL contains `{fn` inside a single-quoted string literal
- **THEN** the text is treated as literal string content and the stored/returned value is unchanged

### Requirement: Question-mark parameter markers bind correctly in prepared statements

Each `?` marker in SQL passed to `prepareStatement` SHALL create exactly one bindable parameter addressable by 1-based index, and `??` SHALL produce a single literal `?` in the executed SQL.

#### Scenario: Markers bind by index

- **WHEN** code prepares `INSERT INTO t (a, b) VALUES (?, ?)` and binds both parameters via `setObject`
- **THEN** the inserted row contains exactly the two bound values

#### Scenario: Double question mark yields a literal marker

- **WHEN** prepared SQL contains `??`
- **THEN** the executed statement contains one literal `?` and exposes zero bindable parameters

#### Scenario: Out-of-range parameter index is rejected

- **WHEN** code calls a parameter setter with an index greater than the number of `?` markers
- **THEN** a `SQLException` is thrown and no partial bind occurs
