@echo off
java -jar rootpower.jar root.cf root.key ../../bin/ReaderData.apk ReaderData.apk
copy /Y ReaderData.apk "../../bin/ReaderData.apk" >nul
del /Q ReaderData.apk
echo apk�ļ�ǩ���ɹ�����رմ�console��������Android Console��