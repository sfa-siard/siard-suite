import Table from "@mui/material/Table";
import TableBody from "@mui/material/TableBody";
import TableCell from "@mui/material/TableCell";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import Typography from "@mui/material/Typography";
import { useParams } from "react-router";
import { useTranslation } from "react-i18next";
import { coreApi } from "../api/core";
import { useReportError } from "./errors";

/** A view's query and columns; views have no rows in a SIARD archive. */
export function ViewView({ archiveId }: { archiveId: string }) {
  const { t } = useTranslation();
  const schemaIndex = Number(useParams().s);
  const viewIndex = Number(useParams().v);
  const view = coreApi.useQuery(
    "get",
    "/api/archives/{archiveId}/schemas/{s}/views/{v}",
    {
      params: { path: { archiveId, s: schemaIndex, v: viewIndex } },
    },
  );
  useReportError(view.error);
  return (
    <>
      <Typography variant="h6" component="h2">
        {view.data?.name}
      </Typography>
      {view.data?.query && (
        <Typography
          component="pre"
          variant="body2"
          sx={{ whiteSpace: "pre-wrap" }}
        >
          {view.data.query}
        </Typography>
      )}
      <Table size="small" aria-label={t("tableContainer.title.columns")}>
        <TableHead>
          <TableRow>
            <TableCell>{t("tableContainer.table.header.columnName")}</TableCell>
            <TableCell>{t("tableContainer.table.header.columnType")}</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {(view.data?.columns ?? []).map((column) => (
            <TableRow key={column.index}>
              <TableCell>{column.name}</TableCell>
              <TableCell>{column.type}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </>
  );
}
