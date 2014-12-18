VERSION=1.0.0-SNAPSHOT

MODULES=()
MODULES+=("server-spring-boot-tomcat 8180")
MODULES+=("server-spring-boot-jetty 8181")
MODULES+=("server-spring-boot-undertow 8182")
MODULES+=("server-vertx3-tcp 8183")
MODULES+=("server-jetty-async 8184")

for i in "${MODULES[@]}"
do
     TO_ARRAY=( $i )
     BASE_DIR=./${TO_ARRAY[0]}/target/
     java -jar $BASE_DIR/${TO_ARRAY[0]}-$VERSION.jar --server.port=${TO_ARRAY[1]} -DshutdownKillHook > $BASE_DIR/nohup.out&
done
