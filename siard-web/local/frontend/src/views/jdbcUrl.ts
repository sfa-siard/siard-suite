/**
 * JDBC URL encode/decode of the server-based DBMS, mirroring the backend's `DbmsRegistry`
 * formats (design D1). Display and input help only: the archive run builds its own URL
 * from the saved fields.
 */

export type JdbcUrlParts = {
  host: string;
  port: string;
  dbName: string;
  options: string;
};

/** Prefix and options separator of the `host:port/db` formats. */
const SLASH_FORMATS: Record<string, string> = {
  db2: "jdbc:db2://",
  mysql: "jdbc:mysql://",
  postgresql: "jdbc:postgresql://",
  oracle: "jdbc:oracle:thin:@",
};
const SQLSERVER_PREFIX = "jdbc:sqlserver://";

function escapeRegExp(text: string): string {
  return text.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
}

/** The URL of `values` for a server-based DBMS, or `undefined` for any other id. */
export function encodeJdbcUrl(
  dbmsId: string,
  values: Partial<JdbcUrlParts>,
): string | undefined {
  const { host = "", port = "", dbName = "", options = "" } = values;
  if (dbmsId === "sqlserver") {
    return `${SQLSERVER_PREFIX}${host}:${port};databaseName=${dbName}${options ? `;${options}` : ""}`;
  }
  const prefix = SLASH_FORMATS[dbmsId];
  if (prefix === undefined) {
    return undefined;
  }
  return `${prefix}${host}:${port}/${dbName}${options ? `?${options}` : ""}`;
}

/**
 * The parts of `url` if it is a complete URL of the given DBMS (exact prefix, host,
 * numeric port and database), otherwise `undefined` (design D2).
 */
export function decodeJdbcUrl(
  dbmsId: string,
  url: string,
): JdbcUrlParts | undefined {
  if (dbmsId === "sqlserver") {
    const match = new RegExp(
      `^${escapeRegExp(SQLSERVER_PREFIX)}([^:;/?\\s]+):(\\d+);(.*)$`,
    ).exec(url);
    if (!match) {
      return undefined;
    }
    const parts = match[3].split(";").filter((part) => part !== "");
    const dbName = parts
      .find((part) => part.startsWith("databaseName="))
      ?.substring("databaseName=".length);
    if (!dbName) {
      return undefined;
    }
    return {
      host: match[1],
      port: match[2],
      dbName,
      options: parts
        .filter((part) => !part.startsWith("databaseName="))
        .join(";"),
    };
  }
  const prefix = SLASH_FORMATS[dbmsId];
  if (prefix === undefined) {
    return undefined;
  }
  const match = new RegExp(
    `^${escapeRegExp(prefix)}([^:/?;\\s]+):(\\d+)/([^?]+)(?:\\?(.*))?$`,
  ).exec(url);
  if (!match) {
    return undefined;
  }
  return {
    host: match[1],
    port: match[2],
    dbName: match[3],
    options: match[4] ?? "",
  };
}
