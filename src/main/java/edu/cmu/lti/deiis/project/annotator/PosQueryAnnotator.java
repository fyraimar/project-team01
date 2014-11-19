/**
 * 
 */
package edu.cmu.lti.deiis.project.annotator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.lti.oaqa.type.input.Question;
import edu.cmu.lti.oaqa.type.retrieval.AtomicQueryConcept;
import edu.cmu.lti.oaqa.type.retrieval.ComplexQueryConcept;
import util.PosTagNamedEntityRecognizer;
import util.Utils;

/**
 * A annotator that generates queries based on the POS tag NER results.
 * 
 * @author Zexi Mao <zexim@cs.cmu.edu>
 *
 */
public class PosQueryAnnotator extends JCasAnnotator_ImplBase {
  
  private PosTagNamedEntityRecognizer mRecognizer;
  
  /**
   * Initialize a POS tag named entity recognizer.
   * 
   * @see org.apache.uima.analysis_component.AnalysisComponent_ImplBase#initialize(UimaContext)
   */
  @Override
  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    try {
      mRecognizer = new PosTagNamedEntityRecognizer();
    } catch (ResourceInitializationException e) {
      e.printStackTrace();
    }
  }

  /** 
   * Generate the query strings and put them into ComplexQueryConcept.
   * 
   * @see org.apache.uima.analysis_component.JCasAnnotator_ImplBase#process(org.apache.uima.jcas.JCas)
   */
  @Override
  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    FSIterator<Annotation> iter = aJCas.getAnnotationIndex(Question.type).iterator();
    
    if (iter.isValid() && iter.hasNext()) {
      // Get the question first
      Question question = (Question) iter.next();
      String queString = question.getText();
      
      TreeMap<Integer, Integer> begin2end = (TreeMap<Integer, Integer>) mRecognizer.getGeneSpans(queString);
      for (Map.Entry<Integer, Integer> entry : begin2end.entrySet()) {
        // Create an atomic query first
        AtomicQueryConcept atomicQuery = new AtomicQueryConcept(aJCas);
        atomicQuery.setText(queString.substring(entry.getKey(), entry.getValue()));
        atomicQuery.addToIndexes();
      }
    }

  }

}