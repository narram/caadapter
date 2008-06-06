/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
 
package gov.nih.nci.caadapter.common.map;


import java.awt.*;

/**
 * Interface for graphical view information.
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: phadkes $
 * @since     caAdapter v1.2
 * @version    $Revision: 1.3 $
 * @date        $Date: 2008-06-06 18:54:28 $
 */

public interface View {
    public boolean isVisible();
    public int getX();
    public int getY();
    public int getHeight();
    public int getWidth();
    public Color getColor();
	public String getComponentId() ;
	public void setComponentId(String componentId);
}
