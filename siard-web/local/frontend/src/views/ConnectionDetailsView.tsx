import { useCallback, useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import LinearProgress from "@mui/material/LinearProgress";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import { useTranslation } from "react-i18next";
import { Link, useNavigate, useSearchParams } from "react-router";
import { $api, isUnauthorized } from "../api/client";
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

/**
 * The descriptor-driven form, mounted once the catalog entry (and, for `?connection=`,
 * the saved profile) is known. Catalog examples stay placeholders; a saved profile
 * prefills every field but the password, which is never sent (design D6).
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
  const { mutate, isPending } = $api.useMutation("post", "/api/connections");
  const [name, setName] = useState(saved?.name ?? "");
  const [values, setValues] = useState<Record<string, string>>(() => {
    const initial: Record<string, string> = {};
    for (const descriptor of entry.fields ?? []) {
      const key = descriptor.key ?? "";
      const profileKey = FIELD_VALUES[key];
      initial[key] = saved && profileKey ? (saved[profileKey] ?? "") : "";
    }
    return initial;
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [saveFailed, setSaveFailed] = useState(false);

  const setField = (key: string) => (event: { target: { value: string } }) =>
    setValues((current) => ({ ...current, [key]: event.target.value }));

  const submit = useCallback(() => {
    const found: Record<string, string> = {};
    for (const descriptor of entry.fields ?? []) {
      const key = descriptor.key ?? "";
      const value = (values[key] ?? "").trim();
      if (descriptor.required && value === "") {
        found[key] = t("connections.required");
      } else if (key === "port" && value !== "" && !/^\d+$/.test(value)) {
        found[key] = t("connections.invalidPort");
      }
    }
    setErrors(found);
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
    mutate(
      { body },
      {
        onSuccess: () => navigate("/archive"),
        onError: () => setSaveFailed(true),
      },
    );
  }, [entry, values, name, t, navigate, mutate]);

  usePrimaryAction({
    label: t("connections.save"),
    onClick: submit,
    disabled: isPending,
  });

  return (
    <Box
      component="form"
      noValidate
      onSubmit={(event) => {
        event.preventDefault();
        if (!isPending) submit();
      }}
      sx={{ display: "grid", gap: 2, maxWidth: 480 }}
    >
      <Typography component="h1" variant="h5">
        {saved
          ? `${t("connections.editTitle")} — ${entry.name}`
          : `${t("connections.newTitle")} — ${entry.name}`}
      </Typography>
      {saveFailed && (
        <Alert severity="error">{t("connections.saveError")}</Alert>
      )}
      <TextField
        label={t("connections.name")}
        value={name}
        onChange={(event) => setName(event.target.value)}
        helperText={t("connections.nameHint")}
        fullWidth
      />
      <TextField
        label={t("connections.dbms")}
        value={entry.name ?? ""}
        disabled
        fullWidth
      />
      {(entry.fields ?? []).map((descriptor) => {
        const key = descriptor.key ?? "";
        return (
          <TextField
            key={key}
            label={t(`connections.fields.${key}` as "connections.fields.host")}
            type={descriptor.type === "password" ? "password" : "text"}
            value={values[key] ?? ""}
            onChange={setField(key)}
            placeholder={entry.examples?.[key]}
            required={descriptor.required}
            error={errors[key] !== undefined}
            helperText={errors[key]}
            autoComplete={key === "password" ? "new-password" : "off"}
            fullWidth
          />
        );
      })}
      <button type="submit" hidden disabled={isPending} />
    </Box>
  );
}

/**
 * Connection details page for `/archive/connection?dbms=<id>` (new, placeholders from the
 * catalog) or `?connection=<id>` (saved profile prefilled, DBMS fixed, password empty).
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
  return <ConnectionForm entry={entry} saved={saved} />;
}
