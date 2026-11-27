import { useCallback, useState } from "react";
import FileChooserDialog, {
  type FileChooserOptions,
} from "./FileChooserDialog";

interface Request {
  options: FileChooserOptions;
  resolve: (path: string | null) => void;
}

/**
 * Lets a workflow ask for a path without managing dialog state (design D6):
 * `const { choose, chooser } = useFileChooser()`, render `{chooser}`, then
 * `const path = await choose({ mode: "open", extensions: [".siard"] })` (null when cancelled).
 */
export function useFileChooser() {
  const [request, setRequest] = useState<Request | null>(null);

  const choose = useCallback(
    (options: FileChooserOptions) =>
      new Promise<string | null>((resolve) => setRequest({ options, resolve })),
    [],
  );

  const chooser = request && (
    <FileChooserDialog
      {...request.options}
      onClose={(path) => {
        setRequest(null);
        request.resolve(path);
      }}
    />
  );

  return { choose, chooser };
}
