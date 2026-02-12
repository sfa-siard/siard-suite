/*======================================================================
A primitive implementation of Row.
Application : Access JDBC driver
Description : Primitive row implementation.
Platform    : Java 7   
------------------------------------------------------------------------
Copyright  : 2016, Enter AG, Rüti ZH, Switzerland
Created    : 07.11.2016, Hartwig Thomas
======================================================================*/
package ch.admin.bar.siard2.access;

import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.RowId;
import com.healthmarketscience.jackcess.complex.ComplexValueForeignKey;
import com.healthmarketscience.jackcess.util.OleBlob;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class ResultSetRow
        extends HashMap<String, Object>
        implements Row {
    /** serial number */
    private static final long serialVersionUID = -6968202854661574845L;
    /** row id */
    private RowId _rowId = null;

    /** constructor creates an empty row.
     */
    public ResultSetRow() {
        super();
        _rowId = new ResultSetRowId();
    }


    /** {@link Row} */
    @Override
    public Object put(String key, Object value) {
        return super.put(key, value);
    }


    /** {@link Row} */
    @Override
    public RowId getId() {
        return _rowId;
    }


    /** {@link Row} */
    @Override
    public String getString(String name) {
        return (String) get(name);
    }


    /** {@link Row} */
    @Override
    public Boolean getBoolean(String name) {
        return (Boolean) get(name);
    }


    /** {@link Row} */
    @Override
    public Byte getByte(String name) {
        return (Byte) get(name);
    }


    /** {@link Row} */
    @Override
    public Short getShort(String name) {
        return (Short) get(name);
    }


    /** {@link Row} */
    @Override
    public Integer getInt(String name) {
        return (Integer) get(name);
    }


    /** {@link Row} */
    @Override
    public BigDecimal getBigDecimal(String name) {
        return (BigDecimal) get(name);
    }


    /** {@link Row} */
    @Override
    public Float getFloat(String name) {
        return (Float) get(name);
    }


    /** {@link Row} */
    @Override
    public Double getDouble(String name) {
        return (Double) get(name);
    }


    /** {@link Row} */
    @Override
    public Date getDate(String name) {
        return (Date) get(name);
    }


    /** {@link Row} */
    @Override
    public byte[] getBytes(String name) {
        return (byte[]) get(name);
    }


    /** {@link Row} */
    @Override
    public ComplexValueForeignKey getForeignKey(String name) {
        return (ComplexValueForeignKey) get(name);
    }


    /** {@link Row} */
    @Override
    public OleBlob getBlob(String name) throws IOException {
        return (OleBlob) get(name);
    }


    /** RowId implementation using UUID */
    private class ResultSetRowId implements RowId {
        /** id */
        UUID _id = null;

        /** constructor */
        ResultSetRowId() {
            _id = UUID.randomUUID();
        }

        /** @return id */
        UUID getId() {
            return _id;
        }

        /** compare this row to another */
        @Override
        public int compareTo(RowId riOther) {
            ResultSetRowId mriOther = (ResultSetRowId) riOther;
            return _id.toString()
                      .compareTo(mriOther.getId()
                                         .toString());
        }
    }

}
