#!/usr/local/bin/expect

set username [lindex $argv 0] 

set passwd [lindex $argv 1]

set workingDir [lindex $argv 2]

set shellPath [lindex $argv 3]

set filePath [lindex $argv 4]

spawn su - $username

expect "密码*"

send $passwd\r

expect "#"

####执行脚本

send "cd $workingDir\r"

expect "#"

send "cp $shellPath  $filePath\r"

expect "cp:*"

send "y\r"

interact

exit
