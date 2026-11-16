import Alert from "@mui/material/Alert";
import LinearProgress from "@mui/material/LinearProgress";
import Stack from "@mui/material/Stack";
import Typography from "@mui/material/Typography";
import { useTranslation } from "react-i18next";
import { $api, isUnauthorized } from "../api/client";

/** Version and description, like the desktop app's info dialog. */
export default function AboutView() {
  const { t } = useTranslation();
  const { data, error, isPending } = $api.useQuery("get", "/api/system/info");

  if (isPending) {
    return <LinearProgress />;
  }
  if (error) {
    // 401: the session is gone (agent restarted, or the page was opened without the launch URL)
    return isUnauthorized(error) ? (
      <Alert severity="warning">{t("session.missing")}</Alert>
    ) : (
      <Alert severity="error">{t("about.loadError")}</Alert>
    );
  }
  return (
    <Stack spacing={2}>
      <Typography variant="h5" component="h1">
        {t("info.dialog.title", { arg0: data.appVersion })}
      </Typography>
      <Typography>{t("info.dialog.text")}</Typography>
    </Stack>
  );
}
