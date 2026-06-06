@echo off
setlocal enabledelayedexpansion

set M2_REPO=C:\Users\MRCR\.m2\repository
set MAIN_CLASS=com.example.basketball.BasketballTrainingApplication

set CP=target\classes

for /r "%M2_REPO%" %%f in (*.jar) do (
    set CP=!CP!;%%f
)

java -cp "!CP!" %MAIN_CLASS%
