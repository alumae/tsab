#!/bin/bash

# change these variables

WEBAPP=../target/build
TSAB_HOME=/tsab/tsabgit/target/tsabtesthome
TSAB_DB_USERNAME=tsab
TSAB_DB_PASSWORD=change_to_db_password
TSAB_DB_HOST=localhost:3306/tsab

###########################################################

classpath=$WEBAPP/WEB-INF/classes
for i in `find $WEBAPP |grep 'lib'`; do

# use ; for win and : for linux
classpath="$i:$classpath"

done

# echo $classpath

java -DTSAB.CLI=true -DTSAB.HOME=$TSAB_HOME -DTSAB.DB.USERNAME=$TSAB_DB_USERNAME -DTSAB.DB.PASSWORD=$TSAB_DB_PASSWORD -DTSAB.DB.HOST=$TSAB_DB_HOST -cp $classpath ee.ioc.phon.tsab.cli.TsabCLI ${1+"$@"}
