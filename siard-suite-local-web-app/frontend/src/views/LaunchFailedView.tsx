import Alert from "@mui/material/Alert";
import { useTranslation } from "react-i18next";

/**
 * Target of every rejected launch link (expired, used or unknown token). The agent redirects
 * here without the token, and this page loads no application data.
 */
export default function LaunchFailedView() {
  const { t } = useTranslation();
  return <Alert severity="warning">{t("launchFailed.text")}</Alert>;
}
