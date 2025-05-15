epsilon_def=0.001
delta_def=0.001
N_def=10
min_float_def="-1000"
max_float_def="1000"
min_int_def="-1000"
max_int_def="1000"
min_eps_def=0.0001
int_timeout_def=120
nodes_coverage_def="0.95"
edges_coverage_def="0.95"
stop_type_def="errors"
continue_from_before_def=0
only_gen_java_def=0
override_vars_def=""
other_opts_cmp_def=""
other_opts_exec_def=""

dir_res=/usr/app/res
dir_tmp=/usr/app/tmp
JAVA=java
JAR=$(realpath ./BPMNModelTranslator-1.0-SNAPSHOT-shaded.jar)

echo Process $$ invoked with
echo $*

function usage ()
{
    echo $0 | awk '{printf("Usage: %s [-C] [-v] [-e eps] [-d delta] [-N num_tests] [-m min_float] [-M max_float] [-z min_int] [-Z max_int] [-E min_eps] [-t timeout] [-s stop_type] [-n nodes_coverage] [-c edges_coverage] [-V override_vars_file] [-O other_opts_compiler] [-o other_opts_exec] [-h] input_bpmn [input_dmns]\n\n\n\tnum_test def: '$N_def'\n\teps def (only considered if num_test=0): '$epsilon_def'\n\tdelta def (only considered if num_test=0): '$delta_def'\n\tmin_int def: '$min_int_def'\n\tmax_int def: '$max_int_def'\n\tmin_float def: '$min_float_def'\n\tmax_float def: '$max_float_def'\n\tmin_eps def: '$min_eps_def'\n\ttimeout def: '$int_timeout_def'\n\tstop_type def (admissible values: errors, any_coverage, both_coverage, nodes_coverage, edges_coverage, nostop): '$stop_type_def'\n\tnodes_coverage def (only if -s any_coverage, both_coverage or nodes_coverage): '$nodes_coverage_def'\n\tedges_coverage def (only if -s any_coverage, both_coverage or edges_coverage): '$edges_coverage_def'\n\tother_opts_compiler def: '$other_opts_compiler'\n\tother_opts_exec def: '$other_opts_exec'\n\tIf -C is given and results are already present in the directory chosen for the results (see README.md), continue from that point\n\tIf -v is given, verification is not performed, i.e., the tool only generates the java source files\n\toverride_vars_file must be of the same format of inputs.properties file\n\tin other_opts_compiler and other_opts_exec, replace spaces with colons :\n\nAll input files must be in the directory chosen for the results\n\n\n", $1);}'
    exit 1
}

epsilon=$epsilon_def
delta=$delta_def
min_float=$min_float_def
max_float=$max_float_def
min_int=$min_int_def
max_int=$max_int_def
min_eps=$min_eps_def
int_timeout=$int_timeout_def
nodes_coverage=$nodes_coverage_def
edges_coverage=$edges_coverage_def
stop_type=$stop_type_def
continue_from_before=$continue_from_before_def
only_gen_java=$only_gen_java_def
N=$N_def
override_vars=$override_vars_def
other_opts_cmp=$other_opts_cmp_def
other_opts_exec=$other_opts_exec_def
h=0
while getopts :hCvN:e:d:m:M:E:t:D:T:s:n:c:V:o:O:z:Z: OPT
do
  case "$OPT" in
    C)
      continue_from_before=1
      ;;
    v)
      only_gen_java=1
      ;;
    N)
      N=$OPTARG
      ;;
    e)
      epsilon=$OPTARG
      ;;
    d)
      delta=$OPTARG
      ;;
    m)
      min_float=$OPTARG
      ;;
    M)
      max_float=$OPTARG
      ;;
    z)
      min_int=$OPTARG
      ;;
    Z)
      max_int=$OPTARG
      ;;
    E)
      min_eps=$OPTARG
      ;;
    t)
      int_timeout=$OPTARG
      ;;
    s)
      stop_type=$OPTARG
      ;;
    n)
      edges_coverage=$OPTARG
      ;;
    c)
      nodes_coverage=$OPTARG
      ;;
    V)
      override_vars=$OPTARG
      ;;
    O)
      other_opts_cmp=$OPTARG
      ;;
    o)
      other_opts_exec=$OPTARG
      ;;
    h)
      h=1
      ;;
    \?)
      # getopts issues an error message
      usage
      ;;
  esac
done
if [ $h -eq 1 ]
then
    usage
fi

shift $((OPTIND - 1))
if [ $# -lt 1 ]
then
    echo ""
    echo Please provide at least one argument as the BPMN to be verified
    echo ""
    echo ""
    usage
fi

test -d $dir_res || { echo Directory $dir_res must be mounted before launching; exit; }

if [ "${other_opts_exec}" ]
then
    other_opts_exec=`echo $other_opts_exec | tr ":" " "`
fi
if [ "${other_opts_cmp}" ]
then
    other_opts_cmp=`echo $other_opts_cmp | tr ":" " "`
fi

bpmn_input_file=$dir_res/$1
shift 1
for file in $*
do
    dmn_input_files=$dmn_input_files" "$dir_res/$1
    shift 1
done
if [ "${override_vars}" ]
then
    override_vars=$dir_res/$override_vars
    test -f $override_vars || { echo `basename $override_vars` does not exist inside directory $dir_res; exit; }
fi

for file in $bpmn_input_file $dmn_input_files
do
    test -f $file || { echo `basename $file` does not exist inside directory $dir_res; exit; }
done
mkdir -p $dir_tmp

function init_all ()
{
    local output_name=`grep bpmn:process $bpmn_input_file | awk '{for (i = 1; i <= NF; i++) {if (substr($i, 1, 3) == "id=") {split($i, a, "\""); print a[2]}}}'`
    cp $bpmn_input_file $dmn_input_files $dir_tmp
    local inputs=`basename $bpmn_input_file`
    inputs=$inputs" "`ls -1 $dir_tmp/*.dmn 2> /dev/null | awk -F/ '{print $NF}' | tr "\n" " "`
    pushd $dir_tmp > /dev/null 2>&1
    ln -s $JAR
    # echo $JAVA -jar ./BPMNModelTranslator-1.0-SNAPSHOT-shaded.jar $other_opts_cmp $inputs > just_to_be_sure.sh
    # cat just_to_be_sure.sh 1>&2
    # bash just_to_be_sure.sh > $dir_res/init.log 2>&1
    echo $JAVA -jar ./BPMNModelTranslator-1.0-SNAPSHOT-shaded.jar $other_opts_cmp $inputs 1>&2
    $JAVA -jar ./BPMNModelTranslator-1.0-SNAPSHOT-shaded.jar $other_opts_cmp $inputs > $dir_res/init.log 2>&1
    popd > /dev/null 2>&1
    test -f $dir_tmp/${output_name}_inputs.properties || { echo Generation failed, $dir_tmp/${output_name}_inputs.properties does not exist; exit; }
    mkdir -p $dir_res/translation_output
    cp $dir_tmp/${output_name}.java $dir_tmp/${output_name}.jar $dir_tmp/${output_name}_inputs.properties $dir_tmp/$output_name.graph $dir_res/translation_output
    mv $dir_res/init.log $dir_res/translation_output/log
    echo ${output_name}
}

function run ()
{
    local t=$1
    local bpmn=$2
    local dir_res=$3
    local inp_file=$4
    local output_name=$5
    cd $dir_tmp
    rm -rf exec
    mkdir exec
    cd exec
    cp $inp_file ${output_name}_inputs.properties
    cp $JAR .
    ln -s ../$output_name.* .
    timeout $int_timeout $JAVA -jar ./$output_name.jar $other_opts_exec > $dir_res/logs/$t/exec.log 2>&1
    res=$?
    cp $dir_tmp/exec/*output* $dir_tmp/exec/*trace $dir_res/logs/$t 2> /dev/null
    return $res
}

function gen_curr_input ()
{
    local prop_file=$1
    local output_file=$2
    local override_vars=$3
    python3 > $output_file <<EOF
import random

def gen_all_inputs(content, content_overr, min_int, max_int, min_float, max_float, min_eps):
  content_overr_ar = {}
  for i in range(len(content_overr)):
    if i%2 == 0:
      save = content_overr[i].split("=")[0]
    else:
      content_overr_ar[save] = content_overr[i]
  for i in range(len(content)):
    if i%2 == 0:
      save = content[i].split("=")[0]
    else:
      if save in content_overr_ar:
        type_str = content_overr_ar[save]
      else:
        type_str = content[i]
      if "UNHANDLED" in type_str:
        print("Unable to determine values for " + str(save))
        print(type_str)
      elif type_str.split(":")[0] == "#ENUM":
        print(save + "=" + str(random.choice(type_str.split(": ")[1].split(","))))
        print(type_str)
      elif type_str.split(":")[0] == "#MIN":
        min_ch = float(type_str.split()[1])
        max_ch = float(max_float)
        print(save + "=" + str(random.uniform(min_ch + (min_eps if (type_str.split()[2] == "INCLUSIVE") else 0), max_ch)))
        print(type_str)
      elif type_str.split(":")[0] == "#MAX":
        max_ch = float(type_str.split()[1])
        min_ch = float(-max_float)
        print(save + "=" + str(random.uniform(min_ch, max_ch - (min_eps if (type_str.split()[2] == "INCLUSIVE") else 0))))
        print(type_str)
      elif type_str.split(":")[0] == "#RANGE":
        min_ch = float(type_str.split()[1].split(",")[0][1:])
        incl_mm = [type_str.split()[1].split(",")[0][0] == "["]
        max_ch = float(type_str.split()[1].split(",")[1][:-1])
        incl_mm += [type_str.split()[1].split(",")[1][-1] == "]"]
        print(save + "=" + str(random.uniform(min_ch + (min_eps if incl_mm[0] else 0), max_ch + (min_eps if incl_mm[1] else 0))))
        print(type_str)
      elif type_str.split("(")[0] == "#BALL":
        val = float(type_str.split("(")[1][:-1])
        choices = [val]
        choices += [random.uniform(min_float, val - min_eps)]
        choices += [random.uniform(val + min_eps, max_float)]
        print(save + "=" + str(random.choice(choices)))
        print(type_str)
      elif type_str.split(":")[0] == "#MIN_INT":
        min_ch = int(type_str.split()[1])
        max_ch = int(max_float)
        print(save + "=" + str(random.randint(min_ch + (1 if (type_str.split()[2] == "INCLUSIVE") else 0), max_ch)))
        print(type_str)
      elif type_str.split(":")[0] == "#MAX_INT":
        max_ch = int(type_str.split()[1])
        min_ch = int(-max_float)
        print(save + "=" + str(random.randint(min_ch, max_ch - (1 if (type_str.split()[2] == "INCLUSIVE") else 0))))
        print(type_str)
      elif type_str.split(":")[0] == "#RANGE_INT":
        min_ch = int(type_str.split()[1].split(",")[0][1:])
        incl_mm = [type_str.split()[1].split(",")[0][0] == "["]
        max_ch = int(type_str.split()[1].split(",")[1][:-1])
        incl_mm += [type_str.split()[1].split(",")[1][-1] == "]"]
        print(save + "=" + str(random.randint(min_ch + (1 if incl_mm[0] else 0), max_ch + (1 if incl_mm[1] else 0))))
        print(type_str)
      elif type_str.split("(")[0] == "#BALL_INT":
        val = int(type_str.split("(")[1][:-1])
        choices = [val]
        choices += [random.randint(min_int, val - 1)]
        choices += [random.randint(val + 1, max_int)]
        print(save + "=" + str(random.choice(choices)))
        print(type_str)
      else:
        print("Internal error, unknown specification " + str(save))
        print(type_str)


content = """`cat $prop_file`""".split("\n")
content_overr = """`test "$override_vars" && cat $override_vars`""".split("\n")
gen_all_inputs(content, content_overr, $min_int, $max_int, $min_float, $max_float, $min_eps)
EOF
    grep -q "Unable to determine values for " $output_file
    return $?
}

#sets nodes, labels, edges, num_nodes, num_edges
function load_graph ()
{
    local graph_file=$1
    num_nodes=0
    num_edges=0
    for n in $(fgrep "[" $graph_file | awk -F'[' '{print $1}')
    do
	test "${nodes[$n]}" || ((num_nodes++))
	nodes[$n]=0
	labels[$n]=`grep -w $n $graph_file | head -1 | awk -F'"' '{print $2}'`
    done
    for e in $(fgrep " -> " $graph_file | awk '{print $1","$3}' | tr -d '"')
    do
	test "${edges[$e]}" || ((num_edges++))
	edges[$e]=0
    done
}

function update_arrays ()
{
    local trace_file=$1
    for e in $(fgrep " -> " $trace_file | awk -F'"' '{print $2","$4}')
    do
	((edges[$e]++))
    done
    for n in $(fgrep -v " -> " $trace_file | awk -F'[' '{print $1}')
    do
	((nodes[$n]++))
    done
}

function update_coverage ()
{
    local dir_res=$1
    local num_nodes=$2
    local num_edges=$3
    for n in ${!nodes[*]}
    do
	echo $n:" "${nodes[$n]}
    done > $dir_res/nodes_coverage.txt
    for e in ${!edges[*]}
    do
	echo $e:" "${edges[$e]}
    done > $dir_res/edges_coverage.txt
    cov_n=`grep ": 0" $dir_res/nodes_coverage.txt | wc -l | awk '{printf("%lf", (1 - $1/'$num_nodes'));}'`
    cov_e=`grep ": 0" $dir_res/edges_coverage.txt | wc -l | awk '{printf("%lf", (1 - $1/'$num_edges'));}'`
    echo $cov_n | awk '{printf("\nOverall coverage: %.3lf%%\n", 100*$1);}' >> $dir_res/nodes_coverage.txt
    echo $cov_e | awk '{printf("\nOverall coverage: %.3lf%%\n", 100*$1);}' >> $dir_res/edges_coverage.txt
    echo $cov_n $cov_e
}

bpmn=`init_all`
test $only_gen_java -eq 1 && { echo Source Java files generated, exiting; exit; }
prop_file=$dir_tmp/${bpmn}_inputs.properties
#echo BPMN and prop: $bpmn $prop_file
declare -A nodes
declare -A labels
declare -A edges
load_graph $dir_tmp/$bpmn.graph
if [ $N -eq 0 ]
then
    M=`python3 << EOF
import math
print(math.ceil(math.log($delta)/math.log(1 - $epsilon)))
EOF`
else
    M=$N
fi
echo Maximum number of iterations to be done: $M
echo Simply create a "(empty)" file named stop inside your examples directory if you want to abort the verification
for ((t = 1; t <= M; t++))
do
    echo Iteration $t
    if [ $continue_from_before -eq 0 -o \( ! \( -d $dir_res/logs/$t \) \) ]
    then
	rm -fr $dir_res/logs/$t
	mkdir -p $dir_res/logs/$t
	gen_curr_input $prop_file $dir_res/logs/$t/${bpmn}_inputs.properties $override_vars && { cat $dir_res/logs/$t/${bpmn}_inputs.properties; exit; }
	run $t $bpmn $dir_res $dir_res/logs/$t/${bpmn}_inputs.properties $bpmn
	last_res=$?
    fi
    update_arrays $dir_res/logs/$t/$bpmn.trace
    covs=`update_coverage $dir_res $num_nodes $num_edges`
    if [ "$stop_type" == "errors" ]
    then
	if [ -f $dir_res/logs/$t/${bpmn}_outputs.properties ]
	then
	    grep -q "output_success=true" $dir_res/logs/$t/${bpmn}_outputs.properties || { echo exiting because an error has been reached; break; }
	else
	    echo $last_res | awk '{printf("Warning: execution '$dir_res/logs/$t' failed %s\n", $1 == 124? "for timeout" : "with exit code"$1)}'
	fi
    else
	if [ "$stop_type" == "both_coverage" ]
	then
	    (echo $covs | awk '{exit ($1 >= '$nodes_coverage' && $2 >= '$edges_coverage');}') || { echo exiting because coverage $stop_type is enough; break; }
	elif [ "$stop_type" == "any_coverage" ]
	then
	    (echo $covs | awk '{ exit ($1 >= '$nodes_coverage' || $2 >= '$edges_coverage');}') || { echo exiting because coverage $stop_type is enough; break; }
	elif [ "$stop_type" == "nodes_coverage" ]
	then
	    (echo $covs | awk '{ exit ($1 >= '$nodes_coverage');}') || { echo exiting because coverage $stop_type is enough; break; }
	elif [ "$stop_type" == "edges_coverage" ]
	then
	    (echo $covs | awk '{ exit ($2 >= '$edges_coverage');}') || { echo exiting because coverage $stop_type is enough; break; }
	    #missing case is nostop...
	fi
    fi
    test -f $dir_res/stop && { echo stop file detected; break; }
done
