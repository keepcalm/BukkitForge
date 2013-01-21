@echo off

if %PROCESSOR_ARCHITECTURE%==x86 { set JAVA="C:\Program Files\Java\jre7\bin\java.exe" } else { set JAVA="C:\Program Files (x86)\Java\jre7\bin\java.exe" }

set /p pluginJar="Enter the name of the jar file to translate: "

set /p mcVer="Enter the MC version of the plugin: "

echo "Transforming the jar..."

java -Xms1024M -Xmx1024M -jar srgtool-2.0.jar apply --srg cb2obf_%mcVer%.srg --in %pluginJar% --out ported_%pluginJar%

echo "Complete!"

PAUSE
