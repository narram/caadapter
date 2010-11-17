package gov.nih.nci.cbiit.cdms.formula.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

 

public class MenuBarMain extends JMenuBar implements ActionListener {

	public MenuBarMain()
	{
		super();
		add(createNewMenu());
		add(createExcuteMenu());
		add(createHelpMenu());
	}
 
	private JMenu createNewMenu()
	{
		JMenu rtnMenu=new JMenu ("File");
		
		JMenuItem newItem=new JMenuItem("New Formula");
		rtnMenu.add(newItem);
		
		JMenuItem openItem=new JMenuItem("Open Formula");
		rtnMenu.add(openItem);
		JMenuItem saveItem=new JMenuItem("Save Formual");
		rtnMenu.add(saveItem);
		JMenuItem saveAsItem=new JMenuItem("Save As ...");
		rtnMenu.add(saveAsItem);
		rtnMenu.addSeparator();
		JMenuItem closeItem=new JMenuItem("Close");
		rtnMenu.add(closeItem);
		JMenuItem exitItem=new JMenuItem("Exit");
		exitItem.addActionListener(this);
		rtnMenu.add(exitItem);
		return rtnMenu;
		
	}
	
	private JMenu createExcuteMenu()
	{
		JMenu rtnMenu=new JMenu ("Run");
		
		JMenuItem excItem=new JMenuItem("Execute Formula");
		rtnMenu.add(excItem);
		return rtnMenu;
		
	}
	
	private JMenu createHelpMenu()
	{
		JMenu rtnMenu=new JMenu ("Help");
		
		JMenuItem abtItem=new JMenuItem("About Us..");
		rtnMenu.add(abtItem);
		return rtnMenu;
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		Component comp=(Component)arg0.getSource();
		if (arg0.getActionCommand().contains("Exit"));
		{
			System.exit(-1);
		}
		

	}
}