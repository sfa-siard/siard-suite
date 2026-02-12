/*== MetaTriggerImpl.java ==============================================
MetaTriggerImpl implements the interface MetaTrigger.
Application : SIARD 2.0
Description : MetaTriggerImpl implements the interface MetaTrigger.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 27.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.meta;

import ch.admin.bar.siard2.api.MetaTable;
import ch.admin.bar.siard2.api.MetaTrigger;
import ch.admin.bar.siard2.api.generated.ActionTimeType;
import ch.admin.bar.siard2.api.generated.TriggerType;
import ch.admin.bar.siard2.api.primary.ArchiveImpl;
import ch.enterag.utils.DU;
import ch.enterag.utils.SU;
import ch.enterag.utils.xml.XU;

import java.io.IOException;


/**
 * MetaTriggerImpl implements the interface MetaTrigger.
 *
 */
public class MetaTriggerImpl
        extends MetaSearchImpl
        implements MetaTrigger {
    private MetaTable _mtParent = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public MetaTable getParentMetaTable() {
        return _mtParent;
    }

    private final TriggerType _tt;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid() {
        boolean bValid = getActionTime() != null;
        if (getTriggerEvent() == null)
            bValid = false;
        if (getTriggeredAction() == null)
            bValid = false;
        return bValid;
    }

    /**
     * get archive
     *
     * @return archive.
     */
    private ArchiveImpl getArchive() {
        return (ArchiveImpl) getParentMetaTable().getTable()
                                                 .getParentSchema()
                                                 .getParentArchive();
    } 

    /**
     * set template meta data.
     *
     * @param ttTemplate template data.
     */
    public void setTemplate(TriggerType ttTemplate) {
        if (!SU.isNotEmpty(getDescription()))
            setDescription(XU.fromXml(ttTemplate.getDescription()));
    } 

    /**
     * constructor
     *
     * @param mtParent table meta data object of SIARD archive.
     * @param tt       TriggerType instance (JAXB).
     */
    private MetaTriggerImpl(MetaTable mtParent, TriggerType tt) {
        _mtParent = mtParent;
        _tt = tt;
    } 

    /**
     * factory
     *
     * @param mtParent table meta data object of SIARD archive.
     * @param tt       TriggerType instance (JAXB).
     * @return new MetaTrigger instance.
     */
    public static MetaTrigger newInstance(MetaTable mtParent, TriggerType tt) {
        return new MetaTriggerImpl(mtParent, tt);
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return XU.fromXml(_tt.getName());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setActionTime(String sActionTime)
            throws IOException {
        if (getArchive().canModifyPrimaryData()) {
            try {
                ActionTimeType att = ActionTimeType.fromValue(sActionTime.toUpperCase()
                                                                         .trim());
                if (getArchive().isMetaDataDifferent(getActionTime(), sActionTime))
                    _tt.setActionTime(att);
            } catch (IllegalArgumentException iae) {
                throw new IllegalArgumentException("Invalid action time value! (Must be \"BEFORE\", \"INSTEAD OF\" or \"AFTER\".)");
            }
        } else
            throw new IOException("Action time cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getActionTime() {
        String sActionTime = null;
        ActionTimeType att = _tt.getActionTime();
        if (att != null)
            sActionTime = XU.fromXml(att.value());
        return sActionTime;
    } 

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTriggerEvent(String sTriggerEvent)
            throws IOException {
        if (getArchive().canModifyPrimaryData()) {
            if (getArchive().isMetaDataDifferent(getTriggerEvent(), sTriggerEvent))
                _tt.setTriggerEvent(XU.toXml(sTriggerEvent));
        } else
            throw new IOException("Trigger event cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTriggerEvent() {
        return XU.fromXml(_tt.getTriggerEvent());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAliasList(String sAliasList)
            throws IOException {
        if (getArchive().canModifyPrimaryData()) {
            if (getArchive().isMetaDataDifferent(getAliasList(), sAliasList))
                _tt.setAliasList(XU.toXml(sAliasList));
        } else
            throw new IOException("Alias list cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAliasList() {
        return XU.fromXml(_tt.getAliasList());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTriggeredAction(String sTriggeredAction)
            throws IOException {
        if (getArchive().canModifyPrimaryData()) {
            if (getArchive().isMetaDataDifferent(getTriggeredAction(), sTriggeredAction))
                _tt.setTriggeredAction(XU.toXml(sTriggeredAction));
        } else
            throw new IOException("Triggered action cannot be set!");
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTriggeredAction() {
        return XU.fromXml(_tt.getTriggeredAction());
    }

    

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(String sDescription) {
        if (getArchive().isMetaDataDifferent(getDescription(), sDescription))
            _tt.setDescription(XU.toXml(sDescription));
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return XU.fromXml(_tt.getDescription());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getSearchElements(DU du)
            throws IOException {
        return new String[]
                {
                        getName(),
                        getActionTime(),
                        getTriggerEvent(),
                        getAliasList(),
                        getTriggeredAction(),
                        getDescription()
                };
    } 

    /**
     * {@inheritDoc}
     * toString() returns the name of the trigger which is to be displayed
     * as the label of the trigger node of the tree displaying the archive.
     */
    @Override
    public String toString() {
        return getName();
    }
} 
