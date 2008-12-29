/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */


package gov.nih.nci.cbiit.cmps.ui.properties;


import gov.nih.nci.cbiit.cmps.ui.util.GeneralUtilities;

import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * This class provides basic functions to help update properties information along user's selection.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: linc $
 * @since     CMPS v1.0
 * @version    $Revision: 1.1 $
 * @date       $Date: 2008-12-29 22:18:18 $
 */
public class DefaultPropertiesSwitchController implements PropertiesSwitchController, TreeSelectionListener, FocusListener
{

	private static final String DEFAULT_TITLE = "Properties";

	private Object selectedItem;

	private DefaultPropertiesPage propertiesPage;

	public DefaultPropertiesSwitchController()
	{
	}

	/**
	 * Called whenever the value of the selection changes.
	 *
	 * @param e the event that characterizes the change.
	 */
	public void valueChanged(TreeSelectionEvent e)
	{
		TreePath newPath = e.getNewLeadSelectionPath();
		if(newPath==null)
		{
			setSelectedItem(null);
			ChangeEvent changeEvent = new ChangeEvent(this);
			notifyPropertiesPageSelectionChanged(changeEvent);
		}
		else
		{
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) newPath.getLastPathComponent();
			Object newSelection = treeNode.getUserObject();
			setSelectedItem(newSelection);
			ChangeEvent changeEvent = new ChangeEvent(this);
			notifyPropertiesPageSelectionChanged(changeEvent);
		}
	}

	protected void notifyPropertiesPageSelectionChanged(ChangeEvent e)
	{
		 propertiesPage.updateProptiesDisplay(e);
	}

	public DefaultPropertiesPage getPropertiesPage()
	{
		return propertiesPage;  //To change body of implemented methods use File | Settings | File Templates.
	}

	public void setPropertiesPage(DefaultPropertiesPage newProperitesView)
	{
		this.propertiesPage = newProperitesView;
	}

	/**
	 * This functions will return an array of PropertyDescriptor that would
	 * help Properties GUI to figure out what column information would be
	 * displayed in the View.
	 */
	public PropertiesResult getPropertyDescriptors()
	{
		PropertiesResult result = new PropertiesResult();
		if(selectedItem instanceof PropertiesProvider)
		{
			try
			{
				PropertiesResult localResult = ((PropertiesProvider) selectedItem).getPropertyDescriptors();
				if(localResult!=null)
				{
					result = localResult;
				}
			}
			catch(Throwable e)
			{
				//Log.logException(this, e);
				e.printStackTrace();
				System.out.println("getPropertyDescriptors() received: '" + e + "'.");
			}
		}
		return result;
	}

	public Object getSelectedItem()
	{
		return selectedItem;
	}

	/**
	 * To faciliate sub-classes to set new selected item from different occassions.
	 * @param newSelectedItem
	 */
	protected void setSelectedItem(Object newSelectedItem)
	{
		if(!GeneralUtilities.areEqual(newSelectedItem, selectedItem))
		{
			selectedItem = newSelectedItem;
		}
	}

	public String getTitleOfPropertiesPage()
	{
		String result = null;
		if (selectedItem instanceof PropertiesProvider)
		{
			try
			{
				result = ((PropertiesProvider) selectedItem).getTitle();
			}
			catch (Exception e)
			{
//				System.out.println("getPropertyDescriptors() received: '" + e + "'. I will continue...");
				e.printStackTrace();
				//Log.logException(this, "I will continue...", e);
				result = null;
			}
		}

		if(result==null)
		{
			result = DEFAULT_TITLE;
		}
		return result;
	}

	/**
	 * Invoked when a component gains the keyboard focus.
	 */
	public void focusGained(FocusEvent e)
	{
//		System.out.println(e.getSource() + " Gained focus.");
	}

	/**
	 * Invoked when a component loses the keyboard focus.
	 */
	public void focusLost(FocusEvent e)
	{
//		System.out.println(e.getSource() + " Lost focus.");
	}
}

/**
 * HISTORY      : $Log: not supported by cvs2svn $
 */
