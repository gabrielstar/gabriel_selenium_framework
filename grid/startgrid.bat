ECHO this script runs selenium grid given the paths to drivers are properly set
ECHO you can specify number of selenium grid nodes

ECHO Trying to kill stale drivers ...
@echo off
taskkill /f /im chromedriver.exe
taskkill /f /im IEDriverServer.exe
@echo off

set /p nodes_number="Enter number of grid nodes: "
ECHO Starting up %nodes_number% nodes:

START /B CMD /C CALL "starthub.bat"

FOR /L %%A IN (1,1,%nodes_number%) DO (
  ECHO Starting node %%A.
  START /B CMD /C CALL "startnode.bat" 
)

ECHO Waiting for hub and nodes to start

start chrome http://localhost:4444/grid/console