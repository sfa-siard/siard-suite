import Box from "@mui/material/Box";
import Button from "@mui/material/Button";
import Checkbox from "@mui/material/Checkbox";
import FormControlLabel from "@mui/material/FormControlLabel";
import TextField from "@mui/material/TextField";
import Typography from "@mui/material/Typography";
import { useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { coreFetchClient } from "../api/core";

export interface Match {
  row: number;
  column: number;
}

interface TableSearchProps {
  path: { archiveId: string; s: number; t: number };
  /** The match shown now; "next" searches after it. */
  match: Match | null;
  onMatch: (match: Match | null) => void;
}

/**
 * Search field and "next" (archive design D5): each request scans a bounded number of rows, so a
 * `found: false, complete: false` answer is continued from `nextRow` until a match or the end.
 */
export function TableSearch({ path, match, onMatch }: TableSearchProps) {
  const { t } = useTranslation();
  const [query, setQuery] = useState("");
  const [matchCase, setMatchCase] = useState(false);
  const [searching, setSearching] = useState(false);
  const [noHits, setNoHits] = useState(false);
  // a newer search, or leaving the view, makes a running continuation stale
  const generation = useRef(0);

  async function next() {
    const current = ++generation.current;
    setSearching(true);
    setNoHits(false);
    let afterRow = match?.row ?? -1;
    try {
      for (;;) {
        const { data } = await coreFetchClient.GET(
          "/api/archives/{archiveId}/schemas/{s}/tables/{t}/search",
          {
            params: {
              path,
              query: {
                q: query,
                matchCase: String(matchCase),
                afterRow: String(afterRow),
              },
            },
          },
        );
        if (current !== generation.current || !data) {
          return;
        }
        if (data.found) {
          onMatch({ row: data.row ?? 0, column: data.column ?? 0 });
          return;
        }
        if (data.complete || data.nextRow === undefined) {
          onMatch(null);
          setNoHits(true);
          return;
        }
        afterRow = data.nextRow - 1;
      }
    } finally {
      if (current === generation.current) {
        setSearching(false);
      }
    }
  }

  function changed() {
    generation.current++;
    setSearching(false);
    setNoHits(false);
    onMatch(null);
  }

  return (
    <Box
      component="form"
      sx={{ display: "flex", gap: 1, alignItems: "center", mb: 1 }}
      onSubmit={(event) => {
        event.preventDefault();
        void next();
      }}
    >
      <TextField
        size="small"
        label={t("search.table.dialog.search")}
        value={query}
        slotProps={{ htmlInput: { maxLength: 200 } }}
        onChange={(event) => {
          setQuery(event.target.value);
          changed();
        }}
      />
      <FormControlLabel
        control={
          <Checkbox
            checked={matchCase}
            onChange={(event) => {
              setMatchCase(event.target.checked);
              changed();
            }}
          />
        }
        label={t("archive.search.matchCase")}
      />
      <Button
        type="submit"
        variant="outlined"
        disabled={query === "" || searching}
      >
        {t("button.next")}
      </Button>
      {noHits && (
        <Typography variant="body2">
          {t("search.table.dialog.nodata")}
        </Typography>
      )}
    </Box>
  );
}
