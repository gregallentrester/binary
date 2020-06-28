# https://docs.oracle.com/cd/E40518_01/integrator.311/integrator_install/src/cli_ldi_server_config.html


export JAVA_OPTS="$JAVA_OPTS  -Xms4096m  -Xmx4096m  -DLog4jContextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector"


export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk1.8.0_92.jdk/Contents/Home"

echo  "   "
echo  "   "
echo  "   setenv.sh"
echo  "   "
echo  "   JAVA_OPTS:   " $JAVA_OPTS
echo  "   "
echo  "   "

