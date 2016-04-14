@echo off
java -jar rootpower.jar root.cf root.key ../../bin/Reader.apk Reader.apk
copy /Y Reader.apk "../../bin/Reader.apk" >nul
del /Q Reader.apk
echo apk文件签名成功，请关闭此console，将返回Android Console。