import { useState } from "react";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Typography from "@mui/material/Typography";
import { Trans, useTranslation } from "react-i18next";
import {
  startPageArchive,
  startPageArchiveRed,
  startPageDatabase,
  startPageDatabaseRed,
  startPageExport,
  startPageExportRed,
  startPageArrow,
} from "@siard-web/shared";
import "./StartView.css";

type Action = "archive" | "open" | "upload" | "export";

/** Start page; the archive, display and upload entry points follow with their features. */
export default function StartView() {
  const { t } = useTranslation();
  const [hovered, setHovered] = useState<Action | null>(null);
  const [focused, setFocused] = useState<Action | null>(null);
  const active = hovered ?? focused;
  const action = (name: Action) => (
    <Button
      type="button"
      variant="contained"
      className="workflow-button"
      data-action={name}
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
    <Box component="section" aria-label="SIARD" className="start-overview">
      <Typography component="h1" className="sr-only">
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
          src={active === "archive" ? startPageDatabaseRed : startPageDatabase}
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
            src={active === "upload" ? startPageDatabaseRed : startPageDatabase}
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
  );
}
