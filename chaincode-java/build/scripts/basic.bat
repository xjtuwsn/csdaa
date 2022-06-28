@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  basic startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and BASIC_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\basic-1.0-SNAPSHOT.jar;%APP_HOME%\lib\bcprov-jdk16-1.46.jar;%APP_HOME%\lib\jna-3.2.5.jar;%APP_HOME%\lib\jpbc-api-2.0.0.jar;%APP_HOME%\lib\jpbc-benchmark-2.0.0.jar;%APP_HOME%\lib\jpbc-crypto-2.0.0.jar;%APP_HOME%\lib\jpbc-mm-2.0.0.jar;%APP_HOME%\lib\jpbc-pbc-2.0.0.jar;%APP_HOME%\lib\jpbc-plaf-2.0.0.jar;%APP_HOME%\lib\fabric-chaincode-shim-2.4.1.jar;%APP_HOME%\lib\org.everit.json.schema-1.12.1.jar;%APP_HOME%\lib\json-20220320.jar;%APP_HOME%\lib\genson-1.5.jar;%APP_HOME%\lib\fabric-chaincode-protos-2.4.1.jar;%APP_HOME%\lib\commons-cli-1.4.jar;%APP_HOME%\lib\commons-validator-1.6.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\bcpkix-jdk15on-1.62.jar;%APP_HOME%\lib\bcprov-jdk15on-1.62.jar;%APP_HOME%\lib\classgraph-4.8.47.jar;%APP_HOME%\lib\protobuf-java-util-3.11.1.jar;%APP_HOME%\lib\grpc-netty-shaded-1.34.1.jar;%APP_HOME%\lib\grpc-protobuf-1.34.1.jar;%APP_HOME%\lib\opentelemetry-exporter-otlp-1.6.0.jar;%APP_HOME%\lib\opentelemetry-exporter-otlp-trace-1.6.0.jar;%APP_HOME%\lib\grpc-stub-1.39.0.jar;%APP_HOME%\lib\opentelemetry-extension-trace-propagators-1.6.0.jar;%APP_HOME%\lib\opentelemetry-grpc-1.6-1.5.3-alpha.jar;%APP_HOME%\lib\opentelemetry-sdk-extension-autoconfigure-1.6.0-alpha.jar;%APP_HOME%\lib\opentelemetry-sdk-extension-autoconfigure-spi-1.6.0.jar;%APP_HOME%\lib\opentelemetry-exporter-otlp-common-1.6.0.jar;%APP_HOME%\lib\opentelemetry-sdk-1.6.0.jar;%APP_HOME%\lib\opentelemetry-sdk-trace-1.6.0.jar;%APP_HOME%\lib\opentelemetry-instrumentation-api-1.5.3-alpha.jar;%APP_HOME%\lib\opentelemetry-sdk-metrics-1.6.0-alpha.jar;%APP_HOME%\lib\opentelemetry-sdk-common-1.6.0.jar;%APP_HOME%\lib\opentelemetry-semconv-1.6.0-alpha.jar;%APP_HOME%\lib\opentelemetry-api-metrics-1.6.0-alpha.jar;%APP_HOME%\lib\opentelemetry-api-1.6.0.jar;%APP_HOME%\lib\opentelemetry-proto-1.6.0-alpha.jar;%APP_HOME%\lib\opentelemetry-context-1.6.0.jar;%APP_HOME%\lib\protobuf-java-3.17.3.jar;%APP_HOME%\lib\javax.annotation-api-1.3.2.jar;%APP_HOME%\lib\handy-uri-templates-2.1.8.jar;%APP_HOME%\lib\re2j-1.3.jar;%APP_HOME%\lib\grpc-core-1.34.1.jar;%APP_HOME%\lib\grpc-protobuf-lite-1.34.1.jar;%APP_HOME%\lib\grpc-api-1.39.0.jar;%APP_HOME%\lib\guava-30.1-android.jar;%APP_HOME%\lib\error_prone_annotations-2.4.0.jar;%APP_HOME%\lib\gson-2.8.6.jar;%APP_HOME%\lib\perfmark-api-0.19.0.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\proto-google-common-protos-1.17.0.jar;%APP_HOME%\lib\animal-sniffer-annotations-1.18.jar;%APP_HOME%\lib\commons-digester-1.8.1.jar;%APP_HOME%\lib\commons-collections-3.2.2.jar;%APP_HOME%\lib\joda-time-2.10.2.jar;%APP_HOME%\lib\annotations-4.1.1.4.jar;%APP_HOME%\lib\failureaccess-1.0.1.jar;%APP_HOME%\lib\listenablefuture-9999.0-empty-to-avoid-conflict-with-guava.jar;%APP_HOME%\lib\checker-compat-qual-2.5.5.jar;%APP_HOME%\lib\j2objc-annotations-1.3.jar;%APP_HOME%\lib\grpc-context-1.39.0.jar;%APP_HOME%\lib\slf4j-api-1.7.30.jar


@rem Execute basic
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %BASIC_OPTS%  -classpath "%CLASSPATH%" org.hyperledger.fabric.contract.ContractRouter %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable BASIC_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%BASIC_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
