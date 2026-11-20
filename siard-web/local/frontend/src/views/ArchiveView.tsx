import { useState } from "react";
import Alert from "@mui/material/Alert";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Card from "@mui/material/Card";
import CardActionArea from "@mui/material/CardActionArea";
import CardContent from "@mui/material/CardContent";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import DialogTitle from "@mui/material/DialogTitle";
import IconButton from "@mui/material/IconButton";
import LinearProgress from "@mui/material/LinearProgress";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemText from "@mui/material/ListItemText";
import Typography from "@mui/material/Typography";
import { useQueryClient } from "@tanstack/react-query";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router";
import { $api, isUnauthorized } from "../api/client";
import type { components } from "../api/schema";

type SavedConnection = components["schemas"]["SavedConnection"];
type DbmsCatalogEntry = components["schemas"]["DbmsCatalogEntry"];

function targetSummary(connection: SavedConnection): string {
  const parts = [connection.host, connection.port].filter(Boolean).join(":");
  const target = connection.dbName ? `${parts}/${connection.dbName}` : parts;
  return connection.file ?? target;
}

function DeleteIcon() {
  return (
    <svg
      width="20"
      height="20"
      viewBox="0 0 24 24"
      fill="currentColor"
      aria-hidden="true"
    >
      <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z" />
    </svg>
  );
}

/** Connection picker: saved profiles above the catalog of server-based DBMS. */
export default function ArchiveView() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const connections = $api.useQuery("get", "/api/connections");
  const dbms = $api.useQuery("get", "/api/dbms");
  const [pendingDelete, setPendingDelete] = useState<SavedConnection | null>(
    null,
  );
  const [deleteFailed, setDeleteFailed] = useState(false);
  const deleteConnection = $api.useMutation("delete", "/api/connections/{id}");

  const confirmDelete = () => {
    if (!pendingDelete?.id) {
      return;
    }
    deleteConnection.mutate(
      { params: { path: { id: String(pendingDelete.id) } } },
      {
        onSuccess: () => {
          setPendingDelete(null);
          setDeleteFailed(false);
          queryClient.invalidateQueries();
        },
        onError: () => setDeleteFailed(true),
      },
    );
  };

  const connectionError = connections.error;
  const dbmsError = dbms.error;

  return (
    <Box component="section" sx={{ display: "grid", gap: 3 }}>
      <Typography component="h1" variant="h5">
        {t("archive.title")}
      </Typography>

      <Box component="section" sx={{ display: "grid", gap: 1 }}>
        <Typography component="h2" variant="h6">
          {t("archive.savedConnections")}
        </Typography>
        {connections.isPending ? (
          <LinearProgress />
        ) : connectionError ? (
          <Alert
            severity={isUnauthorized(connectionError) ? "warning" : "error"}
          >
            {isUnauthorized(connectionError)
              ? t("session.missing")
              : t("archive.loadError")}
          </Alert>
        ) : connections.data?.length === 0 ? (
          <Typography>{t("archive.empty")}</Typography>
        ) : (
          <List disablePadding>
            {connections.data?.map((connection) => (
              <ListItem
                key={connection.id}
                disablePadding
                secondaryAction={
                  <IconButton
                    edge="end"
                    aria-label={t("archive.delete", {
                      name: connection.name,
                    })}
                    onClick={() => {
                      setDeleteFailed(false);
                      setPendingDelete(connection);
                    }}
                  >
                    <DeleteIcon />
                  </IconButton>
                }
              >
                <ListItemButton
                  onClick={() =>
                    navigate(`/archive/connection?connection=${connection.id}`)
                  }
                >
                  <ListItemText
                    primary={connection.name}
                    secondary={targetSummary(connection)}
                  />
                </ListItemButton>
              </ListItem>
            ))}
          </List>
        )}
      </Box>

      <Box component="section" sx={{ display: "grid", gap: 1 }}>
        <Typography component="h2" variant="h6">
          {t("archive.newConnection")}
        </Typography>
        {dbms.isPending ? (
          <LinearProgress />
        ) : dbmsError ? (
          <Alert severity={isUnauthorized(dbmsError) ? "warning" : "error"}>
            {isUnauthorized(dbmsError)
              ? t("session.missing")
              : t("archive.loadError")}
          </Alert>
        ) : (
          <Box
            sx={{
              display: "grid",
              gridTemplateColumns: {
                xs: "minmax(0, 1fr)",
                sm: "repeat(3, minmax(0, 1fr))",
              },
              gap: 2,
            }}
          >
            {dbms.data?.map((entry: DbmsCatalogEntry) => (
              <Card key={entry.id} variant="outlined" sx={{ minWidth: 0 }}>
                <CardActionArea
                  onClick={() =>
                    navigate(`/archive/connection?dbms=${entry.id}`)
                  }
                >
                  <CardContent>
                    <Typography>{entry.name}</Typography>
                  </CardContent>
                </CardActionArea>
              </Card>
            ))}
          </Box>
        )}
      </Box>

      <Dialog
        open={pendingDelete !== null}
        onClose={() => setPendingDelete(null)}
      >
        <DialogTitle>{t("archive.deleteTitle")}</DialogTitle>
        <DialogContent>
          <DialogContentText>
            {t("archive.deleteText", { name: pendingDelete?.name })}
          </DialogContentText>
          {deleteFailed && (
            <Alert severity="error" sx={{ mt: 2 }}>
              {t("archive.deleteError")}
            </Alert>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setPendingDelete(null)}>
            {t("archive.deleteCancel")}
          </Button>
          <Button
            color="error"
            onClick={confirmDelete}
            disabled={deleteConnection.isPending}
          >
            {t("archive.deleteConfirm")}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
