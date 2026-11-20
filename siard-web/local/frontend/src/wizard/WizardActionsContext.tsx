import { createContext, useContext, useEffect } from "react";

export type PrimaryAction = {
  label: string;
  onClick: () => void;
  disabled: boolean;
};

export const WizardActionsContext = createContext<{
  registerPrimaryAction: (action: PrimaryAction | null) => void;
} | null>(null);

export function usePrimaryAction({ label, onClick, disabled }: PrimaryAction) {
  const context = useContext(WizardActionsContext);
  if (!context) {
    throw new Error("usePrimaryAction requires a wizard layout");
  }
  const { registerPrimaryAction } = context;
  useEffect(() => {
    registerPrimaryAction({ label, onClick, disabled });
    return () => registerPrimaryAction(null);
  }, [registerPrimaryAction, label, onClick, disabled]);
}
