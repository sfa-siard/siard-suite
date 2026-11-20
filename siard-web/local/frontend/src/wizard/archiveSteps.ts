export const archiveSteps = [
  { path: "/archive", titleKey: "archiveWizard.selectConnection" },
  { path: "/archive/connection", titleKey: "archiveWizard.connectionDetails" },
] as const;

export function resolveArchiveStep(pathname: string): number {
  return archiveSteps.reduce<number>((active, step, index) => {
    const matches =
      pathname === step.path || pathname.startsWith(`${step.path}/`);
    return matches &&
      (active === -1 || step.path.length > archiveSteps[active].path.length)
      ? index
      : active;
  }, -1);
}
