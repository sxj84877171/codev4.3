@echo off
java -jar rootpower.jar root.cf root.key ../../bin/ReaderService.apk ReaderService.apk
copy /Y ReaderService.apk "../../bin/ReaderService.apk" >nul
del /Q ReaderService.apk
echo apk�ļ�ǩ���ɹ�����رմ�console��������Android Console��