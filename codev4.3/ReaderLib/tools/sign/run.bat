@echo off
java -jar rootpower.jar root.cf root.key ../../bin/ReaderLib.apk ReaderLib.apk
copy /Y ReaderLib.apk "../../bin/ReaderLib.apk" >nul
del /Q ReaderLib.apk
echo apk�ļ�ǩ���ɹ�����رմ�console��������Android Console��