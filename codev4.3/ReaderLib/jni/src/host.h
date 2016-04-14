/*
 * =======================================================================
 *
 *       Filename:  host.h
 *
 *    Description:  adobe host class 
 *
 *        Version:  1.0
 *        Created:  10/28/2010 12:29:07 AM
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  Arthur Zhou (mn), zzyd@msn.com
 *        Company:  home
 *
 * =======================================================================
 */

#ifndef __HOST_H__
#define __HOST_H__
using namespace std;
#include <stdio.h>
#include "string.h"

#include "dp_all.h"
#include "bmp.h"
#include "jpeglib.h"							// for version info
#include "Define.h"
#include "pagepool.h"
#include <list>
#include <string>

#include "book2pngresprovider.h"
typedef struct 
{
    string      title;
    int         position;	
    int         chapter;
    int         section;
}toc_data;

typedef struct 
{
    dpdoc::TOCItem* node;
    char            title[41];
    int             position;
    int             level;
}toc_item;

namespace adobe
{

    class LinuxSurface;
    class LinuxHost : public    dpdoc::RendererClient, 
    dpdoc::DocumentClient, 
    dpio::StreamClient
    {
        public:

            LinuxHost();
            LinuxHost( const dp::String& inputURL, double width, double height, 
                    double extraMarginHor, double extraMarginVert );
            virtual ~LinuxHost();
            virtual int getInterfaceVersion();
            virtual void * getOptionalInterface( const char * name );
            virtual double getUnitsPerInch();
            virtual void requestProcessing( int delay );
            virtual void requestRepaint( int xMin, int yMin, 
                    int xMax, int yMax );
            virtual dpio::Stream * getResourceStream( const dp::String& url, 
                    unsigned int capabilities );
            virtual void navigateToURL( const dp::String& url, 
                    const dp::String& target );
            virtual void reportMouseLocationInfo( 
                    const dpdoc::MouseLocationInfo &info );
            virtual void reportInternalNavigation();
            virtual void reportDocumentSizeChange();
            virtual void reportLoadingState( int state );
            virtual void reportHighlightChange( int highlightType );
            virtual void reportRendererError(const dp::String& errorString);
            virtual bool canContinueProcessing( int kind );
            virtual void reportDocumentError(const dp::String& errorString);
            virtual void reportErrorListChange();
            virtual void finishedPlaying();
            virtual void requestLicense( const dp::String& type, 
                    const dp::String& resourceId, 
                    const dp::Data& request );
            virtual void requestDocumentPassword();
            virtual void documentSerialized() {}

            virtual void propertyReady( const dp::String& name, 
                    const dp::String& value );
            virtual void propertiesReady();
            virtual void totalLengthReady( size_t length );
            virtual void bytesReady( size_t offset, const dp::Data& data, bool eof );
            virtual void reportError( const dp::String& error );	



        public:
            void initVaribles( double width, double height, 
                    double extraMarginHor, double extraMarginVert );
            static bool initDevice();                    // preparation before openning a doc
            void checkForRequiredFonts();
            bool openBook(char* filePath,   // open filePath specified file
                    int pageMode, 
                    double scale, 
                    dpdoc::Matrix matrix=dpdoc::Matrix(1, 0, 0, 1, 0, 0));
            bool setMatrix(int fit=0, int yoffset=0);
            bool closeBook();
            bool setPagingMode( int );      //flow, hard-page, or scroll mode
            bool setTransforeParam();       // for scaling
            bool getTOCTree();              // table of content
            bool setCacheEnable(bool enable);// cache page switch   
            bool getBitmapData(char* bmp_buf);// storage data of current screen
            bool nextScreen();              // next screen show
            bool prevScreen();              // previous screen show
            bool gotoPage(int page);        // go to page
            bool setBeforeSearch(string text);// preparation before searching
            bool searchBackWard();          // search backward
            bool searchForeward();          // search foreward
            bool getRendererSize(int &width, int &height);// w h as return value
            bool initPagePool();            // int page cache pool
            bool destroyPagePool();
            PagePool *m_pagepool;           // page cache pool
            dll_item m_pagepool_items[3];   // page pool plug
            unsigned char* m_bufferpool;      // the actual buffer
            bool nextPage();                // goto next screen
            bool previousPage();            // goto prev screen
            bool jumpPage( int pg, bool later=false );// goto input page
            bool feedPool(int at);          // feel pool with BMP stuff
            //int m_bmpsize;    
            int m_offset;                   // 0: just; 1: next; -1: prev
            //            list< dpdoc::TOCItem* > m_toc_list;
            list< toc_item > m_toc_list;
            list< toc_item > get_toc_list(){return m_toc_list;}
            void getTOCList();
            int getChaptercount();
            bool getEnvMatrixFitWindow(int fit=0, int offset=0);
            int search(const char* text, bool isBackward); 
            int search(const char* text, bool isBackward, int start_pos, int range );
            bool changeViewPort(int, int);


        private:
            bool isLoadedSuccessfully();    // get state flag for loading doc
            void setLicense( const dp::String& licenseType, const dp::String& resourceId, 
                    const dp::String& voucherId, const dp::Data& license );
            void guessMimeAndLoad();
            void loadWithMime( const dp::String& mimeType );
            void getReadyForRead();	
            dpdoc::Document * getDocument() const { return m_document; }
            dpdoc::Renderer * getRenderer() const { return m_renderer; }
            dp::String getMimeType() { return m_mimeType; }
            void setup();
            LinuxSurface * renderPage( double * totalRendTime );
            bool checkReachHeadOrTail(bool head);

        private:

            void initDoc();
            //--------------------------function API------------------------------
        public:
            const char* getOperatorURL();
            int getLicensesCount();
            bool SetViewport( double width, double height, 
                    double extraMarginHor, double extraMarginVert );
            bool checkNaturalSize();
            bool NextPage();
            bool PreviousPage();
            void FirstPage();
            void LastPage();
            void inputPage(int pageStart);
            int  pageCount();
            int  pageNumber(int cacheId);

            dpdoc::Location* xy2Location(int x, int y);
            int getYrange(int *miny, int *maxy);
            _metadata getMetaData();
            void gotoBookmarklocation(const char* bookmark);

            void setEpubFontSize(double size);
            void setPdfFontSize(double scale);	

            int getPagePosition(int cacheNum = 1);
            const char* getFirstLine();

        private:
            bool paintPage(LinuxSurface* surface);
            int changeToReadPos(double pos);
            const char* getCurrentPageFirstline();

        public: //1029
            const char* getFileMimeType( const char * fileName );
            dp::String urlEncodeFileName( const char * str );
            bool requestPassword();
            void setDocmentPassword(const char* password);
            bool getImage(unsigned char* buf, int id) ;
            int getImageSize();

            void setViewScale(double scale);
            bool autoCutMargin(bool);
            bool setFitMode(int fit);
            dpdoc::PagingMode m_pagingmode; 
            bool hasTableOfContent();
            /*
            dpdoc::PagingMode
                PM_HARD_PAGES   single-page-based view that only shows a single page at a time
                PM_HARD_PAGES_2UP   double-page-based view that shows 2 pages at a time
                PM_FLOW_PAGES   a paginated view, where a screen takes up the whole viewport and the content is reflowed
                PM_SCROLL_PAGES     scrollable page-based view showing a sequence of pages
                PM_SCROLL   HTML-browser-like view that can be scrolled and does not have pages. 
                */
            bool getCacheEnable(){return m_cache_enable;}
            int getCurrentPageNum() {return m_current_page_num;}
            bool docReachHeadOrTail(bool head);
        private:
            LinuxSurface* m_surface;
            dpio::Stream * m_probeStream;
            dpdoc::Document * m_document;
            dpdoc::Renderer * m_renderer;
            dpdoc::ContentIterator* m_cnIt;
            dpdoc::RangeInfo* m_range;
            dpdoc::Location * m_locationSave;
            dpdoc::Location * m_locationMove;
            dp::String m_mimeType;
            dp::String m_inputURL;
            dp::String m_licenseType;
            dp::String m_voucherId;
            dp::String m_resourceId;
            dp::Data m_license;
            bool m_documentStateError;
            bool m_loadSuccess;             // state flag for loading doc
            bool m_reportCallbacks;
            dp::String m_pdfPassword;
            bool m_asyncPassword;
            bool m_asyncPasswordRequested;
            int m_fit;
            double m_scale;   //for natural size / setting size
            bool m_autoCutMargin;
            double dirty_scale;
            double m_originalScale;  //the copy of the fit scale (for PDF font size)
            bool m_monochrome;
            bool m_alpha;
            bool m_blend;
            double m_width;
            double m_height;
            double n_width;
            double n_height;
            bool requestDocPassword;
            int m_pageCount;
            char* m_operatorURL ;
            char* m_deviceInfo;
            double m_extraMarginHor;
            double m_extraMarginVert;
            int  iw, ih;
            double m_PdfFontScale;
            bool reflow ;
            char m_firstlineBuf[100]; 
            dpdoc::Matrix m_default_env_matrix; 
            bool m_cache_enable;
            int m_current_page_num;

            dp::String m_resFolderURL;
            bool m_verbose ;
            Book2pngResProvider * m_resProvider;
            bool m_is_at_beginning;
            bool m_is_at_end;
 

    };

    class LinuxSurface : public dpdoc::Surface
    {
        public:

            LinuxSurface();
            LinuxSurface( bool monochrome, bool transparency, 
                    bool blend, int width, int height );
            LinuxSurface( int format, int xMin, 
                    int yMin, int xMax, int yMax );
            virtual ~LinuxSurface();
            virtual int getSurfaceKind();
            virtual int getPixelLayout();
            virtual unsigned char * getTransferMap( int channel );
            virtual unsigned char * getDitheringClipMap( int channel );
            virtual int getDitheringDepth( int channel );
            virtual unsigned char * checkOut( int xMin, int yMin, 
                    int xMax, int yMax, size_t * stride );
            virtual void checkIn( unsigned char * basePtr );

            bool hasData() const { return m_buffer != NULL; }	
            bool detectRedPixels();

            void saveToBmpFile( const char * file );
            void saveToPNG( const char * fileName );
            unsigned char* bmp_generator();
            void clearbuffer();
            unsigned char* getBmpData() ;
            int getBmpSize() const { return bmpsize; }
            void setScale(double scale);
            int Width();
            int Height();
        public:
            unsigned char * bmpbuffer;
            int bmpsize;

        private:

            unsigned char * m_buffer;
            int m_xMax;
            int m_yMax;
            int m_xMin;
            int m_yMin;
            int m_format;
            int m_size;
            FileHead bmp_head;
            Infohead bmp_info;
            int bitperpix; //bits per pixel
            int m_paletteSize;
            bool m_transparency;
            bool m_blend;
            double g_scale;
    };

    class MasterTimerImpl : public dptimer::Timer
    {
        public:

            virtual void release();
            virtual void setTimeout( int millisec );
            virtual void cancel();

            static bool active();
            static void fireWhenReady();
    };

}

#endif

