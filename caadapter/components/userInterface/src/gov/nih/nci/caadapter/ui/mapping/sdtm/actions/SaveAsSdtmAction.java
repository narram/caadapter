package gov.nih.nci.caadapter.ui.mapping.sdtm.actions;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingExecutionException;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.dataviewer.MainDataViewerFrame;
import gov.nih.nci.caadapter.dataviewer.util.QBParseMappingFile;
import gov.nih.nci.caadapter.sdtm.SDTMMappingGenerator;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.DefaultSaveAsAction;
import gov.nih.nci.caadapter.ui.mapping.AbstractMappingPanel;
import gov.nih.nci.caadapter.ui.mapping.sdtm.Database2SDTMMappingPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * This class saves the map file and passes the control over to the
 * Data Viewer
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: jayannah $
 * @version Since caAdapter v4.0 revision
 *          $Revision: 1.9 $
 *          $Date: 2007-08-22 15:01:24 $
 */
public class SaveAsSdtmAction extends DefaultSaveAsAction {
    /**
     * Logging constant used to identify source of log entry, that could be later used to create logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: SaveAsSdtmAction.java,v $";
    /**
     * String that identifies the class version and solves the serial version UID problem. This String is for informational purposes only and MUST not be made
     * final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/sdtm/actions/SaveAsSdtmAction.java,v 1.9 2007-08-22 15:01:24 jayannah Exp $";
    protected AbstractMappingPanel mappingPanel;
    public SDTMMappingGenerator sdtmMappingGenerator;
    private boolean alreadySaved = false;

    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    public SaveAsSdtmAction(AbstractMappingPanel mappingPanel, SDTMMappingGenerator _sdtmMappingGenerator) {
        this(COMMAND_NAME, mappingPanel);
        this.sdtmMappingGenerator = _sdtmMappingGenerator;
    }

    public SaveAsSdtmAction(String command, AbstractMappingPanel mappingPanel, SDTMMappingGenerator _sdtmMappingGenerator) {
        this(command, mappingPanel);
        this.sdtmMappingGenerator = _sdtmMappingGenerator;
    }

    /**
     * Defines an <code>Action</code> object with the specified description string and a default icon.
     */
    public SaveAsSdtmAction(String name, AbstractMappingPanel mappingPanel) {
        this(name, null, mappingPanel);
    }

    /**
     * Defines an <code>Action</code> object with the specified description string and a the specified icon.
     */
    public SaveAsSdtmAction(String name, Icon icon, AbstractMappingPanel mappingPanel) {
        super(name, icon, null);
        this.mappingPanel = mappingPanel;
        // setAdditionalAttributes();
    }

    /**
     * Invoked when an action occurs.
     */
    protected boolean doAction(ActionEvent e) throws Exception {
        if (this.mappingPanel != null) {
            if (!mappingPanel.isSourceTreePopulated() || !mappingPanel.isTargetTreePopulated()) {
                String msg = "Enter both source and target information before saving the map specification.";
                JOptionPane.showMessageDialog(mappingPanel, msg, "Error", JOptionPane.ERROR_MESSAGE);
                setSuccessfullyPerformed(false);
                return false;
            }
        }
        File file = DefaultSettings.getUserInputOfFileFromGUI(this.mappingPanel, Config.MAP_FILE_DEFAULT_EXTENTION, "Save As...", true, true);
        if (file != null) {
            System.out.println("SaveSdtmAction ------ " + mappingPanel.getMappingDataManager().retrieveMappingData(false));
            // setSuccessfullyPerformed(processSaveFile(file));
            processSaveFile(file);
        }
        return isSuccessfullyPerformed();
    }

    protected boolean processSaveFile(File file) throws Exception {
        preActionPerformed(mappingPanel);
        BufferedOutputStream bw = null;
        boolean oldChangeValue = mappingPanel.isChanged();
        try {
            StringBuffer out = new StringBuffer();
            out.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
            out.append("<mapping>\n");
            out.append("  <components>\n");
            if (((Database2SDTMMappingPanel) mappingPanel).isConnectDB()) {
                Hashtable params = ((Database2SDTMMappingPanel) mappingPanel).getConnectionParameters();
                String paramString = params.get("URL").toString() + "~" + params.get("Driver").toString() + "~" + params.get("UserID").toString() + "~" + params.get("SCHEMA").toString();
                out.append("  \t<component kind=\"Database\" param=\"" + paramString + "\"/>\n");
            } else {
                out.append("  \t<component kind=\"SCS\" location=\"" + sdtmMappingGenerator.getScsSDTMFile() + "\"/>\n");
            }
            out.append("  \t<component kind=\"XML\" location=\"" + sdtmMappingGenerator.getScsDefineXMLFIle() + "\"/>\n");
            out.append("  </components>\n");
            for (int i = 0; i < sdtmMappingGenerator.results.size(); i++) {
                out.append("  <link>\n");
                StringTokenizer strTk = new StringTokenizer(sdtmMappingGenerator.results.get(i), "~");
                out.append("  \t<source>");
                out.append(strTk.nextToken());
                out.append("</source>\n");
                out.append("  \t<target>");
                out.append(strTk.nextToken());
                out.append("</target>\n");
                out.append("  </link>\n");
            }
            BufferedWriter out1 = new BufferedWriter(new FileWriter(file));
            out1.write(out.toString());
            out1.write("</mapping>");
            out1.close();
            // clear the change flag.
            mappingPanel.setChanged(false);
            // try to notify affected panels
            postActionPerformed(mappingPanel);
            //JOptionPane.showMessageDialog(mappingPanel.getParent(), "Mapping data has been saved successfully.", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
            //Custom button text
            if (!alreadySaved) {
                Object[] options = {"Yes", "No"};
                int n;
                if (((Database2SDTMMappingPanel) mappingPanel).isConnectDB()) {
                    //so the map file is saved successfully; now go enable the transform and dataviewer button just because a save file exists(*.map)
                    ((Database2SDTMMappingPanel) mappingPanel).get_commonBut().setEnabled(true);
                    //
                    n = JOptionPane.showOptionDialog(mappingPanel.getParent(), file.getAbsolutePath() + " is saved successfully \n Do you want to open the SQL Query builder using the \" " + file.getName() + " \" file", "Open query builder", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                    if (n == 0) {
                        //parse the mapping file
                        OpenQueryBuilder((Hashtable) getMappingsFromMapFile(file).get(0), (HashSet) getMappingsFromMapFile(file).get(1), file, out.toString());
                    }
                    alreadySaved = true;
                }
            }
            return true;
        } catch (Throwable e) {
            // restore the change value since something occurred and believe
            // the save process is aborted.
            mappingPanel.setChanged(oldChangeValue);
            // rethrow the exeception
            throw new Exception(e);
            // return false;
        } finally {
            try {
                // close buffered writer will automatically close enclosed file
                // writer.
                if (bw != null) {
                    bw.close();
                }
                mappingPanel.setSaveFile(file);
            } catch (Throwable e) {// intentionally ignored.
            }
        }
    }

    public ArrayList getMappingsFromMapFile(File mapFile) {
        ArrayList retAry = new ArrayList();
        QBParseMappingFile _qbparse = new QBParseMappingFile(mapFile);
        _qbparse.parseFile();
        retAry.add(_qbparse.getHashTable());
        retAry.add(_qbparse.getHashTblColumns());
        return retAry;
    }

    public void OpenQueryBuilder(final Hashtable list, final HashSet cols, final File file, final String out) {        
        try {
            nickyb.sqleonardo.querybuilder.QueryBuilder.getDefaultLocale();
        } catch (Error e) {
            Object[] options = {"Open Instructions page..", "Cancel..."};
            int n = JOptionPane.showOptionDialog(mainFrame, "This module is missing the JAR file and unable to continue further processing \n" +
                    "To download the jar file, please refer to the instructions", "Missing SQLeonardo.jar file...",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
            if (n == 0) {
                try {
                    BrowserLauncher.openURL("https://cabig.nci.nih.gov/tools/caAdapter");
                    return;
                } catch (UnsupportedOperatingSystemException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (BrowserLaunchingExecutionException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (BrowserLaunchingInitializingException e1) {
                    e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            } else {
                return;
            }
        }
        final Dialog d = new Dialog(mainFrame, "SQL Query", true);
        final ArrayList tempArray;
        (new Thread() {
            public void run() {
                try {
                    new MainDataViewerFrame(((Database2SDTMMappingPanel) mappingPanel).isOpenDBmap(), d, list, cols, ((Database2SDTMMappingPanel) mappingPanel).getConnectionParameters(), file, out, null, ((Database2SDTMMappingPanel) mappingPanel).getTransFormBut());
                } catch (Exception e) {
                    d.dispose();
                    JOptionPane.showMessageDialog(mainFrame, e.getMessage().toString(), "Could not open the Querybuilder", JOptionPane.ERROR_MESSAGE);
                }
                d.dispose();
            }
        }).start();
        //d = new Dialog(mainFrame, "SQL Query", true);
        JPanel pane = new JPanel();
        TitledBorder _title = BorderFactory.createTitledBorder("Visual SQL Builder");
        pane.setBorder(_title);
        pane.setLayout(new GridLayout(0, 1));
        JLabel _jl = new JLabel("SQL Query Builder Loading , please wait.....");
        pane.add(_jl);
        d.add(pane, BorderLayout.CENTER);
        d.setLocation(400, 400);
        d.setSize(500, 130);
        d.setVisible(true);
    }
}
/**
 * Change History
 * $Log: not supported by cvs2svn $
 * Revision 1.8  2007/08/16 19:39:46  jayannah
 * Reformatted and added the Comments and the log tags for all the files
 *
 */
