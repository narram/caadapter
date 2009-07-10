/**
 * <!-- LICENSE_TEXT_START -->
The contents of this file are subject to the caAdapter Software License (the "License"). You may obtain a copy of the License at the following location: 
[caAdapter Home Directory]\docs\caAdapter_license.txt, or at:
http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent/docs/caAdapter_License
 * <!-- LICENSE_TEXT_END -->
 */
package gov.nih.nci.caadapter.mms.generator;

import java.util.LinkedHashMap;

import org.jdom.Element;

import gov.nih.nci.caadapter.common.Log;
import gov.nih.nci.caadapter.common.metadata.AssociationMetadata;
import gov.nih.nci.caadapter.common.metadata.ColumnMetadata;
import gov.nih.nci.caadapter.common.metadata.ModelMetadata;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociation;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAssociationEnd;
import gov.nih.nci.ncicb.xmiinout.domain.UMLAttribute;
import gov.nih.nci.ncicb.xmiinout.domain.UMLClass;
import gov.nih.nci.ncicb.xmiinout.domain.UMLDependency;
import gov.nih.nci.ncicb.xmiinout.domain.UMLModel;
import gov.nih.nci.ncicb.xmiinout.domain.UMLTaggableElement;
import gov.nih.nci.ncicb.xmiinout.domain.UMLTaggedValue;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLAssociationEndBean;
import gov.nih.nci.ncicb.xmiinout.domain.bean.UMLDependencyBean;
import gov.nih.nci.ncicb.xmiinout.util.ModelUtil;

/**
 * Description of class definition
 *
 * @author   OWNER: wangeug  $Date: Jul 9, 2009
 * @author   LAST UPDATE: $Author: wangeug 
 * @version  REVISION: $Revision: 1.1 $
 * @date 	 DATE: $Date: 2009-07-10 19:53:51 $
 * @since caAdapter v4.2
 */

public class XMIAnnotationUtil {

	private static Log annotatationLogger=new Log();
	/**
	 * Add/update a tag value with a UMLTaggedElement<br>
	 * <ul>
	 *  <li>Add a new tag if no tag exists with given name
	 *  <li>Update the tag with new value if a tag exists with given name
	 * </ul>
	 * @param taggedElement Target to add/update tag value
	 * @param tagName Name of the tag to add/update
	 * @param tagValue Value of the tag to add/update
	 */
	public static void addTagValue(UMLTaggableElement taggedElement, String tagName, String tagValue)
	{
		UMLTaggedValue discValueTag=taggedElement.getTaggedValue(tagName);
		if (discValueTag!=null)
		{
			discValueTag.setValue(tagValue);
			annotatationLogger.logInfo(taggedElement, "...update existing tag...tagName:"+tagName +"... tagValue:"+tagValue);
		}
		else
		{
			taggedElement.addTaggedValue(tagName, tagValue);
			annotatationLogger.logInfo(taggedElement, "...create new tag...tagName:"+tagName +"... tagValue:"+tagValue);
		}
	}
	/**
	 * Create a link between an Object in Logical Model and a Table in Data ModeldataSource<br>
	 * Only one DataSource link is allowed for an object since it can only be supported by one table, <br>
	 * but more than one DataSource links are allowed for a table since it can support more than one objects. 
	 * <ul>
	 * <li>UML client: UML element of  table in Data Model package
	 * <li>UML supplier: UML element of object in Logical Model package
	 * <li>Source: table in Data Model
	 * <li>Target: object in Logical Model
	 * <li>link name: dependency
	 * <li>Direction: Source -> Destination
	 * <li>Stereotype: DataSource
	 * </ul><br>
	 * <hr>
	 * Since a correlation table never work as the data source of any object in the Logical Model, 
	 * all the correlation-table tags with value of the target table should be cleared once the table is assigned as data source of an object
	 * @param umlModel The target UML model
	 * @param tablePath The full path of a table in the Data Model
	 * @param objectPath The full path of an Object in the Logical Model
	 * @return <ul> 
	 * 			<li>true if new dependency is successfully created.
	 * 			<li>false if a dependency exist or failed with other reason
	 */
	public static boolean addDataObjectDependency(UMLModel umlModel, String tablePath, String objectPath )
	{
		UMLClass client = ModelUtil.findClass(umlModel,tablePath);
	    UMLClass supplier = ModelUtil.findClass(umlModel,objectPath);
	    
	    System.out.println("XMIAnnotationUtil.addDataObjectDependency()..table:"+tablePath);
	    System.out.println("XMIAnnotationUtil.addDataObjectDependency()..object:"+objectPath);
	    if (client==null||supplier==null)
	    	return false;
	    
	    //check if a dependency exist for the object
	    for(UMLDependency existDep:supplier.getDependencies())
	    {
	    	if (existDep.getStereotype().equalsIgnoreCase("DataSource"))
	    	{
	    		annotatationLogger.logWarning(supplier,"...DataSource link exists..Logical Model.Object:"+supplier.getName()+"..Data Model.Table:"+client.getName());
	    		return false;
	    	}
	    }
		UMLDependency dep =umlModel.createDependency(client, supplier, "dependency");
		annotatationLogger.logInfo(dep, "...Logical Model.Object:"+supplier.getName() +"... Data Model.Table:"+client.getName());		
		dep = umlModel.addDependency( dep );	    
	    dep.addStereotype("DataSource");
	    dep.addTaggedValue("stereotype", "DataSource");
	    dep.addTaggedValue("ea_type", "Dependency");
	    dep.addTaggedValue("direction", "Source -> Destination");
	    dep.addTaggedValue("style", "3");
	    dep.addTaggedValue("ea_sourceName", client.getName());
	    dep.addTaggedValue("ea_targetName", supplier.getName());
	    
	    //check if any column of the target has been mapped to
	    //an association end in the Logical Model, the target table could
	    //be used as "correlation-table" of the association. 
	    //The "correlation-table" tag should be removed
	    for (UMLAttribute tblColumn:client.getAttributes())
	    {
	    	UMLTaggedValue asscMappingTag=tblColumn.getTaggedValue("implements-association");
	    	if (asscMappingTag!=null)
	    	{
	    		String asscClearPath=asscMappingTag.getValue();
	    		ModelMetadata modelMeta=CumulativeMappingGenerator.getInstance().getMetaModel();
	    		String asscFullPath=modelMeta.getMmsPrefixObjectModel()+"."+asscClearPath;
	    		AssociationMetadata asscMeta=(AssociationMetadata)modelMeta.getModelMetadata().get(asscFullPath);
	    		if (asscMeta!=null)
	    		{
	    			UMLAssociation umlAssc=asscMeta.getUMLAssociation();
	    			UMLTaggedValue crlTag=umlAssc.getTaggedValue("correlation-table");
	    			if (crlTag!=null&&crlTag.getValue().equalsIgnoreCase(client.getName()));
	    				removeTagValue(umlAssc, crlTag.getName());
	    		}
	    		
	    	}
	    	
	    }
		return true;
	}
	
	/**
	 * Remove a UML tag from an element
	 * @param taggedElement
	 * @param tagName
	 * @return
	 */
	public static boolean removeTagValue(UMLTaggableElement taggedElement, String tagName)
	{
		UMLTaggedValue discValueTag=taggedElement.getTaggedValue(tagName);
		if (discValueTag!=null)
		{
			taggedElement.removeTaggedValue(tagName);
 			annotatationLogger.logInfo(taggedElement, "...remove tag...tagName:"+tagName +"... tagValue:"+discValueTag.getValue());
 			return true;
		}
		else
			annotatationLogger.logInfo(taggedElement, "...missing tag...tagName:"+tagName );

		return false;
	}
	
	/**
	 * Delete the implements-association and correlation-table annotation tags for an association mapping  
	 * <ul>
	 *  <li> always delete association mapping tag:implements-association
	 *  <li> delete correlation-table tag if the association is uni-directional
	 *  <li> delete correlation-table tag if the association is uni-directional and the other end is not mapped
	 *</ul>
	 * @param umlModel The target UML model
	 * @param sourcePath The full path of an Object in the Logical Model
	 * @param targetPath The full path of a table in the Data Model
	 * @return <ul> 
	 * 			<li>true if the association mapping is successfully created.
	 * 			<li>false if he association mapping exist or failed with other reason
	 */
	public static boolean deAnnotateAssociationMapping(UMLModel umlModel, String sourcePath, String targetPath)
	{
		//remove "mapped-attributes" tag from UMLModel
		UMLAttribute xpathAttr=ModelUtil.findAttribute(umlModel,targetPath);
		xpathAttr.removeTaggedValue("implements-association");
		ModelMetadata modelMeta=CumulativeMappingGenerator.getInstance().getMetaModel();
		AssociationMetadata asscMeta=(AssociationMetadata)modelMeta.getModelMetadata().get(sourcePath);
		if (asscMeta==null)
			return false;
		
		UMLAssociation umlAssc=asscMeta.getUMLAssociation();
		//check if the association is uni-directional
		UMLAssociationEnd endOne=(UMLAssociationEnd)umlAssc.getAssociationEnds().get(0);
		UMLAssociationEnd endTwo=(UMLAssociationEnd)umlAssc.getAssociationEnds().get(1);
		if (endOne.getRoleName()==null|endOne.getRoleName().equals("")
				|endTwo.getRoleName()==null|endTwo.getRoleName().equals(""))
			return removeTagValue(umlAssc, "correlation-table");
		
		UMLAssociationEndBean otherEnd=(UMLAssociationEndBean)endOne;
		UMLAssociationEndBean thisEnd=(UMLAssociationEndBean)endTwo;
		
		if (endTwo.getRoleName().equals(asscMeta.getRoleName()))
		{
			otherEnd=(UMLAssociationEndBean)endTwo;
			thisEnd=(UMLAssociationEndBean)endOne;
		}
		String otherEndFullPath= ModelUtil.getFullName((UMLClass)otherEnd.getUMLElement())+"."+thisEnd.getRoleName();	
		ColumnMetadata targetColumn=(ColumnMetadata)modelMeta.getModelMetadata().get(targetPath);
		if (targetColumn==null)
			return false;
		
		UMLClass targetTbl=ModelUtil.findClass(umlModel, targetColumn.getParentXPath());
		if (targetTbl==null)
			return false;
		System.out.println("XMIAnnotationUtil.deAnnotateAssociationMapping()..the other end:"+otherEndFullPath);
		String otherEndClearPath=getCleanPath(modelMeta.getMmsPrefixObjectModel(), otherEndFullPath);
		for(UMLAttribute tblColumnAttr:targetTbl.getAttributes())
		{
			UMLTaggedValue oneAttrAsscTag=tblColumnAttr.getTaggedValue("implements-association");

			if (oneAttrAsscTag!=null &&oneAttrAsscTag.getValue().equalsIgnoreCase(otherEndClearPath))
			{
				annotatationLogger.logInfo(oneAttrAsscTag, "..the other end is mapped:"+otherEndClearPath+"..."+tblColumnAttr.getName());
				return false;
			}
		}
		//the association end is the only mapped end, remove the correlation tag
		return removeTagValue(umlAssc, "correlation-table");
	}
	
	/**
	 * Add/update the implements-association and correlation-table annotation tags for an association mapping  
	 * <ul>
	 *  <li> always add association mapping tag:implements-association
	 *  <li> determine if a correlation-table is used.
	 *  <ul>
	 *  	<li>A correlation-table can not work as the data source of any object
	 *  	<li>Add a correlation-table tag when the first end of an association is mapped
	 *  	<li>Update/delete the correlation-table tag when the second association end is mapped
	 *		<li>All the correlation-table tag should be cleared once the table is assigned as data source of an object
	 *  </ul>
	 *  <li> add correlation-table tag if not exist and required:correlation-table
	 *  <li> update correlation-table tag if exist and required:correlation-table
	 *</ul>
	 * @param umlModel The target UML model
	 * @param sourcePath The full path of an Object in the Logical Model
	 * @param targetPath The full path of a table in the Data Model
	 * @return <ul> 
	 * 			<li>true if the association mapping is successfully created.
	 * 			<li>false if he association mapping exist or failed with other reason
	 */
	public static boolean annotateAssociationMapping(UMLModel umlModel, String sourcePath, String targetPath)
	{
		UMLAttribute tblColumn=ModelUtil.findAttribute(umlModel, targetPath);
		ModelMetadata modelMeta=CumulativeMappingGenerator.getInstance().getMetaModel();
		//remove the leading string:"Logical View.Logical Model." from source path
		String pureSrcPath=XMIAnnotationUtil.getCleanPath(modelMeta.getMmsPrefixObjectModel(),  sourcePath);
		XMIAnnotationUtil.addTagValue(tblColumn, "implements-association", pureSrcPath);
		
		LinkedHashMap modelMetaHash =modelMeta.getModelMetadata();
		ColumnMetadata columnMeta =(ColumnMetadata)modelMetaHash.get(targetPath);
		if (columnMeta==null)
			return false;
		String tblPath=columnMeta.getTableMetadata().getXPath();
		
		UMLClass tblClass=ModelUtil.findClass(umlModel, tblPath);
		//check if table is the data source of any object
		for(UMLDependency existDep:tblClass.getDependencies())
	    {
	    	if (existDep.getStereotype().equalsIgnoreCase("DataSource"))
	    	{
	    		annotatationLogger.logInfo(existDep, "...DataSource link exist...target table:"+((UMLClass)existDep.getClient()).getName()+" is the datasource of object:"+((UMLClass)existDep.getSupplier()).getName());
	    		return false;
	    	}
	    }	
		//check the association end in the Logical Model
		AssociationMetadata asscMeta=(AssociationMetadata)modelMetaHash.get(sourcePath);
		UMLAssociation umlAssc=asscMeta.getUMLAssociation();
				
		addTagValue(umlAssc,"correlation-table",tblClass.getName());
		return true;
	}
	/**
	 * Remove the Data Source dependency from an object in the Logical Model
	 * 
	 * @param umlModel
	 * @param objectPath
	 * @return <ul> 
	 * 			<li>true if the dependency is successfully removed.
	 * 			<li>false if the dependency  does not exist or failed to remove with other reason
	 */
	public static boolean removeDataObjectDependency(UMLModel umlModel, String objectPath)
	{
		UMLClass supplier = ModelUtil.findClass(umlModel,objectPath);
	    if (supplier==null)
	    	return false;
	    //check if a dependency exist for the object
	    UMLDependency foundDep=null;
	    UMLClass client=null;
	    for(UMLDependency existDep:supplier.getDependencies())
	    {
	    	if (existDep.getStereotype().equalsIgnoreCase("DataSource"))
	    	{
	    		client=(UMLClass)existDep.getClient();
	    		foundDep=existDep;
	    	}
	    }
	    if (foundDep==null)
	    	return false;
	    //remove the dependency from list of dependency associated with model
	    annotatationLogger.logInfo(foundDep, "Dependency deleted from model...Logical Model.Object:"+supplier.getName() +"... Data Model.Table:"+client.getName());   		
	    supplier.getDependencies().remove(foundDep);
	    client.getDependencies().remove(foundDep);
	    umlModel.getDependencies().remove(foundDep); 
	    //remove the JDomElement from Model 	    
	    Element depElt=((UMLDependencyBean)foundDep).getJDomElement();
	    annotatationLogger.logInfo(foundDep, "Remove dependency section from XMI file....Logical Model.Object:"+supplier.getName() +"... Data Model.Table:"+client.getName());
	    depElt.getParentElement().removeContent(depElt);//childElmnt);
   
		return true;
	}
	
	/**
	 * @param grossPath
	 * @return cleanPath
	 */
    public static String getCleanPath(String leading, String grossPath)
    {
		String cleanPath = null;
		if (grossPath.startsWith( leading ))
		{
		    cleanPath = grossPath.replaceAll(leading + ".", "" );
		}
		return cleanPath;
	}
}



/**
* HISTORY: $Log: not supported by cvs2svn $
**/