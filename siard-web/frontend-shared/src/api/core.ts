import { createApiClient } from "./client";
import type { paths } from "./core-schema";

/** The typed client of the shared backend API (`backend-core/openapi.yaml`, archive design D10/D11). */
export const { fetchClient: coreFetchClient, $api: coreApi } =
  createApiClient<paths>();
