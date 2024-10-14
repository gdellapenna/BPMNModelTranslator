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

    public T generateBpmnSource(BPMNDecoded bpmn);

    public BPMNDecoded decodeBpmn(BpmnModelInstance dmn) throws FeelTranslatorException, BpmnTranslatorException;

    public BPMNDecodedProcess decodeProcessNode(Process p) throws FeelTranslatorException, BpmnTranslatorException;

    public Code generateManualTaskCode(ManualTask t) throws BpmnTranslatorException;

    public Code generateScriptTaskCode(ScriptTask t) throws BpmnTranslatorException;

    public Code generateUserTaskCode(UserTask t) throws BpmnTranslatorException;

    public Code generateServiceTaskCode(ServiceTask t) throws BpmnTranslatorException;

    public Code generateSendTaskCode(SendTask t) throws BpmnTranslatorException;

    public Code generateReceiveTaskCode(ReceiveTask t) throws BpmnTranslatorException;

    public Code generateBusinessRuleTaskCode(BusinessRuleTask t) throws BpmnTranslatorException;

    public Code generateGenericTaskCode(Task t) throws BpmnTranslatorException;

    public Code generateEndEventCode(EndEvent t) throws BpmnTranslatorException;

    public Code generateStartEventCode(StartEvent t) throws BpmnTranslatorException;

    public Code generateParallelGatewayCode(ParallelGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public Code generateEventGatewayCode(EventBasedGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public Code generateInclusiveGatewayCode(InclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public Code generateExclusiveGatewayCode(ExclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public Code generateParallelJoiningGatewayCode(ParallelGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public Code generateEventJoiningGatewayCode(EventBasedGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public Code generateInclusiveJoiningGatewayCode(InclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public Code generateExclusiveJoiningGatewayCode(ExclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException;

}
