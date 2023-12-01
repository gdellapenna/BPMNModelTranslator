/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dellapenna.personal.bpmnmodeltest;

import java.util.Map;
import org.camunda.bpm.model.dmn.DmnModelInstance;
import org.camunda.bpm.model.dmn.instance.DecisionTable;

/**
 *
 * @author giuse
 * @param <T>
 */
public interface DMNTranslator<T> {

    Map<String, T> translate(DmnModelInstance dmn) throws FeelTranslatorException;

    T translate(DecisionTable t) throws FeelTranslatorException;

}
