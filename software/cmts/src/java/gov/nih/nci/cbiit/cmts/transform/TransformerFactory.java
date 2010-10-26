package gov.nih.nci.cbiit.cmts.transform;

import gov.nih.nci.cbiit.cmts.transform.csv.Csv2XmlTransformer;
import gov.nih.nci.cbiit.cmts.transform.hl7v2.Hl7v2XmlTransformer;

import javax.xml.xquery.XQException;

public class TransformerFactory {
	public static TransformationService getTransformer(String transformerType) throws XQException
	{
		if (transformerType.equals(TransformationService.TRANSFER_XML_TO_XML))
 			return new XQueryTransformer();
		else if (transformerType.equals(TransformationService.TRANSFER_HL7_V2_TO_XML))
			return new Hl7v2XmlTransformer();
 		else if (transformerType.equals(TransformationService.TRANSFER_CSV_TO_XML))
 			return new Csv2XmlTransformer();
 		else if (transformerType.equals(TransformationService.TRANSFER_XML_TO_CDA))
 		{	XQueryTransformer rtnTransformer= new XQueryTransformer();
 			rtnTransformer.setPresentable(true);
 			return rtnTransformer;
 		}
 		else if (transformerType.equals(TransformationService.TRANSFER_CSV_TO_CDA))
 		{	XQueryTransformer rtnTransformer= new Csv2XmlTransformer();
 			rtnTransformer.setPresentable(true);
 			return rtnTransformer;
 		}
 		else if (transformerType.equals(TransformationService.TRANSFER_HL7_V2_TO_CDA))
 		{	XQueryTransformer rtnTransformer= new Hl7v2XmlTransformer();
 			rtnTransformer.setPresentable(true);
 			return rtnTransformer;
 		}
 		else if (transformerType.equalsIgnoreCase("XML"))
 			return new XQueryTransformer();
		else if (transformerType.equalsIgnoreCase("CSV"))
 			return new Csv2XmlTransformer();
 		else if (transformerType.toUpperCase().contains("HL7"))
 			return new Hl7v2XmlTransformer();
		
		return new XQueryTransformer();
	}
}