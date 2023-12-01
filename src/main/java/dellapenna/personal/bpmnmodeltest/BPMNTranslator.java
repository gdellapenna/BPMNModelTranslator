/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package dellapenna.personal.bpmnmodeltest;

import java.util.Map;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.StartEvent;

/**
 *
 * @author giuse
 */
interface BPMNTranslator<T> {
    T translate(StartEvent s) throws FeelTranslatorException;
    Map<String, T> translate(BpmnModelInstance bpmn) throws FeelTranslatorException;
}
