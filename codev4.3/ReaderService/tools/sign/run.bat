@echo off
java -jar rootpower.jar root.cf root.key ../../bin/ReaderService.apk ReaderService.apk
copy /Y ReaderService.apk "../../bin/ReaderService.apk" >nul
del /Q ReaderService.apk
echo apk文件签名成功，请关闭此console，将返回Android Console。