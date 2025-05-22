$common_opts = "-t 5"
$opts = @{
    ".\examples\surgery" = "-s any_coverage"
    ".\examples\simple\Simple_Loop_Alt" = "-O -fi:a"
    ".\examples\simple\Simple_Loop_int" = "-V Simple_Loop_override.txt"
    ".\examples\simple\Simple_Loop_float" = "-V Simple_Loop_override.txt"
}

$directories = @(
    ".\examples\surgery.noopts"
    ".\examples\surgery"
    ".\examples\shipment"
)

$directories += Get-ChildItem -Path ".\examples\simple" -Directory  -Depth 0 | Where-Object { $_.Name -notmatch "^R" } |  ForEach-Object { ".\examples\simple\" + $_.Name }


foreach ($d in $directories) {
    $dirPath = $d
	
    $bpmnFiles = Get-ChildItem -Path "$dirPath" -Depth 0 -Filter "*.bpmn" | ForEach-Object { $_.Name }
    $dmnFiles = Get-ChildItem -Path "$dirPath"  -Depth 0 -Filter "*.dmn" -ErrorAction SilentlyContinue | ForEach-Object { $_.Name }

    $opt = if ($opts.ContainsKey($dirPath)) { $opts[$dirPath] } else { "" }

    Write-Output "Executing:"
    Write-Output ""
    Write-Output "docker run -v ${dirPath}:/usr/app/res -t bpmn-translator-and-verifier $common_opts $opt $bpmnFiles $dmnFiles"
    Write-Output ""
    Write-Output "Results will be in directory $dirPath, overall log in $dirPath.docker.log"
    Write-Output ""
    Write-Output ""

	$command = "docker run -v ${dirPath}:/usr/app/res -t bpmn-translator-and-verifier $common_opts $opt $bpmnFiles $dmnFiles"
    Invoke-Expression $command | Out-File -FilePath "$dirPath.docker.log"
	
}