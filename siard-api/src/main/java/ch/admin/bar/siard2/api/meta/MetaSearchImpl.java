/*== MetaSearchImpl.java ===============================================
MetaSearchImpl implements the interface MetaSearch.
Application : SIARD 2.0
Description : MetaSearchImpl implements the interface MetaSearch.
------------------------------------------------------------------------
Copyright  : Swiss Federal Archives, Berne, Switzerland, 2016
Created    : 24.06.2016, Hartwig Thomas, Enter AG, Rüti ZH
======================================================================*/
package ch.admin.bar.siard2.api.meta;

import ch.admin.bar.siard2.api.MetaSearch;
import ch.enterag.utils.DU;
import ch.enterag.utils.logging.IndentLogger;

import java.io.IOException;


/**
 * MetaSearchImpl implements the interface MetaSearch.
 *
 */
public abstract class MetaSearchImpl
        implements MetaSearch {
  /*====================================================================
  (private) data members
  ====================================================================*/
    /**
     * logger
     */
    private static final IndentLogger _il = IndentLogger.getIndentLogger(MetaSearch.class.getName());

    /**
     * find string
     */
    protected String _sFindString = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFindString() {
        return _sFindString;
    }

    /**
     * match case
     */
    protected boolean _bMatchCase = false;

    /**
     * element which contains the found string (in interface order)
     */
    protected int _iFoundElement = 0;

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFoundElement()
            throws IOException {
        return _iFoundElement;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFoundString(DU du)
            throws IOException {
        return getSearchElements(du)[_iFoundElement];
    } 

    /**
     * offset in element at position where string was found if _mdFind == this
     */
    protected int _iFoundOffset = -1;

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFoundOffset() {
        return _iFoundOffset;
    }

  /*====================================================================
  abstract methods
  ====================================================================*/

    /**
     * get searchable elements
     *
     * @return searchable elements of the searchable meta data.
     */
    abstract protected String[] getSearchElements(DU du) throws IOException;

    /**
     * get searchable sub meta data
     * N.B.: Must be overridden by meta data elements that have sub meta
     * data elements!
     *
     * @return searchable sub meta data.
     */
    protected MetaSearch[] getSubMetaSearches()
            throws IOException {
        return new MetaSearch[]{};
    } 
  
  /*====================================================================
  interface methods
  ====================================================================*/

    /**
     * {@inheritDoc}
     */
    @Override
    public void find(String sFindString, boolean bMatchCase)
            throws IOException {
        _il.enter(sFindString, String.valueOf(bMatchCase), getClass().getName());
        _iFoundElement = 0;
        _iFoundOffset = -1;
        _bMatchCase = bMatchCase;
        if (bMatchCase)
            _sFindString = sFindString;
        else
            _sFindString = sFindString.toLowerCase();
        /* distribute find string to all elements */
        MetaSearch[] amsSubMeta = getSubMetaSearches();
        for (int iSubMeta = 0; iSubMeta < amsSubMeta.length; iSubMeta++) {
            if (amsSubMeta[iSubMeta] != null)
                amsSubMeta[iSubMeta].find(_sFindString, _bMatchCase);
        }
        _il.exit();
    } 

    /**
     * {@inheritDoc}
     * must be called at the end by all derived methods!
     */
    @Override
    public MetaSearch findNext(DU du)
            throws IOException {
        _il.enter(this.getClass()
                      .getName());
        MetaSearch msFind = null;
        if (_sFindString != null) {
            _il.event("Find string: " + _sFindString);
            /* skip previous find */
            _iFoundOffset++;
            
            String[] asElement = getSearchElements(du);
            while ((msFind == null) && (_iFoundElement < asElement.length)) {
                int iPos = -1;
                String sElement = asElement[_iFoundElement];
                if (sElement != null) {
                    if (_bMatchCase)
                        iPos = sElement.indexOf(_sFindString, _iFoundOffset);
                    else
                        iPos = sElement.toLowerCase()
                                       .indexOf(_sFindString, _iFoundOffset);
                    if (iPos >= 0) {
                        _il.event("Found in \"" + sElement + "\" " + _iFoundElement + " at position " + iPos);
                        msFind = this;
                        _iFoundOffset = iPos;
                    } else {
                        _iFoundOffset = 0;
                        _iFoundElement++;
                    }
                } else
                    _iFoundElement++;
            }
            /* check sub meta data */
            if (msFind == null) {
                _iFoundOffset = -1;
                MetaSearch[] amsSubMeta = getSubMetaSearches();
                while ((msFind == null) && (_iFoundElement < asElement.length + amsSubMeta.length)) {
                    if (amsSubMeta[_iFoundElement - asElement.length] != null)
                        msFind = amsSubMeta[_iFoundElement - asElement.length].findNext(du);
                    if (msFind == null)
                        _iFoundElement++;
                }
            }
            /* if nothing found, reset find string */
            if (msFind == null)
                _sFindString = null;
            else
                _il.event("Element: " + _iFoundElement + " / Offset: " + _iFoundOffset);
        }
        _il.exit(String.valueOf(msFind));
        return msFind;
    } 

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canFindNext() {
        return (_sFindString != null);
    } 

} 
