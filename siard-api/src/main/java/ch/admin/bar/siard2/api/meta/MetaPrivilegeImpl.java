/*== MetaPrivilegeImpl.java ============================================
MetaPrivilegeImpl implements the interface MetaPrivilege.
Application : SIARD 2.0
Description : MetaPrivilegeImpl implements the interface MetaPrivilege.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 24.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.meta;

import ch.admin.bar.siard2.api.MetaData;
import ch.admin.bar.siard2.api.MetaPrivilege;
import ch.admin.bar.siard2.api.generated.PrivOptionType;
import ch.admin.bar.siard2.api.generated.PrivilegeType;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.DU;
import ch.enterag.utils.SU;
import ch.enterag.utils.xml.XU;

import java.io.IOException;


/**
 * MetaPrivilegeImpl implements the interface MetaPrivilege.
 *
 */
public class MetaPrivilegeImpl
        extends MetaSearchImpl
        implements MetaPrivilege {
    private MetaData _mdParent = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaData getParentMetaData() {
        return _mdParent;
    }

    private PrivilegeType _pt = null;

    /**
     * get archive
     *
     * @return archive.
     */
    private ArchiveImpl getArchive() {
        return (ArchiveImpl) getParentMetaData().getArchive();
    } 

    /**
     * set template meta data.
     *
     * @param ptTemplate
     */
    public void setTemplate(PrivilegeType ptTemplate) {
        if (!SU.isNotEmpty(getDescription()))
            setDescription(XU.fromXml(ptTemplate.getDescription()));
    } 

    /**
     * constructor
     *
     * @param mdParent global meta data object of SIARD archive.
     * @param pt       PrivilegeType instance (JAXB).
     */
    private MetaPrivilegeImpl(MetaData mdParent, PrivilegeType pt) {
        _mdParent = mdParent;
        _pt = pt;
    } 

    /**
     * factory
     *
     * @param mdParent global meta data object of SIARD archive.
     * @param pt       PrivilegeType instance (JAXB).
     * @return new MetaPrivilege instance.
     */
    public static MetaPrivilege newInstance(MetaData mdParent, PrivilegeType pt) {
        return new MetaPrivilegeImpl(mdParent, pt);
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return XU.fromXml(_pt.getType());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public String getObject() {
        return XU.fromXml(_pt.getObject());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public String getGrantor() {
        return XU.fromXml(_pt.getGrantor());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public String getGrantee() {
        return XU.fromXml(_pt.getGrantee());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOption(String sOption)
            throws IOException {
        if (getArchive().canModifyPrimaryData()) {
            PrivOptionType pot = PrivOptionType.fromValue(sOption.toUpperCase()
                                                                 .trim());
            if (pot != null) {
                if (getArchive().isMetaDataDifferent(_pt.getOption(), pot))
                    _pt.setOption(pot);
            } else
                throw new IOException("Invalid privilege option value!");
        } else
            throw new IOException("Privilege option value cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOption() {
        String sOption = null;
        if (_pt.getOption() != null)
            sOption = _pt.getOption()
                         .value();
        return sOption;
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(String sDescription) {
        if (getArchive().isMetaDataDifferent(getDescription(), sDescription))
            _pt.setDescription(XU.toXml(sDescription));
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return XU.fromXml(_pt.getDescription());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getSearchElements(DU du)
            throws IOException {
        return new String[]
                {
                        getType(),
                        getObject(),
                        getGrantor(),
                        getGrantee(),
                        getOption(),
                        getDescription()
                };
    } 

    /**
     * {@inheritDoc}
     * toString() returns the a description of the privilege which is to
     * be displayed as the label of the privilege node of the tree
     * displaying the archive.
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        if (getGrantor() != null)
            sb.append(getGrantor());
        else
            sb.append("*");
        if (getGrantee() != null) {
            sb.append(" ");
            sb.append(getGrantee());
        } else
            sb.append("*");
        if (getType() != null) {
            sb.append(" ");
            sb.append(getType());
        }
        if (getObject() != null) {
            sb.append(" ");
            sb.append(getObject());
        }
        return sb.toString();
    } 

} 
