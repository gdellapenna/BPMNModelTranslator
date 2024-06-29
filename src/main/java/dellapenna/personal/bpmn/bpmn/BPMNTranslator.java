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

    public T translateStatementSequence(List<T> statements) throws FeelTranslatorException, BpmnTranslatorException;
    
    public T translateBpmn(BpmnModelInstance dmn) throws FeelTranslatorException, BpmnTranslatorException;

    public T translateProcess(Process p) throws FeelTranslatorException, BpmnTranslatorException;

    public List<T> translateManualTask(ManualTask t) throws BpmnTranslatorException;

    public List<T> translateScriptTask(ScriptTask t) throws BpmnTranslatorException;

    public List<T> translateUserTask(UserTask t) throws BpmnTranslatorException;

    public List<T> translateServiceTask(ServiceTask t) throws BpmnTranslatorException;

    public List<T> translateSendTask(SendTask t) throws BpmnTranslatorException;

    public List<T> translateReceiveTask(ReceiveTask t) throws BpmnTranslatorException;

    public List<T> translateBusinessRuleTask(BusinessRuleTask t) throws BpmnTranslatorException;

    public List<T> translateGenericTask(Task t) throws BpmnTranslatorException;

    public List<T> translateEndEvent(EndEvent t) throws BpmnTranslatorException;

    public List<T> translateStartEvent(StartEvent t) throws BpmnTranslatorException;

    public List<T> translateParallelGateway(ParallelGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public List<T> translateEventGateway(EventBasedGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public List<T> translateInclusiveGateway(InclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException;
    
    public List<T> translateExclusiveGateway(ExclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException;
    
    public List<T> translateParallelJoiningGateway(ParallelGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public List<T> translateEventJoiningGateway(EventBasedGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public List<T> translateInclusiveJoiningGateway(InclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException;

    public List<T> translateExclusiveJoiningGateway(ExclusiveGateway n) throws FeelTranslatorException, BpmnTranslatorException;
    
    

}
