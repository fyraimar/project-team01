package edu.cmu.lti.deiis.project.annotator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse.Finding;
import edu.cmu.lti.oaqa.type.kb.Concept;
import edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept;
import edu.cmu.lti.oaqa.type.retrieval.ConceptSearchResult;

/**
 * Query to get the concept.
 * 
 * @author Fei Xia <feixia@cs.cmu.edu>, Zexi Mao <zexim@cs.cmu.edu, Anurag Kumar <alnu@cs.cmu.edu>
 */
public class QueryConcept extends JCasAnnotator_ImplBase {

  /**
   * Name of configuration parameter that must be set to the number of returned concepts in each
   * service.
   */
  public static final String PARAM_RESULTS_PER_PAGE = "ResultsPerPage";

  private Integer mResultsPerPage;

  // The GoPubMedService
  private GoPubMedService service;

  /**
   * Perform initialization logic. Initialize the service.
   * 
   * @see org.apache.uima.analysis_component.AnalysisComponent_ImplBase#initialize(UimaContext)
   */
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);

    mResultsPerPage = (Integer) aContext.getConfigParameterValue(PARAM_RESULTS_PER_PAGE);

    try {
      service = new GoPubMedService("project.properties");
    } catch (Exception ex) {
      throw new ResourceInitializationException();
    }
  }

  /**
   * Get the concepts and add them to JCas index
   * 
   * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<TOP> queryIter = aJCas.getJFSIndexRepository().getAllIndexedFS(
            ComplexQueryConcept.type);

    try {
      ComplexQueryConcept query = (ComplexQueryConcept) queryIter.next();

      // Get the query text
      String text = query.getWholeQueryWithOp();
      // String text = query.getWholeQueryWithoutOp();

      // Use Mesh service

      mResultsPerPage = 10;
      OntologyServiceResponse.Result meshResult = service.findMeshEntitiesPaged(text, 0,
              mResultsPerPage);

      // Add multiple sources here
      // Combine them in some way

      System.out.println(text);

      System.out.println("Mesh Results: " + meshResult.getFindings().size());
      for (OntologyServiceResponse.Finding finding : meshResult.getFindings()) {
        // System.out.println(" > " + finding.getConcept().getLabel() + " "
        // + finding.getConcept().getUri()+"\t Score"+finding.getScore());
      }

      int DOretsize = 0;
      int GOretsize = 0;
      int JOretsize = 0;
      int UOretsize = 0;

      //Double mthres = 0.1;
      //Double DOthres = 0.1;
      //Double GOthres = 0.09;
      //Double JOthres = 0.06;
      //Double UOthres = 0.06;
      
      Double mthres = 0.2;
      Double DOthres = 0.2;
      Double GOthres = 0.15;
      Double JOthres = 0.15;
      Double UOthres = 0.15;

      /*OntologyServiceResponse.Result diseaseOntologyResult = service
              .findDiseaseOntologyEntitiesPaged(text, 0, DOretsize);
      System.out.println("Disease ontology: " + diseaseOntologyResult.getFindings().size());
      for (OntologyServiceResponse.Finding finding : diseaseOntologyResult.getFindings()) {
        // System.out.println(" > " + finding.getConcept().getLabel() + " "
        // + finding.getConcept().getUri()+"\t Score"+finding.getScore());
      }

      OntologyServiceResponse.Result geneOntologyResult = service.findGeneOntologyEntitiesPaged(
              text, 0, GOretsize);
      System.out.println("Gene ontology: " + geneOntologyResult.getFindings().size());
      for (OntologyServiceResponse.Finding finding : geneOntologyResult.getFindings()) {
        // System.out.println(" > " + finding.getConcept().getLabel() + " "
        // + finding.getConcept().getUri()+"\t Score"+finding.getScore());
      }

      OntologyServiceResponse.Result jochemResult = null;
      try {
        jochemResult = service.findJochemEntitiesPaged(text, 0,
                JOretsize);
        System.out.println("Jochem: " + jochemResult.getFindings().size());
        for (OntologyServiceResponse.Finding finding : jochemResult.getFindings()) {
          // System.out.println(" > " + finding.getConcept().getLabel() + " "
          // + finding.getConcept().getUri()+"\t Score"+finding.getScore());
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

      OntologyServiceResponse.Result uniprotResult = service.findUniprotEntitiesPaged(text, 0,
              UOretsize);
      System.out.println("UniProt: " + uniprotResult.getFindings().size());
      for (OntologyServiceResponse.Finding finding : uniprotResult.getFindings()) {
        // System.out.println(" > " + finding.getConcept().getLabel() + " "
        // + finding.getConcept().getUri()+"\t Score"+finding.getScore());
      }*/

      // Add selective sources here
      // Double threshold = 0.1; //might decide to set different threshold for each service
      // List<Finding> meshPruneFinding;
      // meshPruneFinding = new ArrayList<Finding>();
      
      OntologyServiceResponse.Result diseaseOntologyResult = null;
      OntologyServiceResponse.Result geneOntologyResult = null;
      OntologyServiceResponse.Result jochemResult = null;
      OntologyServiceResponse.Result uniprotResult = null;
      
      
      List<Finding> meshPrunedFinding = pruneFindings(meshResult, mthres);

      List<Finding> DOPrunedFinding = pruneFindings(diseaseOntologyResult, DOthres);

      List<Finding> GOPrunedFinding = pruneFindings(geneOntologyResult, GOthres);

      List<Finding> JOPrunedFinding = pruneFindings(jochemResult, JOthres);

      List<Finding> UOPrunedFinding = pruneFindings(uniprotResult, UOthres);

      // Map<Double, Finding> unionFinding = new TreeMap<Double,
      // Finding>(Collections.reverseOrder());
      List<Finding> unionFinding = new ArrayList<Finding>();

      unionFinding = CombineSources(meshPrunedFinding, unionFinding);
      unionFinding = CombineSources(DOPrunedFinding, unionFinding);
      unionFinding = CombineSources(GOPrunedFinding, unionFinding);
      unionFinding = CombineSources(JOPrunedFinding, unionFinding);
      unionFinding = CombineSources(UOPrunedFinding, unionFinding);

      // sort union finding
      Collections.sort(unionFinding,
              (s1, s2) -> ((Double) s2.getScore()).compareTo((Double) s1.getScore()));

      // print the union
      // System.out.println("Printing the Union"+unionFinding.size());
      for (Finding finding : unionFinding) {

        // Double value = entry.getKey();
        // Finding finding = entry.getValue();
        // System.out.println(" > " + finding.getConcept().getLabel() + " "
        // + finding.getConcept().getUri()+"\t Score"+finding.getScore());
      }

      aJCas = addSelectedService(unionFinding, text, aJCas);

    } catch (Exception ex) {
      System.err.println("Ontology Service Exception!");
      ex.printStackTrace();
    }
  }

  private List<Finding> CombineSources(List<Finding> PrunedFinding, List<Finding> unionFinding) {

    // Map<Double, Finding> unionFinding = new TreeMap<Double, Finding>();
    for (Finding finding : PrunedFinding) {
      unionFinding.add(finding);
    }
    return unionFinding;
  }

  private List<Finding> pruneFindings(OntologyServiceResponse.Result Result, Double threshold) {

    List<Finding> prunedFinding;
    prunedFinding = new ArrayList<Finding>();
    
    if (Result == null) {
      return prunedFinding;
    }
    
    for (Finding finding : Result.getFindings()) {

      if (finding.getScore() >= threshold) {
        prunedFinding.add(finding);
      }

    }

    return prunedFinding;

  }

  private JCas addSelectedService(List<Finding> unionFinding, String text, JCas aJCas) {

    // Rank the returned concepts and add them to CAS
    int currRank = 0;
    for (Finding finding : unionFinding) {

      // Double value = entry.getKey();
      // Finding finding = entry.getValue();

      Concept concept = new Concept(aJCas);
      concept.setName(finding.getConcept().getLabel());
      concept.addToIndexes();

      ConceptSearchResult result = new ConceptSearchResult(aJCas);
      result.setConcept(concept);
      result.setUri(finding.getConcept().getUri());
      result.setScore(finding.getScore());
      result.setText(finding.getConcept().getLabel());
      result.setRank(currRank++);
      result.setQueryString(text);
      result.addToIndexes();
    }
    return aJCas;
  }
}
