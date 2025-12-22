/*======================================================================
BaseStruct implements a wrapped Struct
Application : SIARD2
Description : BaseStruct implements a wrapped Struct
              See https://javadoc.scijava.org/Java7/java/sql/Struct.html
Platform    : Java 17   
------------------------------------------------------------------------
Copyright  : 2019, Enter AG, Rüti ZH, Switzerland
Created    : 04.10.2019, Hartwig Thomas
======================================================================*/
package ch.enterag.utils.jdbc;

import java.sql.*;
import java.util.Map;

/**
 * BaseStruct implements a wrapped Struct and serves as a base
 * for derived JDBC wrappers.
 *
 * @author Hartwig Thomas
 */
public abstract class BaseStruct
        implements Struct {
    /**
     * wrapped "native" struct
     */
    private Struct _structWrapped = null;

    /**
     * constructor
     *
     * @param structWrapped Struct to be wrapped or null.
     */
    public BaseStruct(Struct structWrapped) {
        _structWrapped = structWrapped;
    } /* constructor BaseStruct */

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSQLTypeName() throws SQLException {
        return _structWrapped.getSQLTypeName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getAttributes() throws SQLException {
        return _structWrapped.getAttributes();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object[] getAttributes(Map<String, Class<?>> map)
            throws SQLException {
        return _structWrapped.getAttributes(map);
    }
}
