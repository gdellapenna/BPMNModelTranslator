/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dellapenna.personal.bpmn.bpmn;

import java.util.List;
import java.util.Map;

/**
 *
 * @author giuse
 */
public record FunctionDefinition(String name, List<String> body, List<String> triggers, Class returnType, Map<String,Class> parameters) {
    
}
