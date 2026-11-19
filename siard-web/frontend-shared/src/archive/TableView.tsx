import Link from "@mui/material/Link";
import Typography from "@mui/material/Typography";
import {
  DataGrid,
  type GridColDef,
  type GridPaginationModel,
} from "@mui/x-data-grid";
import { deDE, enUS, frFR, itIT } from "@mui/x-data-grid/locales";
import { useState } from "react";
import { useParams, useSearchParams } from "react-router";
import { useTranslation } from "react-i18next";
import type { components } from "../api/core-schema";
import { coreApi } from "../api/core";
import { useReportError } from "./errors";
import { type Match, TableSearch } from "./TableSearch";

type CellDto = components["schemas"]["CellDto"];

// the grid's own (built-in) texts (pagination, loading) in the interface language
const gridLocales = { de: deDE, fr: frFR, it: itIT, en: enUS };

function gridLocaleText(language: string) {
  const locale = gridLocales[language as keyof typeof gridLocales] ?? enUS;
  return locale.components.MuiDataGrid.defaultProps.localeText;
}

/** One page size (archive design D12): the Community grid's maximum. */
export const PAGE_SIZE = 100;

type GridRow = { id: number } & Record<string, CellDto | number>;

/** A table's columns and rows, paged on the server; the 1-based page lives in `?page=`. */
export function TableView({ archiveId }: { archiveId: string }) {
  const { s: schemaIndex, t: tableIndex } = useParams();
  // a new table starts without a search match
  return (
    <TableContent
      key={`${schemaIndex}/${tableIndex}`}
      archiveId={archiveId}
      schemaIndex={Number(schemaIndex)}
      tableIndex={Number(tableIndex)}
    />
  );
}

function TableContent({
  archiveId,
  schemaIndex,
  tableIndex,
}: {
  archiveId: string;
  schemaIndex: number;
  tableIndex: number;
}) {
  const { t, i18n } = useTranslation();
  const [searchParams, setSearchParams] = useSearchParams();
  const page = Math.max(1, Number(searchParams.get("page")) || 1);
  const path = { archiveId, s: schemaIndex, t: tableIndex };
  const [match, setMatch] = useState<Match | null>(null);

  const table = coreApi.useQuery(
    "get",
    "/api/archives/{archiveId}/schemas/{s}/tables/{t}",
    {
      params: { path },
    },
  );
  const rows = coreApi.useQuery(
    "get",
    "/api/archives/{archiveId}/schemas/{s}/tables/{t}/rows",
    {
      params: {
        path,
        query: {
          offset: String((page - 1) * PAGE_SIZE),
          limit: String(PAGE_SIZE),
        },
      },
    },
  );
  useReportError(table.error);
  useReportError(rows.error);

  const columns: GridColDef<GridRow>[] = (table.data?.columns ?? []).map(
    (column) => ({
      field: `c${column.index}`,
      headerName: column.name,
      description: column.type,
      minWidth: 120,
      flex: 1,
      sortable: false,
      filterable: false,
      renderCell: ({ value, row }) => (
        <Cell
          cell={value as CellDto | undefined}
          contentUrl={`/api/archives/${encodeURIComponent(archiveId)}/schemas/${schemaIndex}/tables/${tableIndex}/rows/${row.id}/columns/${column.index}/content`}
        />
      ),
    }),
  );
  const gridRows: GridRow[] = (rows.data?.rows ?? []).map((row) => ({
    id: row.row ?? 0,
    ...Object.fromEntries((row.cells ?? []).map((cell, c) => [`c${c}`, cell])),
  }));

  function showPage(target: number) {
    setSearchParams((params) => {
      params.set("page", String(target));
      return params;
    });
  }

  function changePage(model: GridPaginationModel) {
    showPage(model.page + 1);
  }

  function showMatch(found: Match | null) {
    setMatch(found);
    if (found) {
      showPage(Math.floor(found.row / PAGE_SIZE) + 1);
    }
  }

  return (
    <>
      <Typography variant="h6" component="h2">
        {table.data?.name}
      </Typography>
      <Typography variant="body2" sx={{ mb: 1 }}>
        {t("tableContainer.labelNumberOfRows")}:{" "}
        <span data-testid="row-count">{table.data?.rows}</span>
      </Typography>
      <TableSearch path={path} match={match} onMatch={showMatch} />
      <DataGrid
        rows={gridRows}
        columns={columns}
        loading={rows.isLoading}
        paginationMode="server"
        rowCount={table.data?.rows ?? 0}
        paginationModel={{ page: page - 1, pageSize: PAGE_SIZE }}
        onPaginationModelChange={changePage}
        pageSizeOptions={[PAGE_SIZE]}
        localeText={gridLocaleText(i18n.language)}
        disableColumnSorting
        disableColumnFilter
        disableColumnMenu
        disableRowSelectionOnClick
        // the search match is marked as the selected row
        rowSelectionModel={{
          type: "include",
          ids: new Set(match ? [match.row] : []),
        }}
        // jsdom has no layout, so a virtualized grid would render no rows there (design D12)
        disableVirtualization={import.meta.env.MODE === "test"}
      />
    </>
  );
}

function Cell({
  cell,
  contentUrl,
}: {
  cell: CellDto | undefined;
  contentUrl: string;
}) {
  const { t } = useTranslation();
  switch (cell?.type) {
    case "text":
      return <>{cell.truncated ? `${cell.value}…` : cell.value}</>;
    case "lob":
      return (
        <Link href={contentUrl} download aria-label={t("archive.lob.download")}>
          {t("archive.lob.size", { size: cell.length ?? 0 })}
        </Link>
      );
    default:
      return null;
  }
}
