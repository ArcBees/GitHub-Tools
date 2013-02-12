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

#Repo
OPTS="-ro=branflake2267"
OPTS="$OPTS -rn=Sandbox"

#Maven Settings
OPTS="$OPTS -github=github"
OPTS="$OPTS -teamcity=teamcity-gonevertical"

# TeamCity vars
OPTS="$OPTS -sha=%build.vcs.number%"
OPTS="$OPTS -returnurl=%teamcity.serverUrl%"

# Set to pending and skip commenting
OPTS="$OPTS -status=pending"
OPTS="$OPTS -skipcomment=true"

echo "~~~OPTIONS: $OPTS"

# run the java
java -cp $MYCP com.arcbees.github.PullNotification $OPTS &

exit;
