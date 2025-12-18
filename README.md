# BDTransTest

> This tool is described in the following paper:
> Della Penna, G. and Melatti, I. (2025). Automating Execution and Verification of BPMN+DMN Business Processes. https://arxiv.org/abs/2512.15214 

## Building

To build the Docker image use the following command :

```console
docker build -t bpmn-translator-and-verifier .
```

## Running

To see the available options:

```console
docker run -t bpmn-translator-and-verifier -h
```

To run the process:

```console
docker run -v PATH_TO_DIR_OF_RESULTS:/usr/app/res -t bpmn-translator-and-verifier FILE_BPMN [FILES_DMN]
```

Input files `FILE_BPMN` and `FILES_DMN` must be inside `PATH_TO_DIR_OF_RESULTS`.

If you want the application to only generate the Java sources, without performing the verification part, simply add option `-v` before the `FILE_BPMN`.

### Examples

To run or the Shipment case study on Linux systems use the command

```console
docker run -v ./examples/Shipment:/usr/app/res -t bpmn-translator-and-verifier Shipment.bpmn choose_consent.dmn determine_mode.dmn get_length.dmn
```
On Windows systems, you have to specify the path differently

```console
docker run -v .\examples\Shipment:/usr/app/res -t bpmn-translator-and-verifier Shipment.bpmn choose_consent.dmn determine_mode.dmn get_length.dmn
```

To run or the Surgery case study on Linux systems use the command

```console
docker run -v ./examples/Surgery:/usr/app/res -t bpmn-translator-and-verifier Surgery.bpmn choose_consent.dmn determine_mode.dmn get_length.dmn
```
On Windows systems, you have to specify the path differently

```console
docker run -v .\examples\Surgery:/usr/app/res -t bpmn-translator-and-verifier bash main.sh Surgery.bpmn choose_consent.dmn determine_mode.dmn get_length.dmn
```

