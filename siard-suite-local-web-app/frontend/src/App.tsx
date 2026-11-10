import AppBar from "@mui/material/AppBar";
import Button from "@mui/material/Button";
import Container from "@mui/material/Container";
import CssBaseline from "@mui/material/CssBaseline";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import { ThemeProvider } from "@mui/material/styles";
import { Link, Outlet, Route, Routes } from "react-router";
import { useTranslation } from "react-i18next";
import { useAgentEvents } from "./api/events";
import { theme } from "./theme";
import AboutView from "./views/AboutView";
import LaunchFailedView from "./views/LaunchFailedView";
import StartView from "./views/StartView";

function Layout({ navigation }: { navigation: boolean }) {
  const { t } = useTranslation();
  return (
    <>
      <AppBar position="static" elevation={0}>
        <Toolbar variant="dense">
          <Typography
            component={navigation ? Link : "span"}
            to="/"
            sx={{ flexGrow: 1, color: "inherit", textDecoration: "none" }}
          >
            SIARD Suite
          </Typography>
          {navigation && (
            <Button color="inherit" component={Link} to="/about">
              {t("menu.item.info")}
            </Button>
          )}
        </Toolbar>
      </AppBar>
      <Container sx={{ py: 3 }}>
        <Outlet />
      </Container>
    </>
  );
}

// application pages: this window counts as connected while one of them is shown
function ConnectedLayout() {
  useAgentEvents();
  return <Layout navigation />;
}

export default function App() {
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Routes>
        <Route element={<ConnectedLayout />}>
          <Route index element={<StartView />} />
          <Route path="about" element={<AboutView />} />
        </Route>
        {/* no session here, so no event stream and no navigation into the app */}
        <Route element={<Layout navigation={false} />}>
          <Route path="launch-failed" element={<LaunchFailedView />} />
        </Route>
      </Routes>
    </ThemeProvider>
  );
}
