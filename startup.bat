@echo off

set CUR_DIR=%cd%
set CUR_DATE=%date:~0,4%%date:~5,2%%date:~8,2%

::GC日志文件路径
set GC_LOG_DIR=%CUR_DIR%\logs\gc
set GC_LOG_PATH=%GC_LOG_DIR%\gc.log

::heap dump文件路径
set HEAP_DUMP_DIR=%CUR_DIR%\logs\dump
set HEAP_DUMP_PATH=%HEAP_DUMP_DIR%\heap.dump

rem 获取应用配置app.conf,忽略#号的行
for /f "eol=# tokens=1* delims==" %%i in (%CUR_DIR%\app.conf) do (
	rem JAVA_HOME
    if "%%i"=="JAVA_HOME" (
        set JAVA_HOME=%%j
     )

	rem 获取应用名
	if "%%i"=="APP_NAME" (
		set APP_NAME=%%j
	)

	rem 获取版本号
	if "%%i"=="APP_VERSION" (
		set APP_VERSION=%%j
	)

	rem 获取jvm参数
	if "%%i"=="JAVA_OPTS" (
		set JAVA_OPTS=%%j
	)


	rem 获取运行参数
	if "%%i"=="RUN_ARGS" (
		set RUN_ARGS=%%j
	)
)

if "%JAVA_HOME%"=="" (
	echo JAVA_HOME is empty
	goto end
)

if "%APP_NAME%"=="" (
	echo APP_NAME is empty
	goto end
)

if "%APP_VERSION%"=="" (
	echo APP_VERSION is empty
	goto end
)

rem 判断是否配置JAVA_HOME
if "%JAVA_HOME%"=="" (
	echo JAVA_HOME env variable not config
	goto end
)

rem 应用
set JAR_FILE=%APP_NAME%-%APP_VERSION%
set JAR_FILE_PATH=%CUR_DIR%\%JAR_FILE%.jar

rem 设置标题
title %JAR_FILE%

if not exist %GC_LOG_DIR% (
	echo create dir "%GC_LOG_DIR%"
	mkdir %GC_LOG_DIR%
)

if not exist %HEAP_DUMP_DIR% (
	echo create dir "%HEAP_DUMP_DIR%"
	mkdir %HEAP_DUMP_DIR%
)

rem jre
set CLASSPATH=.;%JAVA_HOME%\lib\dt.jar;%JAVA_HOME%\lib\tools.jar;
set Path=%JAVA_HOME%\bin;%Path%

rem 添加gc、dump日志
SET JVM_OPTS_GC=-Xloggc:%GC_LOG_PATH% -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=%HEAP_DUMP_PATH%
set JVM_OPTS=%JAVA_OPTS% %JVM_OPTS_GC%
if not "%JVM_OPTS%"=="" (
    echo JAVA_OPTS
    echo %JVM_OPTS%
)

rem springboot启动参数
set SPRINGBOOT_OPTS=%RUN_ARGS%
if not "%SPRINGBOOT_OPTS%"=="" (
    echo SPRINGBOOT_OPTS
    echo %SPRINGBOOT_OPTS%
)

echo starting application

java %JVM_OPTS% -jar %JAR_FILE_PATH% %SPRINGBOOT_OPTS%

:end

Pause >nul&&Exit