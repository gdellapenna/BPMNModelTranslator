# BDTransTest Examples

To run any example, after building the docker image, type:

```console
 (On Linux) docker run -v ./examples/EXAMPLE_NAME:/usr/app/res -t bpmn-translator-and-verifier FILES

 (On Windows) docker run -v .\examples\EXAMPLE_NAME:/usr/app/res -t bpmn-translator-and-verifier FILES
```

where `EXAMPLE_NAME` is the folder for the selected example, and `FILES` are all the .bpmn and .dmn files of the example contained in the `EXAMPLE_NAME` folder.

**Please look at the information specific for the example you want to run before trying to execute it**.


