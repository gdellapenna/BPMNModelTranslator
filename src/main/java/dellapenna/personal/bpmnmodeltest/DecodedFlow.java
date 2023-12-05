/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dellapenna.personal.bpmnmodeltest;

/**
 *
 * @author giuse
 */
public record DecodedFlow<T>(String flowId, String condition, T flow) {

}
