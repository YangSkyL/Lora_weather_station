#!/bin/bash -x

function installMaven()
{
	echo "installing maven"
	sudo apt-get install maven;
	if [[ $? != 0 ]]; then
		echo "install maven fails";
		exit 1;
	fi
}

function installJdk()
{
	echo "installing jdk"
	sudo sudo apt-get install openjdk-8-jdk;
	if [[ $? != 0 ]]; then
		echo "install jdk fails, make sure you run this script with root premission";
		exit 1;
	fi
}

function installElastic()
{
	echo "installing elasticsearch";
	wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-5.2.2.zip;
	if [[ $? != 0 ]]; then
		echo "download elasticsearch fails";
		exit 1;
	fi
	unzip elasticsearch-5.2.2.zip 1> /dev/null;
	if [[ $? != 0 ]]; then
		echo "unzip elasticsearch fails"
		exit 1;
	fi
}

function installKibana()
{
	echo "installing Kibana";
	wget https://artifacts.elastic.co/downloads/kibana/kibana-5.2.2-linux-x86_64.tar.gz;
	if [[ $? != 0 ]]; then
		echo "download kibana fails";
		exit 1;
	fi
	tar -xzf kibana-5.2.2-linux-x86_64.tar.gz 1> /dev/null;
	if [[ $? != 0 ]]; then
		echo "unzip kibana fails"
		exit 1;
	fi
}

function build()
{
	echo "start building project"
	cd services/msf4j/;
	mvn package;
	if [[ $? != 0 ]]; then
		echo "build project fails."
		exit 1;
	fi
	cd ../../;
}

function checkRequirements()
{
	#check maven
	if ! type mvn > /dev/null; then
		echo "mvn not installed";
		installMaven;
	else
		echo "mvn installed";
	fi

	#check jdk
	if ! type javac > /dev/null; then
		echo "jdk not installed";
		installJdk;
	else
		echo "jdk installed";
	fi
	#check elasticsearch
	if [[ -f ./elasticsearch-5.2.2/bin/elasticsearch ]]; then
		echo "elasticsearch installed";
	else
		echo " elasticsearch not installed";
		installElastic;
	fi
	#check kibana
	if [[ -f ./kibana-5.2.2-linux-x86_64/bin/kibana ]]; then
		echo "kibana installed";
	else
		echo " kibana not installed";
		installKibana;
	fi
}

function checkConfig()
{
	sed -i 's/#server.host: "localhost"/server.host: "0.0.0.0"/' ./kibana-5.2.2-linux-x86_64/config/kibana.yml;
}

if [[ "$1" == "--install" ]]; then
	checkRequirements;
	checkConfig;
	build;
	echo "-------------------------------------------------------------------------------"
	echo "installation is completed, run this script with --start to start the restApi";
elif [[ "$1" == "--start" ]]; then
	checkConfig;
	echo "starting elasticsearch";
	./elasticsearch-5.2.2/bin/elasticsearch -d;
	echo "waiting elasticsearch to start.....";
	sleep 5;
	echo "starting kibana";
	./kibana-5.2.2-linux-x86_64/bin/kibana &
	disown;
	echo "starting restApi";
	java -jar services/msf4j/target/msf4j-0.1-SNAPSHOT-elastic.jar;
fi