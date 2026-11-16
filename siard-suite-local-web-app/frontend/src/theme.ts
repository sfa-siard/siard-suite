import { createTheme } from "@mui/material/styles";
import "./fonts.css";

// colours and font of the desktop app (siard-suite-app root.css), to keep today's look
export const theme = createTheme({
  palette: {
    primary: { main: "#323f4b" },
    text: { primary: "#1e2731", secondary: "#52606d" },
    background: { default: "#f5f7fa" },
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
  },
});
