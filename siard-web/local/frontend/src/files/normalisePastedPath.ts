/**
 * Turns what a user types or pastes into the file chooser's path field into an absolute path, or
 * null when it is empty or not absolute (design D6, spec "Path field"). File managers copy paths
 * in different shapes:
 * - Windows Explorer "Copy as path": `"C:\Users\me\My Data\db.siard"`, with double quotes
 * - macOS Finder (Option+Cmd+C): `/Users/me/My Data/db.siard`
 * - Linux file managers: often a URI, `file:///home/me/My%20Data/db.siard`
 *
 * Nothing here touches the file system; the agent decides whether the path exists.
 */
export function normalisePastedPath(input: string): string | null {
  let text = input.trim();
  if (text.length >= 2 && text.startsWith('"') && text.endsWith('"')) {
    text = text.slice(1, -1).trim();
  }
  if (/^file:/i.test(text)) {
    const path = fileUriToPath(text);
    return path !== null && isAbsolute(path) ? path : null;
  }
  return text !== "" && isAbsolute(text) ? text : null;
}

/** POSIX `/…`, a Windows drive `C:\…` or `C:/…`, or a UNC path `\\server\share…`. */
export function isAbsolute(path: string): boolean {
  return (
    path.startsWith("/") ||
    /^[A-Za-z]:[\\/]/.test(path) ||
    path.startsWith("\\\\")
  );
}

// file:///home/x, file:/home/x, file://localhost/home/x, file:///C:/x, file://server/share/x
function fileUriToPath(uri: string): string | null {
  let rest = uri.replace(/^file:/i, "");
  let host = "";
  if (rest.startsWith("//")) {
    const end = rest.indexOf("/", 2);
    host = end === -1 ? rest.slice(2) : rest.slice(2, end);
    rest = end === -1 ? "" : rest.slice(end);
  }
  let path: string;
  try {
    path = decodeURIComponent(rest);
  } catch {
    return null; // malformed percent-encoding
  }
   // File URIs with a host (file://server/share/…) or a drive (file:///C:/…) are Windows forms:
  // they become UNC or drive paths. On macOS and Linux they hardly occur; if they do, the
  // agent rejects the result as not absolute (400), so the input fails safely.
  if (host !== "" && host.toLowerCase() !== "localhost") {
    return `\\\\${host}${path.replaceAll("/", "\\")}`;
  }
  const drive = /^\/([A-Za-z]:)(\/.*)?$/.exec(path);
  if (drive) {
    return drive[1] + (drive[2] ?? "/").replaceAll("/", "\\");
  }
  return path;
}
