@echo off
setlocal enabledelayedexpansion

for /L %%A IN (1,1,10) DO (
   start cmd /k  CALL runService.bat  
   timeout /t 5 
)
 
 