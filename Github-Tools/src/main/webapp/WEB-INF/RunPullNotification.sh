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

# run the java
java -cp $MYCP com.arcbees.github.PullNotification "$@"

exit;