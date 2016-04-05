package mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.fittingcriteria;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.FileRelationship;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.Forbid;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.PumsTrait;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.PumsTrait.Type;
import mil.army.usace.ehlschlaeger.digitalpopulations.censusgen.filerelationship.Trait;
import mil.army.usace.ehlschlaeger.rgik.core.DataException;
import mil.army.usace.ehlschlaeger.rgik.util.CustomMessageVEH;
import mil.army.usace.ehlschlaeger.rgik.util.FileUtil;
import mil.army.usace.ehlschlaeger.rgik.util.ObjectUtil;

import org.xml.sax.SAXException;



/**
 * Describes how to use the contents of a relationship file to guide a specific
 * run of Digital Populations 'censusgen'.
 * <p>
 * Copyright <a href="http://faculty.wiu.edu/CR-Ehlschlaeger2/">Charles R.
 * Ehlschlaeger</a>, work: 309-298-1841, fax: 309-298-3003, This software is
 * freely usable for research and educational purposes. Contact C. R.
 * Ehlschlaeger for permission for other purposes. Use of this software requires
 * appropriate citation in all published and unpublished documentation.
 * 
 * @author William R. Zwicky
 */
@XmlRootElement(name = "FittingCriteria")
public class FittingCriteria {
    /**
     * Path and name of dprxml that lists our input files and their
     * relationships.
     */
    @XmlAttribute(required=true)
    public String relationshipFile;

    /**
     * Verify that the relationship file is unchanged. Master list is in
     * FileRelationship. This list must precisely match it to convince DP that
     * this file is in sync with that one.
     */
    @XmlElementWrapper(name = "traits", required=true)
    @XmlElements({
        @XmlElement(name="trait", type=PumsTrait.class),
        @XmlElement(name="forbid", type=Forbid.class)})
    protected List<Trait> traits;

    /**
     * Relative importance of traits.  Used by all phases.
     */
    @XmlElement(name = "weights", required=true)
    protected Weights     weights;

    /**
     * Adjustments to phase 1.
     */
    @XmlElement(name = "expansion-factor", required=false)
    public    ExpansionFactor phase1;

    /**
     * Specs for phase 4.
     */
    @XmlElement(name = "position-rules", required=false)
    public PreciseLocation phase4;

    
    //----------
    // Transient Working Data
    //
    /** Absolute path to file from which we were loaded. */
    @XmlTransient public File sourceFile;
    /** Time stamp on source File when we last loaded or saved. */
    @XmlTransient public Date sourcFileTime;
    /** Contents of relationship file named in our file. */
    @XmlTransient public FileRelationship relationship;
    /**
     * Weighting value to apply to location traits (which are internally
     * generated for every run).
     */
    @XmlTransient public double locationWeight;
    // Dev note: LinkedHashMap is necessary as HashMap.keySet() returns traits
    // in random order. Not hash order, RANDOM order -- the ordering changes
    // every few hours. This is of course impossible, but has been observed
    // repeatedly.
    /**
     * Mapping from traits listed in the weights section to the weighting
     * values assigned to them.
     */
    @XmlTransient public LinkedHashMap<PumsTrait, Double> traitWeights;
    /**
     * Mapping from traits to their clustering specs.
     */
    @XmlTransient public LinkedHashMap<Trait,TraitRefElement> traitCluster;
    //----------

    
    /** Construct new blank instance. */
    public FittingCriteria() {
        this.sourcFileTime = new Date();
        // To get default value, just create and see.
        this.locationWeight = new Weights().locationWeight;
    }
    
    /**
     * Create instance, partially populated for convenience.
     * @return
     */
    public FittingCriteria neu() {
        FittingCriteria inst = new FittingCriteria();
        inst.traits = new ArrayList<Trait>();
        inst.relationship = FileRelationship.neu();
        inst.traitWeights = new LinkedHashMap<PumsTrait, Double>();
        return inst;
    }

    /**
     * Return absolute path to referenced relationship file.
     * @return absolute path, or null if not set
     */
    public File getRelationshipFile() {
        if(relationship.sourceFile != null)
            return relationship.sourceFile;

        File parent;
        if(sourceFile != null)
            parent = sourceFile.getParentFile();
        else
            parent = new File(".").getAbsoluteFile();

        File relFile = FileUtil.resolve(parent, relationshipFile);
        return relFile;
    }

    /**
     * Report whether fitting criteria file contains clustering specs.
     * 
     * @return true if there is a non-empty cluster element in the fitting
     *         criteria file
     */
    public boolean hasCluster() {
        return traitCluster != null && !traitCluster.isEmpty();
    }
    
    /**
     * Find a project file, relative to our file's location.
     * 
     * @param path relative path to file to find
     * @return absolute path to file
     * @throws NullPointerException if we don't have a file location
     */
    public File findFile(String path) {
        return FileUtil.resolve(sourceFile.getParentFile(), path);
    }
    
    /**
     * Assemble a list of all columns that this fitting-criteria actually needs
     * from the population table.
     *
     * @return list of referenced column names
     */
    public Set<String> getRefdPopCols() {
        Set<String> cols = new HashSet<String>();
        cols.add(relationship.population.household);
        for (PumsTrait t : traitWeights.keySet()) {
            if(t.pumsTraitTable == Type.POPULATION)
                cols.add(t.pumsTraitField);
            if(t.pumsTotalTable == Type.POPULATION)
                cols.add(t.pumsTotalField);
        }
        return cols;
    }

    /**
     * Create formatted XML from this object.
     * 
     * @param out strem to receive XML
     * @throws JAXBException on any XML error
     */
    public void dumpXML(PrintStream out) throws JAXBException {
        preSave();
        
        JAXBContext context = JAXBContext.newInstance(FittingCriteria.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(this, out);

        postSave();
    }

    /**
     * Perform simple validation.
     */
    public void validate() {
        DataException.verifyNotBlank(this.relationshipFile, "'relationshipFile' attribute is mandatory");
        DataException.verifyNotNull(this.traits, "'traits' attribute is mandatory");
        DataException.verifyNotNull(this.weights, "'weights' attribute is mandatory");
    }
    
    /**
     * Write all data into a new file. If file exists, it will be overwritten.
     *
     * @param file file to create
     * @throws JAXBException on any XML error
     * @throws FileNotFoundException if file cannot be created
     *
     * @see sourceFile
     */
    public void saveFile(File file) throws JAXBException, IOException {
        file = file.getAbsoluteFile();
        sourceFile = file;
        if(relationship.sourceFile == null)
            relationshipFile = null;
        else
            relationshipFile = FileUtil.relativize(sourceFile.getParentFile(), relationship.sourceFile);

        File tmp = File.createTempFile(this.getClass().getSimpleName()+".", ".tmp", file.getParentFile());
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(tmp));
            dumpXML(out);
            out.close();
            file.delete();
            tmp.renameTo(file);
        }
        finally {
            // ensure file is closed, else future attempts to access will fail
            if(out != null)
                out.close();
        }
        sourcFileTime = new Date(file.lastModified());
    }

    /**
     * Move transient fields into savable spots.
     */
    protected void preSave() {
        // 'traits' must be a perfect copy from the relationship file.
        traits = new ArrayList<Trait>(this.relationship.traits);
        // 'weights' contains only the enabled traits.
        weights = new Weights();
        weights.locationWeight = locationWeight;

        // Cut spurious entries from map.
        traitWeights.keySet().retainAll(traits);
        // Rebuild weight elements.
        for (PumsTrait trait : traitWeights.keySet()) {
            TraitWeight tw = new TraitWeight(trait.id, null, null, traitWeights.get(trait));
            tw.validate();
            weights.weights.add(tw);
        }

        // Cut spurious entries from map.
        if (traitCluster == null || traitCluster.isEmpty()) {
            phase4 = null;
        }
        else {
            traitCluster.keySet().retainAll(traits);
            // Rebuild cluster elements.
            phase4 = new PreciseLocation();
            for(Trait trait : traitCluster.keySet()) {
                phase4.add(traitCluster.get(trait));
            }
        }
    }

    /**
     * Clear things that are needed to save, but useless during runtime.
     */
    protected void postSave() {
        traits = null;
        weights = null;
    }

    /**
     * Load reference file, and rearrange things in a more usable form.
     * 
     * @throws JAXBException
     *             on any XML error
     * @throws IOException
     *             on any file error
     * @throws SAXException
     *             on any error reading schema or validating input
     * @throws DataException
     *             on any error interpreting contents of input file. Note that
     *             if this exception is thrown, this object will still be
     *             usable, and will contain as much of the input file as we were
     *             able to recover. Catch and ignore if the recovered data is
     *             acceptible.
     */
    protected void postLoad() throws JAXBException, IOException, SAXException {
        ArrayList<String> errs = new ArrayList<String>();

        // No try/catch/append; I want these first ones to crash the program.
        validate();
        
        File relFile = FileUtil.resolve(sourceFile.getParentFile(), relationshipFile);
        relationship = FileRelationship.loadFile(relFile);
        relationship.validate();

        // Verify our traits are in rel file
        for(Trait trait : traits) {
            int p = relationship.traits.indexOf(trait);
            if(p < 0)
                errs.add("Trait record does not match any in relationship file: " + trait);
        }
        if(traits.size() != relationship.traits.size())
            errs.add("Fitting criteria <traits> element doesn't contain every trait in the relationship file.");
        
        // Nothing useful here, nuke it.
        traits = null;

        // Validate trait weights.
        try {
            weights.validate();
        } catch (Exception e) {
            errs.add(ObjectUtil.getMessage(e));
        }
        
        // Build map of Trait->weight
        traitWeights = new LinkedHashMap<PumsTrait, Double>();
        for (TraitWeight weight : weights.weights) {
            Trait trait = weight.find(relationship.traits);
            if(trait == null)
                errs.add("Referenced trait not defined in relationship file: "+weight);
            else if(!(trait instanceof PumsTrait))
                errs.add("Only plain <trait> elements can be weighted: "+weight);
            else {
                traitWeights.put((PumsTrait) trait, weight.weight);
            }
        }

        this.locationWeight = weights.locationWeight;

        // Also useless.
        weights = null;

        // Validate phase 1 specs.
        try {
            if(phase1 != null)
                phase1.validate();
        } catch (Exception e) {
            errs.add(ObjectUtil.getMessage(e));
        }
        
        if(phase4 != null) {
            // Validate cluster specs.
            try {
                phase4.validate();
            } catch (Exception e) {
                errs.add(ObjectUtil.getMessage(e));
            }
        
            // Build map of Trait->ClusterSpec
            traitCluster = new LinkedHashMap<Trait, TraitRefElement>();
            if(phase4 != null && phase4.traits != null) {
                //
                // Sort elements.
                //
                ArrayList<TraitRefElement> sorted = new ArrayList<TraitRefElement>(phase4.traits);
                Collections.sort(sorted, new Comparator<TraitRefElement>() {
                    // <match> elements with 'rank' should be in order of rank
                    // all other elements retain file order
                    public int compare(TraitRefElement o1, TraitRefElement o2) {
                        if(o1 instanceof MatchSpec && o2 instanceof MatchSpec) {
                            MatchSpec m1 = (MatchSpec)o1;
                            MatchSpec m2 = (MatchSpec)o2;
                            if(m1.rank == null) {
                                if(m2.rank == null)
                                    return 0;
                                else
                                    return +1;
                            }
                            else {
                                if(m2.rank == null)
                                    return -1;
                                else
                                    return Integer.signum(m1.rank - m2.rank);
                            }
                        }
                        else {
                            // just to be nice, ensure <cluster> all appear before <match>
                            int n1 = o1 instanceof ClusterSpec ? -1 : +1;
                            int n2 = o2 instanceof ClusterSpec ? -1 : +1;
                            return Integer.signum(n1 - n2);
                        }
                    }
                });

                //
                // Attach each spec to it's trait.
                //
                for(TraitRefElement spec : sorted) {
                    Trait trait = spec.find(relationship.traits);
                    if(trait == null)
                        errs.add("Referenced trait not defined in relationship file: "+spec);
                    traitCluster.put(trait, spec);
                }
    
                // Now useless.
                phase4 = null;
            }
        }
        
        // If problems, throw an exception.  We only throw one exception at the
        // end so that it contains every error we could find.  Also, if user
        // catches and ignores exception, we will contain all the data we
        // could recover.
        if(!errs.isEmpty())
            throw new DataException("Problems while loading fitting criteria:\n  " + ObjectUtil.join(errs, "\n  "));
    }

    /**
     * Create a new instance from the contents of a file.
     * 
     * @param file
     *            path and name of file to load
     * @param messages
     *            container to receive recoverable errors. If not null and
     *            validation fails, a description of the problem will be added
     *            here and data will be recovered as much as possible. If null
     *            and validation fails, an exception will be thrown.
     * @return new object
     * 
     * @throws JAXBException
     *             on any XML error
     * @throws IOException
     *             on any file error
     * @throws SAXException
     *             on any error reading schema or validating input
     * @throws DataException
     *             if messages == null and error interpreting contents of input
     *             file
     */
    public static FittingCriteria loadFile(File file, List<String> messages)
            throws JAXBException, IOException, SAXException {
        file = file.getAbsoluteFile();
        
        JAXBContext context = JAXBContext.newInstance(FittingCriteria.class);
        Unmarshaller um = context.createUnmarshaller();
        
        ValidationEventHandler errors = new CustomMessageVEH(file.toString());
        um.setEventHandler(errors);
        
        try {
            String s = "FittingCriteria.xsd";
            URL f = FittingCriteria.class.getResource(s);
            if(f == null)
                throw new FileNotFoundException("Resource "+s);
            Schema schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema(f);
            um.setSchema(schema);
            
            FittingCriteria crit = (FittingCriteria) um.unmarshal(new FileReader(file));
            crit.sourceFile = file;
            crit.sourcFileTime = new Date(file.lastModified());

            if(messages == null)
                // No container, let exceptions propagate.
                crit.postLoad();
            else {
                // Container provided; save errors there and return normally.
                try {
                    crit.postLoad();
                }
                catch(DataException e) {
                    messages.add(e.getMessage());
                }
            }

            return crit;
        } catch(UnmarshalException e) {
            // If "soft" open is desired, then add exception to messages,
            // setSchema(null), and unmarshal again.
            if(e.getCause() instanceof SAXException)
                throw (SAXException)e.getCause();
            else
                throw e;
        }
    }

    /** Simple exerciser for debugging. */
    public static void main(String[] args) throws Exception {
        loadFile(new File("projects/RhodeIslandDigitalPopulations/data/fittingCriteria.dpfxml"), null).dumpXML(System.out);
    }
}
