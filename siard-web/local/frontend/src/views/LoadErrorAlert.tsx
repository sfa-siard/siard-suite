import Alert from "@mui/material/Alert";
import { useTranslation } from "react-i18next";
import { isUnauthorized } from "../api/client";

/** A failed load: a lost session is a warning, anything else shows the view's own error text. */
export default function LoadErrorAlert({
  error,
  messageKey,
}: {
  error: unknown;
  messageKey: "archive.loadError" | "connections.loadError";
}) {
  const { t } = useTranslation();
  const unauthorized = isUnauthorized(error);
  return (
    <Alert severity={unauthorized ? "warning" : "error"}>
      {t(unauthorized ? "session.missing" : messageKey)}
    </Alert>
  );
}
