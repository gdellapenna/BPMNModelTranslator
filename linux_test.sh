common_opts="-t 5"
declare -A opts
opts["./examples/surgery"]="-s any_coverage"
opts["./examples/simple/Simple_Loop_Alt"]="-O -fi:a"
opts["./examples/simple/Simple_Loop_int"]="-V Simple_Loop_override.txt"
opts["./examples/simple/Simple_Loop_float"]="-V Simple_Loop_override.txt"

for d in ./examples/surgery ./examples/surgery.noopts ./examples/shipment ./examples/simple/[^R]*
do
    echo Executing:
    echo ""
    echo docker run -v $d:/usr/app/res -t bpmn-translator-and-verifier $common_opts ${opts[$d]} `ls -1 $d/*.bpmn | awk -F/ '{print $NF}'` `ls $d/*.dmn 2> /dev/null | awk -F/ '{print $NF}'`
    echo ""
    echo Results will be in directory $d, overall log in $d.docker.log
    echo ""
    echo ""
    echo ""
    docker run -v $d:/usr/app/res -t bpmn-translator-and-verifier $common_opts ${opts[$d]} `ls -1 $d/*.bpmn | awk -F/ '{print $NF}'` `ls $d/*.dmn 2> /dev/null | awk -F/ '{print $NF}'` > $d.docker.log 2>&1
done
