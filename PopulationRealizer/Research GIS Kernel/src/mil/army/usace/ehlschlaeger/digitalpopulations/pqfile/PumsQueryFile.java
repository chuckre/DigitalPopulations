package mil.army.usace.ehlschlaeger.digitalpopulations.pqfile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import mil.army.usace.ehlschlaeger.digitalpopulations.PumsQuery;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.util.CustomMessageVEH;

import org.xml.sax.SAXException;



/**
 * Speficies the format of an XML file that describes one PumsQuery.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 *
 * @author William R. Zwicky
 */
@XmlRootElement(name = "PumsQuery")
public class PumsQueryFile implements PQNode {
    //
    // DEV NOTE:
    // The Java tool "schemagen" should be used to generate a schema for these
    // classes. Using a schema in loadFile allows JAXB to catch some errors it
    // would otherwise ignore (like extra text or elements inside another
    // element, or markers that aren't enforced in code (like
    // @XmlElement(required)). Note that the classes are considered the master
    // spec; if changes need to be made, make them in the code, then run
    // schemagen to rebuild the xsd file.
    //

    /**
     * Root condition in file. There can only be one, and it must be either an
     * &lt;or&gt; structure or a lone &lt;condition&gt;.
     */
    @XmlElements({
        @XmlElement(name="condition", type=PQCondition.class),
        @XmlElement(name="or", type=PQOr.class)})
    public PQNode condition = null;
    
    @XmlTransient
    public File sourceFile;
    

    /**
     * Create blank instance.
     */
    public PumsQueryFile() {
    }

    /**
     * Add the nodes in this object to a query.
     * 
     * @param hohSchema schema for households table
     * @param popSchema schema for population table
     * @param query object to receive query terms
     */
    public void addTo(PumsQuery query, List<String> hohSchema, List<String> popSchema) {
        PQNode node = this.condition;
        if(node instanceof PQCondition) {
            PQCondition cond = (PQCondition) node;
            PQAnd and = new PQAnd();
            and.conditions.add(cond);
            and.addTo(query, hohSchema, popSchema);
        }
        else if(node instanceof PQOr) {
            PQOr or = (PQOr) node;
            or.addTo(query, hohSchema, popSchema);
        }
        else
            throw new DataException("PQOr cannot contain objects of type "+node.getClass().getName());
    }
    
    /**
     * Write all data into a new file. If file exists, it will be overwritten.
     * 
     * @param file
     * @throws JAXBException
     * @throws IOException 
     */
    public void saveFile(File file) throws JAXBException, IOException {
        sourceFile = file;
        JAXBContext context = JAXBContext.newInstance(PumsQueryFile.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(this, file);
    }
    
    /**
     * Create a new instance from the contents of a file.
     * <P>
     * Be sure to call validate() afterwards to test the result. You can then
     * report problems to the user or dispose of the result, as appropriate.
     * 
     * @param file
     *            path and name of file to load
     * @return new object
     * @throws JAXBException
     *             on any XML error
     * @throws IOException
     *             on any file error
     * @throws SAXException
     *             on any error reading schema or validating input
     */
    public static PumsQueryFile load(File file) throws JAXBException, SAXException, IOException {
        file = file.getAbsoluteFile();

        JAXBContext context = JAXBContext.newInstance(PumsQueryFile.class);
        Unmarshaller um = context.createUnmarshaller();
        ValidationEventHandler errors = new CustomMessageVEH();
        um.setEventHandler(errors);

        try {
//WRZ no schema file for now; i'm lazy
//            String s = "PumsQueryFile.xsd";
//            URL f = PumsQueryFile.class.getResource(s);
//            if(f == null)
//                throw new FileNotFoundException("Resource "+s);
//            Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
//                .newSchema(f);
//            um.setSchema(schema);
            
            PumsQueryFile pqf = (PumsQueryFile) um.unmarshal(new FileReader(file));
            pqf.sourceFile = file;

            return pqf;
        } catch(UnmarshalException e) {
            if(e.getCause() instanceof SAXException)
                throw (SAXException)e.getCause();
            else
                throw e;
        }
    }

    /**
     * Load a query file, and build a PumsQuery from it.
     * 
     * @param file
     *            path and name of file to load
     * @param hohSchema
     *            schema for households table. Can be null, but will crash if
     *            query file references households table.
     * @param popSchema
     *            schema for population table. Can be null, but will crash if
     *            query file references population table.
     * @return new PumsQuery object
     * 
     * @throws IOException
     *             on any file or XML error
     */
    public static PumsQuery loadQuery(File file, List<String> hohSchema, List<String> popSchema) throws IOException {
        try {
            PumsQueryFile spec = load(file);
            PumsQuery query = new PumsQuery();
            spec.addTo(query, hohSchema, popSchema);
            return query;
        } catch (JAXBException e) {
            throw new IOException("Error parsing XML", e);
        } catch (SAXException e) {
            throw new IOException("Error parsing XML", e);
        }
    }
}
