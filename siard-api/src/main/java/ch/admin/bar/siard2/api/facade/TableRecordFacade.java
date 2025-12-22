package ch.admin.bar.siard2.api.facade;

import ch.admin.bar.siard2.api.Cell;
import ch.admin.bar.siard2.api.TableRecord;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

public class TableRecordFacade {

    private TableRecord tableRecord;

    public TableRecordFacade(TableRecord tableRecord) {
        this.tableRecord = tableRecord;
    }

    public List<Cell> getCells() throws IOException {
        int numberOfCells = tableRecord.getCells();
        return IntStream.range(0, numberOfCells)
                        .mapToObj(i -> {
                            try {
                                return tableRecord.getCell(i);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        })
                        .toList();
    }
}
