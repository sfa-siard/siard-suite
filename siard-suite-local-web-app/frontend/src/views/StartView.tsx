import Typography from "@mui/material/Typography";
import { useTranslation } from "react-i18next";

/** Start page; the archive, display and upload entry points follow with their features. */
export default function StartView() {
  const { t } = useTranslation();
  return (
    <Typography variant="h5" component="h1">
      {t("welcome.text")}
    </Typography>
  );
}
