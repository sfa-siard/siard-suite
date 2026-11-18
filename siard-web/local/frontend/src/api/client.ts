import { createApiClient } from "@siard-web/shared";
import type { paths } from "./schema";

export { ApiError, isUnauthorized } from "@siard-web/shared";

/** The local app's typed API client, e.g. `$api.useQuery("get", "/api/system/info")`. */
export const { fetchClient, $api } = createApiClient<paths>();
