REM starts up Selenium Grid Hub with chrome driver
java -jar -Dwebdriver.gecko.driver=..\drivers\geckodriver.exe -Dwebdriver.ie.driver=..\drivers\IEDriverServer.exe selenium-server-standalone-3.14.0.jar -role node -nodeConfig node1config.json
