/*
 *  $Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/impl/MetaFieldImpl.java,v 1.2 2007-07-17 16:11:38 wangeug Exp $
 *
 * ******************************************************************
 * COPYRIGHT NOTICE  
 * ******************************************************************
 *
 *	The caAdapter Software License, Version 1.0
 *
 *	Copyright 2001 SAIC. This software was developed in conjunction with the National Cancer
 *	Institute, and so to the extent government employees are co-authors, any rights in such works
 *	shall be subject to Title 17 of the United States Code, section 105.
 *
 *	Redistribution and use in source and binary forms, with or without modification, are permitted
 *	provided that the following conditions are met:
 *
 *	1. Redistributions of source code must retain the above copyright notice, this list of conditions
 *	and the disclaimer of Article 3, below.  Redistributions in binary form must reproduce the above
 *	copyright notice, this list of conditions and the following disclaimer in the documentation and/or
 *	other materials provided with the distribution.
 *
 *	2.  The end-user documentation included with the redistribution, if any, must include the
 *	following acknowledgment:
 *
 *	"This product includes software developed by the SAIC and the National Cancer
 *	Institute."
 *
 *	If no such end-user documentation is to be included, this acknowledgment shall appear in the
 *	software itself, wherever such third-party acknowledgments normally appear.
 *
 *	3. The names "The National Cancer Institute", "NCI" and "SAIC" must not be used to endorse or
 *	promote products derived from this software.
 *
 *	4. This license does not authorize the incorporation of this software into any proprietary
 *	programs.  This license does not authorize the recipient to use any trademarks owned by either
 *	NCI or SAIC-Frederick.
 *
 *	5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *	WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *	MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *	DISCLAIMED.  IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE, SAIC, OR
 *	THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *	EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *	PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *	PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 *	OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *	NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * ******************************************************************
 */

package gov.nih.nci.caadapter.common.standard.impl;

import gov.nih.nci.caadapter.common.standard.DataField;
import gov.nih.nci.caadapter.common.standard.MetaField;
import gov.nih.nci.caadapter.common.standard.DataSegment;
import gov.nih.nci.caadapter.common.standard.MetaSegment;
import gov.nih.nci.caadapter.common.standard.type.CommonNodeModeType;
import gov.nih.nci.caadapter.common.ApplicationException;

import java.util.List;
import java.util.ArrayList;

/**
 * This class defines ...
 *
 * @author OWNER: Kisung Um
 * @author LAST UPDATE $Author: wangeug $
 * @version Since caAdapter v3.3
 *          revision    $Revision: 1.2 $
 *          date        Jul 2, 2007
 *          Time:       8:20:47 PM $
 */
public class MetaFieldImpl extends CommonFieldImpl implements MetaField
{

    /**
     * Logging constant used to identify source of log entry, that could be later used to create
     * logging mechanism to uniquely identify the logged class.
     */
    private static final String LOGID = "$RCSfile: MetaFieldImpl.java,v $";

    /**
     * String that identifies the class version and solves the serial version UID problem.
     * This String is for informational purposes only and MUST not be made final.
     *
     * @see <a href="http://www.visi.com/~gyles19/cgi-bin/fom.cgi?file=63">JBuilder vice javac serial version UID</a>
     */
    public static String RCSID = "$Header: /share/content/gforge/caadapter/caadapter/components/common/src/gov/nih/nci/caadapter/common/standard/impl/MetaFieldImpl.java,v 1.2 2007-07-17 16:11:38 wangeug Exp $";



    private List<DataField> LinkedDataField = new ArrayList<DataField>();


    public MetaFieldImpl()
    {
        super(CommonNodeModeType.META);

    }

    public MetaFieldImpl(MetaSegment seg, String name) throws ApplicationException
    {
        super(name);
        setParent(seg);
        this.setModeType(CommonNodeModeType.META);
    }

    public void addLinkedDataSegment(DataField a) throws ApplicationException
    {
//        if (getNumberOfLinkedDataFields() >= getMaxCardinality())
//            throw new ApplicationException("This cardinality doesn't allow to add data field node any more (" + getCardinalityType() + ")");
        LinkedDataField.add(a);
    }
    public List<DataField> getLinkedDataFields()
    {
        return LinkedDataField;
    }
    public int getNumberOfLinkedDataFields()
    {
        return LinkedDataField.size();
    }
    public DataField getLinkedDataFieldInSequence(int n)
    {
        return LinkedDataField.get(n);
    }
//    public DataField creatDataSegment(DataSegment par)
//    {
//        if (par == null) return null;
//        if ((getName() == null)||(getName().equals(""))) return null;
//        DataField data = null;
//        try
//        {
//            data = new DataFieldImpl(par, this);
//        }
//        catch(ApplicationException ae)
//        {
//            return null;
//        }
//        addLinkedDataSegment(data);
//        return data;
//    }

    public DataField creatDataField(DataSegment par, DataField target) throws ApplicationException
    {
        if ((getName() == null)||(getName().equals(""))) throw new ApplicationException("This MetaSegment object isn't given any name.");
        if (par == null) throw new ApplicationException("Parent Data Segment node is null.");
        if (target == null) throw new ApplicationException("Target Data Segment node is null.");

        target.setName(this.getName());
        target.cloneNode(target, this, this.getXmlPath(), this.getXPath(), par);
        par.addChildNode(target);
        target.setSourceMetaField(this);
        addLinkedDataSegment(target);

        return target;
    }

    public DataField creatDataField(DataSegment par, DataField target, String val) throws ApplicationException
    {
        target = creatDataField(par, target);
        target.setValue(val);
        return target;
    }

    public String generateMetaFileContent()
    {
        // todo
        return "";
    }

    public MetaField createNewInstance()
    {
        return new MetaFieldImpl();
    }
    public DataField createNewDataInstance()
    {
        return new DataFieldImpl();
    }

    public void clearLinkedDataFields()
    {
        LinkedDataField = new ArrayList<DataField>();
    }


}


/**
 * HISTORY      : $Log: not supported by cvs2svn $
 * HISTORY      : Revision 1.1  2007/07/09 15:39:24  umkis
 * HISTORY      : Basic resource programs for csv cardinality and test instance generating.
 * HISTORY      :
 */
