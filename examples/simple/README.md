# BDTransTest Simple Examples

This folder contains very simple example, mostly used to test the BDTest translation for specific BPMN elements.

## Simple_Loop(_Alt)

These examples contain a simple BPMN loop where the loop variable is updated in a script task using a FEEL expression.

The **Simple_Loop** example contains a case where the tool is unable to detect the input variable (*input_a*) domain, 
since it is neve directly involved in any meaningful expression. 

To run it, you must override the generated input properties file and initialize *input_a* using the following command line:

```console
docker run -v ./examples/simple/:/usr/app/res -t bpmn-translator-and-verifier -t 60 -O Simple_Loop_override.txt Simple_Loop.bpmn
```

The **Simple_Loop_Alt** example contains a case where the tool is unable to detect an input variable, since it is reused and reassigned in the process.
To run it, you must force the variable *a* to be inluded in the input properties (and initialized) using the following command line:

```console
docker run -v ./examples/simple/:/usr/app/res -t bpmn-translator-and-verifier -t 60 -fi a Simple_Loop.bpmn
```