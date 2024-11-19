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
public record FunctionDefinition<T>(String name, Code body, List<T> triggers, String returnType, Map<String,String> parameters) {
    
}
