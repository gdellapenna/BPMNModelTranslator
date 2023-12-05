/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dellapenna.personal.bpmn.bpmn;

import java.util.Map;

/**
 *
 * @author giuse
 */
public record FuctionDefinition(String name, String body, Class returnType, Map<String,Class> parameters) {
    
}
