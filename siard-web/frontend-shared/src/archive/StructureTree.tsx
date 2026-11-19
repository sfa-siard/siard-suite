import List from "@mui/material/List";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemText from "@mui/material/ListItemText";
import ListSubheader from "@mui/material/ListSubheader";
import { NavLink } from "react-router";
import { useTranslation } from "react-i18next";
import type { components } from "../api/core-schema";
import { coreApi } from "../api/core";
import { useReportError } from "./errors";

type SchemaRef = components["schemas"]["SchemaRef"];

/** Schemas with their tables and views; the links are relative to the browser's mount point. */
export function StructureTree({
  archiveId,
  schemas,
}: {
  archiveId: string;
  schemas: SchemaRef[];
}) {
  return (
    <List dense>
      {schemas.map((schema) => (
        <SchemaNode key={schema.index} archiveId={archiveId} schema={schema} />
      ))}
    </List>
  );
}

function SchemaNode({
  archiveId,
  schema,
}: {
  archiveId: string;
  schema: SchemaRef;
}) {
  const { t } = useTranslation();
  const schemaIndex = schema.index ?? 0;
  const detail = coreApi.useQuery(
    "get",
    "/api/archives/{archiveId}/schemas/{s}",
    {
      params: { path: { archiveId, s: schemaIndex } },
    },
  );
  useReportError(detail.error);
  return (
    <li>
      <ListSubheader disableSticky>{schema.name}</ListSubheader>
      <List
        dense
        disablePadding
        aria-label={`${schema.name}: ${t("tableContainer.title.tables")}`}
      >
        {(detail.data?.tables ?? []).map((table) => (
          <ListItemButton
            key={table.index}
            component={NavLink}
            to={`schemas/${schemaIndex}/tables/${table.index}`}
          >
            <ListItemText primary={table.name} />
          </ListItemButton>
        ))}
      </List>
      <List
        dense
        disablePadding
        aria-label={`${schema.name}: ${t("tableContainer.title.views")}`}
      >
        {(detail.data?.views ?? []).map((view) => (
          <ListItemButton
            key={view.index}
            component={NavLink}
            to={`schemas/${schemaIndex}/views/${view.index}`}
          >
            <ListItemText primary={view.name} />
          </ListItemButton>
        ))}
      </List>
    </li>
  );
}
