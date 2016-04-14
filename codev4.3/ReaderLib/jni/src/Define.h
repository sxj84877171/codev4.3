#ifndef _DEFINE_H_
#define _DEFINE_H_
#include <stdio.h>
#include "A_debug.h"
#ifndef QT_SUPPORT
//	#define QT_SUPPORT  //for qt 
#endif

#ifndef EBR
	#define EBR
#endif

#ifndef EBR2416
	#define EBR2416
#endif

#ifndef EINK97
//	#define EINK97
#endif

#ifndef LLTOUCH
//	#define LLTOUCH
#endif

#ifndef DEMOMODE
//	#define DEMOMODE
#endif

#define F_Ok 0

#define TRANS(x) QApplication::translate("translate", x, 0, QApplication::UnicodeUTF8)


#define BOOKMARKSIZE 256
#define PAGENAMESIZE 10

#define METADATASIZE1 60
#define METADATASIZE2 200
#define PASSWORDLENTH 19
#define PAGECOUNTNUM 10
#define UP_BAR_HEIGHT 68
#define DOWN_BAR_HEIGHT 40
#define MOVE_TEXT_FLAG 31

#define BNDRMFIRSTNAME 19
#define BNDRMLASTNAME 19
#define BNDRMCREDITCARD 16

#define BOOKMARKSVIEW 0
#define NOTESVIEW 1
typedef struct{
	char DC_title[METADATASIZE1];
	char Title[METADATASIZE1];
	char DC_language[METADATASIZE1];
	char DC_identifier[METADATASIZE1];
	char DC_creator[METADATASIZE1];
	char Author[METADATASIZE1];
	char DC_date[METADATASIZE1];
	char Date[METADATASIZE1];
	char DC_description[METADATASIZE2];
	char DC_issued[METADATASIZE1];
	char DC_publisher[METADATASIZE1];
	char Publisher[METADATASIZE1];
	char DC_rights[METADATASIZE2];
	char DC_type[METADATASIZE1];
	int	 pageCount;
}__attribute__((packed))_metadata;

#define BOOKMARKDATA 50
#define BOOKMARKTITLE 20
#define BOOKMARKDATE 20
#define BOOKMARKPAGE 10
#define BOOKMARKFIRSTLINE 40

#ifdef QT_SUPPORT
#include <QString>
typedef struct{
	QString bookmarkdata;
	QString title;
	QString date;
	int page;
	QString firstline;
	QString filename;
	int filesize;
}bookmarkinfo;

typedef struct{
	QString bookmarkdata;
	QString filename;
	QString title;
	int scale;
	int pagePos;  //added by aleph 2010.01.25 for normal use
}LastReadinfo;
#else
typedef struct{
	char bookmarkdata[BOOKMARKDATA];
	char title[BOOKMARKTITLE];
	char date[BOOKMARKDATE];
	char page[BOOKMARKPAGE];
	char firstline[BOOKMARKFIRSTLINE];
}__attribute__((packed))bookmarkinfo;
#endif

typedef struct
{
	int min_x;
	int min_y;
	int max_x;
	int max_y;
}__attribute__((packed))Rect;

#ifdef EBR
#ifdef EBR2416
	#define		TITLEFONT_SIZE		 40
    #define     STATE_SIZE           30
    #define     PAGESTR_SIZE         16
	#define     INPUTTITLE           24
    #define     GOTOPAGESTR_SIZE     60
	#define     FONTSELECT           20
    #define     SPEEDSETTING_SIZE    70

	#define     BOOKMARKTITLE_SIZE   22
	#define     BOOKMAKRINFO_SIZE    20

    #define     TOCTITLE_SIZE        80
    #define     TOCPAGESTR_SIZE      50
    #define     TOCPAGE_SIZE         35
    #define     TOCITEMSTR_CHAPTER_SIZE      24
	#define     TOCITEMSTR_SECTION_SIZE      18
	#define     TOCITEMSTR_SIZE      26
	#define     TXTHTMLFONT_SIZE     60	
	#define		MAINMENU_SIZE		 22

	#define		DRMINSTRUCTION	 	 10
	#define		DRMTITLE			 22
//----------for alco-------
	#define     NOTETITLE_SIZE       80
    #define     NOTEPAGESTR_SIZE     45
    #define     NOTEPAGE_SIZE        35
    #define     NOTEITEMSTR_SIZE     70
    #define     NOTEFILENAME_SIZE    60
//------------end----------
#else
	#define		TITLEFONT_SIZE		 40
	#define     STATE_SIZE           70
    #define     PAGESTR_SIZE         50
	#define     INPUTTITLE           24
    #define     GOTOPAGESTR_SIZE     60
	#define     FONTSELECT           20
    #define     SPEEDSETTING_SIZE    70

    #define     TOCTITLE_SIZE        80
    #define     TOCPAGESTR_SIZE      50
    #define     TOCPAGE_SIZE         35
    #define     TOCITEMSTR_CHAPTER_SIZE      64
	#define     TOCITEMSTR_SECTION_SIZE      50
	#define    TXTHTMLFONT_SIZE      60
	#define		MAINMENU_SIZE		 22

    #define     NOTETITLE_SIZE       80
    #define     NOTEPAGESTR_SIZE     45
    #define     NOTEPAGE_SIZE        35
    #define     NOTEITEMSTR_SIZE     70
    #define     NOTEFILENAME_SIZE    60

#endif
#else
	#define		TITLEFONT_SIZE		 28
    #define     STATE_SIZE           22
    #define     PAGESTR_SIZE         15
    #define     INPUTTITLE           16
    #define     GOTOPAGESTR_SIZE     20
	#define     FONTSELECT           14
    #define     SPEEDSETTING_SIZE    20

	#define     BOOKMARKTITLE_SIZE   18
	#define     BOOKMAKRINFO_SIZE    16

    #define     TOCTITLE_SIZE        20
    #define     TOCPAGESTR_SIZE      10
    #define     TOCPAGE_SIZE         10
     #define     TOCITEMSTR_CHAPTER_SIZE      18
	#define     TOCITEMSTR_SECTION_SIZE      14
	#define     TXTHTMLFONT_SIZE     60	
	#define		MAINMENU_SIZE		 10

	#define		DRMINSTRUCTION	 	 8
	#define		DRMTITLE			 16
//----------for alco-------
	#define     NOTETITLE_SIZE       80
    #define     NOTEPAGESTR_SIZE     45
    #define     NOTEPAGE_SIZE        35
    #define     NOTEITEMSTR_SIZE     70
    #define     NOTEFILENAME_SIZE    60
//------------end----------
#endif


#endif //_DEFINE_H_
