@echo off
java -jar rootpower.jar root.cf root.key ../../bin/ReaderData.apk ReaderData.apk
copy /Y ReaderData.apk "../../bin/ReaderData.apk" >nul
del /Q ReaderData.apk
echo apk文件签名成功，请关闭此console，将返回Android Console。