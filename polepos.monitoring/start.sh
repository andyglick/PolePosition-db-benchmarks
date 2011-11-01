export CLASSPATH=./polepos.monitoring.jar:./lib/jmxremote_optional.jar:./lib/xstream/jettison-1.2.jar:./lib/xstream/xstream-1.4.1.jar:./lib/sigar/sigar.jar

java -cp $CLASSPATH org.polepos.monitoring.remote.MonitoringServer

