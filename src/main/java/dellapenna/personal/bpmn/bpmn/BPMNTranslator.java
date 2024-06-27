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

    public T translateBpmn(BpmnModelInstance dmn) throws FeelTranslatorException, BpmnTranslatorException;

    public T translateProcess(Process p) throws FeelTranslatorException, BpmnTranslatorException;

    public T translateManualTask(ManualTask t) throws BpmnTranslatorException;

    public T translateScriptTask(ScriptTask t) throws BpmnTranslatorException;

    public T translateUserTask(UserTask t) throws BpmnTranslatorException;

    public T translateServiceTask(ServiceTask t) throws BpmnTranslatorException;

    public T translateSendTask(SendTask t) throws BpmnTranslatorException;

    public T translateReceiveTask(ReceiveTask t) throws BpmnTranslatorException;

    public T translateBusinessRuleTask(BusinessRuleTask t) throws BpmnTranslatorException;

    public T translateGenericTask(Task t) throws BpmnTranslatorException;

    public T translateEndEvent(EndEvent t) throws BpmnTranslatorException;

    public T translateStartEvent(StartEvent t) throws BpmnTranslatorException;

    public T translateParallelGateway(ParallelGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public T translateEventGateway(EventBasedGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public T translateInclusiveGateway(InclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException;
    
    public T translateExclusiveGateway(ExclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException;
    
    public T translateParallelJoiningGateway(ParallelGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public T translateEventJoiningGateway(EventBasedGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public T translateInclusiveJoiningGateway(InclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public T translateExclusiveJoiningGateway(ExclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException;

}
