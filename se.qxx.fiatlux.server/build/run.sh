if [ -n "$LD_LIBRARY_PATH" ]
then
   LD_LIBRARY_PATH=$PWD:${LD_LIBRARY_PATH}
else
   LD_LIBRARY_PATH=$PWD
fi
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH
java -cp 'jna.jar:fiatlux-server-0.1.9.jar:protobuf-2.4.1.jar:commons-lang3-3.1.jar:protobuf-socket-rpc-2.0.jar' se.qxx.fiatlux.server.FiatLuxServer ${1+"$@"} &
echo $! > fiatlux.pid
