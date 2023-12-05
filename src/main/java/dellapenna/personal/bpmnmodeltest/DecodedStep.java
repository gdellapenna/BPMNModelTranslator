/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dellapenna.personal.bpmnmodeltest;

import org.camunda.bpm.model.bpmn.instance.FlowNode;

/**
 *
 * @author giuse
 */
public record DecodedStep<T>(String flowId, T step, FlowNode nextStep) {

}
