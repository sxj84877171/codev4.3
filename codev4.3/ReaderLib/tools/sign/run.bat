@echo off
java -jar rootpower.jar root.cf root.key ../../bin/ReaderLib.apk ReaderLib.apk
copy /Y ReaderLib.apk "../../bin/ReaderLib.apk" >nul
del /Q ReaderLib.apk
echo apk文件签名成功，请关闭此console，将返回Android Console。