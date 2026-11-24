import { createContext, useContext, useEffect } from "react";
import { ApiError, isUnauthorized } from "../api/client";

/** Why the browse views cannot show the archive: archive design D11, spec "Archive no longer open". */
export type Failure = "notOpen" | "unauthorized" | "load";

export function failureOf(error: unknown): Failure {
  if (error instanceof ApiError && error.status === 404) {
    return "notOpen";
  }
  if (isUnauthorized(error)) {
    return "unauthorized";
  }
  return "load";
}

/** Reports a failed request to the browser, which then replaces all its content. */
export const ReportErrorContext = createContext<(error: unknown) => void>(
  () => {},
);

/** Reports the error of a query once it is there. */
export function useReportError(error: unknown) {
  const report = useContext(ReportErrorContext);
  useEffect(() => {
    if (error) {
      report(error);
    }
  }, [error, report]);
}
