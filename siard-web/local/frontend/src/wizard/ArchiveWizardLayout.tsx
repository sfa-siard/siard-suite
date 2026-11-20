import { useMemo, useState } from "react";
import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Dialog from "@mui/material/Dialog";
import DialogActions from "@mui/material/DialogActions";
import DialogContent from "@mui/material/DialogContent";
import DialogContentText from "@mui/material/DialogContentText";
import DialogTitle from "@mui/material/DialogTitle";
import Step from "@mui/material/Step";
import StepLabel from "@mui/material/StepLabel";
import Stepper from "@mui/material/Stepper";
import { useTranslation } from "react-i18next";
import { Outlet, useLocation, useNavigate } from "react-router";
import { archiveSteps, resolveArchiveStep } from "./archiveSteps";
import {
  WizardActionsContext,
  type PrimaryAction,
} from "./WizardActionsContext";

export default function ArchiveWizardLayout() {
  const { t } = useTranslation();
  const { pathname } = useLocation();
  const navigate = useNavigate();
  const activeStep = resolveArchiveStep(pathname);
  const [action, registerPrimaryAction] = useState<PrimaryAction | null>(null);
  const context = useMemo(() => ({ registerPrimaryAction }), []);
  const [cancelOpen, setCancelOpen] = useState(false);

  return (
    <WizardActionsContext value={context}>
      <Box
        sx={{
          display: "flex",
          flexDirection: "column",
          flex: 1,
          minWidth: 0,
          gap: 3,
        }}
      >
        <Stepper
          alternativeLabel
          activeStep={activeStep}
          data-testid="archive-stepper"
        >
          {archiveSteps.map((step, index) => (
            <Step
              key={step.path}
              aria-current={index === activeStep ? "step" : undefined}
            >
              <StepLabel>{t(step.titleKey)}</StepLabel>
            </Step>
          ))}
        </Stepper>
        <Box sx={{ minWidth: 0 }}>
          <Outlet />
        </Box>
        <Box
          role="group"
          data-testid="wizard-actions"
          sx={{ mt: "auto", pt: 3, display: "flex", flexWrap: "wrap", gap: 1 }}
        >
          <Button
            onClick={() => navigate(archiveSteps[activeStep - 1]?.path ?? "/")}
          >
            {t("archiveWizard.back")}
          </Button>
          <Button onClick={() => setCancelOpen(true)}>
            {t("archiveWizard.cancel")}
          </Button>
          {action && (
            <Button
              variant="contained"
              onClick={action.onClick}
              disabled={action.disabled}
              sx={{ ml: "auto" }}
            >
              {action.label}
            </Button>
          )}
        </Box>
        <Dialog
          open={cancelOpen}
          onClose={() => setCancelOpen(false)}
          aria-labelledby="archive-cancel-title"
          aria-describedby="archive-cancel-text"
        >
          <DialogTitle id="archive-cancel-title">
            {t("archiveWizard.cancelTitle")}
          </DialogTitle>
          <DialogContent>
            <DialogContentText id="archive-cancel-text">
              {t("archiveWizard.cancelText")}
            </DialogContentText>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setCancelOpen(false)}>
              {t("archiveWizard.cancelStay")}
            </Button>
            <Button onClick={() => navigate("/")}>
              {t("archiveWizard.cancelConfirm")}
            </Button>
          </DialogActions>
        </Dialog>
      </Box>
    </WizardActionsContext>
  );
}
