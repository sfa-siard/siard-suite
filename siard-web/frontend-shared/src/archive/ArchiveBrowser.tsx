import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import { useCallback, useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Route, Routes } from "react-router";
import { coreApi } from "../api/core";
import {
  type Failure,
  failureOf,
  ReportErrorContext,
  useReportError,
} from "./errors";
import { StructureTree } from "./StructureTree";
import { TableView } from "./TableView";
import { ViewView } from "./ViewView";

export interface ArchiveBrowserProps {
  /** The id the app received when it registered the archive. */
  archiveId: string;
  /** Called when the API answers 401: the host app's session is gone. */
  onUnauthorized: () => void;
}

/**
 * Browse views of one open archive (archive design D11). The app mounts it under its own route,
 * e.g. `archives/:archiveId/*`; the routes below are relative to that mount point, so the position
 * in the archive lives in the URL. The app provides the router and the QueryClientProvider.
 */
export function ArchiveBrowser({
  archiveId,
  onUnauthorized,
}: ArchiveBrowserProps) {
  const [failure, setFailure] = useState<Failure | null>(null);
  // a lost session or a closed archive outranks a plain load error
  const report = useCallback((error: unknown) => {
    const next = failureOf(error);
    setFailure((current) => (current && current !== "load" ? current : next));
  }, []);
  useEffect(() => {
    if (failure === "unauthorized") {
      onUnauthorized();
    }
  }, [failure, onUnauthorized]);

  if (failure) {
    return <FailureMessage failure={failure} />;
  }
  return (
    <ReportErrorContext.Provider value={report}>
      <Browser archiveId={archiveId} />
    </ReportErrorContext.Provider>
  );
}

/** Replaces all content, so no rows of an archive that is gone remain visible. */
function FailureMessage({ failure }: { failure: Failure }) {
  const { t } = useTranslation();
  switch (failure) {
    case "notOpen":
      return <Alert severity="warning">{t("archive.notOpen")}</Alert>;
    case "load":
      return <Alert severity="error">{t("archive.loadError")}</Alert>;
    default:
      // the host app handles the lost session (onUnauthorized)
      return null;
  }
}

function Browser({ archiveId }: { archiveId: string }) {
  const archive = coreApi.useQuery("get", "/api/archives/{archiveId}", {
    params: { path: { archiveId } },
  });
  useReportError(archive.error);
  return (
    <Box sx={{ display: "flex", gap: 3, alignItems: "flex-start" }}>
      <Box component="nav" sx={{ minWidth: 220 }}>
        <Typography variant="h6" component="h2">
          {archive.data?.dbName}
        </Typography>
        <StructureTree
          archiveId={archiveId}
          schemas={archive.data?.schemas ?? []}
        />
      </Box>
      <Box sx={{ flexGrow: 1, minWidth: 0 }}>
        <Routes>
          <Route index element={null} />
          <Route
            path="schemas/:s/tables/:t"
            element={<TableView archiveId={archiveId} />}
          />
          <Route
            path="schemas/:s/views/:v"
            element={<ViewView archiveId={archiveId} />}
          />
        </Routes>
      </Box>
    </Box>
  );
}
