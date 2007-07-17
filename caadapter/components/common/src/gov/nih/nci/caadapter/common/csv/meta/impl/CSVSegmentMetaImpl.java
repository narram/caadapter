/**
 * <!-- LICENSE_TEXT_START -->
 * $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/meta/impl/CSVSegmentMetaImpl.java,v 1.6 2007-07-17 16:16:34 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE
 * ******************************************************************
 *
 * The caAdapter Software License, Version 1.3
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


package gov.nih.nci.caadapter.common.csv.meta.impl;

import gov.nih.nci.caadapter.common.MetaObjectImpl;
import gov.nih.nci.caadapter.common.csv.meta.CSVFieldMeta;
import gov.nih.nci.caadapter.common.csv.meta.CSVSegmentMeta;
import gov.nih.nci.caadapter.common.util.PropertiesResult;
import gov.nih.nci.caadapter.common.util.Config;
import gov.nih.nci.caadapter.castor.csv.meta.impl.types.CardinalityType;
import gov.nih.nci.caadapter.castor.csv.meta.impl.C_segment;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import gov.nih.nci.caadapter.common.Cardinality;

/**
 * Implementation of a segment metadata (contained within a csv file).
 *
 * @author OWNER: Matthew Giordano
 * @author LAST UPDATE $Author: wangeug $
 * @version $Revision: 1.6 $
 * 			$Date: 2007-07-17 16:16:34 $
 * @since caAdapter v1.2
 */

public class CSVSegmentMetaImpl extends MetaObjectImpl implements CSVSegmentMeta
{
	private static final String LOGID = "$RCSfile: CSVSegmentMetaImpl.java,v $";
	public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/csv/meta/impl/CSVSegmentMetaImpl.java,v 1.6 2007-07-17 16:16:34 wangeug Exp $";
    private String segmentName;
    List<CSVFieldMeta> fields = new ArrayList<CSVFieldMeta>();
    List<CSVSegmentMeta> childSegments = new ArrayList<CSVSegmentMeta>();
    CSVSegmentMeta parent;

    CardinalityType cardinality = (new C_segment()).getCardinality();

    boolean cardinalityChanged = false;
    boolean parentChanged = false;


    public CSVSegmentMeta getParent()
    {
        return parent;
    }

    public void setParent(CSVSegmentMeta newParentSegmentMeta)
    {
        this.parent = newParentSegmentMeta;

        if (parent == null) cardinality = CardinalityType.valueOf(Config.CARDINALITY_ONE_TO_ONE);
    }

    public CSVSegmentMetaImpl(String segmentName, CSVSegmentMeta parent)
    {
        name = segmentName;
        this.segmentName = segmentName;
        this.parent = parent;
        if (parent == null)
        {
            cardinality = CardinalityType.valueOf(Config.CARDINALITY_ONE_TO_ONE);
        }
    }

    public String getName()
    {
        return name;
    }

    public String getSegmentName()
    {
        return name;
    }

    public void setName(String nam)
    {
        segmentName = nam;
        name = nam;
    }

    public List<CSVFieldMeta> getFields()
    {
        return fields;
    }

    public List<CSVSegmentMeta> getChildSegments()
    {
        return childSegments;
    }

    public void addField(CSVFieldMeta f)
    {
        if(!fields.contains(f))
        {//add only if not contains it
            fields.add(f);
        }
    }

    public void addSegment(CSVSegmentMeta s)
    {
        if(!childSegments.contains(s))
        {//add only if not contains it
            childSegments.add(s);
        }
    }

    public void removeAllFields()
    {
        this.fields = new ArrayList<CSVFieldMeta>();
    }

    public void setFields(List<CSVFieldMeta> newFieldMetaList)
    {
        this.fields = newFieldMetaList;
    }

    public boolean removeField(CSVFieldMeta field)
    {
        if(fields!=null)
        {
            return fields.remove(field);
        }
        return false;
    }

    public boolean removeSegment(CSVSegmentMeta segment)
    {
        if(childSegments!=null)
        {
            childSegments.remove(segment);
        }
        return false;
    }

    public String toString()
    {
        String defaultCardinality = (new C_segment()).getCardinality().toString();
        if (isChoiceSegment())
        {
            return Config.SUFFIX_OF_CHOICE_CARDINALITY + " " + getName() + "  [" + getCardinalityWithString() + "] ";
        }
        if ((getParent() != null)&&(getParent().isChoiceSegment())) return getName();

        if (getCardinalityWithString().equals(defaultCardinality)) return getName();
        else return getName() + "  [" + getCardinalityWithString() + "]";

    }

    /**
     * Will clone self, children, fields.
     * The cloned object will share the same parent.
     * @param copyUUID if true, the cloned object will share the same uuid value of this object; otherwise, it will have different UUID value.
     * @return
     * @throws CloneNotSupportedException
     */
    public Object clone(boolean copyUUID) throws CloneNotSupportedException
    {
        CSVSegmentMetaImpl metaObj = (CSVSegmentMetaImpl) super.clone(copyUUID);

        metaObj.parent = this.parent;
        metaObj.segmentName = this.segmentName;

        List<CSVFieldMeta> fieldList = new ArrayList<CSVFieldMeta>();
        int size = this.fields == null ? 0 : this.fields.size();
        for (int i = 0; i < size; i++)
        {
            CSVFieldMetaImpl csvFieldMetaCopy = (CSVFieldMetaImpl)((CSVFieldMetaImpl) (this.fields.get(i))).clone(copyUUID);
            csvFieldMetaCopy.setSegment(metaObj);
            fieldList.add((CSVFieldMeta) csvFieldMetaCopy);
        }
        metaObj.fields = fieldList;

        List<CSVSegmentMeta> segmentList = new ArrayList<CSVSegmentMeta>();
        size = this.childSegments == null ? 0 : this.childSegments.size();
        for (int i = 0; i < size; i++)
        {
            CSVSegmentMetaImpl csvSegmentMetaCopy = (CSVSegmentMetaImpl)((CSVSegmentMetaImpl) this.childSegments.get(i)).clone(copyUUID);
            csvSegmentMetaCopy.setParent(metaObj);
            segmentList.add((CSVSegmentMeta) csvSegmentMetaCopy);
        }
        metaObj.childSegments = segmentList;
        metaObj.setCardinalityType(this.getCardinalityType());
        return metaObj;
    }

    /**
     * Return the title of this provider that may be used to distinguish from others.
     *
     * @return the title for property.
     */
    public String getTitle()
    {
        return "CSV Segment Properties";
    }

    /**
     * This functions will return an array of PropertyDescriptor that would
     * help reflection and/or introspection to figure out what information would be
     * presented to the user.
     */
    public PropertiesResult getPropertyDescriptors() throws Exception
    {
        Class beanClass = this.getClass();

        PropertyDescriptor _parentSegmentName = new PropertyDescriptor("Parent Segment", beanClass, "getParent", null);
        PropertyDescriptor _name = new PropertyDescriptor("Name", beanClass, "getName", null);
        PropertyDescriptor _cardinality = new PropertyDescriptor("Cardinality", beanClass, "getCardinality", null);
        PropertyDescriptor _class = new PropertyDescriptor("Type", beanClass, "getClassName", null);
//		PropertyDescriptor[] propertiesArray = new PropertyDescriptor[]
//		{_parentSegmentName, _name, _class};
//		return propertiesArray;
        List<PropertyDescriptor> propList = new ArrayList<PropertyDescriptor>();
        propList.add(_parentSegmentName);
        propList.add(_name);

        //if (!cardinality.toString().equals((new C_segment()).getCardinality().toString())) propList.add(_cardinality);
        propList.add(_cardinality);

        propList.add(_class);
        PropertiesResult result = new PropertiesResult();
        result.addPropertyDescriptors(this, propList);
        return result;
    }

    public String getCardinalityWithString()
    {
        return cardinality.toString().substring(0, (new C_segment()).getCardinality().toString().length());
    }
    public void setCardinalityWithString(String type) throws IllegalArgumentException
    {
        cardinality = CardinalityType.valueOf(type);
    }
    public CardinalityType getCardinalityType()
    {
        return cardinality;
    }
    public void setCardinalityType(CardinalityType type)
    {
        cardinality = type;
    }
    public boolean isChoiceSegment()
    {
        return cardinality.toString().endsWith(Config.SUFFIX_OF_CHOICE_CARDINALITY);
    }
    public boolean isChoiceMemberSegment()
    {
        if (getParent() == null) return false;
        return getParent().isChoiceSegment();
        //return cardinality.toString().endsWith(Config.SUFFIX_OF_CHOICE_CARDINALITY);
    }
    public int getMaxCardinality()
    {
        Cardinality _cardinality = null;
        try
        {
            _cardinality = new Cardinality(getCardinalityWithString());
        }
        catch(IllegalArgumentException ie) {}
        return _cardinality.getMaximum();
    }
    public int getMinCardinality()
    {
        Cardinality _cardinality = null;
        try
        {
            _cardinality = new Cardinality(getCardinalityWithString());
        }
        catch(IllegalArgumentException ie) {}
        return _cardinality.getMinimum();
    }
	public String getXmlPath()
	{
//		System.out.println("CSVSegmentMetaImpl.getXmlPath()..build dyanmic xmlPath");
		CSVSegmentMeta parentMeta=this.getParent();
        StringBuffer sbXmlPath=new StringBuffer();
        if (parentMeta!=null)
           	sbXmlPath.append(parentMeta.getXmlPath()+".");
        return sbXmlPath.toString()+getName();
	}
}
