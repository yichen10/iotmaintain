#!/bin/bash

export ORACLE_BASE=/app
export ORACLE_HOME=/app/ora10201
export LD_LIBRARY_PATH=/app/ora10201/lib:/lib:/usr/lib:/usr/local/lib:/usr/X11R6/bin
export TNS_ADMIN=/app/ora10201/network/admin
export PATH=/app/ora10201/bin:$PATH

host_name=`hostname`
host_ip=`ifconfig ens33 | grep "inet" | awk -F[:" "]+ '{print $3}' | head -1`
host_date=`date '+%Y/%m/%d %H:%M:%S'`

#-----------------------
inputFile="test.config"
outputFile="parame_upload_temp.txt"
uploadTime="upload_time.txt"
uploadPath="/app/shell/monitor-file/record"
uploadFilePath="/app/shell/monitor-file"
oracleBaseParam="shiot/shiot@ORCL control=$uploadFilePath/test.ctl log=$uploadFilePath/test.log"
apiPaths=("/test" "/test1")
apiFiles=("log1.log log2.log log3.log" "log1.log log2.log log3.log")
servicePaths=("/test2")
serviceFiles=("log1.log log2.log log3.log")
weChatPaths=("/config" "/dubbo" "/spring")
weChatFiles=("log4j.properties" "dubbo-consumer.xml dubbo-provider.xml" "dubbo-config.xml spring-config.xml spring-mvc.xml")
echo "--host_ip:---$host_ip--22222-----"
#---------------------------------------


findAPP(){
	int=1
	uploadTimeFile=$1"_"$2"_"$uploadTime
	for var in $*
	do
		if [ $int -gt 3 ] 
		then
			fileTime=""
			cd ${3}
			fileTime=`ls -l --time-style '+%Y/%m/%d %H:%M:%S' | grep '\b'${var}'\b' | awk '{print $6,$7}'`
			echo "-----------fileTime:-----$fileTime------------"
			#
			cd $uploadPath
			oldFileTime=`cat $uploadTimeFile | grep '\b'${var}'\b' | awk '{print $2,$3,$4}'`
			echo "------oldFileTime:---$oldFileTime----"
			if [[ $fileTime != "`echo $oldFileTime`" ]]
			then
				if [ ! -n "$oldFileTime" ]
				then
					if [ ! -f "$uploadTimeFile" ]
					then
						echo "No file:${uploadTimeFile}"
						touch "$uploadTimeFile"  
					fi
					echo "${var} ${fileTime}" >>"$uploadTimeFile"
					echo "new change : ${var} ${fileTime}"
				else
					sed -i /${var}/d "$uploadTimeFile"
					echo "${var} ${fileTime}" >>"$uploadTimeFile"
					echo "change : ${var} ${fileTime}"
				fi
				cd $uploadFilePath
				echo $host_date,$host_name,$host_ip,$1,$2,$3,$var,$fileTime > "$outputFile"
				cp -f $3"/"$var $uploadFilePath"/"$var
				echo "sqlldr start"
				sqlldr "$oracleBaseParam"
				echo "sqlldr end"
				rm -f $var
				#aaaaa
			fi
		fi
		let "int++"
	done
}

while read line
do
	app=`echo ${line} | awk '{print $1}'`
	echo "This is "$app
	if [ $app = "api" ]
	then
		paths=("${apiPaths[@]}")
		files=("${apiFiles[@]}")
	fi
	if [ $app = "service" ]
	then
		paths=("${servicePaths[@]}")
		files=("${serviceFiles[@]}")
	fi
	if [ $app = "weChat" ]
	then
		paths=("${weChatPaths[@]}")
		files=("${weChatFiles[@]}")
	fi
	if [ "$paths" != "" ]
	then
		i=0;
		while (( $i<${#paths[*]} ))
		do
			param=${line}${paths[${i}]}" "${files[${i}]}
			findAPP `echo $param`;
			let "i++"
		done
	fi
	echo "The $app check end!"
done < $inputFile
