/*== ValueImpl.java ===================================================
ValueImpl implements the interface Value.
Application : SIARD 2.0
Description : ValueImpl implements the interface Value. 
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 05.07.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.primary;

import ch.admin.bar.siard2.api.*;
import ch.admin.bar.siard2.api.generated.CategoryType;
import ch.enterag.utils.BU;
import ch.enterag.utils.DU;
import ch.enterag.utils.FU;
import ch.enterag.utils.database.SqlTypes;
import ch.enterag.utils.mime.MimeTypes;
import ch.enterag.utils.xml.XU;
import lombok.SneakyThrows;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.datatype.Duration;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Logger;


/**
 * ValueImpl implements the interface Value (common stuff for Cell and
 * Field).
 *
 */
public abstract class ValueImpl
        implements Value {

    Logger logger = Logger.getLogger(ValueImpl.class.getName());

    private static final String _sSEQUENCE_PREFIX = "seq";
    private static final String _sRECORD_PREFIX = "record";
    private static final String _sEXTENSION_TEXT = "txt";
    private static final String _sEXTENSION_XML = "xml";
    private static final String _sEXTENSION_BIN = "bin";
    private static final DU _du = DU.getInstance("en", "yyyy-MM-dd HH:mm:ss.S");

    private URI _uriTemporaryLobFolder = null;

    public URI getTemporaryLobFolder() {
        return _uriTemporaryLobFolder;
    }

    private long _lRecord = -1;

    /**
     * return current row number (0-based).
     *
     * @return current row number.
     */
    protected long getRecord() {
        return _lRecord;
    }

    private int _iIndex = -1;

    /**
     * return index (0-based) of cell or field
     *
     * @return index of cell or field.
     */
    protected int getIndex() {
        return _iIndex;
    }

    Element _elValue = null;

    /**
     * get DOM element representing the value.
     *
     * @return DOM element representing the value.
     */
    private Element getValueElement()
            throws IOException {
        if (_elValue == null) {
            if (this instanceof Cell)
                _elValue = TableRecordImpl.getDocument()
                                          .createElementNS(Archive.sSIARD2_TABLE_NAMESPACE, getColumnTag(getIndex()));
            else {
                ValueImpl viParent = (ValueImpl) ((Field) this).getParent();
                int iCardinalityParent = viParent.getCardinality();
                if (iCardinalityParent >= 0)
                    _elValue = TableRecordImpl.getDocument()
                                              .createElementNS(Archive.sSIARD2_TABLE_NAMESPACE, getElementTag(getIndex()));
                else {
                    MetaType mtParent = viParent.getMetaType();
                    CategoryType catParent = mtParent.getCategoryType();
                    if (catParent == CategoryType.UDT)
                        _elValue = TableRecordImpl.getDocument()
                                                  .createElementNS(Archive.sSIARD2_TABLE_NAMESPACE, getAttributeTag(getIndex()));
                }
                viParent.getValueElement()
                        .appendChild(_elValue);
            }
        }
        return _elValue;
    }

    private MetaValue _mv = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaValue getMetaValue() {
        return _mv;
    }

    /**
     * extend an array to new size.
     *
     * @param iSize        new size.
     * @throws IOException if an I/O error occurred.
     */
    protected void extendArray(int iSize)
            throws IOException {
        /* extend field map of array with NULL fields */
        for (int iField = 0; iField < iSize; iField++) {
            String sTag = getElementTag(iField);
            Field field = getFieldMap().get(sTag);
            if (field == null) {
                MetaField mfNull = getMetaField(iField);
                getFieldMap().put(sTag, createField(iField, mfNull, null));
            }
        }
    }

    /**
     * create a child field of this cell or field.
     *
     * @param iField index (0-based) of child field.
     * @param mf     field meta data of child field.
     * @param el     DOM element for value of child field.
     * @return freshly created field.
     * @throws IOException if an I/O error occurred.
     */
    protected abstract Field createField(int iField, MetaField mf, Element el)
            throws IOException;

    /**
     * return the internal LOB folder relative to this table's LOB folder,
     * or null, if it should be stored externally.
     *
     * @return internal LOB folder relative to this table's LOB folder.
     * @throws IOException if an I/O error occurred.
     */
    protected abstract String getInternalLobFolder()
            throws IOException;

    /**
     *
     * @return return the current cell or the cell ancestor of this field.
     */
    public abstract Cell getAncestorCell();

    /**
     * return the number of fields in this column's or field's meta data.
     *
     * @return number of fields in this column's or field's meta data.
     * @throws IOException of an I/O error occurred.
     */
    private int getMetaFields()
            throws IOException {
        return getMetaValue().getMetaFields();
    }

    /**
     * return the field meta data with the given index in this column's or
     * field's meta data.
     *
     * @param iField index (0-based) of the field.
     * @return field meta data.
     */
    private MetaField getMetaField(int iField)
            throws IOException {
        return getMetaValue().getMetaField(iField);
    }

    /**
     * return the absolute LOB folder for externally storing the LOB of
     * this field.
     *
     * @return absolute LOB folder or null, if the LOB of this column
     * is not to be stored externally.
     */
    private URI getAbsoluteLobFolder() {
        return getMetaValue().getAbsoluteLobFolder();
    }

    /**
     * return the Table implementation to which this cell or field belongs.
     *
     * @return Table implementation to which this cell or field belongs.
     */
    private TableImpl getTableImpl() {
        return (TableImpl) getAncestorCell().getParentRecord()
                                            .getParentTable();
    }

    /**
     * return the Archive implementation
     *
     * @return Archive implementation
     */
    private ArchiveImpl getArchiveImpl() {
        return (ArchiveImpl) getTableImpl().getParentSchema()
                                           .getParentArchive();
    }

    /**
     * return the cardinality (maximum length of array) associated with
     * this cell or field.
     *
     * @return cardinality or -1 if it is not an array.
     */
    private int getCardinality()
            throws IOException {
        return getMetaValue().getCardinality();
    }

    /**
     * return the predefined type of this cell or field as a java.sql.Types
     * integer using this mapping:
     * null                               NULL
     * "CHAR[(<Length>)]"                 CHAR
     * "VARCHAR[(<Length>)]"              VARCHAR
     * "CLOB[(<LOB Length>)]"              CLOB
     * "NCHAR[(<Length>)]"                NCHAR
     * "NVARCHAR[(<Length>)]"             NVARCHAR
     * "NCLOB[(<LOB Length>)]"             NCLOB
     * "XML"                              SQLXML
     * "BINARY[(<Length>)]"               BINARY
     * "VARBINARY[(<Length>)]"            VARBINARY
     * "BLOB[(<LOB Length>)]"             BLOB
     * "BOOLEAN"                          BOOLEAN
     * "SMALLINT"                         SMALLINT
     * "INTEGER"                          INTEGER
     * "BIGINT"                           BIGINT
     * "DECIMAL[(<Precision>[,<Scale>])]" DECIMAL
     * "NUMERIC[(<Precision>[,<Scale>])]" NUMERIC
     * "REAL"                             REAL
     * "FLOAT[(<Precision>)]"             FLOAT
     * "DOUBLE PRECISION"                 DOUBLE
     * "DATE"                             DATE
     * "TIME[(<Scale>)]"                  TIME
     * "TIMESTAMP[(<Scale>)]"             TIMESTAMP
     * "INTERVAL ..."                     OTHER
     *
     * @return predefined type of this cell or field.
     * @throws IOException if an I/O error occurred.
     */
    private int getPreType()
            throws IOException {
        return getMetaValue().getPreType();
    }

    /**
     * return the type meta data associated with this cell or field.
     *
     * @return type meta data or null, if no type meta data is available.
     * @throws IOException if an I/O error occurred.
     */
    private MetaType getMetaType()
            throws IOException {
        return getMetaValue().getMetaType();
    }

    /**
     * return the MIME type of the LOBs in this column or field, or null, if
     * it was not set.
     *
     * @return MIME type of the LOBs of this column or field.
     */
    private String getMimeType() {
        return getMetaValue().getMimeType();
    }

    /**
     * column tag
     *
     * @param iColumn index (0-based) of column.
     * @return column tag.
     */
    public static String getColumnTag(int iColumn) {
        return "c" + (iColumn + 1);
    }

    /**
     * ARRAY element tag
     *
     * @param iIndex index (0-based) of ARRAY element.
     * @return ARRAY element tag.
     */
    public static String getElementTag(int iIndex) {
        return "a" + (iIndex + 1);
    }

    /**
     * UDT attribute tag
     *
     * @param iIndex index (0-based) of UDT attribute.
     * @return UDT attribute tag.
     */
    public static String getAttributeTag(int iIndex) {
        return "u" + (iIndex + 1);
    }

    /**
     * ROW field tag
     *
     * @param iIndex index (0-based) of ROW field.
     * @return ROW field tag.
     */
    public static String getFieldTag(int iIndex) {
        return "r" + (iIndex + 1);
    }

    /**
     * get the index of a cell of field from its tag.
     *
     * @param sTag tag name.
     * @return index (0-based)
     */
    public static int getIndex(String sTag) {
        return Integer.parseInt(sTag.substring(1)) - 1;
    }

    /**
     * get suitable tag for the type and index of this cell's or field's child field.
     *
     * @param mtParent           meta type of parent.
     * @param iCardinalityParent cardinality of parent.
     * @param iField             index if field.
     * @return tag for child field.
     */
    private String getTag(MetaType mtParent, int iCardinalityParent, int iField) {
        if (iCardinalityParent >= 0) return getElementTag(iField);
        CategoryType catParent = mtParent.getCategoryType();
        if (catParent == CategoryType.UDT) return getAttributeTag(iField);
        return getFieldTag(iField);
    }

    /**
     * return the file name for a non-inlined value of this cell or field.
     *
     * @return file name for this cell or field.
     */
    public String getLobFilename()
            throws IOException {
        int iPreType = getPreType();
        String sExtension = _sEXTENSION_TEXT;
        if (iPreType == Types.SQLXML)
            sExtension = _sEXTENSION_XML;
        else if ((iPreType == Types.BINARY) ||
                (iPreType == Types.VARBINARY) ||
                (iPreType == Types.BLOB) ||
                (iPreType == Types.DATALINK)) {
            sExtension = _sEXTENSION_BIN;
            String sMimeType = getMimeType();
            if (sMimeType != null)
                sExtension = MimeTypes.getExtension(sMimeType);
        }
        String sFilename = _sRECORD_PREFIX + getRecord();
        return sFilename + "." + sExtension;
    }

    /* the map of the fields contained in this cell or field */
    private Map<String, Field> _mapFields = null;

    private Map<String, Field> getFieldMap()
            throws IOException {
        if (_mapFields == null) {
            _mapFields = new HashMap<>();
            int iCardinality = getCardinality();
            if (iCardinality < 0) {
                MetaType mt = getMetaType();
                if (mt != null) {
                    /* fill it with all NULL fields */
                    for (int iField = 0; iField < getMetaFields(); iField++) {
                        MetaField mf = getMetaField(iField);
                        String sTag = getTag(mt, iCardinality, iField);
                        _mapFields.put(sTag, createField(iField, mf, null));
                    }
                }
            }
        }
        return _mapFields;
    }

    /**
     * set the DOM element of the value of this cell or field.
     *
     * @param elValue DOM value.
     * @throws IOException if an I/O error occurred.
     */
    private void setValue(Element elValue)
            throws IOException {
        if (elValue != null) {
            int iField = 0;
            /* pick up the fields in the value element */
            for (int iChild = 0; iChild < elValue.getChildNodes()
                                                 .getLength(); iChild++) {
                Node node = elValue.getChildNodes()
                                   .item(iChild);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element elChild = (Element) node;
                    MetaField mf = getMetaField(getIndex(elChild.getLocalName()));
                    String sFieldTag = elChild.getLocalName();
                    Field field = createField(iField, mf, elChild);
                    getFieldMap().put(sFieldTag, field);
                    iField++;
                }
            }
        }
        _elValue = elValue;
    }

    /**
     * get the DOM element for this cell's or field's value.
     *
     * @return DOM value.
     * @throws IOException if an I/O error occurred.
     */
    public Element getValue()
            throws IOException {
        if (getFieldMap() != null) {
            if (getMetaFields() > 0)
                XU.clearElement(getValueElement());

            int iCardinality = getCardinality();
            MetaType mt = getMetaType();
            for (int iField = 0; iField < getMetaFields(); iField++) {
                String sTag = getTag(mt, iCardinality, iField);
                Field field = getFieldMap().get(sTag);
                if ((field != null) && (!field.isNull())) {
                    Element elField = ((FieldImpl) field).getValue();
                    if (elField != null)
                        getValueElement().appendChild(elField);
                }
            }
        }
        return _elValue;
    }

    /**
     * to be called in constructor of CellImpl and FieldImpl.
     *
     * @param lRecord               row number of cell.
     * @param uriTemporaryLobFolder URI of temporary folder for LOBs.
     * @param iIndex                index of value in parent cell or field
     *                              (is needed, because element can be null).
     * @param elValue               DOM element representing the value or null for NULL
     *                              value.
     * @param mv                    meta data (column meta data for cell value, field meta data
     *                              for field value).
     */
    protected void initialize(long lRecord, URI uriTemporaryLobFolder, int iIndex, Element elValue, MetaValue mv)
            throws IOException {
        _lRecord = lRecord;
        _uriTemporaryLobFolder = uriTemporaryLobFolder;
        _iIndex = iIndex;
        _mv = mv;
        setValue(elValue);
        if (elValue != null) {
            int iCardinality = mv.getCardinality();
            if (iCardinality > 0) {
                /* get the last element child */
                Element elChild = null;
                for (Node nodeChild = elValue.getLastChild(); (elChild == null) && (nodeChild != null); nodeChild = nodeChild.getPreviousSibling()) {
                    if (nodeChild.getNodeType() == Node.ELEMENT_NODE)
                        elChild = (Element) nodeChild;
                }
                if (elChild != null) {

                    int iArrayIndex = getIndex(elChild.getTagName());
                    /* create NULL fields for array */
                    extendArray(iArrayIndex + 1);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNull() {
        return _elValue == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getString()
            throws IOException {
        String s = null;
        if (!getValueElement().hasAttribute(ArchiveImpl._sATTR_FILE))
            s = XU.fromXml(getValueElement());
        else {
            Reader rdrClob = getReader();
            if (rdrClob != null) {
                StringWriter swr = new StringWriter();
                char[] cbufTransfer = new char[ArchiveImpl._iBUFFER_SIZE];
                for (int iRead = rdrClob.read(cbufTransfer); iRead != -1; iRead = rdrClob.read(cbufTransfer))
                    swr.write(cbufTransfer, 0, iRead);
                rdrClob.close();
                s = swr.getBuffer()
                       .toString();
            }
        }
        return s;
    } /* getString */

    /**
     * {@inheritDoc}
     */
    @Override
    public void setString(String s)
            throws IOException {
        boolean bShort = (_mv.getMaxLength() <= getArchiveImpl().getMaxInlineSize());
        if (bShort)
            XU.toXml(s, getValueElement());
        else {
            StringReader srdr = new StringReader(s);
            setReader(srdr);
        }
    } /* setString */

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] getBytes()
            throws IOException {
        byte[] buf = null;
        if (!getValueElement().hasAttribute(ArchiveImpl._sATTR_FILE))
            buf = BU.fromHex(getValueElement().getTextContent());
        else {
            InputStream isBlob = getInputStream();
            if (isBlob != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] bufTransfer = new byte[ArchiveImpl._iBUFFER_SIZE];
                for (int iRead = isBlob.read(bufTransfer); iRead != -1; iRead = isBlob.read(bufTransfer))
                    baos.write(bufTransfer, 0, iRead);
                isBlob.close();
                buf = baos.toByteArray();
            }
        }
        return buf;
    } /* getBytes */

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBytes(byte[] buf)
            throws IOException {
        boolean bShort = (_mv.getMaxLength() <= getArchiveImpl().getMaxInlineSize());
        if (bShort)
            getValueElement().appendChild(getValueElement().getOwnerDocument()
                                                           .createTextNode(BU.toHex(buf)));
        else {
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            setInputStream(bais);
        }
    } /* setBytes */

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean getBoolean()
            throws IOException {
        Boolean b = null;
        if (!isNull()) {
            int iPreType = getPreType();
            if (iPreType == Types.BOOLEAN)
                b = Boolean.parseBoolean(getValueElement().getTextContent());
            else if (iPreType != Types.NULL)
                throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be converted to boolean!");
            else
                throw new IllegalArgumentException("Value of cell of complex type cannot be converted to boolean!");
        }
        return b;
    } /* getBoolean */

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBoolean(boolean b)
            throws IOException {
        int iPreType = getPreType();
        if (iPreType == Types.BOOLEAN)
            getValueElement().setTextContent(String.valueOf(b));
        else if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be set to boolean value!");
        else
            throw new IllegalArgumentException("Value of cell of complex type cannot be set to boolean!");
    } /* setBoolean */

    /**
     * {@inheritDoc}
     */
    @Override
    public Short getShort() throws IOException {
        if (isNull()) return null;
        int iPreType = getPreType();
        if (isIntegerType(iPreType)) return Short.parseShort(getValueElement().getTextContent());

        if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be converted to short!");
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to short!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setShort(short sh)
            throws IOException {
        int iPreType = getPreType();
        if (isIntegerType(iPreType))
            getValueElement().setTextContent(String.valueOf(sh));
        else if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be set to short value!");
        else
            throw new IllegalArgumentException("Value of cell of complex type cannot be set to short!");
    } /* setShort */

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getInt() throws IOException {
        if (isNull()) return null;

        int iPreType = getPreType();
        if (isIntegerType(iPreType)) return Integer.parseInt(getValueElement().getTextContent());
        if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be converted to int!");
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to int!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInt(int i)
            throws IOException {
        int iPreType = getPreType();
        if (isIntegerType(iPreType))
            getValueElement().setTextContent(String.valueOf(i));
        else if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be set to int value!");
        else
            throw new IllegalArgumentException("Value of cell of complex type cannot be set to int!");
    } /* setInt */

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getLong() throws IOException {
        if (isNull()) return null;
        int iPreType = getPreType();
        if (isIntegerType(iPreType)) return Long.parseLong(getValueElement().getTextContent());
        if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be converted to int!");
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to int!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLong(long l)
            throws IOException {
        int iPreType = getPreType();
        if (isIntegerType(iPreType))
            getValueElement().setTextContent(String.valueOf(l));
        else if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be set to int value!");
        else
            throw new IllegalArgumentException("Value of cell of complex type cannot be set to int!");
    } /* setLong */

    /**
     * {@inheritDoc}
     */
    @Override
    public BigInteger getBigInteger() throws IOException {
        if (isNull()) return null;
        int iPreType = getPreType();
        if (isIntegerType(iPreType)) return new BigInteger(getValueElement().getTextContent());
        if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be converted to BigInteger!");
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to BigInteger!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBigInteger(BigInteger bi)
            throws IOException {
        int iPreType = getPreType();
        if (isIntegerType(iPreType))
            getValueElement().setTextContent(bi.toString());
        else if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be set to BigInteger value!");
        else
            throw new IllegalArgumentException("Value of cell of complex type cannot be set to BigInteger!");
    } /* setBigInteger */

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getBigDecimal() throws IOException {
        if (isNull()) return null;
        int iPreType = getPreType();
        if (isBigDecimalType(iPreType)) return new BigDecimal(getValueElement().getTextContent());
        if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be converted to BigDecimal!");

        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to BigDecimal!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBigDecimal(BigDecimal bd)
            throws IOException {
        int iPreType = getPreType();
        if (isBigDecimalType(iPreType)) {
            /* avoid scientific notation and confusion with approximate float/double */
            String s = bd.toPlainString();
            try {
                long l = bd.longValueExact(); // TODO: what! set bigdecimal converts to long? this is probably a bug!
                s = String.valueOf(l);
            } catch (ArithmeticException ae) {
                logger.warning("conversion to bigdecimal not possible for " + this);
            }
            getValueElement().setTextContent(s);
        } else if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be set to BigDecimal value!");
        else
            throw new IllegalArgumentException("Value of cell of complex type cannot be set to BigDecimal!");
    } /* setBigDecimal */

    /**
     * {@inheritDoc}
     */
    @Override
    public Float getFloat() throws IOException {
        if (isNull()) return null;
        int iPreType = getPreType();
        if (isNumberType(iPreType)) return Float.parseFloat(getValueElement().getTextContent());
        if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be converted to float!");
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to float!");
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setFloat(float f)
            throws IOException {
        int iPreType = getPreType();
        if (isNumberType(iPreType))
            getValueElement().setTextContent(String.valueOf(f));
        else if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be set to float value!");
        else
            throw new IllegalArgumentException("Value of cell of complex type cannot be set to float!");
    } /* setFloat */

    /**
     * {@inheritDoc}
     */
    @Override
    public Double getDouble() throws IOException {
        if (isNull()) return null;
        int iPreType = getPreType();
        if (isDoubleType(iPreType)) return Double.parseDouble(getValueElement().getTextContent());
        if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be converted to double!");
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to double!");
    } /* getDouble */

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDouble(double d)
            throws IOException {
        int iPreType = getPreType();
        if (isNumberType(iPreType))
            getValueElement().setTextContent(String.valueOf(d));
        else if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be set to double value!");
        else
            throw new IllegalArgumentException("Value of cell of complex type cannot be set to double!");
    } /* setDouble */

    /**
     * {@inheritDoc}
     */
    @Override
    public Date getDate() throws IOException {
        if (isNull()) return null;
        int iPreType = getPreType();
        if (iPreType == Types.DATE) {
            try {
                return _du.fromXsDate(getValueElement().getTextContent());
            } catch (ParseException pe) {
                throw new IllegalArgumentException("Cell value " + getValueElement().getTextContent() + " could not be parsed as xs:date!", pe);
            }
        }
        if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be converted to date!");
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to date!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDate(Date date)
            throws IOException {
        int iPreType = getPreType();
        if (iPreType == Types.DATE)
            getValueElement().setTextContent(_du.toXsDate(date));
        else if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be set to date value!");
        else
            throw new IllegalArgumentException("Value of cell of complex type cannot be set to date!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Time getTime() throws IOException {
        if (isNull()) return null;
        int iPreType = getPreType();
        if (iPreType == Types.TIME) {
            try {
                return _du.fromXsTime(getValueElement().getTextContent());
            } catch (ParseException pe) {
                throw new IllegalArgumentException("Cell value " + getValueElement().getTextContent() + " could not be parsed as xs:time!", pe);
            }
        }
        if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be converted to time!");
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to time!");
    } /* getTime */

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTime(Time time)
            throws IOException {
        int iPreType = getPreType();
        if (iPreType == Types.TIME)
            getValueElement().setTextContent(_du.toXsTime(time));
        else if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be set to time value!");
        else
            throw new IllegalArgumentException("Value of cell of complex type cannot be set to time!");
    } /* setTime */

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp getTimestamp()
            throws IOException {
        if (isNull()) return null;
        int iPreType = getPreType();
        if (iPreType == Types.TIMESTAMP) {
            try {
                return _du.fromXsDateTime(getValueElement().getTextContent());
            } catch (ParseException pe) {
                throw new IllegalArgumentException("Cell value " + getValueElement().getTextContent() + " could not be parsed as xs:dateTime!", pe);
            }
        }
        if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be converted to timestamp!");
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to timestamp!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTimestamp(Timestamp ts)
            throws IOException {
        int iPreType = getPreType();
        if (iPreType == Types.TIMESTAMP)
            getValueElement().setTextContent(_du.toXsDateTime(ts));
        else if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be set to timestamp value!");
        else
            throw new IllegalArgumentException("Value of cell of complex type cannot be set to timestamp!");
    } /* setTimestamp */

    /**
     * {@inheritDoc}
     */
    @Override
    public Duration getDuration() throws IOException {
        if (isNull()) return null;
        int iPreType = getPreType();
        if (iPreType == Types.OTHER) {
            try {
                return _du.fromXsDuration(getValueElement().getTextContent());
            } catch (ParseException pe) {
                throw new IllegalArgumentException("Cell value " + getValueElement().getTextContent() + " could not be parsed as xs:duration!", pe);
            }
        }
        if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be converted to duration!");
        throw new IllegalArgumentException("Value of cell of complex type cannot be converted to duration!");
    } /* getDuration */

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDuration(Duration duration)
            throws IOException {
        int iPreType = getPreType();
        if (iPreType == Types.OTHER)
            getValueElement().setTextContent(_du.toXsDuration(duration));
        else if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be set to duration value!");
        else
            throw new IllegalArgumentException("Value of cell of complex type cannot be set to duration!");
    } /* setDuration */

    /**
     * {@inheritDoc}
     */
    @Override
    public Reader getReader() throws IOException {
        if (isNull()) return null;
        int iPreType = getPreType();
        if (isReaderType(iPreType)) return new ValidatingReader(getValueElement(), getInputStreamFromLobFile());
        if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be read from input stream!");
        throw new IllegalArgumentException("Value of cell of complex type cannot be read from input stream!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getCharLength()
            throws IOException {
        if (isNull()) return -1;

        int iPreType = getPreType();
        if (!isReaderType(iPreType)) return Long.MIN_VALUE;

        String sLength = getValueElement().getAttribute(ArchiveImpl._sATTR_LENGTH);
        if (!sLength.isEmpty()) return Long.parseLong(sLength);
        return getString().length();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReader(Reader reader) throws IOException {
        int iPreType = getPreType();
        if (isReaderType(iPreType)) {

            int iMaxInlineSize = getArchiveImpl().getMaxInlineSize();
            /* try to read iMaxInLineSize+1 characters */
            char[] bufferPrefix = new char[iMaxInlineSize + 1];
            int iOffset = 0;
            int iRead;
            for (iRead = reader.read(bufferPrefix);
                 (iOffset < bufferPrefix.length) && (iRead != -1);
                 iRead = reader.read(bufferPrefix, iOffset, bufferPrefix.length - iOffset)) {
                iOffset = iOffset + iRead;
            }

            String lobFilename = getLobFilename();
            URI externalLobFolder = getAbsoluteLobFolder();

            AbstractMap.SimpleEntry<File, String> lobFileAndLobFilename = getLobFileAndUpdateLobFilename(lobFilename, externalLobFolder);
            File lobFile = lobFileAndLobFilename.getKey();
            lobFilename = lobFileAndLobFilename.getValue();

            OutputStream outputStream = new FileOutputStream(lobFile);
            getValueElement().setAttribute(ArchiveImpl._sATTR_FILE, lobFilename);
            Writer writer = new ValidatingWriter(getValueElement(), outputStream);
            writer.write(bufferPrefix, 0, iOffset);
            if (iRead != -1) {
                char[] bufferTransfer = new char[ArchiveImpl._iBUFFER_SIZE];
                for (iRead = reader.read(bufferTransfer); iRead != -1; iRead = reader.read(bufferTransfer))
                    writer.write(bufferTransfer, 0, iRead);
            }
            writer.close(); // sets LENGTH and DIGEST
            reader.close();
        } else if (iPreType != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(iPreType) + " cannot be set using a reader!");
        else
            throw new IllegalArgumentException("Value of cell of complex type cannot be set using a reader!");
    } /* setReader */


    /**
     * {@inheritDoc}
     */
    @Override
    public String getFilename() throws IOException {
        if (getValueElement().hasAttribute(ArchiveImpl._sATTR_FILE))
            return getValueElement().getAttribute(ArchiveImpl._sATTR_FILE);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getInputStream() throws IOException {
        if (isNull()) return null;
        if (isBinaryType()) return new ValidatingInputStream(getValueElement(), getInputStreamFromLobFile());

        if (getPreType() != Types.NULL)
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(getPreType()) + " cannot be read from input stream!");
        throw new IllegalArgumentException("Value of cell of complex type cannot be read from input stream!");
    }

    private InputStream getInputStreamFromLobFile() throws IOException {
        InputStream inputStream = null;
        String lobFileName;
        if (getValueElement().hasAttribute(ArchiveImpl._sATTR_DLURLPATHONLY)) {
            lobFileName = getValueElement().getAttribute(ArchiveImpl._sATTR_DLURLPATHONLY);
            URL dlURL = new URL(lobFileName);
            inputStream = dlURL.openStream();
        } else if (getValueElement().hasAttribute(ArchiveImpl._sATTR_FILE)) {
            lobFileName = getValueElement().getAttribute(ArchiveImpl._sATTR_FILE);
            URI externalLobFolderUri = getAbsoluteLobFolder();
            if (externalLobFolderUri == null)
                inputStream = getArchiveImpl().openFileEntry(lobFileName);
            else {
                URI uriExternal = externalLobFolderUri.resolve(lobFileName);
                inputStream = new FileInputStream(FU.fromUri(uriExternal));
            }
        }
        return inputStream;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getByteLength()
            throws IOException {
        if (isNull()) return -1;
        int iPreType = getPreType();
        if ((iPreType == Types.BINARY) ||
                (iPreType == Types.VARBINARY) ||
                (iPreType == Types.BLOB) ||
                (iPreType == Types.DATALINK)) {
            String sLength = getValueElement().getAttribute(ArchiveImpl._sATTR_LENGTH);

            if (!sLength.isEmpty()) return Long.parseLong(sLength);
            return getBytes().length;
        }
        return Long.MIN_VALUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setInputStream(InputStream inputStream) throws IOException {
        if (isBinaryType()) {

            int iMaxInlineSize = getArchiveImpl().getMaxInlineSize();
            /* try to read 1 + iMaxInLineSize bytes */
            byte[] bufferPrefix = new byte[1 + iMaxInlineSize];
            int iOffset = 0;
            int iRead;
            for (iRead = inputStream.read(bufferPrefix);
                 (iOffset < bufferPrefix.length) && (iRead != -1);
                 iRead = inputStream.read(bufferPrefix, iOffset, bufferPrefix.length - iOffset)) {
                iOffset = iOffset + iRead;
            }

            String lobFilename = getLobFilename();
            URI externalLobFolder = getAbsoluteLobFolder();

            AbstractMap.SimpleEntry<File, String> lobFileAndLobFilename = getLobFileAndUpdateLobFilename(lobFilename, externalLobFolder);
            File lobFile = lobFileAndLobFilename.getKey();
            lobFilename = lobFileAndLobFilename.getValue();

            OutputStream outputStream = Files.newOutputStream(lobFile.toPath());
            getValueElement().setAttribute(ArchiveImpl._sATTR_FILE, lobFilename);
            outputStream = new ValidatingOutputStream(getValueElement(), outputStream);
            outputStream.write(bufferPrefix, 0, iOffset);

            if (iRead != -1) {
                byte[] bufferTransfer = new byte[ArchiveImpl._iBUFFER_SIZE];
                for (iRead = inputStream.read(bufferTransfer); iRead != -1; iRead = inputStream.read(bufferTransfer))
                    outputStream.write(bufferTransfer, 0, iRead);
            }

            outputStream.close();
            inputStream.close();

        } else if (getPreType() != Types.NULL) {
            throw new IllegalArgumentException("Cell of type " + SqlTypes.getTypeName(getPreType()) + " cannot be set using an input stream!");
        } else {
            throw new IllegalArgumentException("Value of cell of complex type cannot be set using an input stream!");
        }
    }

    private AbstractMap.SimpleEntry<File, String> getLobFileAndUpdateLobFilename(String lobFilename, URI externalLobFolderUri) throws IOException {
        File lobFile;
        if (externalLobFolderUri == null) {
            lobFilename = getInternalLobFolder() + lobFilename;
            URI uriTemporaryLobFolder = getTemporaryLobFolder();
            lobFile = FU.fromUri(uriTemporaryLobFolder.resolve(lobFilename));
            lobFilename = getTableImpl().getTableFolder() + lobFilename;
        } else {
            int iMaxLobsPerFolder = getArchiveImpl().getMaxLobsPerFolder();
            if ((iMaxLobsPerFolder > 0) && (getTableImpl().getMetaTable()
                                                          .getRows() > iMaxLobsPerFolder)) {
                long lSequence = getRecord() / getArchiveImpl().getMaxLobsPerFolder();
                lobFilename = _sSEQUENCE_PREFIX + lSequence + File.separator + lobFilename;
            }
            URI uriExternal = externalLobFolderUri.resolve(lobFilename);
            lobFile = FU.fromUri(uriExternal);
        }
        if (!lobFile.getParentFile()
                    .exists()) {
            boolean success = lobFile.getParentFile()
                                     .mkdirs();
            if (!success) logger.warning("Could not create lob file: " + lobFile.getParentFile()
                                                                                .getAbsolutePath());
        }
        return new AbstractMap.SimpleEntry<>(lobFile, lobFilename);
    }

    @Override
    public void setInputStream(InputStream inputStream, String filePath) throws IOException {
        if (getPreType() == Types.DATALINK) {
            setInputStream(inputStream);
            if (getValueElement().hasAttribute(ArchiveImpl._sATTR_DLURLPATHONLY)) {
                getValueElement().setAttribute(ArchiveImpl._sATTR_DLURLPATHONLY, filePath);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getElements()
            throws IOException {
        int iElements = 0;
        int iCardinality = getCardinality();
        if (iCardinality >= 0)
            iElements = getMetaValue().getMetaFields();
        return iElements;
    } /* getElements */

    /**
     * {@inheritDoc}
     */
    @Override
    public Field getElement(int iElement)
            throws IOException {
        int iCardinality = getCardinality();
        if (iCardinality >= 0) {
            /* extend field map of array array with NULL fields */
            extendArray(iElement + 1);
            return getFieldMap().get(getElementTag(iElement));
        }
        throw new IllegalArgumentException("Cell or field is not an ARRAY!");
    } /* getElement */

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAttributes()
            throws IOException {
        int iAttributes = 0;
        MetaType mt = getMetaType();
        CategoryType cat = null;
        if (mt != null)
            cat = mt.getCategoryType();
        if (cat == CategoryType.UDT)
            iAttributes = getFieldMap().size();
        return iAttributes;
    } /* getAttributes */

    /**
     * {@inheritDoc}
     */
    @Override
    public Field getAttribute(int iAttribute)
            throws IOException {
        Field field;
        MetaType mt = getMetaType();
        CategoryType cat = null;
        if (mt != null)
            cat = mt.getCategoryType();
        if (cat == CategoryType.UDT)
            field = getFieldMap().get(getAttributeTag(iAttribute));
        else
            throw new IllegalArgumentException("Cell or field is not a UDT!");
        return field;
    } /* getAttribute */

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getObject()
            throws IOException {
        if (isNull()) return null;
        int iPreType = getPreType();
        if (iPreType != Types.NULL) {
            return switch (iPreType) {
                case Types.CHAR, Types.VARCHAR, Types.NCHAR, Types.NVARCHAR -> getString();
                case Types.CLOB, Types.NCLOB, Types.SQLXML -> getReader();
                case Types.BINARY, Types.VARBINARY -> getBytes();
                case Types.BLOB, Types.DATALINK -> getInputStream();
                case Types.DECIMAL, Types.NUMERIC -> getBigDecimal();
                case Types.SMALLINT -> getInt();
                case Types.INTEGER -> getLong();
                case Types.BIGINT -> getBigInteger();
                case Types.FLOAT, Types.DOUBLE -> getDouble();
                case Types.REAL -> getFloat();
                case Types.BOOLEAN -> getBoolean();
                case Types.DATE -> getDate();
                case Types.TIME -> getTime();
                case Types.TIMESTAMP -> getTimestamp();
                case Types.OTHER -> getDuration();
                default -> null;
            };
        }

        throw new IllegalArgumentException("Cell is a structured type!");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Value> getValues(boolean bSupportsArrays, boolean bSupportsUdts)
            throws IOException {
        List<Value> listValues = new ArrayList<>();
        if (!bSupportsArrays) {
            for (int iElement = 0; iElement < getElements(); iElement++)
                listValues.addAll(getElement(iElement).getValues(bSupportsArrays, bSupportsUdts));
        }
        if (!bSupportsUdts) {
            for (int iAttribute = 0; iAttribute < getAttributes(); iAttribute++)
                listValues.addAll(getAttribute(iAttribute).getValues(bSupportsArrays, bSupportsUdts));
        }
        if (listValues.isEmpty())
            listValues.add(this);
        return listValues;
    } /* getValues */

    private void dumpElement(Element el, String sIndent) {
        System.out.print("\r\n" + sIndent + el.getTagName() + ":");
        int iElements = 0;
        for (int i = 0; i < el.getChildNodes()
                              .getLength(); i++) {
            Node node = el.getChildNodes()
                          .item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elChild = (Element) node;
                dumpElement(elChild, sIndent + "  ");
                iElements++;
            }
        }
        if (iElements == 0)
            System.out.println(" " + el.getTextContent());
    }

    public void dumpDom() {
        if (_elValue != null)
            dumpElement(_elValue, "");
        else
            System.out.println("null");
    } /* dumpDom */


    private boolean isIntegerType(int iPreType) {
        return (iPreType == Types.SMALLINT) ||
                (iPreType == Types.INTEGER) ||
                (iPreType == Types.BIGINT);
    }

    private boolean isBigDecimalType(int iPreType) {
        return (iPreType == Types.DECIMAL) ||
                (iPreType == Types.NUMERIC) ||
                (iPreType == Types.SMALLINT) ||
                (iPreType == Types.INTEGER) ||
                (iPreType == Types.BIGINT) ||
                (iPreType == Types.FLOAT) ||
                (iPreType == Types.REAL) ||
                (iPreType == Types.DOUBLE);
    }

    private boolean isDoubleType(int iPreType) {
        return (iPreType == Types.REAL) || (iPreType == Types.FLOAT) || (iPreType == Types.DOUBLE);
    }

    private boolean isNumberType(int iPreType) {
        return (iPreType == Types.REAL) ||
                (iPreType == Types.DOUBLE) ||
                (iPreType == Types.FLOAT);
    }

    private static boolean isReaderType(int iPreType) {
        return (iPreType == Types.CHAR) ||
                (iPreType == Types.VARCHAR) ||
                (iPreType == Types.CLOB) ||
                (iPreType == Types.NCHAR) ||
                (iPreType == Types.NVARCHAR) ||
                (iPreType == Types.NCLOB) ||
                (iPreType == Types.SQLXML) ||
                (iPreType == Types.DATALINK);
    }

    private boolean isBinaryType() throws IOException {
        return (getPreType() == Types.BINARY) ||
                (getPreType() == Types.VARBINARY) ||
                (getPreType() == Types.BLOB) ||
                (getPreType() == Types.DATALINK);
    }

    @SneakyThrows
    @Override
    public String toString() {
        String newLine = System.lineSeparator();
        MetaValue metaValue = this.getMetaValue();
        return "{ name: " + metaValue.getName()
                + newLine
                + "  type: " + metaValue.getType()
                + newLine
                + "  typeOriginal: " + metaValue.getTypeOriginal()
                + newLine
                + "}";
    }
}