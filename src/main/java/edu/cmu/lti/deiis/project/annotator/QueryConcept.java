package edu.cmu.lti.deiis.project.annotator;

import java.util.Iterator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.bio.bioasq.services.GoPubMedService;
import edu.cmu.lti.oaqa.bio.bioasq.services.OntologyServiceResponse;
import edu.cmu.lti.oaqa.type.input.Question;

public class QueryConcept extends JCasAnnotator_ImplBase {

  GoPubMedService service;

  /**
   * Perform initialization logic. Initialize the service.
   * 
   * @param aContext
   *          the UimaContext object
   */
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    super.initialize(aContext);

    try {
      service = new GoPubMedService("project.properties");
    } catch (Exception ex) {
      throw new ResourceInitializationException();
    }
  }

  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIndex<?> QuestionIndex = aJCas.getAnnotationIndex(Question.type);
    Iterator<?> QuestionIter = QuestionIndex.iterator();

    try {
      Question question = (Question) QuestionIter.next();

      String text = question.getText();
      OntologyServiceResponse.Result meshResult = service.findMeshEntitiesPaged(text, 0);
      System.out.println("MeSH: " + meshResult.getFindings().size());
      for (OntologyServiceResponse.Finding finding : meshResult.getFindings()) {
        System.out.println(" > " + finding.getConcept().getLabel() + " "
                + finding.getConcept().getUri());
      }
      System.out.println("Query Concept!");
    } catch (Exception ex) {

    }
  }
}