set -e

cd ../expr-parser

git pull
mvn clean install

cd ../org.lemsml.model

git pull
mvn clean install

cd ../lems-domogen-maven-plugin/

git pull
mvn clean install

cd ../neuroml2model

git pull
mvn clean install

cd ../neuroml2modelLite

rm -f src/main/resources/*
cp ../neuroml2model/target/org.neuroml.neuroml2-model-*-jar-with-dependencies.jar src/main/resources/

mvn clean install


