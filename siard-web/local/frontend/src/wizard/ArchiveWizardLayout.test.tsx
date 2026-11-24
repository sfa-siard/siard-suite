import { useMemo, useState } from "react";
import { fireEvent, render, screen, within } from "@testing-library/react";
import { MemoryRouter, Route, Routes, useLocation } from "react-router";
import { i18n } from "@siard-web/shared";
import en from "@siard-web/shared/i18n/en.json";
import de from "@siard-web/shared/i18n/de.json";
import fr from "@siard-web/shared/i18n/fr.json";
import it from "@siard-web/shared/i18n/it.json";
import { renderAt, respondWith } from "../test/render";
import ArchiveWizardLayout from "./ArchiveWizardLayout";
import { expect, test, vi } from "vitest";
import {
  WizardActionsContext,
  usePrimaryAction,
  type PrimaryAction,
} from "./WizardActionsContext";

function DetailsStep() {
  const { pathname, search } = useLocation();
  return (
    <>
      <input aria-label="Host" defaultValue="" />
      <output data-testid="location">{pathname + search}</output>
    </>
  );
}

function renderWizard(path: string) {
  return render(
    <MemoryRouter initialEntries={[path]}>
      <Routes>
        <Route path="/" element={<h1>Start</h1>} />
        <Route path="archive" element={<ArchiveWizardLayout />}>
          <Route index element={<h1>Picker</h1>} />
          <Route path="connection" element={<DetailsStep />} />
        </Route>
      </Routes>
    </MemoryRouter>,
  );
}

test.each([
  ["/archive", 0],
  ["/archive/connection?dbms=postgresql", 1],
] as const)("marks the active and completed steps on %s", (path, active) => {
  renderWizard(path);
  const stepper = screen.getByTestId("archive-stepper");
  const titles = within(stepper).getAllByText(
    /Select connection|Connection details/,
  );
  expect(titles.map((title) => title.textContent)).toEqual([
    en.archiveWizard.selectConnection,
    en.archiveWizard.connectionDetails,
  ]);
  expect(titles[active].closest("[aria-current=step]")).not.toBeNull();
  expect(titles[0].classList.contains("Mui-completed")).toBe(active === 1);
  expect(titles[1]).not.toHaveClass("Mui-completed");
  expect(screen.getByTestId("wizard-actions")).toBeInTheDocument();
});

test("picker has only Back and Cancel, and Back returns through the previous step to start", () => {
  renderWizard("/archive/connection?dbms=postgresql");
  fireEvent.click(screen.getByRole("button", { name: en.archiveWizard.back }));
  expect(screen.getByRole("heading", { name: "Picker" })).toBeInTheDocument();
  expect(
    within(screen.getByTestId("wizard-actions"))
      .getAllByRole("button")
      .map((button) => button.textContent),
  ).toEqual([en.archiveWizard.back, en.archiveWizard.cancel]);
  fireEvent.click(screen.getByRole("button", { name: en.archiveWizard.back }));
  expect(screen.getByRole("heading", { name: "Start" })).toBeInTheDocument();
});

test.each(["/archive", "/archive/connection?dbms=postgresql"])(
  "Cancel confirms leaving %s",
  (path) => {
    renderWizard(path);
    fireEvent.click(
      screen.getByRole("button", { name: en.archiveWizard.cancel }),
    );
    const dialog = screen.getByRole("dialog", {
      name: en.archiveWizard.cancelTitle,
    });
    expect(dialog).toHaveTextContent(en.archiveWizard.cancelText);
    fireEvent.click(
      within(dialog).getByRole("button", {
        name: en.archiveWizard.cancelConfirm,
      }),
    );
    expect(screen.getByRole("heading", { name: "Start" })).toBeInTheDocument();
  },
);

test("dismissing Cancel preserves query parameters and entered values", () => {
  renderWizard("/archive/connection?dbms=postgresql");
  fireEvent.change(screen.getByLabelText("Host"), {
    target: { value: "archive.example.org" },
  });
  fireEvent.click(
    screen.getByRole("button", { name: en.archiveWizard.cancel }),
  );
  fireEvent.click(
    screen.getByRole("button", { name: en.archiveWizard.cancelStay }),
  );
  expect(screen.getByTestId("location")).toHaveTextContent(
    "/archive/connection?dbms=postgresql",
  );
  expect(screen.getByLabelText("Host")).toHaveValue("archive.example.org");
});

test.each([
  ["de", de],
  ["fr", fr],
  ["it", it],
  ["en", en],
] as const)("localizes wizard titles in %s", async (language, messages) => {
  await i18n.changeLanguage(language);
  respondWith(200, []);
  renderAt("/archive");
  const stepper = screen.getByTestId("archive-stepper");
  expect(
    within(stepper).getByText(messages.archiveWizard.selectConnection),
  ).toBeInTheDocument();
  expect(
    within(stepper).getByText(messages.archiveWizard.connectionDetails),
  ).toBeInTheDocument();
  expect(stepper).not.toHaveTextContent("archiveWizard.");
});

test("missing session still shows the indicator, missing-session message and usable Cancel", async () => {
  respondWith(401, { title: "Unauthorized" });
  renderAt("/archive");
  expect(
    (await screen.findAllByText(en.session.missing)).length,
  ).toBeGreaterThan(0);
  const stepper = screen.getByTestId("archive-stepper");
  expect(
    within(stepper).getByText(en.archiveWizard.selectConnection),
  ).toBeInTheDocument();
  expect(
    within(stepper).getByText(en.archiveWizard.connectionDetails),
  ).toBeInTheDocument();
  expect(
    screen.queryByRole("button", { name: en.connections.save }),
  ).not.toBeInTheDocument();
  fireEvent.click(
    screen.getByRole("button", { name: en.archiveWizard.cancel }),
  );
  fireEvent.click(
    screen.getByRole("button", { name: en.archiveWizard.cancelConfirm }),
  );
  expect(screen.getByRole("region", { name: "SIARD" })).toBeInTheDocument();
});

const onClick = vi.fn();

function ActionStep({ disabled }: { disabled: boolean }) {
  usePrimaryAction({ label: "Save", onClick, disabled });
  return null;
}

function ActionHarness({
  show,
  disabled,
}: {
  show: boolean;
  disabled: boolean;
}) {
  const [action, registerPrimaryAction] = useState<PrimaryAction | null>(null);
  const context = useMemo(() => ({ registerPrimaryAction }), []);
  return (
    <WizardActionsContext value={context}>
      {show && <ActionStep disabled={disabled} />}
      {action && (
        <button onClick={action.onClick} disabled={action.disabled}>
          {action.label}
        </button>
      )}
    </WizardActionsContext>
  );
}

test("registers a primary action, updates pending state and clears on unmount", () => {
  const view = render(<ActionHarness show disabled={false} />);
  fireEvent.click(screen.getByRole("button", { name: "Save" }));
  expect(onClick).toHaveBeenCalledOnce();
  view.rerender(<ActionHarness show disabled />);
  expect(screen.getByRole("button", { name: "Save" })).toBeDisabled();
  view.rerender(<ActionHarness show={false} disabled={false} />);
  expect(
    screen.queryByRole("button", { name: "Save" }),
  ).not.toBeInTheDocument();
});
