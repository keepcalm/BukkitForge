@echo off

if %PROCESSOR_ARCHITECTURE%==x86 {

	set JAVA="C:\Program Files\Java\jre7\bin\java.exe"
}
else {
	set JAVA="C:\Program Files (x86)\Java\jre7\bin\java.exe"
}

set /p pluginJar="Enter the name of the jar file to translate: "

set /p mcVer="Enter the MC version of the plugin: "

echo "Transforming the jar..."

%JAVA% -jar srgtool-2.0.jar apply --srg cb2obf_%mcVer%.srg --in %pluginJar% --out PORTED_%pluginJar%

echo "Complete!"
