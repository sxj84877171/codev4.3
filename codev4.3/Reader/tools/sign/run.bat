@echo off
java -jar rootpower.jar root.cf root.key ../../bin/Reader.apk Reader.apk
copy /Y Reader.apk "../../bin/Reader.apk" >nul
del /Q Reader.apk
echo apk�ļ�ǩ���ɹ�����رմ�console��������Android Console��