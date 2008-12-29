//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2008.12.23 at 11:43:13 AM EST 
//


package gov.nih.nci.cbiit.cmps.core;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the gov.nih.nci.cbiit.cmps.core package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Mapping_QNAME = new QName("http://cmps.cbiit.nci.nih.gov/core", "mapping");
    private final static QName _Functions_QNAME = new QName("http://cmps.cbiit.nci.nih.gov/core", "functions");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: gov.nih.nci.cbiit.cmps.core
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LinkType }
     * 
     */
    public LinkType createLinkType() {
        return new LinkType();
    }

    /**
     * Create an instance of {@link Mapping }
     * 
     */
    public Mapping createMapping() {
        return new Mapping();
    }

    /**
     * Create an instance of {@link FunctionMeta }
     * 
     */
    public FunctionMeta createFunctionMeta() {
        return new FunctionMeta();
    }

    /**
     * Create an instance of {@link AttributeMeta }
     * 
     */
    public AttributeMeta createAttributeMeta() {
        return new AttributeMeta();
    }

    /**
     * Create an instance of {@link FunctionType }
     * 
     */
    public FunctionType createFunctionType() {
        return new FunctionType();
    }

    /**
     * Create an instance of {@link Mapping.Components }
     * 
     */
    public Mapping.Components createMappingComponents() {
        return new Mapping.Components();
    }

    /**
     * Create an instance of {@link Mapping.Links }
     * 
     */
    public Mapping.Links createMappingLinks() {
        return new Mapping.Links();
    }

    /**
     * Create an instance of {@link FunctionDef }
     * 
     */
    public FunctionDef createFunctionDef() {
        return new FunctionDef();
    }

    /**
     * Create an instance of {@link ViewType }
     * 
     */
    public ViewType createViewType() {
        return new ViewType();
    }

    /**
     * Create an instance of {@link LinkpointType }
     * 
     */
    public LinkpointType createLinkpointType() {
        return new LinkpointType();
    }

    /**
     * Create an instance of {@link BaseMeta }
     * 
     */
    public BaseMeta createBaseMeta() {
        return new BaseMeta();
    }

    /**
     * Create an instance of {@link FunctionData }
     * 
     */
    public FunctionData createFunctionData() {
        return new FunctionData();
    }

    /**
     * Create an instance of {@link ElementMeta }
     * 
     */
    public ElementMeta createElementMeta() {
        return new ElementMeta();
    }

    /**
     * Create an instance of {@link Mapping.Views }
     * 
     */
    public Mapping.Views createMappingViews() {
        return new Mapping.Views();
    }

    /**
     * Create an instance of {@link Component }
     * 
     */
    public Component createComponent() {
        return new Component();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Mapping }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://cmps.cbiit.nci.nih.gov/core", name = "mapping")
    public JAXBElement<Mapping> createMapping(Mapping value) {
        return new JAXBElement<Mapping>(_Mapping_QNAME, Mapping.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link FunctionMeta }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://cmps.cbiit.nci.nih.gov/core", name = "functions")
    public JAXBElement<FunctionMeta> createFunctions(FunctionMeta value) {
        return new JAXBElement<FunctionMeta>(_Functions_QNAME, FunctionMeta.class, null, value);
    }

}
