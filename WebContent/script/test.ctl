load data
CHARACTERSET AL32UTF8
infile '/app/shell/monitor-file/parame_upload_temp.txt'
append into table MONITOR_FILE_LOG
fields terminated by ','
TRAILING NULLCOLS 
(HOST_DATE DATE "YYYY/MM/DD HH24:MI:SS",
HOST_NAME,
HOST_IP,
APP_NAME,
HOST_PORT,
FILE_PATH,
FILE_NAME,
FILE_CHANGE_DATE DATE "YYYY/MM/DD HH24:MI:SS",
FILE_CONTENT LOBFILE(FILE_NAME) TERMINATED BY EOF NULLIF FILE_NAME = 'NONE')
