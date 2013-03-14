/*L
 * Copyright SAIC.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/caadapter/LICENSE.txt for details.
 */

/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */


package gov.nih.nci.cbiit.cmts.ui.common;

import gov.nih.nci.cbiit.cmts.ui.util.GeneralUtilities;
import gov.nih.nci.cbiit.cmts.ui.main.MainFrameContainer;

import javax.swing.JFrame;
import javax.swing.Action;
import javax.swing.JOptionPane;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.Iterator;
import java.awt.*;


/**
 * This class defines a list of functions to facilitate the management and synchronization
 * of files that may be simultaneously accessed across different context client, such as
 * an SCM file being used at SCM and Map panel.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMTS v1.0
 * @version    $Revision: 1.2 $
 * @date       $Date: 2008-12-09 19:04:17 $
 */
public class ContextFileManager
{
    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: ContextFileManager.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/cmts/src/gov/nih/nci/cbiit/cmts/ui/common/ContextFileManager.java,v 1.2 2008-12-09 19:04:17 linc Exp $";

    private Map <File, java.util.Set<ContextManagerClient>> fileUsageMap = null;

    //private ContextManager contextManager;
    JFrame contextManagerOwner = null;
    MainFrameContainer mainFrame = null;

    public ContextFileManager(JFrame myOwner)//ContextManager contextManager)
    {
        //this.contextManager = contextManager;
        contextManagerOwner=myOwner;
        initilizeFileUsageMap();
    }
    public ContextFileManager(MainFrameContainer myOwner)//ContextManager contextManager)
        {
            //this.contextManager = contextManager;
            mainFrame = myOwner;
            initilizeFileUsageMap();
        }

    private void initilizeFileUsageMap()
    {
        if(fileUsageMap!=null)
        {
            fileUsageMap.clear();
        }
        else
        {
            fileUsageMap = new HashMap<File, java.util.Set<ContextManagerClient>>();
        }
    }

    /**
     * Register a given ContextManagerClient to associate with the given file.
     * @param file
     * @param usageClient
     */
    public void registerFileUsageListener(File file, ContextManagerClient usageClient)
    {
        if(file==null || usageClient==null)
        {
            return;
        }
//		Log.logInfo(this, "To Add: File:'"+file.getName()+"',context='"+DefaultSettings.getClassNameWithoutPackage(usageClient.getClass())+"'.");
        Set<ContextManagerClient> usageList = fileUsageMap.get(file);
        if(usageList==null)
        {//nothing found
            usageList = new LinkedHashSet<ContextManagerClient>();
            fileUsageMap.put(file, usageList);
        }
        usageList.add(usageClient);
    }

    /**
     * Register a given ContextManagerClient to associate with the given file.
     * @param usageClient
     */
    public void registerFileUsageListener(ContextManagerClient usageClient)
    {
        if(usageClient==null)
        {
            return;
        }
        List fileList = usageClient.getAssociatedFileList();
        int size = (fileList==null)? 0 : fileList.size();
        for(int i=0; i<size; i++)
        {
            File file = (File) fileList.get(i);
            registerFileUsageListener(file, usageClient);
        }
    }

    /**
     * Remove the given ContextManagerClient from the association of the given file.
     * @param file
     * @param usageClient
     */
    public void removeFileUsageListener(File file, ContextManagerClient usageClient)
    {
        if(file==null || usageClient==null)
        {
            return;
        }
//		Log.logInfo(this, "To Remove: File:'"+file.getName()+"',context='"+DefaultSettings.getClassNameWithoutPackage(usageClient.getClass())+"'.");
        Set<ContextManagerClient> usageList = fileUsageMap.get(file);
        if(usageList!=null)
        {//find the list
            usageList.remove(usageClient);
        }
    }

    /**
     * Remove the given ContextManagerClient from the association of the files.
     * @param usageClient
     */
    public void removeFileUsageListener(ContextManagerClient usageClient)
    {
        if(usageClient==null)
        {
            return;
        }
        List fileList = usageClient.getAssociatedFileList();
        int size = (fileList==null)? 0 : fileList.size();
        for(int i=0; i<size; i++)
        {
            File file = (File) fileList.get(i);
            removeFileUsageListener(file, usageClient);
        }
    }

    public void clearFileCache()
    {
        initilizeFileUsageMap();
    }

    /**
     * Execute the transition examination logic and determine the panel flow.
     * @param contextManagerClient
     * @return true if it is good to transition from the given contextManagerClient to another;
     * false, intends to stay on current client, i.e. veto the transition;
     */
    public boolean transitAwayFrom(ContextManagerClient contextManagerClient)
    {
        if(contextManagerClient==null)
        {
            return true;
        }

        boolean result = true;
        if(contextManagerClient.isChanged())
        {
            Component ownerC = null;
            if (contextManagerOwner != null) ownerC = contextManagerOwner;
            else ownerC = mainFrame.getAssociatedUIComponent();
            int userChoice = JOptionPane.showConfirmDialog(ownerC, //contextManager.getMainFrame(),
                    "Content has been changed on this panel. Would you like to save it before switching away?", "Question", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if(userChoice!=JOptionPane.OK_OPTION)
            {
                return false;
            }
            Action action = contextManagerClient.getDefaultSaveAction();
            if(action!=null && action.isEnabled())
            {
                action.actionPerformed(null);
            }
            //notify will not be called, but by explict calling or SaveAsHL7V3MessageAction.
            result = true;
        }

        return result;
    }

    /**
     * To notify those affected context clients, if any, which have interest in the files that are associated with the given contextManagerClient.
     * @param contextManagerClient
     */
    public void notifyAffectContextMangerClients(ContextManagerClient contextManagerClient)
    {
        /**
         * Design Rationale:
         * 1) Given the contextManagerClient, for each associated file in the list, find the associated context client in the fileUsageMap;
         * 2) If the item in the list from the map is not the same as the contextManagerClient, add it to the notification Map and its name in the list for user opinion;
         * 3) Display the list of affected context;
         * 4) If user would like to reload all of them, do so, otherwise, just simply ignore;
         */
        if(contextManagerClient==null)
        {
            return;
        }
        //key: the name of the contextClient, value: the contextClient itself.
        Map<String,ContextManagerClient> associatedClientMap = new HashMap<String,ContextManagerClient>();
        List fileList = contextManagerClient.getAssociatedFileList();
        int size = fileList==null? 0: fileList.size();
        for(int i=0; i<size; i++)
        {
            File file = (File) fileList.get(i);
            Set<ContextManagerClient> contexClientList = this.fileUsageMap.get(file);
            if(contexClientList == null) return;
            Iterator it = contexClientList.iterator();
            while(it.hasNext())
            {
                ContextManagerClient localClient = (ContextManagerClient) it.next();
                if(!GeneralUtilities.areEqual(localClient, contextManagerClient))
                {
                    String className = DefaultSettings.getClassNameWithoutPackage(localClient.getClass());
                    associatedClientMap.put(className, localClient);
                }
            }
        }

        if(associatedClientMap.isEmpty())
        {//if no association, just return.
            return;
        }
//		ContextManagerClientReloadDialog dialog = new ContextManagerClientReloadDialog(contextManagerOwner //contextManager.getMainFrame()
//				, associatedClientMap);
//		DefaultSettings.centerWindow(dialog);
//		dialog.setModal(true);
//		dialog.setVisible(true);
    }
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2008/12/03 20:46:14  linc
 * HISTORY      : UI update.
 * HISTORY      :
 */
