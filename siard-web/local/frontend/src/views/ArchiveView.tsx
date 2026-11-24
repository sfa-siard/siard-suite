import { useState } from "react";
import DeleteIcon from "@mui/icons-material/Delete";
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
import { $api } from "../api/client";
import type { components } from "../api/schema";
import LoadErrorAlert from "./LoadErrorAlert";

type SavedConnection = components["schemas"]["SavedConnection"];
type DbmsCatalogEntry = components["schemas"]["DbmsCatalogEntry"];

function targetSummary(connection: SavedConnection): string {
  const parts = [connection.host, connection.port].filter(Boolean).join(":");
  const target = connection.dbName ? `${parts}/${connection.dbName}` : parts;
  return connection.file ?? target;
}

/** Connection picker: saved profiles above the catalog of server-based DBMS. */
export default function ArchiveView() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const connections = $api.useQuery("get", "/api/connections");
  // the catalog is static for the agent's lifetime
  const dbms = $api.useQuery("get", "/api/dbms", undefined, {
    staleTime: Infinity,
  });
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
          queryClient.invalidateQueries({
            queryKey: ["get", "/api/connections"],
          });
        },
        onError: () => setDeleteFailed(true),
      },
    );
  };

  // read before the isPending checks, which narrow the query results
  const connectionError = connections.error;
  const dbmsError = dbms.error;

  return (
    <Box component="section" sx={{ display: "grid", gap: 3 }}>
      <Typography component="h1" variant="h5">
        {t("archive.title")}
      </Typography>

      <Box component="section" sx={{ display: "grid", gap: 1 }}>
        <Typography component="h2" variant="h6">
          {t("archive.newConnection")}
        </Typography>
        {dbms.isPending ? (
          <LinearProgress />
        ) : dbmsError ? (
          <LoadErrorAlert error={dbmsError} messageKey="archive.loadError" />
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

      <Box component="section" sx={{ display: "grid", gap: 1 }}>
        <Typography component="h2" variant="h6">
          {t("archive.savedConnections")}
        </Typography>
        {connections.isPending ? (
          <LinearProgress />
        ) : connectionError ? (
          <LoadErrorAlert
            error={connectionError}
            messageKey="archive.loadError"
          />
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
                    <DeleteIcon fontSize="small" />
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
