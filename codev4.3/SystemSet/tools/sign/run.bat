@echo off
java -jar rootpower.jar root.cf root.key ../../bin/SystemSet.apk SystemSet.apk
copy /Y SystemSet.apk "../../bin/SystemSet.apk" >nul
del /Q SystemSet.apk
echo apk�ļ�ǩ���ɹ�����رմ�console��������Android Console��