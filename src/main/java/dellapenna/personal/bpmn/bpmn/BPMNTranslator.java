package dellapenna.personal.bpmn.bpmn;

import dellapenna.personal.bpmn.feel.FeelTranslatorException;
import java.util.List;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BusinessRuleTask;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.InclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.ManualTask;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.EventBasedGateway;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.ReceiveTask;
import org.camunda.bpm.model.bpmn.instance.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.SendTask;
import org.camunda.bpm.model.bpmn.instance.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.Process;

/**
 *
 * @author giuse
 * @param <T>
 */
public interface BPMNTranslator<T> {

    public T generateCompoundStatementCode(List<T> statements) throws FeelTranslatorException, BpmnTranslatorException;
    
    public BPMNDecoded<T> decodeBpmn(BpmnModelInstance dmn) throws FeelTranslatorException, BpmnTranslatorException;

    public BPMNDecodedProcess<T> decodeProcessNode(Process p) throws FeelTranslatorException, BpmnTranslatorException;

    public Code<T> generateManualTaskCode(ManualTask t) throws BpmnTranslatorException;

    public Code<T> generateScriptTaskCode(ScriptTask t) throws BpmnTranslatorException;

    public Code<T> generateUserTaskCode(UserTask t) throws BpmnTranslatorException;

    public Code<T> generateServiceTaskCode(ServiceTask t) throws BpmnTranslatorException;

    public Code<T> generateSendTaskCode(SendTask t) throws BpmnTranslatorException;

    public Code<T> generateReceiveTaskCode(ReceiveTask t) throws BpmnTranslatorException;

    public Code<T> generateBusinessRuleTaskCode(BusinessRuleTask t) throws BpmnTranslatorException;

    public Code<T> generateGenericTaskCode(Task t) throws BpmnTranslatorException;

    public Code<T> generateEndEventCode(EndEvent t) throws BpmnTranslatorException;

    public Code<T> generateStartEventCode(StartEvent t) throws BpmnTranslatorException;

    public Code<T> generateParallelGatewayCode(ParallelGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public Code<T> generateEventGatewayCode(EventBasedGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public Code<T> generateInclusiveGatewayCode(InclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException;
    
    public Code<T> generateExclusiveGatewayCode(ExclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException;
    
    public Code<T> generateParallelJoiningGatewayCode(ParallelGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public Code<T> generateEventJoiningGatewayCode(EventBasedGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public Code<T> generateInclusiveJoiningGatewayCode(InclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public Code<T> generateExclusiveJoiningGatewayCode(ExclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException;
    
    

}
