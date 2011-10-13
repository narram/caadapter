/**
 * The content of this file is subject to the caAdapter Software License (the "License").  
 * A copy of the License is available at:
 * [caAdapter CVS home directory]\etc\license\caAdapter_license.txt. or at:
 * http://ncicb.nci.nih.gov/infrastructure/cacore_overview/caadapter/indexContent
 * /docs/caAdapter_License
 */
package gov.nih.nci.cbiit.cmts.mapping;

import java.io.File;
import java.math.BigInteger;
import java.util.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import gov.nih.nci.cbiit.cmts.common.XSDParser;
import gov.nih.nci.cbiit.cmts.core.AttributeMeta;
import gov.nih.nci.cbiit.cmts.core.BaseMeta;
import gov.nih.nci.cbiit.cmts.core.Component;
import gov.nih.nci.cbiit.cmts.core.ComponentType;
import gov.nih.nci.cbiit.cmts.core.ElementMeta;
import gov.nih.nci.cbiit.cmts.core.KindType;
import gov.nih.nci.cbiit.cmts.core.LinkType;
import gov.nih.nci.cbiit.cmts.core.LinkpointType;
import gov.nih.nci.cbiit.cmts.core.Mapping;
import gov.nih.nci.cbiit.cmts.core.MetaConstants;
import gov.nih.nci.cbiit.cmts.core.TagType;
import gov.nih.nci.cbiit.cmts.core.Mapping.Components;
import gov.nih.nci.cbiit.cmts.core.Mapping.Links;

/**
 * This class is used to generate CMTS Mapping
 *
 * @author Chunqing Lin
 * @author LAST UPDATE $Author: wangeug $
 * @since     CMTS v1.0
 * @version    $Revision: 1.6 $
 * @date       $Date: 2009-10-15 18:36:23 $
 *
 */
public class MappingFactory
{
	public static XSDParser sourceParser;
	public static XSDParser targetParser;
    public static ElementMeta sourceHeadMeta;
    public static ElementMeta targetHeadMeta;

    public static void loadMetaXSD(Mapping m, XSDParser schemaParser,String rootNS, String root, ComponentType type) {

		ElementMeta e = schemaParser.getElementMeta(rootNS, root);
		if(e==null)
			e = schemaParser.getElementMetaFromComplexType(rootNS, root, MetaConstants.SCHEMA_LAZY_LOADINTG_INITIAL);

        //if (type==ComponentType.SOURCE) sourceHeadMeta = e;
        //else targetHeadMeta = e;

        if (m.getComponents()!=null)
			for (Component mapComp:m.getComponents().getComponent())
			{
				if (mapComp.getRootElement()!=null
						&mapComp.getType().equals(type))
				{
					//clear the childElement list and attribute list for backward compatible
					mapComp.getRootElement().getChildElement().clear();
					mapComp.getRootElement().getChildElement().addAll(e.getChildElement());
					mapComp.getRootElement().getAttrData().clear();
					mapComp.getRootElement().getAttrData().addAll(e.getAttrData());
                    if (type==ComponentType.SOURCE) sourceHeadMeta = mapComp.getRootElement();
                    else targetHeadMeta = mapComp.getRootElement();
                    return;
				}
			}
		Component endComp = new Component();
		endComp.setKind(KindType.XML);
		endComp.setId(getNewComponentId(m));
		endComp.setLocation(schemaParser.getSchemaURI());

		endComp.setRootElement(e);
		endComp.setType(type);
		m.getComponents().getComponent().add(endComp);
	}

	private static String getNewComponentId(Mapping m){
		if(m.getComponents() == null)
			m.setComponents(new Components());
		int num = 0;
		for(Component c:m.getComponents().getComponent()){
			int tmp = -1;
			try{
				tmp = Integer.parseInt(c.getId());
			}catch(Exception ignored){}
			if(tmp>=num)
				num = tmp+1;
		}
		return String.valueOf(num);
	}
	/**
	 * add link to specified Mapping
	 * @param m - Mapping object to load into
	 * @param srcComponentId -  source component id
	 * @param srcPath - source object path
	 * @param tgtComponentId - target component id
	 * @param tgtPath - target object path
	 */
	public static void addLink(Mapping m, String srcComponentId, String srcPath, String tgtComponentId, String tgtPath) {
		LinkType l = new LinkType();
		LinkpointType lp = new LinkpointType();
		lp.setComponentid(srcComponentId);
		lp.setId(srcPath);
		l.setSource(lp);
		lp = new LinkpointType();
		lp.setComponentid(tgtComponentId);
		lp.setId(tgtPath);
		l.setTarget(lp);
		if(m.getLinks() == null) m.setLinks(new Links());
		m.getLinks().getLink().add(l);
	}

	public static Mapping loadMapping(File f) throws JAXBException
    {
		System.out.println("MappingFactory.loadMapping()...mappingFile:"+f.getAbsolutePath());
		String mappingParentPath=f.getAbsoluteFile().getParentFile().getAbsolutePath();
		System.out.println("MappingFactory.loadMapping()..mapping Parent:"+mappingParentPath);
		JAXBContext jc=null;
//			jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmts.core" );
		jc=com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl.newInstance("gov.nih.nci.cbiit.cmts.core");

		Unmarshaller u = jc.createUnmarshaller();
		JAXBElement<Mapping> jaxbElmt = u.unmarshal(new StreamSource(f), Mapping.class);
		Mapping mapLoaded=jaxbElmt.getValue();
		System.out.println("MappingFactory.loadMapping()...mapLoaded:"+mapLoaded);
		//re-connect the meta structure for source and target schemas
		for (Component mapComp:mapLoaded.getComponents().getComponent())
		{
            String xsdLocation=mappingParentPath+File.separator+mapComp.getLocation();
            try
            {
                if (mapComp.getRootElement()!=null)
                {
                    if ((mapComp.getType() != ComponentType.SOURCE)&&
                        (mapComp.getType() != ComponentType.TARGET)) continue;
                    System.out.println("MappingFactory.loadMapping()..schema:"+mapComp.getType()+"="+xsdLocation);
                    XSDParser metaParser = new XSDParser();
                    if (mapComp.getType()==ComponentType.SOURCE)
                    {
                        //sourceHeadMeta = mapComp.getRootElement();
                        sourceParser=metaParser;
                    }
                    else
                    {
                        //targetHeadMeta = mapComp.getRootElement();
                        targetParser=metaParser;
                    }
                    metaParser.loadSchema(new File(xsdLocation).toURI().toString(),null);
//                    mapComp.setLocation(xsdLocation);
                    MappingFactory.loadMetaXSD(mapLoaded, metaParser, mapComp.getRootElement().getNameSpace(),mapComp.getRootElement().getName(),mapComp.getType() );
                    mapComp.setLocation(metaParser.getSchemaURI());
                }
            }
            catch(Exception ee)
            {
                String msg = ee.getMessage();
                if ((msg == null)||(msg.trim().equals("")))
                	msg = "Failed to read or parse schema document - " + xsdLocation;
                ee.printStackTrace();
                throw new JAXBException(ee.getClass().getCanonicalName()+":"+msg);

            }
        }


        if ((mapLoaded.getTags().getTag() != null)&&(mapLoaded.getTags().getTag().size() > 0))
        {
            Hashtable <String, BaseMeta> srcMetaHash=new Hashtable <String, BaseMeta>();
            Hashtable <String, BaseMeta> trgtMetaHash=new Hashtable <String, BaseMeta>();
            //pre-process mapping for annotation
            for (Component mapComp:mapLoaded.getComponents().getComponent())
            {
                if (mapComp.getType().value().equals(ComponentType.SOURCE.value()))
                {
                    mapComp.getRootElement().setId("/"+mapComp.getRootElement().getName());
                    processMeta(srcMetaHash, mapComp.getRootElement(), null, mapLoaded.getTags().getTag());
                }
                else if (mapComp.getType().value().equals(ComponentType.TARGET.value()))
                {
                    mapComp.getRootElement().setId("/"+mapComp.getRootElement().getName());
                    processMeta(trgtMetaHash,mapComp.getRootElement(), null, mapLoaded.getTags().getTag());
                }
            }

            //sort tags with precedence from low to high
            // 0 -- componentType; enumValues: source, taret, function
            // 1 -- key; allowing value: entry's xpath
            // 2 -- componentKind; enumValues: choice, clone
            // 3 -- value; the ordered ASCII characters
            Collections.sort(mapLoaded.getTags().getTag());
            for (TagType tag:mapLoaded.getTags().getTag())
            {
                if (tag.getComponentType().value().equals(ComponentType.SOURCE.value()))
                {
                    processAnnotationTag (tag, srcMetaHash, mapLoaded.getTags().getTag());
                }
                else if (tag.getComponentType().value().equals(ComponentType.TARGET.value()))
                {
                    processAnnotationTag (tag, trgtMetaHash, mapLoaded.getTags().getTag());
                }
            }
        }




        if ((mapLoaded.getLinks().getLink() != null)&&(mapLoaded.getLinks().getLink().size() > 0))
        {

            for (LinkType link:mapLoaded.getLinks().getLink())
            {
                if ((link.getSource().getComponentid().equals("0"))||
                    (link.getSource().getComponentid().equals("1")))
                {
                    String id = link.getSource().getId();
                    //System.out.println("CCCX Source("+link.getSource().getComponentid()+") : id=" + id);
                    BaseMeta meta = searchElementMeta(id);
                    //if (meta == null) meta = searchElementMeta(id, ComponentType.TARGET);
                }

                if ((link.getTarget().getComponentid().equals("0"))||
                    (link.getTarget().getComponentid().equals("1")))
                {
                    String id = link.getTarget().getId();
                    //System.out.println("CCCX Target("+link.getTarget().getComponentid()+") : id=" + id);
                    BaseMeta meta = searchElementMeta(id);
                    //if (meta == null) meta = searchElementMeta(id, ComponentType.SOURCE);
                }

            }
        }




        return  jaxbElmt.getValue();
	}

	private static void processAnnotationTag(TagType tag, Hashtable <String, BaseMeta>  metaHash, List<TagType> tagList)
	{
        //v System.out.println("CCCX processAnnotationTag: " + tag.getKey());
        if ((metaHash == null)||(metaHash.size() ==0)) return;
        String parentKey=tag.getKey().substring(0, tag.getKey().lastIndexOf("/"));
		ElementMeta elmntMeta=(ElementMeta)metaHash.get(tag.getKey());
        if (elmntMeta == null)
        {
            BaseMeta meta = searchElementMeta(tag.getKey(), tag.getComponentType());
            if (meta == null) System.out.println("*UUU1 Not found element meta: " + tag.getKey());
            else if (!(meta instanceof ElementMeta)) System.out.println("*UUU1 This is Not an element meta: " + tag.getKey());
            else elmntMeta = (ElementMeta)meta;
        }
        ElementMeta parentMeta=(ElementMeta)metaHash.get(parentKey);
        if (parentMeta == null)
        {
            BaseMeta meta = searchElementMeta(parentKey, tag.getComponentType());
            if (meta == null) System.out.println("*UUU2 Not found parent meta: " + parentKey);
            else if (!(meta instanceof ElementMeta)) System.out.println("*UUU2 This is Not an element meta: " + tag.getKey());
            else parentMeta = (ElementMeta)meta;
        }
        if (tag.getKind().value().equals(KindType.CLONE.value()))
		{
			ElementMeta cloneElement=(ElementMeta)elmntMeta.clone();
			int insertingIndx=0;

			//find the position of the element being cloned
			for (ElementMeta siblingElmnt:parentMeta.getChildElement())
			{
				insertingIndx++;
				if (siblingElmnt.getName().equals(elmntMeta.getName()))
					break;
			}
			cloneElement.setMultiplicityIndex(BigInteger.valueOf(Integer.valueOf(tag.getValue()).intValue()));
			cloneElement.setId(parentMeta.getId()+"/"+cloneElement.getName());
			parentMeta.getChildElement().add(insertingIndx+cloneElement.getMultiplicityIndex().intValue()-1, cloneElement);
			List<ElementMeta> pList = new ArrayList<ElementMeta>();
            pList.add(parentMeta);
            processMeta(metaHash, cloneElement, pList, tagList);
		}
		else if (tag.getKind().value().equals(KindType.CHOICE.value()))
		{
			System.out.println("MappingFactory.processAnnotationTag()..choosen element:"+tag.getKey());
			elmntMeta.setIsChosen(true);
            if (elmntMeta.getChildElement().size() == 0)
            {
                if (tag.getComponentType()==ComponentType.SOURCE) sourceParser.expandElementMetaWithLazyLoad(elmntMeta);
                else targetParser.expandElementMetaWithLazyLoad(elmntMeta);
            }
        }
		else if (tag.getKind().value().equals(KindType.RECURSION.value()))
		{

			ElementMeta recursiveMeta=searchRecursiveAncestor(metaHash, elmntMeta, elmntMeta.getType());
            if (recursiveMeta != null)
            {
                ElementMeta recursiveMetaClone=(ElementMeta)recursiveMeta.clone();

                //add the cloned Attributes and childElement to
                //the recursive element, then it will be referred by parent elementMeta
                elmntMeta.getAttrData().addAll(recursiveMetaClone.getAttrData());
                elmntMeta.getChildElement().addAll(recursiveMetaClone.getChildElement());

                List<ElementMeta> pList = new ArrayList<ElementMeta>();
                pList.add(parentMeta);
                processMeta(metaHash, elmntMeta, pList, tagList);
                elmntMeta.setIsEnabled(true);
            }
            else System.out.println("Cannot find the ancester of recursive node : name="+ elmntMeta.getName() + ", namespace=" + elmntMeta.getNameSpace() + ", type=" + elmntMeta);
        }

	}
	/**
	 * Recursive search ancestor element meta to find the recursive type
	 * @param metaHash
	 * @param element
	 * @param recursionType
	 * @return
	 */
	private static ElementMeta searchRecursiveAncestor(Hashtable <String, BaseMeta>  metaHash, ElementMeta element, String recursionType)
	{
		ElementMeta parentMeta;
		String parentKey=element.getId().substring(0,element.getId().lastIndexOf("/"));
		parentMeta=(ElementMeta)metaHash.get(parentKey);
		if (parentMeta==null)
			return parentMeta;
        else if (parentMeta.getType() == null) {}
        else if (element.getType() == null) {}
        else if (parentMeta.getType().equals(element.getType()))
			return parentMeta;
		return searchRecursiveAncestor(metaHash,  parentMeta, recursionType);
	}

	/**
	 * Set unique ID for each element meta and attribute meta, these IDs are
	 * referred as processing links
	 * @param metaHash
	 * @param element
	 */
    //private static void processMeta(Hashtable <String, BaseMeta>  metaHash, ElementMeta element, ElementMeta parent)
	//{
    //    processMeta(metaHash, element, parent, null);
    //}
    private static void processMeta(Hashtable <String, BaseMeta>  metaHash, ElementMeta element, List<ElementMeta> parents, List<TagType> tagList)
	{
        if (parents == null) parents = new ArrayList<ElementMeta>();
        String addedTag = getAddedTag(element, tagList);
        boolean find = false;

        if (tagList == null) find = true;
        else if (parents == null) find = false;
        else
        {
            if (addedTag != null) find = true;
        }
        if (find)
        {
            //System.out.println("*UUU1 Add meta       : " + element.getId());
            metaHash.put(element.getId(), element);

        }
        if ((parents.size() > 1000)||(find))
        {
            //int j = 0;
            for (int i=(parents.size()-1);i>=0;i--)
            {
                ElementMeta meta = parents.get(i);


                String id = meta.getId() + "/";
                if (!element.getId().startsWith(id))
                {
                    //System.out.println("   UUU9 delete meta("+i+"): " + meta.getId());
                    parents.remove(i);
                    continue;
                }
                BaseMeta e = metaHash.get(meta.getId());
                if (e != null) continue;
                if (!find) continue;
                //j++;
                //System.out.println("*UUU2 Add parent meta("+i+", "+j+"): " + meta.getId());
                metaHash.put(meta.getId(), meta);
            }
        }





        if (element.isIsRecursive()&&element.isIsEnabled()) //typeStack.contains(currentType))
			return;
		//process attribute
        if ((find)&&(addedTag.indexOf("/@") > 0))
        {
            for (AttributeMeta attr:element.getAttrData())
            {
                String attrMetaKey=element.getId()+"/@"+attr.getName();
                attr.setId(attrMetaKey);
                //addToMetaHash(metaHash, attr, element, tagList, true);
                metaHash.put(attrMetaKey, attr);
                System.out.println("*UUU3 Add attr meta  : " + attr.getId());
            }
        }
        //process child elements

        if (element.getChildElement().size() > 0)
        {
            parents.add(element);

            for(ElementMeta childElement:element.getChildElement())
            {
                childElement.setId(element.getId()+"/"+childElement.getName());
                processMeta(metaHash, childElement, parents, tagList);
            }
        }
	}
    private static String getAddedTag(BaseMeta baseMeta, List<TagType> tagList)
    {
        String addedTag = null;

            for (TagType tag:tagList)
            {
                String tagStr = tag.getKey().trim();
                int idx = tagStr.indexOf("/@");
                if (idx > 0) tagStr = tagStr.substring(0, idx);

                if (tagStr.equals(baseMeta.getId().trim()))
                {
                    addedTag = tag.getKey().trim();
                    break;
                }
            }


        return addedTag;
    }

    public static void saveMapping(File f, Mapping m) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance( "gov.nih.nci.cbiit.cmts.core" );
		Marshaller u = jc.createMarshaller();

		//do not persistent the meta structure
		Hashtable<String, List<ElementMeta>> rootChildListHash=new Hashtable<String, List<ElementMeta>>();
		Hashtable<String, List<AttributeMeta>> rootAttrListHash=new Hashtable<String, List<AttributeMeta>>();
		for (Component mapComp:m.getComponents().getComponent())
		{
			if (mapComp.getRootElement()!=null)
			{
				List<ElementMeta> childList=new ArrayList<ElementMeta>();
				childList.addAll(mapComp.getRootElement().getChildElement());
				rootChildListHash.put(mapComp.getLocation()+mapComp.getId(), childList);
				mapComp.getRootElement().getChildElement().clear();

				List<AttributeMeta> attrList=new ArrayList<AttributeMeta>();
				attrList.addAll(mapComp.getRootElement().getAttrData());
				rootAttrListHash.put(mapComp.getLocation()+mapComp.getId(), attrList);
				mapComp.getRootElement().getAttrData().clear();
			}
		}
		u.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
		u.marshal(new JAXBElement<Mapping>(new QName("mapping"),Mapping.class, m), f);

		//put the unmarshalled children back
		for (Component mapComp:m.getComponents().getComponent())
		{
			if (mapComp.getRootElement()!=null)
			{
				mapComp.getRootElement().getChildElement().addAll(rootChildListHash.get(mapComp.getLocation()+mapComp.getId()));
				mapComp.getRootElement().getAttrData().addAll(rootAttrListHash.get(mapComp.getLocation()+mapComp.getId()));
				String xsdLocation=f.getParent()+File.separator+mapComp.getLocation();
				mapComp.setLocation(xsdLocation);
			}
		}
    }
    private static BaseMeta searchElementMeta(String path)
    {
       return searchElementMeta(path, null);
    }
    private static BaseMeta searchElementMeta(String path, ComponentType type)
    {
        while(path.startsWith("/")) path = path.substring(1);
        ElementMeta elem = null;
        if (type == null)
        {
            if (path.startsWith(sourceHeadMeta.getName())) type = ComponentType.SOURCE;
            else if (path.startsWith(targetHeadMeta.getName())) type = ComponentType.TARGET;
            else
            {
                System.out.println("This id is not identified. => " + path);
                return null;
            }
        }

        if (type==ComponentType.SOURCE) elem = sourceHeadMeta;
        else elem = targetHeadMeta;


        StringTokenizer st = new StringTokenizer(path, "/");
        int n = 0;
        //ElementMeta parent = null;
        String attrID = null;
        BaseMeta ret = null;
        while(st.hasMoreTokens())
        {
            String token = st.nextToken();
            if (token == null) token = "";
            else token = token.trim();
            if (token.equals("")) continue;

            if (attrID != null)
            {
                System.out.println("Invalid attribute item(" + type.value() + ") => " + attrID);
                return null;
            }
            if (token.startsWith("@")) attrID = token;

            if (n == 0)
            {
                if (token.equals(elem.getName()))
                {
                    n++;
                    if ((elem.getChildElement() == null)||(elem.getChildElement().size() == 0))
                    {
                        System.out.println("Head Node doesn't have any child element(" + type.value() + ") => " + token);
                        return null;
                    }
                    else continue;
                }
                else
                {
                    System.out.println("Head Node not found searchElementMeta(" + type.value() + ") => " + token + " <> " + elem.getName());
                    return null;
                }
            }

            if ((elem.getChildElement() == null)||(elem.getChildElement().size() == 0))
            {
                //XSDParser parser = null;
                if (type==ComponentType.SOURCE) sourceParser.expandElementMetaWithLazyLoad(elem);
                else targetParser.expandElementMetaWithLazyLoad(elem);

                //parser.expandElementMetaWithLazyLoad(elem);
                /*
                ElementMeta eleT = parser.expandElementMetaWithLazyLoad(parent, elem);
                if ((eleT == null)||(eleT.getChildElement() == null)||(eleT.getChildElement().size() == 0))
                {
                    System.out.println("Element expanding failure searchElementMeta(" + type.value() + ") => " + token + " at " + parent.getName());
                    return null;
                }
                else elem = eleT;
                */
            }
            BaseMeta tEle = null;
            if (token.startsWith("@"))
            {
                for (int i=0;i<elem.getAttrData().size();i++)
                {
                    AttributeMeta meta = elem.getAttrData().get(i);
                    if (meta.getName().equals(token.substring(1)))
                    {
                        tEle = meta;
                        break;
                    }
                }
            }
            else
            {
                for (int i=0;i<elem.getChildElement().size();i++)
                {
                    ElementMeta meta = elem.getChildElement().get(i);
                    if (meta.getName().equals(token))
                    {
                        tEle = meta;
                        break;
                    }
                }
            }
            if (tEle == null)
            {
                System.out.println("Node not found node searchElementMeta(" + type.value() + ") => " + token + " at " + path);
                return null;
            }
            //parent = elem;
            if (tEle instanceof ElementMeta)
            {
                elem = (ElementMeta)tEle;
            }
            ret = tEle;
            n++;
        }
        if (ret instanceof ElementMeta)
        {
            ElementMeta ele = (ElementMeta) ret;
            if (ele.getChildElement().size() == 0)
            {
                if (type==ComponentType.SOURCE) sourceParser.expandElementMetaWithLazyLoad(ele);
                else targetParser.expandElementMetaWithLazyLoad(ele);
                return ele;
            }
        }
        return ret;
    }
}

/**
 * HISTORY: $Log: not supported by cvs2svn $
 * HISTORY: Revision 1.5  2008/12/10 15:43:03  linc
 * HISTORY: Fixed component id generator and delete link.
 * HISTORY:
 * HISTORY: Revision 1.4  2008/12/09 19:04:17  linc
 * HISTORY: First GUI release
 * HISTORY:
 * HISTORY: Revision 1.3  2008/12/03 20:46:14  linc
 * HISTORY: UI update.
 * HISTORY:
 * HISTORY: Revision 1.2  2008/10/22 19:01:17  linc
 * HISTORY: Add comment of public methods.
 * HISTORY:
 * HISTORY: Revision 1.1  2008/10/21 15:56:55  linc
 * HISTORY: updated
 * HISTORY:
 */
