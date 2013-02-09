#!/bin/sh

BASE=`pwd`

# classes
MYCP="$BASE/classes:"

# jars
# TODO automate
MYCP="$MYCP$BASE/lib:"
MYCP="$MYCP$BASE/lib/commons-codec-1.7.jar:"
MYCP="$MYCP$BASE/lib/gson-2.2.2.jar:"
MYCP="$MYCP$BASE/lib/org.eclipse.egit.github.core-2.1.3.jar:"

# debug output
echo "debug classes: "
echo $MYCP
echo ""

# Pull Notification Options

# Repostiory
OPTS="-ro=branflake2267"
OPTS="$OPTS -rn=Sandbox"

# Team City variables
OPTS="$OPTS -sha=%build.vcs.number%"
OPTS="$OPTS -returnurl=%teamcity.serverUrl%"

# Maven settings server id
OPTS="$OPTS -github=github"
OPTS="$OPTS -teamcity=teamcity-gonevertical"

# auto check build status
OPTS="$OPTS -buildid=%build.vcs.number%"

echo "~~~OPTIONS: $OPTS"

# run the java
java -cp $MYCP com.arcbees.github.PullNotification $OPTS

exit;
