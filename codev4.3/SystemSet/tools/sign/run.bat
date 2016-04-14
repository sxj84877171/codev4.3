@echo off
java -jar rootpower.jar root.cf root.key ../../bin/SystemSet.apk SystemSet.apk
copy /Y SystemSet.apk "../../bin/SystemSet.apk" >nul
del /Q SystemSet.apk
echo apk文件签名成功，请关闭此console，将返回Android Console。