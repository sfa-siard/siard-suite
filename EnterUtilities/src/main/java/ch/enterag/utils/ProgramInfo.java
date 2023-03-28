/*======================================================================
ProgramInfo class contains program info.
Application : Utilities
Description : ProgramInfo is a container of program info.
------------------------------------------------------------------------
Copyright  : Enter AG, Rüti ZH, Switzerland, 2005, 2017
Created    : November 2005, Hartwig Thomas
======================================================================*/

package ch.enterag.utils;

import ch.enterag.utils.logging.IndentLogger;

import java.util.*;

/*====================================================================*/

/**
 * ProgramInfo is the container of program info.
 *
 * @author Hartwig Thomas, Katja Reinhardt
 */
public class ProgramInfo {
    /*====================================================================
    (private) constants
    ====================================================================*/
    private static final String sSPECIFIED_BY = "Specified by : ";
    private static final String sDEVELOPED_BY = "Developed by : ";
    private static final String sTESTED_BY = "Tested by    : ";
    private static final String sMANAGED_BY = "Managed by   : ";
    private static final String sTRANSLATED_BY = "Translated by: ";
    private static final String sJAVA = "JAVA         : ";
	
  /*====================================================================
  (private) data members
  ====================================================================*/
    /**
     * the only instance of this class
     */
    protected static ProgramInfo _info = null;
    /**
     * logger
     */
    private static IndentLogger _il = IndentLogger.getIndentLogger(ProgramInfo.class.getName());

    /**
     * Application name
     */
    private String _sApplication = null;
    /**
     * Application version
     */
    private String _sAppVersion = null;
    /**
     * Program (component) name
     */
    private String _sProgram = null;
    /**
     * Program (component) version
     */
    private String _sVersion = null;
    /**
     * Description
     */
    private String _sDescription = null;
    /**
     * Copyright statement
     */
    private String _sCopyright = null;
    /**
     * Program specification authors
     */
    private List<String> _listSpecifiers = new ArrayList<String>();
    /**
     * Program developers
     */
    private List<String> _listDevelopers = new ArrayList<String>();
    /**
     * Program testers
     */
    private List<String> _listTesters = new ArrayList<String>();
    /**
     * Program managers
     */
    private List<String> _listManagers = new ArrayList<String>();
    /**
     * Program translators
     */
    private Map<String, String> _mapTranslators = new HashMap<String, String>();

    /*====================================================================
    (private) utility method
    ====================================================================*/
    public String toString(List<String> list) {
        StringBuffer sb = new StringBuffer();
        for (Iterator<String> iterator = list.iterator(); iterator.hasNext(); ) {
            String s = iterator.next();
            if (sb.length() > 0)
                sb.append("; ");
            sb.append(s);
        }
        return sb.toString();
    } /* toString */
  
  /*====================================================================
  Accessors
  ====================================================================*/
    /*------------------------------------------------------------------*/

    /**
     * returns OS name (e.g. Windows 7).
     *
     * @return operating system name.
     */
    public String getOs() {
        return System.getProperty("os.name");
    } /* getOs */

    /*------------------------------------------------------------------*/

    /**
     * returns OS version (e.g. 6.1).
     *
     * @return operating system version.
     */
    public String getOsVersion() {
        return System.getProperty("os.version");
    } /* getOsVersion */

    /*------------------------------------------------------------------*/

    /**
     * returns OS architecture (e.g. amd64).
     *
     * @return operating system architecture (32/64 bit).
     */
    public String getOsArchitecture() {
        return System.getProperty("os.arch");
    } /* getOsArchitecture */

    /*------------------------------------------------------------------*/

    /**
     * returns JAVA version (e.g. 1.7.0_01).
     *
     * @return JAVA (runtime) version.
     */
    public String getJavaVersion() {
        return System.getProperty("java.version");
    } /* getJavaVersion */

    /*------------------------------------------------------------------*/

    /**
     * returns JAVA architecture.
     *
     * @return JAVA architecture (32/64 bit).
     */
    public String getJavaArchitecture() {
        return System.getProperty("sun.arch.data.model");
    } /* getJavaArchitecture */

    /*------------------------------------------------------------------*/

    /**
     * returns application name.
     *
     * @return application name.
     */
    public String getApplication() {
        return _sApplication;
    } /* getApplication */

    /*------------------------------------------------------------------*/

    /**
     * returns application version.
     *
     * @return application version.
     */
    public String getAppVersion() {
        return _sAppVersion;
    } /* getAppVersion */

    /*------------------------------------------------------------------*/

    /**
     * returns program name.
     *
     * @return program name.
     */
    public String getProgram() {
        return _sProgram;
    } /* getProgram */

    /*------------------------------------------------------------------*/

    /**
     * returns program version.
     *
     * @return program version.
     */
    public String getVersion() {
        return _sVersion;
    } /* getVersion */

    /*------------------------------------------------------------------*/

    /**
     * returns program description.
     *
     * @return program description.
     */
    public String getDescription() {
        return _sDescription;
    } /* getDescription */

    /*------------------------------------------------------------------*/

    /**
     * returns program description.
     *
     * @param sDescription program description.
     */
    public void setDescription(String sDescription) {
        _sDescription = sDescription;
    } /* setDescription */

    /*------------------------------------------------------------------*/

    /**
     * returns program copyright.
     *
     * @return program copyright.
     */
    public String getCopyright() {
        return _sCopyright;
    } /* getCopyright */

    /*------------------------------------------------------------------*/

    /**
     * returns program specification authors.
     *
     * @return program specification authors.
     */
    public List<String> getSpecifierList() {
        return _listSpecifiers;
    } /* getSpecifierList */

    /*------------------------------------------------------------------*/

    /**
     * returns program specification authors.
     *
     * @return program specification authors.
     */
    public String getSpecifiers() {
        return toString(_listSpecifiers);
    } /* getSpecifiers */

    /*------------------------------------------------------------------*/

    /**
     * add a program specification author.
     *
     * @param sSpecifier program specification author.
     */
    public void addSpecifier(String sSpecifier) {
        _listSpecifiers.add(sSpecifier);
    } /* addSpecifier */

    /*------------------------------------------------------------------*/

    /**
     * returns program developers.
     *
     * @return program developers.
     */
    public List<String> getDeveloperList() {
        return _listDevelopers;
    } /* getDeveloperList */

    /*------------------------------------------------------------------*/

    /**
     * returns program developers.
     *
     * @return program developers.
     */
    public String getDevelopers() {
        return toString(_listDevelopers);
    } /* getDevelopers */

    /*------------------------------------------------------------------*/

    /**
     * add a program developer.
     *
     * @param sDeveloper program developer.
     */
    public void addDeveloper(String sDeveloper) {
        _listDevelopers.add(sDeveloper);
    } /* addDeveloper */

    /*------------------------------------------------------------------*/

    /**
     * returns program testers.
     *
     * @return program testers.
     */
    public List<String> getTesterList() {
        return _listTesters;
    } /* getTesterList */

    /*------------------------------------------------------------------*/

    /**
     * returns program testers.
     *
     * @return program testers.
     */
    public String getTesters() {
        return toString(_listTesters);
    } /* getTesters */

    /*------------------------------------------------------------------*/

    /**
     * add a program tester.
     *
     * @param sTester program tester.
     */
    public void addTester(String sTester) {
        _listTesters.add(sTester);
    } /* addTester */

    /*------------------------------------------------------------------*/

    /**
     * returns program development managers.
     *
     * @return program development managers.
     */
    public List<String> getManagerList() {
        return _listManagers;
    } /* getManagerList */

    /*------------------------------------------------------------------*/

    /**
     * returns program development managers.
     *
     * @return program development managers.
     */
    public String getManagers() {
        return toString(_listManagers);
    } /* getManagers */

    /*------------------------------------------------------------------*/

    /**
     * add a program development manager.
     *
     * @param sManager program development manager.
     */
    public void addManager(String sManager) {
        _listManagers.add(sManager);
    } /* addManager */

    /*------------------------------------------------------------------*/

    /**
     * returns program translators.
     *
     * @return program translators (null for untranslated).
     */
    public Map<String, String> getTranslatorMap() {
        return _mapTranslators;
    } /* getTranslatorMap */

    /*------------------------------------------------------------------*/

    /**
     * add a program translator.
     *
     * @param sLanguage   ISO 639-1 code (2 characters, lower-case) for language.
     * @param sTranslator for this language.
     */
    public void addTranslator(String sLanguage, String sTranslator) {
        _mapTranslators.put(sLanguage, sTranslator);
    } /* addManager */
  
  /*====================================================================
  methods
  ====================================================================*/
    /*------------------------------------------------------------------*/

    /**
     * returns the new instance with initialized fields.
     *
     * @param sApplication   application name.
     * @param sAppVersion    application version.
     * @param sProgram       program (component) name.
     * @param sVersion       program (component) version.
     * @param sDescription   program description.
     * @param sCopyright     program copyright.
     * @param listSpecifiers list of program specification authors.
     * @param listDevelopers list of program developers.
     * @param listTesters    list of program testers.
     * @param listManagers   list of program development managers.
     * @return new instance with initialized fields.
     */
    public static ProgramInfo getProgramInfo(String sApplication, String sAppVersion,
                                             String sProgram, String sVersion,
                                             String sDescription, String sCopyright,
                                             List<String> listSpecifiers,
                                             List<String> listDevelopers,
                                             List<String> listTesters,
                                             List<String> listManagers) {
        _info = new ProgramInfo(sApplication, sAppVersion,
                                sProgram, sVersion,
                                sDescription, sCopyright,
                                listSpecifiers,
                                listDevelopers,
                                listTesters,
                                listManagers);
        return _info;
    } /* getProgramInfo */

    /*------------------------------------------------------------------*/

    /**
     * returns the new instance with initialized fields.
     *
     * @param sApplication application name.
     * @param sAppVersion  application version.
     * @param sProgram     program (component) name.
     * @param sVersion     program (component) version.
     * @param sDescription program description.
     * @param sCopyright   program copyright.
     * @return new instance with initialized fields.
     */
    public static ProgramInfo getProgramInfo(String sApplication, String sAppVersion,
                                             String sProgram, String sVersion, String sDescription, String sCopyright) {
        _info = new ProgramInfo(sApplication, sAppVersion,
                                sProgram, sVersion, sDescription, sCopyright);
        return _info;
    } /* getProgramInfo */

    /*------------------------------------------------------------------*/

    /**
     * returns the existing instance.
     *
     * @return existing instance of singleton class.
     */
    public static ProgramInfo getProgramInfo() {
        if (_info == null)
            throw new RuntimeException("ProgramInfo must be initialized first!");
        return _info;
    } /* getProgramInfo */

    /*------------------------------------------------------------------*/

    /**
     * prints a list.
     */
    private void printList(String sLabel, List<String> list) {
        int iItem = 0;
        if (iItem < list.size()) {
            System.out.println(sLabel + list.get(iItem));
            String sIndent = SU.repeat(sLabel.length(), ' ');
            for (iItem++; iItem < list.size(); iItem++)
                System.out.println(sIndent + list.get(iItem));
        }
    } /* printList */

    private void printMap(String sLabel, Map<String, String> map) {
        int iItem = 0;
        if (iItem < map.size()) {
            String[] asLanguage = map.keySet().toArray(new String[]{});
            System.out.println(sLabel + map.get(asLanguage[iItem]) + " (" + asLanguage[iItem] + ")");
            String sIndent = SU.repeat(sLabel.length(), ' ');
            for (iItem++; iItem < asLanguage.length; iItem++)
                System.out.println(sIndent + map.get(asLanguage[iItem]) + " (" + asLanguage[iItem] + ")");
        }
    } /* printMap */

    /*------------------------------------------------------------------*/

    /**
     * displays initial program information.
     */
    public void printStart() {
        String sDescription = "";
        if (getDescription() != null)
            sDescription = " - " + getDescription();
        System.out.println(getProgram() + " " + getVersion() + sDescription);
        System.out.println(getApplication() + " " + getAppVersion() + ": (c) " + getCopyright());
        printList(sSPECIFIED_BY, _listSpecifiers);
        printList(sDEVELOPED_BY, _listDevelopers);
        printList(sTESTED_BY, _listTesters);
        printList(sMANAGED_BY, _listManagers);
        printMap(sTRANSLATED_BY, _mapTranslators);
        System.out.println(sJAVA + "Version " + getJavaVersion() + " (" + getJavaArchitecture() + ")");
        StringBuilder sbOs = new StringBuilder(getOs());
        while (sbOs.length() < sJAVA.length() - 2)
            sbOs.append(" ");
        sbOs.append(": ");
        System.out.println(sbOs.toString() + "Version " + getOsVersion() + " (" + getOsArchitecture() + ")");
    } /* printStart */

    /*------------------------------------------------------------------*/

    /**
     * displays program termination information.
     */
    public void printTermination() {
        System.out.println(getProgram() + " " + getVersion() + " terminates.");
    } /* printTermination */

    /*------------------------------------------------------------------*/

    /**
     * logs a list.
     */
    private void logList(String sLabel, List<String> list) {
        int iItem = 0;
        if (iItem < list.size()) {
            _il.info(sLabel + list.get(iItem));
            String sIndent = SU.repeat(sLabel.length(), ' ');
            for (iItem++; iItem < list.size(); iItem++)
                _il.info(sIndent + list.get(iItem));
        }
    } /* logList */

    /*------------------------------------------------------------------*/

    /**
     * logs a map.
     */
    private void logMap(String sLabel, Map<String, String> map) {
        int iItem = 0;
        if (iItem < map.size()) {
            String[] asLanguage = map.keySet().toArray(new String[]{});
            _il.info(sLabel + map.get(asLanguage[iItem]) + " (" + asLanguage[iItem] + ")");
            String sIndent = SU.repeat(sLabel.length(), ' ');
            for (iItem++; iItem < asLanguage.length; iItem++)
                _il.info(sIndent + map.get(asLanguage[iItem]) + " (" + asLanguage[iItem] + ")");
        }
    } /* logMap */

    /*------------------------------------------------------------------*/

    /**
     * logs initial program information.
     */
    public void logStart() {
        String sDescription = "";
        if (getDescription() != null)
            sDescription = " - " + getDescription();
        _il.info(getProgram() + " " + getVersion() + sDescription);
        _il.info(getApplication() + " " + getAppVersion() + ": " + getCopyright());
        logList(sSPECIFIED_BY, _listSpecifiers);
        logList(sDEVELOPED_BY, _listDevelopers);
        logList(sTESTED_BY, _listTesters);
        logList(sMANAGED_BY, _listManagers);
        logMap(sTRANSLATED_BY, _mapTranslators);
        _il.info(sJAVA + "Version " + getJavaVersion() + " (" + getJavaArchitecture() + ")");
        StringBuilder sbOs = new StringBuilder(getOs());
        while (sbOs.length() < sJAVA.length() - 2)
            sbOs.append(" ");
        sbOs.append(": ");
        _il.info(sbOs.toString() + "Version " + getOsVersion() + " (" + getOsArchitecture() + ")");
    } /* logStart */

    /*------------------------------------------------------------------*/

    /**
     * logs program termination information.
     */
    public void logTermination() {
        _il.info(getProgram() + " " + getVersion() + " terminates.");
    } /* logTermination */
  
  /*====================================================================
  constructors
  ====================================================================*/
    /*------------------------------------------------------------------*/

    /**
     * creates an instance of ProgramInfo with  partially initialized fields.
     *
     * @param sApplication application name.
     * @param sAppVersion  application version.
     * @param sProgram     program (component) name.
     * @param sVersion     program (component) version.
     * @param sCopyright   program copyright.
     */
    protected ProgramInfo(String sApplication, String sAppVersion,
                          String sProgram, String sVersion,
                          String sDescription, String sCopyright) {
        _info = this;
        _sApplication = sApplication;
        _sAppVersion = sAppVersion;
        _sProgram = sProgram;
        _sVersion = sVersion;
        _sDescription = sDescription;
        _sCopyright = sCopyright;
    } /* constructor ProgramInfo */

    /*------------------------------------------------------------------*/

    /**
     * creates an instance of ProgramInfo with initialized fields.
     *
     * @param sApplication   application name.
     * @param sAppVersion    application version.
     * @param sProgram       program (component) name.
     * @param sVersion       program (component) version.
     * @param sDescription   program description.
     * @param sCopyright     program copyright.
     * @param listSpecifiers list of program specification authors.
     * @param listDevelopers list of program developers.
     * @param listTesters    list of program testers.
     * @param listManagers   list of program development managers.
     */
    protected ProgramInfo(String sApplication, String sAppVersion,
                          String sProgram, String sVersion,
                          String sDescription, String sCopyright,
                          List<String> listSpecifiers,
                          List<String> listDevelopers,
                          List<String> listTesters,
                          List<String> listManagers) {
        _info = this;
        _sApplication = sApplication;
        _sAppVersion = sAppVersion;
        _sProgram = sProgram;
        _sVersion = sVersion;
        _sDescription = sDescription;
        _sCopyright = sCopyright;
        if (listSpecifiers != null)
            _listSpecifiers = listSpecifiers;
        if (listDevelopers != null)
            _listDevelopers = listDevelopers;
        if (listTesters != null)
            _listTesters = listTesters;
        if (listManagers != null)
            _listManagers = listManagers;
    } /* constructor ProgramInfo */
  
  /*====================================================================
  methods
  ====================================================================*/
    /*------------------------------------------------------------------*/

    /**
     * compare the given version with the this program info's internal
     * version.
     *
     * @param sVersion given version.
     * @return -1, if the internal version is less than the given one,
     * 0, if they are equal,
     * 1 if the internal version is greater than the given one.
     */
    public int compareVersion(String sVersion) {
        int iCompare = 1;
        if (sVersion != null) {
            iCompare = 0;
            String[] asInternal = getVersion().split("\\.");
            String[] asExternal = sVersion.split("\\.");
            int iIndex = 0;
            for (; (iCompare == 0) &&
                    (iIndex < asInternal.length) &&
                    (iIndex < asExternal.length); iIndex++) {
                int iInternal = Integer.parseInt(asInternal[iIndex]);
                int iExternal = Integer.parseInt(asExternal[iIndex]);
                iCompare = Integer.compare(iInternal, iExternal);
            }
            if (iCompare == 0) {
                if (iIndex < asInternal.length)
                    iCompare = 1;
                else if (iIndex < asExternal.length)
                    iCompare = -1;
            }
        }
        return iCompare;
    } /* compareVersion */

} /* class ProgramInfo */
