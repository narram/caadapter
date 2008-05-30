/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/userInterface/src/gov/nih/nci/caadapter/ui/mapping/V2V3/actions/V2V3MapAction.java,v 1.4 2008-05-30 01:03:53 umkis Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 3.2
 * Copyright Notice.
 * 
 * Copyright 2006 SAIC. This software was developed in conjunction with the National Cancer Institute. To the extent government employees are co-authors, any rights in such works are subject to Title 17 of the United States Code, section 105. 
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the Copyright Notice above, this list of conditions, and the disclaimer of Article 3, below. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution. 
 * 
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * 
 * 
 * "This product includes software developed by the SAIC and the National Cancer Institute."
 * 
 * 
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself, wherever such third-party acknowledgments normally appear. 
 * 
 * 3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or promote products derived from this software. 
 * 
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize the recipient to use any trademarks owned by either NCI or SAIC-Frederick. 
 * 
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE UNITED STATES GOVERNMENT, THE NATIONAL CANCER INSTITUTE, SAIC, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.ui.mapping.V2V3.actions;

import edu.knu.medinfo.hl7.v2tree.MetaDataLoader;
import gov.nih.nci.caadapter.common.util.CaadapterUtil;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.common.util.FileUtil;
import gov.nih.nci.caadapter.ui.common.AbstractMainFrame;
import gov.nih.nci.caadapter.ui.common.DefaultSettings;
import gov.nih.nci.caadapter.ui.common.actions.AbstractContextAction;
import gov.nih.nci.caadapter.ui.mapping.V2V3.V2ConverterToSCSPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;


/**
 * This class creates a Menu item Create V2 V3 action and assigns the action to it.
 *
 * @author OWNER: Harsha Jayanna
 * @author LAST UPDATE $Author: umkis $
 * @version Since caAdapter v3.2
 *          revision    $Revision: 1.4 $
 *          date        $Date: 2008-05-30 01:03:53 $
 */
public class V2V3MapAction extends AbstractContextAction  {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final String COMMAND_NAME = "HL7 v2 to SCS & CSV Conversion";

    private static final Character COMMAND_MNEMONIC = new Character('P');

    private static final KeyStroke ACCELERATOR_KEY_STROKE = KeyStroke.getKeyStroke(KeyEvent.VK_1, Event.CTRL_MASK, false);

    private AbstractMainFrame mainFrame;

    /**
         * Defines an <code>Action</code> object with a default description
         * string and default icon.
         */
    public V2V3MapAction(AbstractMainFrame mainFrame) {
	this(COMMAND_NAME, mainFrame);
	// mainContextManager = cm;
    }

    /**
         * Defines an <code>Action</code> object with the specified
         * description string and a default icon.
         */
    public V2V3MapAction(String name, AbstractMainFrame mainFrame) {
	this(name, null, mainFrame);
    }

    /**
         * Defines an <code>Action</code> object with the specified
         * description string and a the specified icon.
         */
    public V2V3MapAction(String name, Icon icon, AbstractMainFrame mainFrame) {
	super(name, icon);
	this.mainFrame = mainFrame;
	setMnemonic(COMMAND_MNEMONIC);
	setAcceleratorKey(ACCELERATOR_KEY_STROKE);
	setActionCommandType(DESKTOP_ACTION_TYPE);
	// do not know how to set the icon location name, or just do not
	// matter.
    }

    /**
         * The abstract function that descendant classes must be overridden to
         * provide customsized handling.
         * 
         * @param e
         * @return true if the action is finished successfully; otherwise,
         *         return false.
         */
    protected boolean doAction(ActionEvent e) throws Exception {
		if (!super.isRequestAuthorized(mainFrame))
		{
			setSuccessfullyPerformed(false);
			return isSuccessfullyPerformed();
		}
//		verify resource
		if (!isResourceReady(mainFrame))
		{
			setSuccessfullyPerformed(false);
			return isSuccessfullyPerformed();
		}
        //new MapV2V3(mainFrame);
        //new MapV2V3(mainFrame, "");
        MetaDataLoader loader = FileUtil.getV2ResourceMetaDataLoader();
        int check = -1;
        if (loader == null)
        {
            check = JOptionPane.showConfirmDialog(mainFrame, "v3 meta resource zip file isn't exist.\n Do you have another v2 meta source?",
                                                      "No v2 resource zip file", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (check != JOptionPane.YES_OPTION)
            {
                setSuccessfullyPerformed(false);
                return isSuccessfullyPerformed();
            }
        }

        try
        {
            V2ConverterToSCSPanel v2ConverterPanel = null;
            if (check == JOptionPane.YES_OPTION)
            {
                v2ConverterPanel = new V2ConverterToSCSPanel(null);
            }
            else v2ConverterPanel = new V2ConverterToSCSPanel(loader);
            JDialog dialog = v2ConverterPanel.setupDialogBasedOnMainFrame(mainFrame);
            v2ConverterPanel.setNextButtonVisible();
            v2ConverterPanel.setCloseButtonVisible();
            dialog.setSize(v2ConverterPanel.getMinimumSize());
            dialog.setVisible(true);
            DefaultSettings.centerWindow(dialog);
        }
        catch (Exception e1)
        {
            JOptionPane.showMessageDialog(mainFrame, "Error : " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            setSuccessfullyPerformed(false);
            return isSuccessfullyPerformed();
        }

//        V2ConverterToSCSPanel v2ConverterPanel = new V2ConverterToSCSPanel(FileUtil.getV2DataDirPath());
//        JDialog dialog = v2ConverterPanel.setupDialogBasedOnMainFrame(mainFrame);
//        v2ConverterPanel.setNextButtonVisible();
//        v2ConverterPanel.setCloseButtonVisible();
//        dialog.setSize(v2ConverterPanel.getMinimumSize());
//        dialog.setVisible(true);
//        DefaultSettings.centerWindow(dialog);
	    setSuccessfullyPerformed(true);
	    return isSuccessfullyPerformed();
    }

    /**
         * Return the associated UI component.
         * 
         * @return the associated UI component.
         */
    protected Component getAssociatedUIComponent() {
	return mainFrame;
    }

	@Override
	protected ArrayList getMissedResources() {
		// TODO Auto-generated method stub
		return CaadapterUtil.getModuleResourceMissed(Config.CAADAPTER_HL7_V2V3_CONVERSION_RESOURCE_REQUIRED);
	}
}
/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.3  2007/10/04 18:09:48  wangeug
 * HISTORY      : verify resource based on module
 * HISTORY      :
 * HISTORY      : Revision 1.2  2007/09/19 16:42:37  wangeug
 * HISTORY      : authorized user request
 * HISTORY      :
 * HISTORY      : Revision 1.1  2007/07/03 19:32:58  wangeug
 * HISTORY      : initila loading
 * HISTORY      :
 * HISTORY      : Revision 1.7  2006/11/28 15:13:59  jayannah
 * HISTORY      : Changed the order and names of the menuitems
 * HISTORY      :
 * HISTORY      : Revision 1.6  2006/11/27 21:47:51  jayannah
 * HISTORY      : *** empty log message ***
 * HISTORY      :
 * HISTORY      : Revision 1.5  2006/11/10 04:01:44  umkis
 * HISTORY      : Switch MapV2V3 to V2Converter to SCS Panel as running module.
 * HISTORY      :
 * HISTORY      : Revision 1.4  2006/11/08 15:44:53  wuye
 * HISTORY      : Main Menu Re-Org
 * HISTORY      :
 * HISTORY      : Revision 1.3  2006/10/30 16:31:42  wuye
 * HISTORY      : Updated the menu item
 * HISTORY      :
 * HISTORY      : Revision 1.2  2006/10/03 13:50:47  jayannah
 * HISTORY      : This class creates a Menu item Create V2 V3 action and assigns the action to it
 * HISTORY      :
 * HISTORY      : Revision 1.1  2006/10/03 13:46:57  jayannah
 * HISTORY      : This class creates a Menu item Create V2 V3 action and assigns the action to it.
 * HISTORY      :
 */
