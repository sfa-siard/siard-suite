import { useCallback, useLayoutEffect, useRef, useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Divider from "@mui/material/Divider";
import LinearProgress from "@mui/material/LinearProgress";
import IconButton from "@mui/material/IconButton";
import InputAdornment from "@mui/material/InputAdornment";
import SvgIcon from "@mui/material/SvgIcon";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import { useTranslation } from "react-i18next";
import { Link, useNavigate, useSearchParams } from "react-router";
import { $api, ApiError, isUnauthorized } from "../api/client";
import type { components } from "../api/schema";
import { usePrimaryAction } from "../wizard/WizardActionsContext";

type DbmsCatalogEntry = components["schemas"]["DbmsCatalogEntry"];
type SavedConnection = components["schemas"]["SavedConnection"];

const FIELD_VALUES: Record<
  string,
  "host" | "port" | "dbName" | "user" | "options"
> = {
  host: "host",
  port: "port",
  dbName: "dbName",
  user: "user",
  options: "options",
};

/** A port is a whole number in the TCP range; the backend enforces the same. */
function isValidPort(value: string): boolean {
  return (
    /^\d{1,5}$/.test(value) && Number(value) >= 1 && Number(value) <= 65535
  );
}

/** Paired fields of the desktop-inspired grid; every other descriptor key is additional. */
const PAIRED_KEYS = new Set(["host", "port", "dbName", "user", "password"]);

/**
 * The descriptor-driven form, mounted once the catalog entry (and, for `?connection=`,
 * the saved profile) is known. New connections get editable defaults — `localhost` and
 * the catalog's port example — while a saved profile prefills every field but the
 * password, which is never sent (design D6).
 */
function ConnectionForm({
  entry,
  saved,
}: {
  entry: DbmsCatalogEntry;
  saved: SavedConnection | undefined;
}) {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const create = $api.useMutation("post", "/api/connections");
  const update = $api.useMutation("put", "/api/connections/{id}");
  const isPending = create.isPending || update.isPending;
  // mutate is stable while the mutation result object is new on every render
  const { mutate: createConnection } = create;
  const { mutate: updateConnection } = update;
  const [name, setName] = useState(saved?.name ?? "");
  const [values, setValues] = useState<Record<string, string>>(() => {
    const initial: Record<string, string> = {};
    for (const descriptor of entry.fields ?? []) {
      const key = descriptor.key ?? "";
      const profileKey = FIELD_VALUES[key];
      if (saved) {
        initial[key] = profileKey ? (saved[profileKey] ?? "") : "";
      } else if (key === "host") {
        initial[key] = "localhost";
      } else if (key === "port") {
        initial[key] = entry.examples?.port ?? "";
      } else {
        initial[key] = "";
      }
    }
    return initial;
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [saveFailed, setSaveFailed] = useState(false);
  const [nameError, setNameError] = useState<string | undefined>();
  const [showPassword, setShowPassword] = useState(false);

  const setField = (key: string) => (event: { target: { value: string } }) =>
    setValues((current) => ({ ...current, [key]: event.target.value }));

  const submit = () => {
    const found: Record<string, string> = {};
    for (const descriptor of entry.fields ?? []) {
      const key = descriptor.key ?? "";
      const value = (values[key] ?? "").trim();
      if (descriptor.required && value === "") {
        found[key] = t("connections.required");
      } else if (key === "port" && value !== "" && !isValidPort(value)) {
        found[key] = t("connections.invalidPort");
      }
    }
    setErrors(found);
    setSaveFailed(false);
    setNameError(undefined);
    if (Object.keys(found).length > 0) {
      return;
    }
    const trimmedName = name.trim();
    if (trimmedName === "") {
      // unnamed use is not saved
      navigate("/archive");
      return;
    }
    const body: Record<string, string> = {
      name: trimmedName,
      dbmsId: entry.id ?? "",
    };
    for (const descriptor of entry.fields ?? []) {
      const key = descriptor.key ?? "";
      if (key === "password") {
        continue; // never sent, never persisted
      }
      const value = (values[key] ?? "").trim();
      if (value !== "") {
        body[key] = value;
      }
    }
    const callbacks = {
      onSuccess: () => navigate("/archive"),
      onError: (error: unknown) => {
        if (error instanceof ApiError && error.status === 409) {
          setNameError(t("connections.duplicateName"));
        } else {
          setSaveFailed(true);
        }
      },
    };
    if (saved?.id !== undefined) {
      updateConnection(
        { params: { path: { id: String(saved.id) } }, body },
        callbacks,
      );
    } else {
      createConnection({ body }, callbacks);
    }
  };

  // stable handler, so typing does not re-register the action bar's Save
  const submitRef = useRef(submit);
  useLayoutEffect(() => {
    submitRef.current = submit;
  });
  const submitLatest = useCallback(() => submitRef.current(), []);

  usePrimaryAction({
    label: t("connections.save"),
    onClick: submitLatest,
    disabled: isPending,
  });

  const descriptors = new Map(
    (entry.fields ?? []).map((descriptor) => [
      descriptor.key ?? "",
      descriptor,
    ]),
  );
  const additionalKeys = (entry.fields ?? [])
    .map((descriptor) => descriptor.key ?? "")
    .filter((key) => !PAIRED_KEYS.has(key));

  const renderField = (key: string, spanFullWidth?: boolean) => {
    const descriptor = descriptors.get(key);
    if (!descriptor) {
      return null;
    }
    return (
      <TextField
        key={key}
        label={t(`connections.fields.${key}` as "connections.fields.host")}
        type={
          descriptor.type === "password" && !showPassword ? "password" : "text"
        }
        slotProps={{
          input:
            key === "password"
              ? {
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        type="button"
                        aria-label={t(
                          showPassword
                            ? "connections.hidePassword"
                            : "connections.showPassword",
                        )}
                        onClick={() => setShowPassword((visible) => !visible)}
                        edge="end"
                      >
                        <SvgIcon>
                          <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5C21.27 7.61 17 4.5 12 4.5zm0 12.5a5 5 0 1 1 0-10 5 5 0 0 1 0 10zm0-8a3 3 0 1 0 0 6 3 3 0 0 0 0-6z" />
                          {showPassword && (
                            <path d="m3 2 19 19-1.4 1.4-19-19z" />
                          )}
                        </SvgIcon>
                      </IconButton>
                    </InputAdornment>
                  ),
                }
              : undefined,
        }}
        value={values[key] ?? ""}
        onChange={setField(key)}
        placeholder={entry.examples?.[key]}
        required={descriptor.required}
        error={errors[key] !== undefined}
        helperText={errors[key]}
        autoComplete={key === "password" ? "new-password" : "off"}
        fullWidth
        sx={spanFullWidth ? { gridColumn: "1 / -1" } : undefined}
      />
    );
  };

  return (
    <Box
      component="form"
      noValidate
      onSubmit={(event) => {
        event.preventDefault();
        if (!isPending) submit();
      }}
      sx={{
        display: "grid",
        gap: 2,
        gridTemplateColumns: { xs: "1fr", sm: "1fr 1fr" },
      }}
    >
      <Typography component="h1" variant="h5" sx={{ gridColumn: "1 / -1" }}>
        {saved
          ? `${t("connections.editTitle")} — ${entry.name}`
          : `${t("connections.newTitle")} — ${entry.name}`}
      </Typography>
      {saveFailed && (
        <Alert severity="error" sx={{ gridColumn: "1 / -1" }}>
          {t("connections.saveError")}
        </Alert>
      )}
      <Box
        sx={{
          display: "grid",
          gap: 2,
          gridTemplateColumns: {
            xs: "minmax(0, 1fr)",
            sm: "minmax(0, 1fr) 10ch",
          },
        }}
      >
        {renderField("host")}
        {renderField("port")}
      </Box>
      {renderField("dbName")}
      {renderField("user")}
      {renderField("password")}
      {additionalKeys.map((key) => renderField(key, true))}
      <Divider sx={{ gridColumn: "1 / -1", my: 1 }} />
      <TextField
        label={t("connections.name")}
        value={name}
        onChange={(event) => setName(event.target.value)}
        error={nameError !== undefined}
        helperText={nameError ?? t("connections.nameHint")}
        fullWidth
        sx={{ gridColumn: "1 / -1" }}
      />
      <button type="submit" hidden disabled={isPending} />
    </Box>
  );
}

/**
 * Connection details page for `/archive/connection?dbms=<id>` (new, editable defaults)
 * or `?connection=<id>` (saved profile prefilled, DBMS fixed, password empty).
 */
export default function ConnectionDetailsView() {
  const { t } = useTranslation();
  const [params] = useSearchParams();
  const dbmsParam = params.get("dbms");
  const connectionParam = params.get("connection");

  const dbms = $api.useQuery("get", "/api/dbms");
  const connections = $api.useQuery("get", "/api/connections", undefined, {
    enabled: connectionParam !== null,
  });

  const saved: SavedConnection | undefined = connectionParam
    ? connections.data?.find(
        (connection) => String(connection.id) === connectionParam,
      )
    : undefined;
  const dbmsId = saved?.dbmsId ?? dbmsParam;
  const entry: DbmsCatalogEntry | undefined = dbms.data?.find(
    (candidate) => candidate.id === dbmsId,
  );

  const loading =
    dbms.isPending || (connectionParam !== null && connections.isPending);
  const loadError = dbms.error ?? (connectionParam ? connections.error : null);
  const unknown =
    !loading &&
    !loadError &&
    (dbmsId === null ||
      entry === undefined ||
      (connectionParam !== null &&
        connections.isSuccess &&
        saved === undefined));

  if (loading) {
    return <LinearProgress />;
  }
  if (loadError) {
    return (
      <Alert severity={isUnauthorized(loadError) ? "warning" : "error"}>
        {isUnauthorized(loadError)
          ? t("session.missing")
          : t("connections.loadError")}
      </Alert>
    );
  }
  if (unknown || !entry || (connectionParam !== null && !saved)) {
    return (
      <Box sx={{ display: "grid", gap: 2, justifyItems: "start" }}>
        <Alert severity="error">{t("connections.unknown")}</Alert>
        <Button component={Link} to="/archive">
          {t("connections.backToArchive")}
        </Button>
      </Box>
    );
  }
  // a new key remounts the form, so its state follows a changed ?dbms= or ?connection=
  return (
    <ConnectionForm
      key={`${entry.id}-${saved?.id ?? "new"}`}
      entry={entry}
      saved={saved}
    />
  );
}
