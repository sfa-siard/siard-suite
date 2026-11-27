import { expect, test } from "vitest";
import { decodeJdbcUrl, encodeJdbcUrl } from "./jdbcUrl";

// Spec requirement "JDBC URL kept in sync with the connection inputs"

const parts = { host: "db.example.org", port: "15432", dbName: "archive" };

test.each([
  ["postgresql", "jdbc:postgresql://db.example.org:15432/archive", "?ssl=true"],
  ["mysql", "jdbc:mysql://db.example.org:15432/archive", "?ssl=true"],
  ["db2", "jdbc:db2://db.example.org:15432/archive", "?ssl=true"],
  ["oracle", "jdbc:oracle:thin:@db.example.org:15432/archive", "?ssl=true"],
  [
    "sqlserver",
    "jdbc:sqlserver://db.example.org:15432;databaseName=archive",
    ";ssl=true",
  ],
])("%s encodes and round-trips", (id, url, optionsSuffix) => {
  expect(encodeJdbcUrl(id, { ...parts, options: "" })).toBe(url);
  expect(decodeJdbcUrl(id, url)).toEqual({ ...parts, options: "" });

  const withOptions = encodeJdbcUrl(id, { ...parts, options: "ssl=true" });
  expect(withOptions).toBe(url + optionsSuffix);
  expect(decodeJdbcUrl(id, withOptions ?? "")).toEqual({
    ...parts,
    options: "ssl=true",
  });
});

test("sqlserver keeps options around databaseName", () => {
  expect(
    decodeJdbcUrl(
      "sqlserver",
      "jdbc:sqlserver://h:1433;encrypt=false;databaseName=db;loginTimeout=5",
    ),
  ).toEqual({
    host: "h",
    port: "1433",
    dbName: "db",
    options: "encrypt=false;loginTimeout=5",
  });
});

test.each([
  ["postgresql", "jdbc:mysql://other:3306/x"],
  ["postgresql", "jdbc:postgresql:db.example.org:5432/x"],
  ["postgresql", "jdbc:postgresql://db.example.org"],
  ["postgresql", "jdbc:postgresql://db.example.org/archive"],
  ["postgresql", "jdbc:postgresql://db.example.org:5432/"],
  ["postgresql", "jdbc:postgresql://:5432/archive"],
  ["postgresql", "postgres://db"],
  ["oracle", "jdbc:oracle:thin:@db.example.org/archive"],
  ["sqlserver", "jdbc:sqlserver://db.example.org:1433"],
  ["sqlserver", "jdbc:sqlserver://db.example.org:1433;encrypt=false"],
  ["sqlserver", "jdbc:sqlserver://db.example.org;databaseName=archive"],
  ["access", "jdbc:access:/tmp/db.mdb"],
])("%s rejects %s", (id, url) => {
  expect(decodeJdbcUrl(id, url)).toBeUndefined();
});

test("no URL for a file-based DBMS", () => {
  expect(encodeJdbcUrl("access", parts)).toBeUndefined();
});
