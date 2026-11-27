import { useCallback, useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Link from "@mui/material/Link";
import { useTranslation } from "react-i18next";
import { Link as RouterLink, useNavigate, useParams } from "react-router";
import { ArchiveBrowser } from "@siard-web/shared";
import { $api, ApiError, fetchClient, isUnauthorized } from "../api/client";

/**
 * The inspect workflow (design D7, spec "Inspect workflow"): browses an archive the start page
 * opened by its path. Only the archive id is in the URL; the file location path never is.
 */
export default function ArchiveInspectView() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { archiveId = "" } = useParams();
  const [sessionLost, setSessionLost] = useState(false);
  const onUnauthorized = useCallback(() => setSessionLost(true), []);
  // same query key as the browser's own archive query, so this sends no extra request
  const archive = $api.useQuery("get", "/api/archives/{archiveId}", {
    params: { path: { archiveId } },
  });

  const close = async () => {
    try {
      await fetchClient.DELETE("/api/archives/{archiveId}", {
        params: { path: { archiveId } },
      });
    } catch {
      // already closed, evicted or the session is gone: leaving is still what the user asked for
    }
    void navigate("/");
  };

  // a thrown ApiError, which the generated error type does not know
  const error: unknown = archive.error;
  if (sessionLost || isUnauthorized(error)) {
    return <Alert severity="warning">{t("session.missing")}</Alert>;
  }
  if (error instanceof ApiError && error.status === 404) {
    return (
      <Alert severity="warning">
        {t("archive.notOpen")}{" "}
        <Link component={RouterLink} to="/">
          {t("inspect.backToStart")}
        </Link>
      </Alert>
    );
  }
  return (
    <Box sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
      <Box sx={{ display: "flex", justifyContent: "flex-end" }}>
        <Button variant="outlined" onClick={() => void close()}>
          {t("inspect.close")}
        </Button>
      </Box>
      <ArchiveBrowser archiveId={archiveId} onUnauthorized={onUnauthorized} />
    </Box>
  );
}
