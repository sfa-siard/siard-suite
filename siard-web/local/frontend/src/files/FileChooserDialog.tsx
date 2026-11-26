import { useState } from "react";
import ArrowUpwardIcon from "@mui/icons-material/ArrowUpward";
import FolderIcon from "@mui/icons-material/Folder";
import FolderOpenIcon from "@mui/icons-material/FolderOpen";
import InsertDriveFileIcon from "@mui/icons-material/InsertDriveFile";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import DialogTitle from "@mui/material/DialogTitle";
import FormControlLabel from "@mui/material/FormControlLabel";
import IconButton from "@mui/material/IconButton";
import LinearProgress from "@mui/material/LinearProgress";
import List from "@mui/material/List";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import MenuItem from "@mui/material/MenuItem";
import Switch from "@mui/material/Switch";
import TextField from "@mui/material/TextField";
import Tooltip from "@mui/material/Tooltip";
import Typography from "@mui/material/Typography";
import useMediaQuery from "@mui/material/useMediaQuery";
import { useTheme } from "@mui/material/styles";
import { useTranslation } from "react-i18next";
import { $api, ApiError, fetchClient } from "../api/client";
import type { components } from "../api/schema";
import { normalisePastedPath } from "./normalisePastedPath";

type FileEntry = components["schemas"]["FileEntry"];
type Place = components["schemas"]["Place"];

export type ChooserMode = "open" | "save" | "folder";

/** Each mode takes only its own options (design D6). Extensions are written with a dot. */
export type FileChooserOptions =
  /** Offers only files with one of `extensions` (e.g. [".siard"]); none means any file. */
  | { mode: "open"; extensions?: string[] }
  /** Saves with exactly `extension` (e.g. ".siard"), the format the workflow writes. */
  | { mode: "save"; extension: string; initialName?: string }
  | { mode: "folder" };

/** A message above the listing; a lost session is a warning, as elsewhere in the app. */
interface Message {
  text: string;
  severity: "warning" | "error";
}

/** The entry the user picked in the listing or through the path field. */
interface Selection {
  path: string;
  name: string;
  folder: boolean;
}

/**
 * The local file chooser (design D6, spec local-web-file-chooser): the agent lists the user's
 * folders, the browser never sees file contents. Returns one absolute path or null.
 */
export default function FileChooserDialog(
  props: FileChooserOptions & { onClose: (path: string | null) => void },
) {
  const { mode, onClose } = props;
  // files listed and accepted: open's list, save's one extension, none in folder mode
  const extensions =
    props.mode === "open"
      ? (props.extensions ?? [])
      : props.mode === "save"
        ? [props.extension]
        : [];
  const initialName = props.mode === "save" ? (props.initialName ?? "") : "";
  const { t } = useTranslation();
  const theme = useTheme();
  const narrow = useMediaQuery(theme.breakpoints.down("sm"));

  const [folder, setFolder] = useState<string | null>(null);
  const [selection, setSelection] = useState<Selection | null>(null);
  const [showHidden, setShowHidden] = useState(false);
  const [pathInput, setPathInput] = useState("");
  const [name, setName] = useState(initialName);
  const [message, setMessage] = useState<Message | null>(null);
  const [replaceTarget, setReplaceTarget] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const places = $api.useQuery("get", "/api/files/places");
  const home = places.data?.find((place) => place.kind === "home")?.path;
  const shown = folder ?? home ?? null;

  const listing = $api.useQuery(
    "get",
    "/api/files",
    {
      params: {
        query: {
          path: shown ?? "",
          extension: mode === "folder" ? undefined : extensions,
          foldersOnly: mode === "folder",
          hidden: showHidden,
        },
      },
    },
    { enabled: shown !== null },
  );

  const goTo = (path: string, select: Selection | null = null) => {
    setFolder(path);
    setSelection(select);
    setMessage(null);
  };

  const matches = (fileName: string) =>
    extensions.length === 0 ||
    extensions.some((extension) =>
      fileName.toLowerCase().endsWith(extension.toLowerCase()),
    );

  const pick = (entry: FileEntry) => {
    setMessage(null);
    setSelection({ path: entry.path, name: entry.name, folder: entry.folder });
    if (mode === "save" && !entry.folder) {
      setName(entry.name);
    }
  };

  const activate = (entry: FileEntry) => {
    if (entry.folder) {
      goTo(entry.path);
    } else if (mode === "open" && matches(entry.name)) {
      onClose(entry.path);
    } else {
      pick(entry);
    }
  };

  const showPath = async () => {
    const path = normalisePastedPath(pathInput);
    if (path === null) {
      fail(t("fileChooser.error.notAbsolute"));
      return;
    }
    const { data, error } = await lookUp(path);
    if (!data) {
      failWith(error, path);
      return;
    }
    if (data.folder) {
      goTo(data.path);
    } else if (mode === "open" && !matches(data.name)) {
      fail(
        t("fileChooser.error.wrongType", { extensions: extensions.join(", ") }),
      );
    } else if (data.parent !== null && data.parent !== undefined) {
      goTo(
        data.parent,
        mode === "folder"
          ? null
          : { path: data.path, name: data.name, folder: false },
      );
      if (mode === "save") {
        setName(data.name);
      }
    }
  };

  const confirm = async () => {
    if (mode === "open") {
      if (selection && !selection.folder) {
        onClose(selection.path);
      }
      return;
    }
    if (shown === null) {
      return;
    }
    if (mode === "folder") {
      onClose(shown);
      return;
    }
    // save: a typed name of an existing folder opens it (as native save dialogs do); otherwise the
    // folder must be writable, the name with the extension must not be a folder, and an existing
    // file needs confirmation
    setBusy(true);
    try {
      const typed = await lookUp(join(shown, name.trim()));
      if (typed.data?.folder) {
        goTo(typed.data.path);
        setName("");
        return;
      }
      const target = join(shown, withExtension(name.trim(), extensions[0]));
      const folderInfo = await lookUp(shown);
      if (!folderInfo.data) {
        failWith(folderInfo.error, shown);
        return;
      }
      if (!folderInfo.data.writable) {
        fail(t("fileChooser.error.notWritable"));
        return;
      }
      const existing = await lookUp(target);
      if (existing.data?.folder) {
        // the user typed "Archives", not the folder "Archives.siard": opening it would surprise
        fail(t("fileChooser.error.folderExists", { name: existing.data.name }));
      } else if (existing.data) {
        setReplaceTarget(existing.data.path);
      } else if (
        existing.error instanceof ApiError &&
        existing.error.status === 404
      ) {
        onClose(target);
      } else {
        failWith(existing.error, target);
      }
    } finally {
      setBusy(false);
    }
  };

  const reveal = async () => {
    const path = selection?.path ?? shown;
    if (path === null) {
      return;
    }
    setMessage(null);
    try {
      await fetchClient.POST("/api/files/reveal", { body: { path } });
    } catch (error) {
      if (isStatus(error, 401)) {
        failWith(error, path);
      } else {
        fail(t("fileChooser.error.revealFailed"));
      }
    }
  };

  const fail = (text: string) => setMessage({ text, severity: "error" });
  const failWith = (error: unknown, path: string) =>
    setMessage({ text: errorText(error, path), severity: severityOf(error) });

  const errorText = (error: unknown, path: string) => {
    if (!(error instanceof ApiError)) {
      return t("fileChooser.error.loadFailed");
    }
    switch (error.status) {
      case 401:
        return t("session.missing");
      case 403:
        return t("fileChooser.error.notReadable", { path });
      case 404:
        return t("fileChooser.error.notFound");
      case 503:
        return t("fileChooser.error.busy");
      case 504:
        return t("fileChooser.error.unreachable");
      default:
        return t("fileChooser.error.loadFailed");
    }
  };

  const loadError = places.error ?? listing.error;
  const entries = listing.data?.entries ?? [];
  const canConfirm =
    !busy &&
    !loadError &&
    (mode === "open"
      ? selection !== null && !selection.folder && matches(selection.name)
      : mode === "save"
        ? shown !== null && name.trim() !== ""
        : shown !== null);
  const title = {
    open: "fileChooser.titleOpen",
    save: "fileChooser.titleSave",
    folder: "fileChooser.titleFolder",
  } as const;
  const confirmLabel = {
    open: "fileChooser.open",
    save: "fileChooser.save",
    folder: "fileChooser.chooseFolder",
  } as const;
  const placeLabel = (place: Place) =>
    place.kind === "root"
      ? place.path
      : t(`fileChooser.place.${place.kind}` as const);

  return (
    <Dialog
      open
      fullWidth
      maxWidth="md"
      fullScreen={narrow}
      onClose={() => onClose(null)}
      aria-labelledby="file-chooser-title"
    >
      <DialogTitle id="file-chooser-title">{t(title[mode])}</DialogTitle>
      <DialogContent dividers>
        <TextField
          fullWidth
          size="small"
          label={t("fileChooser.path")}
          placeholder={t("fileChooser.pathHint")}
          value={pathInput}
          onChange={(event) => setPathInput(event.target.value)}
          onKeyDown={(event) => {
            if (event.key === "Enter") {
              event.preventDefault();
              void showPath();
            }
          }}
          sx={{ mb: 1 }}
        />
        <Box sx={{ display: "flex", alignItems: "center", gap: 1, mb: 1 }}>
          <Tooltip title={t("fileChooser.parent")}>
            <span>
              <IconButton
                aria-label={t("fileChooser.parent")}
                disabled={!listing.data?.parent}
                onClick={() =>
                  listing.data?.parent && goTo(listing.data.parent)
                }
              >
                <ArrowUpwardIcon />
              </IconButton>
            </span>
          </Tooltip>
          <Typography
            sx={{ flexGrow: 1, overflowWrap: "anywhere" }}
            aria-live="polite"
          >
            {listing.data?.path ?? shown ?? ""}
          </Typography>
          <FormControlLabel
            control={
              <Switch
                checked={showHidden}
                onChange={(event) => setShowHidden(event.target.checked)}
              />
            }
            label={t("fileChooser.showHidden")}
          />
          <Button
            startIcon={<FolderOpenIcon />}
            disabled={shown === null}
            onClick={() => void reveal()}
          >
            {t("fileChooser.reveal")}
          </Button>
        </Box>
        {message && (
          <Alert severity={message.severity} sx={{ mb: 1 }}>
            {message.text}
          </Alert>
        )}
        <Box
          sx={{
            display: "flex",
            flexDirection: narrow ? "column" : "row",
            gap: 2,
          }}
        >
          {narrow ? (
            <TextField
              select
              size="small"
              label={t("fileChooser.places")}
              value=""
              onChange={(event) => goTo(event.target.value)}
              slotProps={{ select: { displayEmpty: true } }}
            >
              {(places.data ?? []).map((place) => (
                <MenuItem key={place.path} value={place.path}>
                  {placeLabel(place)}
                </MenuItem>
              ))}
            </TextField>
          ) : (
            <List
              dense
              aria-label={t("fileChooser.places")}
              sx={{ width: 200, flexShrink: 0 }}
            >
              {(places.data ?? []).map((place) => (
                <ListItemButton
                  key={place.path}
                  selected={place.path === shown}
                  onClick={() => goTo(place.path)}
                >
                  <ListItemText
                    primary={placeLabel(place)}
                    slotProps={{ primary: { noWrap: true } }}
                  />
                </ListItemButton>
              ))}
            </List>
          )}
          <Box sx={{ flexGrow: 1, minWidth: 0 }}>
            {(places.isLoading || listing.isFetching) && <LinearProgress />}
            {loadError ? (
              <Alert severity={severityOf(loadError)}>
                {errorText(loadError, shown ?? "")}
              </Alert>
            ) : (
              <>
                {listing.data?.truncated && (
                  <Alert severity="info" sx={{ mb: 1 }}>
                    {t("fileChooser.truncated", {
                      count: listing.data.entries.length,
                    })}
                  </Alert>
                )}
                {listing.data && entries.length === 0 && (
                  <Typography sx={{ p: 1 }}>
                    {t("fileChooser.empty")}
                  </Typography>
                )}
                <EntryHeader />
                <List
                  dense
                  aria-label={listing.data?.path}
                  sx={{ maxHeight: narrow ? "none" : 360, overflow: "auto" }}
                >
                  {entries.map((entry) => (
                    <ListItemButton
                      key={entry.path}
                      selected={entry.path === selection?.path}
                      ref={
                        entry.path === selection?.path
                          ? scrollIntoView
                          : undefined
                      }
                      onClick={() => pick(entry)}
                      onDoubleClick={() => activate(entry)}
                      onKeyDown={(event) => {
                        if (event.key === "Enter") {
                          event.preventDefault();
                          activate(entry);
                        }
                      }}
                    >
                      <ListItemIcon sx={{ minWidth: 36 }}>
                        {entry.folder ? (
                          <FolderIcon />
                        ) : (
                          <InsertDriveFileIcon />
                        )}
                      </ListItemIcon>
                      <EntryColumns entry={entry} />
                    </ListItemButton>
                  ))}
                </List>
              </>
            )}
          </Box>
        </Box>
        {mode === "save" && (
          <TextField
            fullWidth
            size="small"
            label={t("fileChooser.name")}
            value={name}
            onChange={(event) => setName(event.target.value)}
            sx={{ mt: 2 }}
          />
        )}
      </DialogContent>
      <DialogActions>
        {mode === "open" && selection && !selection.folder && (
          // the selection may be off-screen, hidden or beyond the listing cap (spec "Path field")
          <Typography
            noWrap
            title={selection.path}
            aria-live="polite"
            sx={{ flexGrow: 1, minWidth: 0, px: 1 }}
          >
            {t("fileChooser.selected", { name: selection.name })}
          </Typography>
        )}
        <Button onClick={() => onClose(null)}>{t("fileChooser.cancel")}</Button>
        <Button
          variant="contained"
          disabled={!canConfirm}
          onClick={() => void confirm()}
        >
          {t(confirmLabel[mode])}
        </Button>
      </DialogActions>
      {replaceTarget && (
        <Dialog open onClose={() => setReplaceTarget(null)}>
          <DialogTitle>{t("fileChooser.replaceTitle")}</DialogTitle>
          <DialogContent>
            <DialogContentText>
              {t("fileChooser.replaceText", { name: baseName(replaceTarget) })}
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setReplaceTarget(null)}>
              {t("fileChooser.cancel")}
            </Button>
            <Button variant="contained" onClick={() => onClose(replaceTarget)}>
              {t("fileChooser.replaceConfirm")}
            </Button>
          </DialogActions>
        </Dialog>
      )}
    </Dialog>
  );
}

function EntryHeader() {
  const { t } = useTranslation();
  return (
    <Box
      aria-hidden
      sx={{
        display: { xs: "none", sm: "grid" },
        gridTemplateColumns: "1fr 7rem 11rem",
        gap: 1,
        pl: 8,
        pr: 2,
        color: "text.secondary",
        typography: "caption",
      }}
    >
      <span>{t("fileChooser.column.name")}</span>
      <span>{t("fileChooser.column.size")}</span>
      <span>{t("fileChooser.column.modified")}</span>
    </Box>
  );
}

function EntryColumns({ entry }: { entry: FileEntry }) {
  const { t, i18n } = useTranslation();
  return (
    <Box
      sx={{
        display: "grid",
        gridTemplateColumns: { xs: "1fr", sm: "1fr 7rem 11rem" },
        gap: 1,
        width: "100%",
        minWidth: 0,
      }}
    >
      <Typography noWrap>{entry.name}</Typography>
      <Typography
        variant="body2"
        color="text.secondary"
        sx={{ display: { xs: "none", sm: "block" } }}
      >
        {entry.folder
          ? t("fileChooser.folder")
          : formatSize(entry.size ?? 0, i18n.language)}
      </Typography>
      <Typography
        variant="body2"
        color="text.secondary"
        sx={{ display: { xs: "none", sm: "block" } }}
      >
        {new Date(entry.modified).toLocaleString(i18n.language, {
          dateStyle: "short",
          timeStyle: "short",
        })}
      </Typography>
    </Box>
  );
}

async function lookUp(path: string) {
  try {
    const { data } = await fetchClient.GET("/api/files/entry", {
      params: { query: { path } },
    });
    return { data, error: undefined };
  } catch (error) {
    return { data: undefined, error };
  }
}

/** Ref callback of the selected row: React calls it when the row mounts or becomes selected. */
function scrollIntoView(row: HTMLElement | null) {
  row?.scrollIntoView?.({ block: "nearest" });
}

/** A lost session (401) is a warning, as in LoadErrorAlert; other failures are errors. */
function severityOf(error: unknown) {
  return isStatus(error, 401) ? "warning" : "error";
}

function isStatus(error: unknown, status: number) {
  return error instanceof ApiError && error.status === status;
}

/** Appends the save extension unless the name already ends with it, in any letter case. */
export function withExtension(name: string, extension: string) {
  return name.toLowerCase().endsWith(extension.toLowerCase())
    ? name
    : name + extension;
}

/** Joins a folder and a name with the folder's own separator (\ on Windows paths). */
export function join(folder: string, name: string) {
  const separator = folder.includes("\\") && !folder.includes("/") ? "\\" : "/";
  return folder.endsWith(separator) ? folder + name : folder + separator + name;
}

function baseName(path: string) {
  return path.split(/[\\/]/).pop() ?? path;
}

function formatSize(bytes: number, language: string) {
  const units = ["B", "kB", "MB", "GB", "TB"];
  let value = bytes;
  let unit = 0;
  while (value >= 1000 && unit < units.length - 1) {
    value /= 1000;
    unit++;
  }
  const number = new Intl.NumberFormat(language, {
    maximumFractionDigits: unit === 0 ? 0 : 1,
  }).format(value);
  return `${number} ${units[unit]}`;
}
