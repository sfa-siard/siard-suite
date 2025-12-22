/*======================================================================
PrimaryDataFromDb transfers primary data from databases to SIARD files. 
Application : Siard2
Description : Transfers primary data from databases to SIARD files.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2008
Created    : 01.09.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.cmd;

import ch.admin.bar.siard2.api.*;
import ch.enterag.sqlparser.identifier.QualifiedId;
import ch.enterag.utils.StopWatch;
import ch.enterag.utils.background.Progress;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;

import javax.xml.datatype.Duration;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.*;

/**
 * Transfers primary data from databases to SIARD files.
 *
 * @author Hartwig Thomas
 */
@Slf4j
public class PrimaryDataFromDb extends PrimaryDataTransfer {

    private static final long REPORT_RECORDS = 1000;
    private Progress progress = null;
    private long recordsDownloaded = -1;
    private long recordsTotal = -1;
    private long recordsPercent = -1;
    private StopWatch getCellStopWatch = null;
    private StopWatch setValueStopWatch = null;
    private final Tika tika = new Tika();


    private PrimaryDataFromDb(Connection connection, Archive archive) {
        super(connection, archive, null, true, true, true);
    }

    /**
     * Factory method to create an instance of {@link PrimaryDataFromDb}
     *
     * @param connection    database connection.
     * @param archive SIARD archive.
     * @return new instance of PrimaryDataFromDb.
     */
    public static PrimaryDataFromDb newInstance(Connection connection, Archive archive) {
        return new PrimaryDataFromDb(connection, archive);
    }

    /**
     * download primary data.
     *
     * @param progress receives progress notifications and sends cancel
     *                 requests.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    public void download(Progress progress) throws IOException, SQLException {

        LOG.info("Start primary data download to archive {}",
                this._archive.getFile().getAbsoluteFile());

        System.out.println("\r\nPrimary Data");
        this.progress = progress;
        /* determine total number of records in the database */
        recordsTotal = 0;
        for (int iSchema = 0; iSchema < _archive.getSchemas(); iSchema++) {
            Schema schema = _archive.getSchema(iSchema);
            for (int iTable = 0; iTable < schema.getTables(); iTable++) {
                recordsTotal = recordsTotal + schema.getTable(iTable).getMetaTable().getRows();
            }
        }
        recordsPercent = (recordsTotal + 99) / 100;
        recordsDownloaded = 0;
        /* now download */
        for (int iSchema = 0; (iSchema < _archive.getSchemas()) && (!cancelRequested()); iSchema++) {
            getSchema(_archive.getSchema(iSchema));
        }
        if (cancelRequested())
            throw new IOException("\r\nDownload of primary data cancelled!");
        System.out.println("\r\nDownload terminated successfully.");
        _conn.rollback();

        LOG.info("Primary data download finished");
    }

    /**
     * increment the number of records downloaded, issuing a notification,
     * when a percent is reached.
     */
    private void incDownloaded() {
        recordsDownloaded++;
        if ((progress != null) && (recordsTotal > 0) && ((recordsDownloaded % recordsPercent) == 0)) {
            int iPercent = (int) ((100 * recordsDownloaded) / recordsTotal);
            progress.notifyProgress(iPercent);
        }
    }

    /**
     * check if cancel was requested.
     *
     * @return true, if cancel was requested.
     */
    private boolean cancelRequested() {
        if (progress != null && progress.cancelRequested()) {
            LOG.info("Cancel downloading of primary data because of request");
            return true;
        }
        return false;
    }

    private void setValue(Cell cell, Object oValue, MimeTypeHandler mimeTypeHandler) throws IOException, SQLException {
        if (cell.getMetaColumn().getTypeOriginal().equals("\"ROWID\"")) return;
        setValue((Value) cell, oValue, mimeTypeHandler);
    }

    private void setValue(Value value, Object oValue, MimeTypeHandler mimeTypeHandler)
            throws IOException, SQLException {

        if (oValue != null) {
            if (oValue instanceof String)
                value.setString((String) oValue);
            else if (oValue instanceof byte[] bytes) {
                mimeTypeHandler.add(value, bytes);
                mimeTypeHandler.applyMimeType(value);
                value.setBytes(bytes);
            } else if (oValue instanceof Boolean)
                value.setBoolean((Boolean) oValue);
            else if (oValue instanceof Short)
                value.setShort((Short) oValue);
            else if (oValue instanceof Integer)
                value.setInt((Integer) oValue);
            else if (oValue instanceof Long)
                value.setLong((Long) oValue);
            else if (oValue instanceof BigInteger)
                value.setBigInteger((BigInteger) oValue);
            else if (oValue instanceof BigDecimal)
                value.setBigDecimal((BigDecimal) oValue);
            else if (oValue instanceof Float)
                value.setFloat((Float) oValue);
            else if (oValue instanceof Double)
                value.setDouble((Double) oValue);
            else if (oValue instanceof Timestamp)
                value.setTimestamp((Timestamp) oValue);
            else if (oValue instanceof Time)
                value.setTime((Time) oValue);
            else if (oValue instanceof Date)
                value.setDate((Date) oValue);
            else if (oValue instanceof Duration)
                value.setDuration((Duration) oValue);
            else if (oValue instanceof Clob clob) {
                mimeTypeHandler.add(value, clob);
                mimeTypeHandler.applyMimeType(value);
                value.setReader(clob.getCharacterStream());
                clob.free();
            } else if (oValue instanceof SQLXML sqlxml) {
                value.setReader(sqlxml.getCharacterStream());
                sqlxml.free();
            } else if (oValue instanceof Blob blob) {
                mimeTypeHandler.add(value, blob);
                mimeTypeHandler.applyMimeType(value);
                value.setInputStream(blob.getBinaryStream());
                blob.free();
            } else if (oValue instanceof URL url) {
                value.setInputStream(url.openStream(), url.getPath());
            } else if (oValue instanceof Array array) {
                Object[] ao = (Object[]) array.getArray();
                for (int iElement = 0; iElement < ao.length; iElement++) {
                    Value valueElement = value.getElement(iElement);
                    setValue(valueElement, ao[iElement], mimeTypeHandler);
                    mimeTypeHandler.applyMimeType(valueElement);
                }
                array.free();
            } else if (oValue instanceof Struct struct) {
                Object[] ao = struct.getAttributes();
                for (int iAttribute = 0; iAttribute < ao.length; iAttribute++) {
                    setValue(value.getAttribute(iAttribute), ao[iAttribute], mimeTypeHandler);
                }
            } else
                throw new SQLException("Invalid value type " + oValue.getClass().getName() + " encountered!");
        }
    }
    /**
     * extract primary data of a record from the result set.
     *
     * @param rs     result set.
     * @param record record to be filled.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    private void getTableRecord(ResultSet rs, TableRecord record, MimeTypeHandler mimeTypeHandler) throws IOException, SQLException {
        if (rs.getMetaData().getColumnCount() != record.getCells())
            throw new IOException("Invalid number of result columns found!");
        for (int cellIndex = 0; cellIndex < record.getCells(); cellIndex++) {
            Cell cell = getCell(record, cellIndex);
            Object oValue = new ObjectValueReader(rs, cell.getMetaColumn(), cellIndex + 1).read();
            setValue(mimeTypeHandler, cell, oValue);
        }
    }

    private Cell getCell(TableRecord record, int cellIndex) throws IOException {
        getCellStopWatch.start();
        Cell cell = record.getCell(cellIndex);
        getCellStopWatch.stop();
        return cell;
    }

    private void setValue(MimeTypeHandler mimeTypeHandler, Cell cell, Object oValue) throws IOException, SQLException {
        setValue(cell, oValue, mimeTypeHandler);
        setValueStopWatch.stop();
    }

    /**
     * download primary data of a table using a SELECT query for all
     * fields.
     *
     * @param table table.
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    private void getTable(Table table)
            throws IOException, SQLException {
        getCellStopWatch = StopWatch.getInstance();
        setValueStopWatch = StopWatch.getInstance();
        QualifiedId qiTable = new QualifiedId(null,
                table.getParentSchema().getMetaSchema().getName(),
                table.getMetaTable().getName());
        System.out.println("  Table: " + qiTable.format());
        long lRecord = 0;
        TableRecordRetainer rr = table.createTableRecords();
        ResultSet rs = openTable(table, null);
        Statement stmt = rs.getStatement();
        StopWatch swCreate = StopWatch.getInstance();
        StopWatch swGet = StopWatch.getInstance();
        StopWatch swPut = StopWatch.getInstance();
        StopWatch sw = StopWatch.getInstance();
        sw.start();
        long lBytesStart = rr.getByteCount();

        MimeTypeHandler mimeTypeHandler = new MimeTypeHandler(tika);
        while (rs.next() && (!cancelRequested())) {
            TableRecord record = createTableRecord(swCreate, rr);
            readTableRecord(swGet, rs, record, mimeTypeHandler);
            putTableRecord(swPut, rr, record);
            lBytesStart = logTableRecordProgress(lRecord++, sw, rr, lBytesStart);
            incDownloaded();
        }
        
        mimeTypeHandler.applyColumnMimeType(table);
        
        System.out.println("    Record " + lRecord + " (" + sw.formatRate(rr.getByteCount() - lBytesStart,
                sw.stop()) + " kB/s)");
        System.out.println("    Total: " + StopWatch.formatLong(lRecord) + " records (" + StopWatch.formatLong(rr.getByteCount()) + " bytes in " + sw.formatMs() + " ms)");
        if (!rs.isClosed())
            rs.close();
        if (!stmt.isClosed())
            stmt.close();
        rr.close();

        LOG.debug("All data of table '{}.{}' successfully downloaded",
                qiTable.getSchema(),
                qiTable.getName());
    }

    private static long logTableRecordProgress(long lRecord, StopWatch sw, TableRecordRetainer rr, long lBytesStart) {
        if ((lRecord % REPORT_RECORDS) == 0) {
            System.out.println("    Record " + lRecord + " (" + sw.formatRate(rr.getByteCount() - lBytesStart,
                    sw.stop()) + " kB/s)");
            lBytesStart = rr.getByteCount();
            sw.start();
        }
        return lBytesStart;
    }

    private static void putTableRecord(StopWatch swPut, TableRecordRetainer rr, TableRecord record) throws IOException {
        swPut.start();
        rr.put(record);
        swPut.stop();
    }

    private void readTableRecord(StopWatch swGet, ResultSet rs, TableRecord record, MimeTypeHandler mimeTypeHandler) throws IOException, SQLException {
        swGet.start();
        getTableRecord(rs, record, mimeTypeHandler);
        swGet.stop();
    }

    private static TableRecord createTableRecord(StopWatch swCreate, TableRecordRetainer rr) throws IOException {
        swCreate.start();
        TableRecord record = rr.create();
        swCreate.stop();
        return record;
    }

    /**
     * download primary data of a schema.
     *
     * @param schema schema
     * @throws IOException  if an I/O error occurred.
     * @throws SQLException if a database error occurred.
     */
    private void getSchema(Schema schema)
            throws IOException, SQLException {

        for (int iTable = 0; (iTable < schema.getTables()) && (!cancelRequested()); iTable++) {
            Table table = schema.getTable(iTable);
            getTable(table);
        }

        LOG.debug("All data of schema '{}' successfully downloaded", schema.getMetaSchema().getName());
    }

}