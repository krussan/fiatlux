PID=`cat fiatlux.pid`

echo Stopping fiatlux server with pid :: $PID
kill $PID

