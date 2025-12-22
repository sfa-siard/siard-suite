package ch.admin.bar.siard2.cmd.utils.siard.assertions;

import ch.admin.bar.siard2.cmd.utils.SetDeltas;
import ch.admin.bar.siard2.cmd.utils.siard.model.SiardArchive;
import ch.admin.bar.siard2.cmd.utils.siard.model.content.Content;
import ch.admin.bar.siard2.cmd.utils.siard.utils.ContentExplorer;
import lombok.Builder;
import lombok.NonNull;
import lombok.val;
import org.assertj.core.api.Assertions;

import java.util.HashSet;
import java.util.stream.Collectors;

class ContentAssertions {

    private static final int MAX_DISPLAYED_ROWS = 100;

    @Builder(buildMethodName = "assertEqual")
    public ContentAssertions(
            @NonNull SiardArchive expected,
            @NonNull SiardArchive actual
    ) {
        val expectedContentExplorer = new ContentExplorer(expected);
        val actualContentExplorer = new ContentExplorer(actual);

        val tableIdsDeltas = SetDeltas.findDeltas(expectedContentExplorer.getAllIds(), actualContentExplorer.getAllIds());

        Assertions.assertThat(tableIdsDeltas.deltasAvailable())
                .as("Not the same table ids available: " + tableIdsDeltas)
                .isFalse();

        expectedContentExplorer.getAllIds().forEach(tableId -> {
            val expectedTable = expectedContentExplorer.findTable(tableId);
            val actualTable = actualContentExplorer.findTable(tableId);

            val rowsDeltas = SetDeltas.findDeltas(
                    new HashSet<>(expectedTable.getTableContent().getRows()),
                    new HashSet<>(actualTable.getTableContent().getRows())
            );

            if (rowsDeltas.deltasAvailable()) {
                Assertions.assertThat(rowsDeltas.getJustInA().stream()
                                .map(Content.TableRow::toString)
                                .sorted()
                                .limit(MAX_DISPLAYED_ROWS)
                                .collect(Collectors.toList()))
                        .as("Rows of table %s are not equal (just first %d displayed)", tableId, MAX_DISPLAYED_ROWS)
                        .isEqualTo(rowsDeltas.getJustInB().stream()
                                .map(Content.TableRow::toString)
                                .sorted()
                                .limit(MAX_DISPLAYED_ROWS)
                                .collect(Collectors.toList()));
            }

            Assertions.assertThat(actualTable)
                    .as("The table %s is not equal", tableId)
                    .isEqualTo(expectedTable);
        });
    }
}
