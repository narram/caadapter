/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.ui.mapping;

import org.jgraph.graph.GraphModel;

import gov.nih.nci.cbiit.cmts.ui.common.DefaultSettings;
import gov.nih.nci.cbiit.cmts.ui.jgraph.MiddlePanelGraphModel;
import gov.nih.nci.cbiit.cmts.ui.jgraph.MiddlePanelJGraph;
import gov.nih.nci.cbiit.cmts.ui.jgraph.MiddlePanelJGraphController;
import gov.nih.nci.cbiit.cmts.ui.jgraph.MiddlePanelJGraphScrollAdjustmentAdapter;
import gov.nih.nci.cbiit.cmts.ui.jgraph.MiddlePanelScrollAdjustmentCoordinator;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.ScrollPaneConstants;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.BorderLayout;

/**
 * The panel is used to render graphical respresentation of the mapping relations between
 * source and target tree panel.
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMPS v1.0
 * @version    $Revision: 1.5 $
 * @date       $Date: 2009-11-04 19:11:59 $
 *
 */
public class MappingMiddlePanel extends JPanel
{
	private MiddlePanelScrollAdjustmentCoordinator adjustmentCoordinator = null;
	private MiddlePanelJGraph graph = null;

	private MiddlePanelJGraphScrollAdjustmentAdapter graphAdjustmentAdapter = null;

	private CmpsMappingPanel mappingPanel = null;
	private JScrollPane graphScrollPane = new JScrollPane();
	public MappingMiddlePanel(CmpsMappingPanel mappingPane)
	{
		super();
		mappingPanel = mappingPane;
		setBorder(BorderFactory.createRaisedBevelBorder());
		setLayout(new BorderLayout());
		setSize(new Dimension((DefaultSettings.FRAME_DEFAULT_WIDTH / 3), (int) (DefaultSettings.FRAME_DEFAULT_HEIGHT / 1.5)));
		// initialize graph
		GraphModel model = new MiddlePanelGraphModel();
		graph = new MiddlePanelJGraph(model);
		graphScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		graphScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		graphScrollPane.getViewport().setView(graph);
		MappingPanelAdjustmentHandler listener=new MappingPanelAdjustmentHandler();
		graphScrollPane.getVerticalScrollBar().addAdjustmentListener(listener);
		add(graphScrollPane, BorderLayout.CENTER);
		adjustmentCoordinator = new MiddlePanelScrollAdjustmentCoordinator(this, graphScrollPane);
		graphAdjustmentAdapter = new MiddlePanelJGraphScrollAdjustmentAdapter(mappingPanel);
    }
	public MiddlePanelScrollAdjustmentCoordinator getAdjustmentCoordinator()
	{
		return adjustmentCoordinator;
	}

	/**
	 * @return the graph
	 */
	public MiddlePanelJGraph getGraph() {
		return graph;
	}

	public MiddlePanelJGraphScrollAdjustmentAdapter getGraphAdjustmentAdapter()
	{
		return graphAdjustmentAdapter;
	}

	public MiddlePanelJGraphController getGraphController(){
        return mappingPanel.getGraphController();// graphController;
    }

	/**
	 * @return the graphScrollPane
	 */
	public JScrollPane getGraphScrollPane() {
		return graphScrollPane;
	}

	public CmpsMappingPanel getMappingPanel()
	{
		return mappingPanel;
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
	
		mappingPanel.getGraphController().renderInJGraph(g);
	}
}
/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.4  2009/11/03 18:32:26  wangeug
 * HISTORY: clean codes: keep MiddlePanelJGraphController only with MiddleMappingPanel
 * HISTORY:
 * HISTORY: Revision 1.3  2009/10/30 14:45:09  wangeug
 * HISTORY: simplify code: only respond to link highter
 * HISTORY:
 * HISTORY: Revision 1.2  2008/12/04 21:34:20  linc
 * HISTORY: Drap and Drop support with new Swing.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/10/30 16:02:14  linc
 * HISTORY: updated.
 * HISTORY:
 */