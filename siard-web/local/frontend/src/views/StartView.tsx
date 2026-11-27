import { useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Typography from "@mui/material/Typography";
import { visuallyHidden } from "@mui/utils";
import { Trans, useTranslation } from "react-i18next";
import { useNavigate } from "react-router";
import {
  startPageArchive,
  startPageArchiveRed,
  startPageDatabase,
  startPageDatabaseRed,
  startPageExport,
  startPageExportRed,
  startPageArrow,
} from "@siard-web/shared";
import { ApiError, fetchClient, isUnauthorized } from "../api/client";
import { useFileChooser } from "../files/useFileChooser";
import "./StartView.css";

type Action = "archive" | "open" | "upload" | "export";

/** Why the chosen file could not be opened (spec "Archive cannot be opened or is gone"). */
function openFailure(error: unknown) {
  if (isUnauthorized(error)) {
    return { key: "session.missing", severity: "warning" } as const;
  }
  const status = error instanceof ApiError ? error.status : undefined;
  return {
    key:
      status === 422
        ? "inspect.cannotOpen"
        : status === 429
          ? "inspect.tooMany"
          : "inspect.openFailed",
    severity: "error",
  } as const;
}

/** Start page; the upload and export entry points follow with their features. */
export default function StartView() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [hovered, setHovered] = useState<Action | null>(null);
  const [focused, setFocused] = useState<Action | null>(null);
  const active = hovered ?? focused;
  const { choose, chooser } = useFileChooser();
  const [failure, setFailure] = useState<ReturnType<typeof openFailure> | null>(
    null,
  );

  // design D7: the path goes only into the POST body, the route carries the archive id
  const openArchive = async () => {
    setFailure(null);
    const path = await choose({ mode: "open", extensions: [".siard"] });
    if (path === null) {
      return;
    }
    try {
      const { data } = await fetchClient.POST("/api/archive-files", {
        body: { path },
      });
      if (data) {
        void navigate(`/archives/${encodeURIComponent(data.archiveId)}`);
      }
    } catch (error) {
      setFailure(openFailure(error));
    }
  };
  const action = (name: Action) => (
    <Button
      type="button"
      variant="contained"
      className="workflow-button"
      data-action={name}
      onClick={
        name === "archive"
          ? () => navigate("/archive")
          : name === "open"
            ? () => void openArchive()
            : undefined
      }
      onPointerEnter={() => setHovered(name)}
      onPointerLeave={() => setHovered(null)}
      onFocus={() => setFocused(name)}
      onBlur={() => setFocused(null)}
    >
      {t(`startPage.${name}`)}
    </Button>
  );
  const arrow = (name: Exclude<Action, "open">) => (
    <Box className="workflow-arrow" aria-hidden="true">
      <img src={startPageArrow} alt="" width="272" height="36" />
      {active === name && (
        <span className="workflow-dot" data-testid={`${name}-dot`} />
      )}
    </Box>
  );
  return (
    <>
      {failure && (
        <Alert severity={failure.severity} sx={{ mb: 2 }}>
          {t(failure.key)}
        </Alert>
      )}
      {chooser}
      <Box component="section" aria-label="SIARD" className="start-overview">
        <Typography component="h1" sx={visuallyHidden}>
          {t("welcome.text")}
        </Typography>
        <Typography component="p" className="archive-description">
          <Trans
            i18nKey="startPage.archiveDescription"
            components={{ strong: <strong /> }}
          />
        </Typography>
        <Box className="archive-flow">
          <img
            data-testid="source-database"
            src={
              active === "archive" ? startPageDatabaseRed : startPageDatabase
            }
            alt=""
            width="64"
            height="64"
          />
          <Box className="workflow-path">
            {arrow("archive")}
            {action("archive")}
          </Box>
        </Box>
        <Box className="siard-flow">
          <img
            className="siard-illustration"
            data-testid="siard"
            src={active ? startPageArchiveRed : startPageArchive}
            alt=""
            width="171"
            height="209"
          />
          {action("open")}
          <Typography component="p">
            <Trans
              i18nKey="startPage.openDescription"
              components={{ strong: <strong /> }}
            />
          </Typography>
        </Box>
        <Typography component="p" className="destination-description">
          <Trans
            i18nKey="startPage.destinationDescription"
            components={{ strong: <strong /> }}
          />
        </Typography>
        <Box className="destination-flows">
          <Box className="destination-flow" data-testid="upload-flow">
            <Box className="workflow-path">
              {arrow("upload")}
              {action("upload")}
            </Box>
            <img
              data-testid="destination-database"
              src={
                active === "upload" ? startPageDatabaseRed : startPageDatabase
              }
              alt=""
              width="64"
              height="64"
            />
          </Box>
          <Box className="destination-flow" data-testid="export-flow">
            <Box className="workflow-path">
              {arrow("export")}
              {action("export")}
            </Box>
            <img
              data-testid="destination-table"
              src={active === "export" ? startPageExportRed : startPageExport}
              alt=""
              width="65"
              height="81"
            />
          </Box>
        </Box>
      </Box>
    </>
  );
}
