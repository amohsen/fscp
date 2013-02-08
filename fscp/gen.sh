#!/bin/sh
rm -rf gen/*.* 
echo $PATH
echo $CLASSPATH
java -cp .:$CLASSPATH demeterf src/syntax/formula.cd src/empty.beh gen  --pcdgp:Getters --dgp:Display:Print:ToStr:PrintToString:HashCode
java -cp .:$CLASSPATH demeterf src/fscp/history.cd src/empty.beh gen  --pcdgp:Getters --dgp:Display:Print:ToStr:PrintToString:HashCode
