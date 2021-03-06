#include <sys/timeb.h>
#include <unistd.h>
#include <math.h>
#include <iostream>
#include <sys/types.h>
#include "uft_trace.h"
#define LOG_TAG "pdfReader-host"
#include "A_debug.h"
#include "host.h"

static bool g_isPDF;
static double m_dpi = 96.0;
int g_ditheringDepth = 3;				//bith depth for grayscale dithering (default 3)
unsigned char g_ditheringClipMap[256];	//array of 256 8-bit values to initialize 
adobe::MasterTimerImpl masterTimer;
uft::time_t timeToFire;

#define CURRENT_OPEN_IS_PDF(mimeType) \
    (::strcmp( "application/pdf", mimeType.utf8() ) == 0)

#define CONV_INT_TO_STR_X_(a) #a
#define CONV_INT_TO_STR(a) CONV_INT_TO_STR_X_(a)

#ifndef EINK97
const struct resolution defaultView={600,800};
#else
const struct resolution defaultView={825,1200};
#endif

double getCurrentTime()
{
    struct timeb timeVal;
    ::ftime( &timeVal );
    return timeVal.time*1000 + timeVal.millitm;
}

inline bool isLittleEndian()
{
    static char probe[] = "\1";
    return *(unsigned short *)probe == 1;
}

int double2int(double data, bool round)
{
    int b = 0; 
    if(round)
    {
        b = int(data);   
        double c = data - b;   
        if(c > 0.5)
            return b+1;
        else
            return b;
    }
    else
    {
        return int(data);
    } 
}


adobe::LinuxHost::LinuxHost()
{
    // initialize all the member varibles
}
adobe::LinuxHost::LinuxHost( const dp::String& inputURL, double width, double height, double extraMarginHor, double extraMarginVert )
{
    // initialize all the member varibles
    //initDevice();
    initVaribles( width,  height,  extraMarginHor,  extraMarginVert);
    initPagePool();
}

void adobe::LinuxHost::initVaribles(double width, double height, double extraMarginHor, double extraMarginVert)
{
    m_document = NULL;
    m_renderer = NULL;
    m_cnIt = NULL;
    m_range = NULL;
    m_locationSave = NULL;
    m_locationMove = NULL;
    m_probeStream = NULL;
    m_surface = NULL;
    m_operatorURL = NULL;
    m_documentStateError = false;
    m_loadSuccess = false;

    m_reportCallbacks = false;
    m_dpi = 96.0;
    //m_resFolderURL = dp::String();
    m_asyncPassword = false;
    m_asyncPasswordRequested = false;
    m_pdfPassword = dp::String();
    m_fit = 0;
    m_scale = 1.0;
    m_originalScale = 1.0;
    m_autoCutMargin = false; 
    //	m_monochrome = false;	//grayscale output produced
    m_monochrome = true;
    m_alpha = false;		//alpha channel produced
    m_blend = false;		//blend rendering onto surface
    m_width = width;
    m_height = height;
    m_extraMarginHor = extraMarginHor;
    m_extraMarginVert = extraMarginVert;
    m_PdfFontScale = 1.0;
    m_pageCount = -1;
    dirty_scale = 1.0;
    iw = (int)::ceil(m_width);	//for surface base on m_width
    ih = (int)::ceil(m_height);	//for surface base on m_height
    n_width = 0;			//for NaturalSize
    n_height = 0;			//for NaturalSize
    reflow = false;
    requestDocPassword = false;
    m_default_env_matrix = dpdoc::Matrix(1.0,0,0,1.0,0,0); 
    m_surface = new adobe::LinuxSurface( m_monochrome, m_alpha, m_blend, iw, ih );
    m_cache_enable = true;

    m_resFolderURL = "rmsdk/";
    m_verbose = true;
	m_resProvider = new Book2pngResProvider( m_resFolderURL, m_verbose );
	dpres::ResourceProvider::setProvider( m_resProvider );
//    checkForRequiredFonts(); 
}
adobe::LinuxHost::~LinuxHost()
{
    if (m_surface!=NULL)
        delete m_surface;
    // delete all the objects created
    destroyPagePool();
}

bool adobe::LinuxHost::changeViewPort(int w, int h)
{
    if( w == (int)::ceil(m_width) && h == (int)::ceil(m_height))
        return true;
    if (m_surface!=NULL)
        delete m_surface;
    m_width = w;
    m_height = h;
    iw = w;
    ih = h;
    m_surface = new adobe::LinuxSurface( m_monochrome, m_alpha, m_blend, w, h );
    if (m_pagingmode == dpdoc::PM_SCROLL_PAGES)
    {
        m_renderer->setViewport( m_width - 2*m_extraMarginHor, \
            m_height- 2*m_extraMarginVert, true );    
        setMatrix();
    }else
    {
        setPdfFontSize(m_PdfFontScale/m_originalScale );
    }
}

//unimportant
int adobe::LinuxHost::getInterfaceVersion()
{
    return 1;
}

//unimportant
void * adobe::LinuxHost::getOptionalInterface( const char * name )
{
    return NULL;
}

//unimportant
double adobe::LinuxHost::getUnitsPerInch()
{
    return m_dpi;
}

//unimportant
void adobe::LinuxHost::requestProcessing( int delay )
{

}

//unimportant
void adobe::LinuxHost::requestRepaint( int xMin, int yMin, int xMax, int yMax )
{
    if ( m_reportCallbacks )
    {
        DEBUGINFO( ": Request Repaint xMin = %i, yMin = %i, xMax = %i, yMax = %i\n", xMin, yMin, xMax, yMax );
    }
}

static char s_defaultDocURL[] = "builtin://";

//used by guessDocument
#ifndef RMSDK_9_2 
dpio::Stream * adobe::LinuxHost::getResourceStream( const dp::String& urlin, unsigned int capabilities )
{
    dp::String url = urlin;
    DEBUGINFO( ": Loading %s\n", url.utf8() );
    if( ::strncmp( url.utf8(), "data:", 5 ) == 0 )
        return dpio::Stream::createDataURLStream( url, NULL, NULL );
    // default document
    if( ::strcmp( url.utf8(), s_defaultDocURL ) == 0 )
    {
        const char * minhtml = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><body><h1>Dummy document</h1></body></html>";
        return dpio::Stream::createDataStream( "application/xhtml+xml", dp::String(minhtml), NULL, NULL );
    }
    // resources: user stylesheet and resources it references
    if( ::strncmp( url.utf8(), "res:///", 7 ) == 0 && url.length() < 1024 && !m_resFolderURL.isNull() )
    {
        char tmp[2048];
        ::strcpy( tmp, m_resFolderURL.utf8() );
        ::strcat( tmp, url.utf8()+7 );
        url = dp::String( tmp );
    }
    dpio::Partition * partition = dpio::Partition::findPartitionForURL( url );
    if( partition != NULL )
        return partition->readFile( url, NULL, capabilities );
    return NULL;
}
#else
dpio::Stream * adobe::LinuxHost::getResourceStream( const dp::String& urlin, unsigned int capabilities )
{
//    m_document->setURL("res:///fonts/AdobeHeitiStd-Regular.otf");
    DEBUGINFO( "Loading %s\n", urlin.utf8() );
	// default document
	if( ::strcmp( urlin.utf8(), s_defaultDocURL ) == 0 )
	{
		const char * minhtml = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><body><h1>Dummy document</h1></body></html>";
		return dpio::Stream::createDataStream( "application/xhtml+xml", dp::String(minhtml), NULL, NULL );
	}

	return m_resProvider->getResourceStream( urlin, capabilities );
}

#endif
//unimportant
void adobe::LinuxHost::navigateToURL( const dp::String& url, const dp::String& target )
{
    DEBUGINFO(  ": Document requested navigation to '%s' (target='%s')\n", 
            url.utf8(), target.isNull() ? "" : target.utf8() );
}

//have not been used here
void adobe::LinuxHost::reportMouseLocationInfo( const dpdoc::MouseLocationInfo & info )
{
#if 0
    if ( m_reportCallbacks )
    {
        DEBUGINFO( ": Report Mouse Info, pointerType = %i, highlight type = %i, highlight index = %i\n",
                info->pointerType, info->highlightType, info->highlightIndex );
        if ( info->linkURL != NULL )
            DEBUGINFO( ":    Link URL = %s\n", info->linkURL );
        if ( info->tooltip != NULL )
            DEBUGINFO( ":    Tool tip = %s\n", info->tooltip );
    }
#endif
}

//report
void adobe::LinuxHost::reportInternalNavigation()
{
    if ( m_reportCallbacks )
    {
        DEBUGINFO( ": Report internal navigation\n" );
    }
}

//report
void adobe::LinuxHost::reportDocumentSizeChange()
{
    if ( m_reportCallbacks )
    {
        DEBUGINFO( ": Report document size change\n" );
    }
}

//important, report the loading state,if load sucessful (password is correct)get the page count of the document
void adobe::LinuxHost::reportLoadingState( int state )
{
    if( state == dpdoc::LS_COMPLETE )
    {
        m_loadSuccess = true;
        DEBUGINFO( ": Document loaded\n" );
        if(requestDocPassword)
        {
            m_pageCount= static_cast<int>(m_document->getPageCount());
            DEBUGINFO(": m_pageCount is %d\n",m_pageCount);
        }
    }
    else if( state == dpdoc::LS_ERROR )
    {
        m_loadSuccess = false;	//added on 12.1
        DEBUGINFO( ": Document loading failed\n" );
    }
    //m_loaded = true;
}

//report
void adobe::LinuxHost::reportHighlightChange( int highlightType )
{
    if ( m_reportCallbacks )
    {
        DEBUGINFO( ": Report highlight change, highlight type = %i\n", highlightType );
    }
}

//report if render error occured
void adobe::LinuxHost::reportRendererError(const dp::String& errorString)
{
    DEBUGINFO( ": Renderer process error detected in %s:\n", m_inputURL.utf8() );
    DEBUGINFO( ": \t%s\n", errorString.utf8() );
}

bool adobe::LinuxHost::canContinueProcessing( int kind )
{
    int * count;
    int tmp = -1;
    switch( kind )
    {
        case dpdoc::PK_BACKGROUND:
            //count = &m_backgroundProcessingLimit;
            count = &tmp;
            break;
        case dpdoc::PK_RENDER:
        case dpdoc::PK_SEARCH:
        case dpdoc::PK_HIT_TEST:	
        case dpdoc::PK_FOREGROUND:
            //count = &m_foregroundProcessingLimit;
            count = &tmp;
            break;
        default:
            return true;
    }
    if ( *count <= 0 )
        return true;
    if ( *count == 1 )
    {
        DEBUGINFO( ": canContinueProcessing will return false\n" );
        return false;
    }
    (*count)--;
    return true;
}

//report if document error occured
void adobe::LinuxHost::reportDocumentError(const dp::String& errorString)
{
    DEBUGINFO( ": Document process error detected in %s\n", m_inputURL.utf8() );
    DEBUGINFO( ": \t%s\n", errorString.utf8() );
    const char *test = strstr(errorString.utf8(),"E_ADEPT_CORE_PASSHASH_NOT_FOUND");
    if(test==NULL)
    {
        m_operatorURL = NULL;
    }
    else
    {
        const char* tmp = strchr(errorString.utf8(),' ');
        m_operatorURL = new char[strlen(tmp)];
        strcpy(m_operatorURL,tmp+1);
        DEBUGINFO(": operatorURL is--%s\n",m_operatorURL);
    }
}

//unimportant
void adobe::LinuxHost::reportErrorListChange()
{
    m_documentStateError = true;
}

//unimportant
void adobe::LinuxHost::finishedPlaying()
{
    if ( m_reportCallbacks )
    {
        DEBUGINFO( ": Finished Playing\n" );
    }
}

//haven't been used now
void adobe::LinuxHost::requestLicense( const dp::String& type, const dp::String& resourceId, const dp::Data& request )
{
    if( !m_resourceId.isNull() && !m_licenseType.isNull() && !m_license.isNull() &&
            ::strcmp( m_resourceId.utf8(), resourceId.utf8() ) == 0 && ::strcmp( m_licenseType.utf8(), type.utf8() ) == 0 )
    {
        // ACS4 packaged documents
        m_document->setLicense( type, resourceId, m_license );
    }
    else if( !m_voucherId.isNull() && !m_license.isNull() &&
            ::strcmp( resourceId.utf8(), m_voucherId.utf8() ) == 0 && ::strcmp( type.utf8(), "http://ns.adobe.com/acs3" ) == 0 )
    {
        m_document->setLicense( type, resourceId, m_license );
    }
    else
        m_document->setLicense( type, resourceId, dp::Data() );
}

//important , if the Document request the password this function would be called,
//if the input password is incorrect ,this function would be called again
void adobe::LinuxHost::requestDocumentPassword()
{
    if ( m_asyncPassword )
    {
        m_asyncPasswordRequested = true;
        return;
    }
    requestDocPassword = true;
    /*	static int depth = 0;
        if ( depth++ == 0 )
        m_document->setDocumentPassword( m_pdfPassword );
        else
        m_document->setDocumentPassword( dp::String() );
        depth--;*/
}

//use this function to test that whether the Document need password
bool adobe::LinuxHost::requestPassword()
{
    return requestDocPassword;
}

//set password for the Reader to decrypt the Document
void adobe::LinuxHost::setDocmentPassword(const char* password)
{
    m_pdfPassword = dp::String( password );
    m_document->setDocumentPassword( m_pdfPassword );
}

//haven't been used now
void adobe::LinuxHost::propertyReady( const dp::String& name, const dp::String& value )
{
    if( ::strcmp( name.utf8(), "Content-Type" ) == 0 )
    {
        // instantiate the document first
        m_mimeType = value;
        m_document = dpdoc::Document::createDocument(this, m_mimeType);

        // release the stream, so that document does not create a duplicate
        // this can indirectly destroy value!
        dpio::Stream *foo = m_probeStream;
        m_probeStream = NULL;
        foo->setStreamClient(NULL);
        foo->release();

        // now initialize the document
        // initDoc(); //TODO:1029 rm
    }
}

//unimportant
void adobe::LinuxHost::propertiesReady()
{
}

//unimportant
void adobe::LinuxHost::totalLengthReady( size_t length )
{
    // don't care
}

//unimportant
void adobe::LinuxHost::bytesReady( size_t offset, const dp::Data& data, bool eof )
{
    // should never be called
}

//unimportant print the error list
void adobe::LinuxHost::reportError(const dp::String& errorString)
{
    DEBUGINFO( ": Stream reading error %s\n", m_inputURL.utf8() );
    DEBUGINFO( ": \t%s\n", errorString.utf8() );
}

//not complete now
void adobe::LinuxHost::setLicense( const dp::String& licenseType, const dp::String& resourceId,
        const dp::String& voucherId, const dp::Data& license )
{
    m_licenseType = licenseType;
    m_resourceId = resourceId;
    m_voucherId = voucherId;
    m_license = license;
}

//I didn't use this function to load a document,I used loadWithMime instead
void adobe::LinuxHost::guessMimeAndLoad()
{
    m_probeStream = getResourceStream( m_inputURL, 0 );
    if( m_probeStream == NULL )
    {
        DEBUGINFO( ": Could not create stream from URL\n" );
        return;
    }
    m_probeStream->setStreamClient( this );
    m_probeStream->requestInfo();
}

bool adobe::LinuxHost::isLoadedSuccessfully()     // get state flag for loading doc
{
    return m_loadSuccess;
}


//void ReaderHost::initDevice()
bool adobe::LinuxHost::initDevice()
{
    int code = dp::platformInit( dp::PI_DEFAULT );
    if(code != dp::IS_OK)
    {
        DEBUGINFO(": platform init faile!\n");
        return false;
    }
//    dp::setVersionInfo( "product", m_deviceInfo );
    dp::setVersionInfo( "product", "CMM-Reader");
    dp::setVersionInfo( "clientVersion", "Adobe.R.M 9.2" );
    dp::setVersionInfo( "jpeg", CONV_INT_TO_STR(JPEG_LIB_VERSION) );//TODO 
    dp::cryptRegisterOpenSSL();
    dp::deviceRegisterPrimary();
    if( !dpdev::isMobileOS() ) // no external devices for mobile OSes
        dp::deviceRegisterExternal();
    dp::deviceMountRemovablePartitions();
    dp::documentRegisterEPUB();
    dp::documentRegisterPDF();

    dpdoc::Surface::initDitheringClipMap( g_ditheringClipMap, g_ditheringDepth );
    dp::timerRegisterMasterTimer( &masterTimer );  //aleph 12.2

    //init complete
    DEBUGINFO(": Platform init sucessfully\n");
    return true;
}

const char* adobe::LinuxHost::getFileMimeType( const char * fileName )
{
    const char * ext = ::strrchr( fileName, '.' );
    const char * mimeType = NULL;
    if( ext == NULL )
        mimeType = "application/octet-stream";
    else if( ::strcmp( ext, ".pdf" ) == 0 || ::strcmp( ext, ".PDF" ) == 0 )
        mimeType = "application/pdf";
    else if( ::strcmp( ext, ".epub" ) == 0 || ::strcmp( ext, ".EPUB" ) == 0 )
        mimeType = "application/epub+zip";
    else if( ::strcmp( ext, ".psf" ) == 0 || ::strcmp( ext, ".PSF" ) == 0 )
        mimeType = "application/psf";
    else if( ::strcmp( ext, ".svg" ) == 0 || ::strcmp( ext, ".SVG" ) == 0 )
        mimeType = "image/svg+xml";
    else if( ::strcmp( ext, ".html" ) == 0 || ::strcmp( ext, ".HTML" ) == 0 )
        mimeType = "text/html";
    else
        mimeType = "application/octet-stream";
    return mimeType;
}

dp::String adobe::LinuxHost::urlEncodeFileName( const char * str )
{
    // filename
    char path[512] = "/";
    if( str[0] == '/' || 
            (str[0] != '\0' && str[1] == ':' && (str[2] == '\\' || str[2] == '/')) ||
            (str[0] == '\\' || str[1] == '\\') )
    {
        // abs path already
    }
    else
    {
        // relative path; non-Unicode on Windows for now
        ::getcwd( path, sizeof path );

        /* Remove any occurances of ".." at front of string and remove a corresponding
           element from the path */
        path[sizeof(path) -1] = '\0';
        size_t pathLength = strlen( path );
        char tempPath[sizeof path];
        memcpy( tempPath, path, pathLength + 1 );
        while ( str[0] == '.' && str[1] == '.' &&
                ( str[2] == '\\' || str[2] == '/' ) &&
                pathLength > 0 )
        {
            if ( path[pathLength - 1] == ':' )
            {
                pathLength = 0;
                break;
            }
            str += 3;
            while ( path[pathLength - 1] != '\\' && path[pathLength - 1] != '/' )
            {
                if ( --pathLength == 0 )
                    break;
                path[pathLength] = '\0';
            }
            if ( pathLength != 0 )
            {
                if ( --pathLength == 0 )
                    break;
                path[pathLength] = '\0';
            }
        }
        if ( pathLength == 0 )
            /* Not enough elements in path for the number of occurances
               of "..". Just restore path. */
            memcpy( path, tempPath, strlen( tempPath ) + 1 );

        if( ::strcmp( str, "." ) != 0 )
        {
            ::strncat( path, "/", sizeof path ); 
            ::strncat( path, str, sizeof path );
        }
        str = path;
    }

    size_t len = 0;
    const char * p = str;
    while( true )
    {
        char c = *(p++);
        if( c == '\0' )
            break;
        len++;
        if( c < ' ' || (unsigned char)c > '~' || c == '%' || c == '+' )
            len += 2;
    }

    bool winShare = str[0] == '\\' && str[1] == '\\';
    bool unixAbs = !winShare && (str[0] == '/' || str[1] == '\\');
    char * url = new char[len+(winShare?6:(unixAbs?8:9))];
    if( winShare )
        ::strcpy( url, "file:" );
    else if( unixAbs )
        ::strcpy( url, "file://" );
    else
        ::strcpy( url, "file:///" );

    char * t = url + ::strlen(url);
    p = str;
    while( true )
    {
        char c = *(p++);
        if( c == '\0' )
            break;
        if( c < ' ' || (unsigned char)c > '~' || c == '%' || c == '+' )
        {
            sprintf( t, "%%%02X", (unsigned char)c );
            t += 3;
        }
        else if( c == ' ' )
        {
            *(t++) = '+';
        }
        else if( c == '\\' )
        {
            *(t++) = '/';
        }
        else
        {
            *(t++) = c;
        }
    }
    *(t++) = '\0';
    dp::String result(url);
    delete[] url;
    return result;
}


bool adobe::LinuxHost::openBook(char* filePath, int pagingmode, double scale, dpdoc::Matrix matrix)
{
    m_fit = -1; // reset page size
    // check filePath then get minetype, m_inputURL 
    m_mimeType = getFileMimeType(filePath) ; 
    m_inputURL = urlEncodeFileName( filePath );

    m_document = dpdoc::Document::createDocument(this, m_mimeType);
    m_document->setURL( m_inputURL );
    if( !m_loadSuccess )
       return false; 

    m_pageCount = static_cast<int>(m_document->getPageCount());

    m_renderer = m_document->createRenderer(this);

    m_renderer->setViewport( m_width - 2*m_extraMarginHor, \
            m_height- 2*m_extraMarginVert, true );    
    setPagingMode(pagingmode);    
    setMatrix();
    
    return true; 
}

//dpdoc::PM_HARD_PAGES
bool adobe::LinuxHost::setPagingMode(int pagingMode)
{
    if (pagingMode==0)
    {
        m_pagingmode = dpdoc::PM_HARD_PAGES;
        m_cache_enable = true;
    }
    else if (pagingMode==1) { 
        m_pagingmode = dpdoc::PM_FLOW_PAGES;
        m_cache_enable = true;
    }
    else if (pagingMode==2) {
        m_pagingmode = dpdoc::PM_SCROLL_PAGES;
        m_cache_enable = false;
    }
    else 
    {
        m_pagingmode = dpdoc::PM_HARD_PAGES;
        m_cache_enable = true;
    }

    if (m_renderer&&m_pagingmode!=m_renderer->getPagingMode())
        m_renderer->setPagingMode( m_pagingmode );
    else
        return false;
    return true;
}

bool adobe::LinuxHost::setMatrix(int fit, int ystart)
{
    m_renderer->setNavigationMatrix(dpdoc::Matrix());
    getEnvMatrixFitWindow(fit, ystart);
    m_renderer->setEnvironmentMatrix(m_default_env_matrix);
    m_surface->setScale(m_scale);  // for PNG
    return true;
}

bool adobe::LinuxHost::getEnvMatrixFitWindow(int fit, int ystart)
{
    bool ret = false;
    double w;
    double h;
    bool tmp;
#ifdef RMSDK_9_2    
    dpdoc::Rectangle r ;//= dpdoc::Rectangle(0,0,0,0);
    m_renderer->getNaturalSize(&r);
    w= r.xMax - r.xMin;
    h= r.yMax - r.yMin;
#else
    m_renderer->getNaturalSize(&w,&h,&tmp);
#endif
//    DEBUGINFO("line: %d w=%8.4f, h=%8.4f, m_scale=%8.4f, dirty_scale=%8.4f",__LINE__, w, h,m_scale, dirty_scale);
    if(w-n_width>0.5||n_width-w>0.5 ||h-n_height>0.5|| n_height-h>0.5 || m_autoCutMargin || fit!=m_fit) //size changed or for cutting margin
    {

        tmp = true;
        n_width = w;
        n_height = h;
        if(m_width-n_width>0.5||n_width-m_width>0.5 ||m_height-n_height>0.5|| n_height-m_height>0.5 || m_autoCutMargin ||fit!=m_fit )
        {
            m_fit = fit;
            double hScale = m_width / n_width;
            double vScale = m_height / n_height;
            if (fit==0)  // fit page, default
                m_scale = (hScale < vScale) ? hScale : vScale;
            else if (fit==1) // fit width
                m_scale = hScale;
            else  // fit height
                m_scale = vScale;

            if (m_pagingmode==dpdoc::PM_SCROLL_PAGES)
            {
                m_scale = hScale;
            }

            if (m_pagingmode==dpdoc::PM_HARD_PAGES)
            {
                m_originalScale = m_scale ;
            }

#if 1
            int move_x =0, move_y=0;
            double l_scale = 1.0;
            if (m_autoCutMargin)
            {
                m_autoCutMargin  = false; 
                int minY=0, maxY=0;
                int Yrange = getYrange(&minY, &maxY);
                DEBUGINFO("%d %d %d \n", minY, maxY , Yrange);
                l_scale = m_height/Yrange;
                l_scale = (l_scale >1.25?1.25:l_scale );
                l_scale = (l_scale <1.0?1:l_scale );
                m_scale *= l_scale;

                //minY = (int)((double)minY/m_scale);
                move_x = minY*m_width/m_height;//(int)(n_width*(l_scale-1));
                move_y = minY;//(int)(n_height*(l_scale-1));
            }
#endif
#if 1
                int s_x = (int) ((m_width -n_width*m_scale/l_scale)/2);
                int s_y = (int) ((m_height-n_height*m_scale/l_scale)/2);
                move_x -= s_x;
                if (m_pagingmode!=dpdoc::PM_SCROLL_PAGES)
                {
                    move_y -= s_y;
                }
#endif
            m_default_env_matrix = dpdoc::Matrix(m_scale*dirty_scale, 0, 0, m_scale*dirty_scale, -move_x, -move_y);
            DEBUGINFO("line: %d w=%8.4f, h=%8.4f, m_scale=%8.4f, dirty_scale=%8.4f",__LINE__, w, h,m_scale, dirty_scale);
        }
        ret = true;
    }

    if (m_pagingmode==dpdoc::PM_SCROLL_PAGES)
    {
        m_default_env_matrix.f = -ystart;  
        ret = true;
    }   
    return ret;
}

bool adobe::LinuxHost::autoCutMargin(bool cut)
{
    m_autoCutMargin  = cut; 
    if (m_autoCutMargin)
    {
        m_fit = 0;//fit page ???
        setMatrix();
    }else
    {
        // reset page, set m_fit to 0 (fit page)
        m_fit = -1;
        setMatrix(0);
    }
}

/*
 *0: fit page
 *1: fit width
 *2: fit height
 */
bool adobe::LinuxHost::setFitMode(int fit)
{
    if(m_fit == fit) 
        return false;
    return setMatrix(fit);
}

bool adobe::LinuxHost::closeBook()
{
    // release relative objects
    if (m_renderer)
    {
        m_renderer->release();
        m_renderer = NULL;
    }

    if (m_document)
    {
        m_document->release();; 
        m_document = NULL;
    }
//    if (m_surface)
//        delete  m_surface;
    return true;
}

bool adobe::LinuxHost::initPagePool()
{
    int paletteSize = 0;
    int bitwidth = 0;
    int bmpsize = 0;
    if ( m_monochrome )
        if( m_alpha)
            bitwidth = 2;
        else
            bitwidth = 1;
    else
        if( m_alpha)
            bitwidth = 4;
        else
            bitwidth = 3;

    paletteSize = bitwidth==1?1024:0;

    bmpsize = sizeof(FileHead)
        + sizeof(Infohead)
        + bitwidth*m_width*m_height
        +paletteSize;

    if ((m_bufferpool=(unsigned char*)malloc(bmpsize*3))==NULL)
    {
        printf("malloc page pool error\n");
        return false;
    }

    m_pagepool_items[0].data.bmp_data = m_bufferpool;
    m_pagepool_items[1].data.bmp_data = m_bufferpool+bmpsize;
    m_pagepool_items[2].data.bmp_data = m_bufferpool+bmpsize*2;
    m_pagepool = new PagePool(m_pagepool_items, 3);

    return true;
}


bool adobe::LinuxHost::destroyPagePool()
{
    if (m_bufferpool!=NULL)
        free(m_bufferpool);
}

bool adobe::LinuxHost::feedPool(int at)
{
    at = at%3;
    dll_item *item = m_pagepool->get_item(at);
    int bmpsize = m_surface->getBmpSize();
    for (int i=0;i<bmpsize;i++)
        item->data.bmp_data[i] = *(m_surface->getBmpData()+i);
#ifdef RMSDK_9_2
    dp::ref<dpdoc::Location> loc = m_renderer->getCurrentLocation();
#else
    dpdoc::Location *loc = m_renderer->getCurrentLocation();
#endif
    item->data.page_num = static_cast<int>(loc->getPagePosition());
#ifndef RMSDK_9_2
    loc->release();
#endif
    item->stat = ITEM_STATE_READY;

    DEBUGINFO("feed pool %d................%d\n", at, item->data.page_num);
}

bool adobe::LinuxHost::nextPage()
{
    DEBUGINFO("%s",__func__);
    if (m_cache_enable)
    {
        DEBUGINFO("offset=========%d\n", m_offset);
        bool ret = false;
        while(m_offset<=0)
        {
            ret = m_renderer->nextScreen();
            if (ret)
                m_offset++;
        }
        m_offset=0;

        m_current_page_num = pageNumber(1);
        //m_current_page_num = pageNumber(0);
        checkReachHeadOrTail(false);//at end ?
        checkReachHeadOrTail(true); //at beginning ?
#if 0
        ret = m_renderer->nextScreen();
#else
/*
 * to solve a problem as:
 *  in reflow-paging mode, at the end of a page,
 *  you can not go to the next screen, just go to
 *  the beginning of this page
 */
        dp::ref<dpdoc::Location> cur_loc = m_renderer->getScreenBeginning();
        ret = m_renderer->nextScreen();

        if (m_pagingmode == dpdoc::PM_FLOW_PAGES)
        {
            if (ret)
            {
                dp::ref<dpdoc::Location> nxt_loc = m_renderer->getScreenBeginning();
                if (nxt_loc->compare(cur_loc)<=0) 
                {
                    dp::ref<dpdoc::Location> lloc = 
                                m_document->getLocationFromPagePosition(static_cast<double>(m_current_page_num+1));
                    if(lloc != NULL)
                    {
                        m_renderer->navigateToLocation( lloc );
                        ret = true;
                    }
                    else
                    {
                        ret = false;
                    }
                }
            }
        }
#endif
        if (ret)
        {
            m_offset++;
            ret = paintPage(m_surface);
            m_pagepool->next();
            int at = (m_pagepool->get_cursor()+1)%3;
            feedPool(at);
        }
        else    // reach the tail of the document
        {
            int at = (m_pagepool->get_cursor()+1)%3;
            dll_item *item = m_pagepool->get_item(at);
            if (item->stat==ITEM_STATE_READY)   //first reach
                m_pagepool->next();
        }



        return ret;  
    }
    else
    {
        bool ret = m_renderer->nextScreen();
        ret = paintPage(m_surface);
#ifdef RMSDK_9_2
        dp::ref<dpdoc::Location> loc = m_renderer->getCurrentLocation();
#else
        dpdoc::Location *loc = m_renderer->getCurrentLocation();
#endif
        m_current_page_num = static_cast<int>(loc->getPagePosition());
        checkReachHeadOrTail(false);//at end ?

#ifndef RMSDK_9_2
    loc->release();
#endif
        return ret;
    }
}


bool adobe::LinuxHost::previousPage()
{
    DEBUGINFO("%s",__func__);
    if (m_cache_enable)
    {
        DEBUGINFO("offset=========%d\n", m_offset);
        bool ret = false;

        while(m_offset>=0)
        {
            ret = m_renderer->previousScreen();
            if (ret)
                m_offset--;
        }
        m_offset=0;
        
        //m_current_page_num = pageNumber(m_pagepool->get_cursor());
        m_current_page_num = pageNumber(-1);
        checkReachHeadOrTail(true); //at beginning ?
        checkReachHeadOrTail(false);//at end ?

        ret = m_renderer->previousScreen();
        if (ret)
        {
            m_offset--;
            ret = paintPage(m_surface);
            m_pagepool->prev();
            int at = (m_pagepool->get_cursor()+2)%3;
            feedPool(at);
        }
        else    // reach the head of the document
        {
            int at = (m_pagepool->get_cursor()+2)%3;
            dll_item *item = m_pagepool->get_item(at);
            if (item->stat==ITEM_STATE_READY)//first reach
                m_pagepool->prev();
        }

        return ret;  
    }
    else
    {
        bool ret = m_renderer->previousScreen();
        if (ret)
            ret = paintPage(m_surface);
#ifdef RMSDK_9_2
        dp::ref<dpdoc::Location> loc = m_renderer->getCurrentLocation();
#else
        dpdoc::Location *loc = m_renderer->getCurrentLocation();
#endif
        m_current_page_num = static_cast<int>(loc->getPagePosition());
        checkReachHeadOrTail(true); //at beginning ?
#ifndef RMSDK_9_2
    loc->release();
#endif        
        return ret;
    }
}

//no cache: just jump to #pg, and feed the buffer with current page
//cache-enable:
//  firstly:  set later false, goto #pg, and feed the buffer with current page
//  secondly: set later ture, feed pool with prev and next page
bool adobe::LinuxHost::jumpPage( int pg, bool later)
{
    DEBUGINFO("in :%s",__func__);
    if (pg<0) pg = 0;
    if (pg>m_pageCount-1) pg = m_pageCount-1;
    bool ret = false;
    if (m_cache_enable)
    {    
        m_offset = 0;
        if (!later)
        {
            m_pagepool->jump();
#ifndef RMSDK_9_2 
            dpdoc::Location * loc =	NULL;
#else
            dp::ref<dpdoc::Location> loc ;            
#endif
            loc = m_document->getLocationFromPagePosition(static_cast<double>(pg));
            if(loc != NULL)
            {
                m_renderer->navigateToLocation( loc );
#ifndef RMSDK_9_2
                loc->release();
#endif
            }
            else
                return false;

            ret = paintPage(m_surface);
            if (ret)
                feedPool(1);

            m_current_page_num = pg; 
            checkReachHeadOrTail(false);//at end ?
            checkReachHeadOrTail(true); //at beginning ?
        }
        else 
        {
            ret = m_renderer->previousScreen();
            if (ret)
            {
                m_offset--;
                ret = paintPage(m_surface);
                if (ret)
                    feedPool(0);
            }

            while(m_offset<0)
            {
                ret = m_renderer->nextScreen();
                if (ret)
                    m_offset++;
            }

            ret = m_renderer->nextScreen();

            if (ret)
            {
                m_offset++;
                ret = paintPage(m_surface);
                if (ret)
                    feedPool(2);
            }
        }
        return ret;  
    }
    else
    {
#ifndef RMSDK_9_2 
        dpdoc::Location * loc =	NULL;
#else
        dp::ref<dpdoc::Location> loc ;            
#endif
        loc = m_document->getLocationFromPagePosition(static_cast<double>(pg));

        if(loc != NULL)
        {
            getRenderer()->navigateToLocation( loc );
#ifndef RMSDK_9_2
            loc->release();
#endif
            ret = paintPage(m_surface);
            m_current_page_num = pg; 
            checkReachHeadOrTail(false);//at end ?
            checkReachHeadOrTail(true); //at beginning ?
            return ret;
        }
        return false;

    }
}

//head: true, check HEAD
//      false, check TAIL
bool adobe::LinuxHost::checkReachHeadOrTail(bool head/*, int at, pool_item_flag flg*/)
{
    DEBUGINFO("in :%s",__func__);
    DEBUGINFO("in :%s:m_current_page_num:%d ",__func__, m_current_page_num);

    if (!getRenderer())
    {
        LOGE("no renderer, initialize it first");
        return false;
    }

    if (head)   //test if reach beginning
    {
        if (m_current_page_num==0)
        {
            //m_is_at_beginning = getRenderer()->isAtBeginning(); //not work as document described
            m_is_at_beginning = !getRenderer()->previousScreen();
            if(!m_is_at_beginning) 
                getRenderer()->nextScreen();
        }
        else
        {
            m_is_at_beginning = false;
        }
    }
    else//test if reach end
    {
        if (m_current_page_num==m_pageCount-1)
        {
            //m_is_at_end = getRenderer()->isAtEnd();//not work as document described
            m_is_at_end = !getRenderer()->nextScreen();
            if(!m_is_at_end) 
                getRenderer()->previousScreen();
        }else
        {
            m_is_at_end = false;
        }
 
    }

#ifdef A_DEBUG
        if (head&&m_is_at_beginning)
            LOGD("set m_is_at_beginning to true");
        if (!head&&m_is_at_end)
            LOGD("set m_is_at_end to true");
#endif
    DEBUGINFO("out:%s",__func__);
    return true;
}

int adobe::LinuxHost::getChaptercount()
{
    int ret = 0;
    if(getDocument()==NULL)
        return -1;
#ifdef RMSDK_9_2
    dpdoc::TOCItem * tocItem = getDocument()->getTocRoot();
#else
    dpdoc::TOCItem * tocItem = getDocument()->getTOCRoot();
#endif
    if( tocItem == NULL )
    {
        DEBUGINFO(": this document has no table of content");
        return 0;
    }
    else
    {	
        ret = tocItem->getChildCount();
        tocItem->release();
        return ret; 
    }
}

void adobe::LinuxHost::getTOCList()
{
    dpdoc::TOCItem *root;
    dpdoc::TOCItem *child;
    dpdoc::TOCItem *curr;
    toc_item item;
    dp::String dp_string;
    int count=0;
    int idx = 0;
    int level=0;
#ifdef RMSDK_9_2
    dp::ref<dpdoc::Location> loc;
#else
    dpdoc::Location* loc = NULL;
#endif

    list< toc_item >::iterator it;
    list< toc_item >::iterator it_i;

#ifdef RMSDK_9_2
    root = m_document->getTocRoot();
#else
    root = m_document->getTOCRoot();
#endif
    if (root!=NULL)
    {
        m_toc_list.clear();

        item.node = root;
        dp_string = "root";//root->getTitle();
        strncpy(item.title, dp_string.utf8(), sizeof(item.title)-1);   
        if ((loc = root->getLocation())!=NULL)
        {
            item.position = (int)(loc->getPagePosition());
#ifndef RMSDK_9_2
            loc->release();
#endif
        }
        item.level = level;

        m_toc_list.push_back(item);
        for (it = m_toc_list.begin(); it!=m_toc_list.end();it++ )
        {
            level = (*it).level;
            curr  = (*it).node;
            count = curr->getChildCount();

            for (idx=0, it_i=it, ++it_i; idx<count; idx++)
            {
                child = curr->getChild(idx); 
                item.node = child;
                dp_string = child->getTitle();
                strncpy(item.title, dp_string.utf8(), sizeof(item.title)-1);   
                if ((loc = child->getLocation())!=NULL)
                {
                    item.position = (int)(loc->getPagePosition());
#ifndef RMSDK_9_2                    
                    loc->release();
#endif
                }
                item.level = level+1;
                m_toc_list.insert(it_i, item);
            }
            (*it).node->release();
        } 

#ifdef  READER_JNI_DEBUG // just for debug
        DEBUGINFO("%d\n", m_toc_list.size());
        int i=0;
        for (it = m_toc_list.begin(); it!=m_toc_list.end();it++ )
        {
            DEBUGINFO("%8d %8d \t%s \n",  i++, (*it).level, (*it).title);
        }
#endif
        if (!m_toc_list.empty())
            m_toc_list.pop_front();//pop the front element(it's useless)
        DEBUGINFO("%d\n", m_toc_list.size());
    }
}
//**important**  render pages into image data(saved in surface)
adobe::LinuxSurface * adobe::LinuxHost::renderPage(  double * rendTime )
{
    double time = 0;

    dpdoc::Matrix current;
    /* If navigation matrix is no longer the identity matrix, change it back */
    getRenderer()->getNavigationMatrix( &current );
    getRenderer()->setNavigationMatrix( dpdoc::Matrix() );

    time = getCurrentTime();
    FirstPage();
    m_surface->clearbuffer();
    getRenderer()->paint( 0, 0, iw, ih, m_surface );

    *rendTime = getCurrentTime() - time;
    return m_surface;
}



const char* adobe::LinuxHost::getOperatorURL()
{
    return m_operatorURL;
}

//get the location to next page
bool adobe::LinuxHost::NextPage()
{
    return m_renderer->nextScreen();
}

//get the location to previous page
bool adobe::LinuxHost::PreviousPage()
{
    return m_renderer->previousScreen();
}

//get the location to first page
void adobe::LinuxHost::FirstPage()
{
    dpdoc::Location * location = NULL;
    location = m_document->getBeginning();
    if( location )
    {
        m_renderer->navigateToLocation(location);
        location->release();
    }
}

//get the location to last page
void adobe::LinuxHost::LastPage()
{
    dpdoc::Location * location = m_document->getEnd();
    if( location )
    {
        m_renderer->navigateToLocation(location);
        location->release();
    }
}

//get the location to input page
void adobe::LinuxHost::inputPage(int pageStart)
{
    --pageStart;
    dpdoc::Location * loc =	NULL;
    loc = getDocument()->getLocationFromPagePosition( static_cast<double>(pageStart) );
    if(loc == NULL)
    {
        DEBUGINFO(": Cannot get loacation\n");
        return ;
    }
    getRenderer()->navigateToLocation( loc );
    checkNaturalSize();  //2010.1.7  added, check the natural size every page
    loc->release();
}


//get the first line of the current page, maybe consume some memory...
const char* adobe::LinuxHost::getCurrentPageFirstline()
{
    dpdoc::Location * beginning = NULL;
    dpdoc::Location * end = NULL;
    beginning = getRenderer()->getScreenBeginning();
    end = getRenderer()->getScreenEnd();
    dp::String text = getDocument()->getText(beginning,end);

    ::memset(m_firstlineBuf,0,BOOKMARKFIRSTLINE);
    ::strncpy(m_firstlineBuf,text.utf8(),BOOKMARKFIRSTLINE);
    if(beginning!=NULL) beginning->release();
    if(end!=NULL) end->release();
    return m_firstlineBuf;
}

//return the page count
int adobe::LinuxHost::pageCount()
{
    return m_pageCount;
}

//return the page count
int adobe::LinuxHost::pageNumber(int id)
{
    //return m_pagepool_items[m_pagepool->get_cursor()].data.page_num;
    int at = (m_pagepool->get_cursor()+id+3)%3;
    return m_pagepool->get_item(at)->data.page_num;
}

// search 'text' in the PDF doc and return the page position
int adobe::LinuxHost::search(const char* text, bool isBackward)
{
    return 0;  /* TODO*/
#if 0
    if(text == NULL) {
        DEBUGINFO(": No text for searching\n");
        return false;
    }

    dpdoc::Location * beginning = NULL;
    dpdoc::Location * end = NULL;
    int res = -1;
    if( m_renderer->getHighlight( dpdoc::HT_SELECTION, 0, &beginning, &end ) )
    {
        if( isBackward )
        {
            end->release();
            end = m_document->getBeginning();
        }
        else
        {
            beginning->release();
            beginning = end;
            end = m_document->getEnd();
        }
    }
    else
    {
        beginning = m_renderer->getScreenBeginning();
        end = m_document->getEnd();
    }
    m_renderer->removeAllHighlights( dpdoc::HT_SELECTION );
    dpdoc::Location * foundBeginning;
    dpdoc::Location * foundEnd;
    dpdoc::SearchFlags flags = static_cast<dpdoc::SearchFlags>(0);

    if ( isBackward ) 
        flags = static_cast<dpdoc::SearchFlags>(flags | dpdoc::SF_BACK);

    if( m_document->findText( beginning, end, flags, text, &foundBeginning, &foundEnd ) )
    {
        int highlight = m_renderer->addHighlight( dpdoc::HT_SELECTION, foundBeginning, foundEnd );
        m_renderer->navigateToHighlight( dpdoc::HT_SELECTION, highlight);

        res= static_cast<int>(foundBeginning->getPagePosition());
        DEBUGINFO("current page number%d\n", res) ;

        foundBeginning->release();
        foundEnd->release();
    }
    else
    {
        DEBUGINFO("Cannot find the text: %s\n",text);
    }
    beginning->release();
    end->release();
    return res;
#endif
}


int adobe::LinuxHost::search(const char* text, bool isBackward, int start_pos, int range )
{
    return 0;   /* TODO*/
#if 0
    if(text == NULL) {
        DEBUGINFO(": No text for searching\n");
        return false;
    }

    dpdoc::Location * beginning = NULL;
    dpdoc::Location * end = NULL;
    int res = -1;
    int end_pos = -1;

    if (start_pos>pageCount()-1)
        start_pos = pageCount()-1;
    if (start_pos<0)
        start_pos = 0;


    if (isBackward)
        end_pos = start_pos-range;
    else
        end_pos = start_pos+range;
    if (end_pos>pageCount()-1)
        end_pos = pageCount()-1;
    if (end_pos<0)
        end_pos = 0;

    if( m_renderer->getHighlight( dpdoc::HT_SELECTION, 0, &beginning, &end ) )
    {
        if( isBackward )
        {
            if (start_pos ==(int)(beginning->getPagePosition()))
                ;
            else
            {
                beginning->release();
                beginning = m_document->getLocationFromPagePosition(static_cast<double>(start_pos));
            }
            end->release();
        }
        else
        {
            beginning->release();
            if (start_pos ==(int)(end->getPagePosition()))
            {
                beginning = end;
                //end->release();
            }
            else
                beginning = m_document->getLocationFromPagePosition(static_cast<double>(start_pos));
        }
    }
    else
       beginning = m_document->getLocationFromPagePosition(static_cast<double>(start_pos));

    if (end_pos==pageCount()-1)
        end = m_document->getEnd();
    else 
        end = m_document->getLocationFromPagePosition(static_cast<double>(end_pos));

    if(beginning != NULL)
        m_renderer->navigateToLocation(beginning);
    m_renderer->removeAllHighlights( dpdoc::HT_SELECTION );

    dpdoc::Location * foundBeginning;
    dpdoc::Location * foundEnd;
    dpdoc::SearchFlags flags = static_cast<dpdoc::SearchFlags>(0);

    if ( isBackward ) 
        flags = static_cast<dpdoc::SearchFlags>(flags | dpdoc::SF_BACK);

#if 0
    beginning = m_renderer->getScreenBeginning();
    end = m_renderer->getScreenEnd();
    flags = static_cast<dpdoc::SearchFlags>(flags | dpdoc::SF_WHOLE_WORD);
#endif
    if( m_document->findText( beginning, end, flags, text, &foundBeginning, &foundEnd ) )
    {
        int highlight = m_renderer->addHighlight( dpdoc::HT_SELECTION, foundBeginning, foundEnd );
        m_renderer->navigateToHighlight( dpdoc::HT_SELECTION, highlight);

        res= static_cast<int>(foundBeginning->getPagePosition());
        DEBUGINFO("current page number%d\n", res) ;

        foundBeginning->release();
        foundEnd->release();
    }
    else
    {
        DEBUGINFO("Cannot find the text: %s\n",text);
    }
FOOT_TRACE();
    if (beginning!=NULL)beginning->release();
FOOT_TRACE();
    if (end!=NULL) end->release();
FOOT_TRACE();
    return res;
#endif
}


#if 0
bool adobe::LinuxHost::isDocBeginning()
{
    if(getRenderer()==NULL)
        return false;
    if(!haveCached)
        return getRenderer()->isAtBeginning();  //2010.1.8
    else 
        return isBegin;
}

bool adobe::LinuxHost::isDocEnd()
{
    if(getRenderer()==NULL)
        return false;
    if(!haveCached)
        return getRenderer()->isAtEnd();   //2010.1.8
    else 
        return isEnd;
}
#endif

//check the natural size of current page
bool adobe::LinuxHost::checkNaturalSize()
{
    if (m_pagingmode!=dpdoc::PM_SCROLL_PAGES)
        setMatrix(m_fit);

    if (m_pagingmode==dpdoc::PM_SCROLL_PAGES)
    {
        if (n_width>780)
            return false;
    }
    else
    {
        #define PAGE_MAX_AREA (750*1000)
        int area = static_cast<int>(n_width*n_height);
        if (area>PAGE_MAX_AREA) //too large
            return false;
    }
    return true;
}

void adobe::LinuxHost::gotoBookmarklocation(const char* bookmark)
{
    dpdoc::Location * loc = getDocument()->getLocationFromBookmark( bookmark );
    if( loc == NULL )
        DEBUGINFO( ": Cound not extract location from the bookmark: %s\n", bookmark );
    else
    {
        getRenderer()->navigateToLocation( loc );
        loc->release();
    }

    checkNaturalSize();  //2010.1.7  added, check the natural size every page
}

const char* adobe::LinuxHost::getFirstLine()
{
#if 0
    const char* tmp = getCurrentPageFirstline();
    if(tmp == NULL)
    {
        DEBUGINFO(": Cannot get any text from current page\n");
        return NULL;
    }
    ::memset(c_firstline, 0, BOOKMARKFIRSTLINE);
    ::strncpy(c_firstline, tmp, BOOKMARKFIRSTLINE);
    return c_firstline;
#endif
}



bool adobe::LinuxHost::getImage(unsigned char* buf, int id)
{
    if (m_cache_enable)
    {
        int at = (m_pagepool->get_cursor()+id+3)%3;
        pool_item_state state = m_pagepool->get_item(at)->stat;
        if (state!=ITEM_STATE_READY)
            return false;
        unsigned char* pbmp=m_pagepool->get_item(at)->data.bmp_data;
        int i = 0;
        int size = getImageSize();
        while(i<size)
        {
            *(buf+i) = *(pbmp+i);
            i++;
        }
//        m_pagepool->get_item(at)->stat = ITEM_STATE_DIRTY;
        DEBUGINFO("get data from pool: %d\n", at);
        return true;
    }
    else
    {
        int size = getImageSize();
        unsigned char*pbmp = m_surface->getBmpData();
        for (int i=0;i<size;i++)
        {
            *(buf+i) = *(pbmp+i);
        }
    }
}

int adobe::LinuxHost::getImageSize()
{
    return m_surface->getBmpSize();
}

bool adobe::LinuxHost::paintPage(LinuxSurface* surface)
{
    LOG_READER_DEBUG("in : %s", __func__);
    if (!checkNaturalSize())
        return false;
    surface->clearbuffer();
    getRenderer()->paint( 0, 0, iw, ih, surface );
    LOG_READER_DEBUG("out: %s", __func__);
    return true;
}

#if 0
//get the Document width when rendering
double adobe::LinuxHost::H_getDocWidth()
{
    if(reflow)
    {
        return m_width;
    }
    else
    {
        return n_width*m_scale;
    }
}

double adobe::LinuxHost::H_getDocHeight()
{
    if(reflow)
    {
        return m_height;
    }
    else
    {
        return n_height*m_scale;
    }
}
#endif


//set the font size of Epub
void adobe::LinuxHost::setEpubFontSize(double size)
{
    getRenderer()->setDefaultFontSize( size );
}

//set the font size of Pdf
void adobe::LinuxHost::setPdfFontSize(double scale)
{
    m_PdfFontScale = scale*m_originalScale;  //use original scale instead
#if 1
    if (scale-1.0<0.05&&scale-1.0>-0.05)
    {
        setPagingMode(0);
        setMatrix();
        reflow = false;
    }else
#endif
    {
        DEBUGINFO(": m_PdfFontScale : %g, input scale : %g, m_scale : %g\n",m_PdfFontScale,scale,m_scale);
        DEBUGINFO(": m_PdfFontScale : %g, input scale : %g, m_originalScale : %g\n",m_PdfFontScale,scale,m_originalScale);
        m_renderer->setViewport( (float)m_width/m_PdfFontScale, (float)m_height/m_PdfFontScale, false );
        m_renderer->setEnvironmentMatrix( dpdoc::Matrix(m_PdfFontScale, 0, 0, m_PdfFontScale, 0, 0) );	

//        m_renderer->setPagingMode( dpdoc::PM_FLOW_PAGES );  //reflow mode
        setPagingMode(1);
        reflow = true;
    }
}


//zoomin or zoomout
void adobe::LinuxHost::setViewScale(double scale)
{
    if(getRenderer()==NULL)
        return ;
    if(reflow)
    {
        m_renderer->setPagingMode( dpdoc::PM_HARD_PAGES );
        reflow = false;
    }
    //m_fit = false;
    dirty_scale = scale;
    dpdoc::Matrix matrix(m_scale*dirty_scale, 0, 0, m_scale*dirty_scale, 0, 0);
    getRenderer()->setEnvironmentMatrix( matrix );
    getRenderer()->setNavigationMatrix( dpdoc::Matrix() );
}

//get the meta data of the ebook
_metadata adobe::LinuxHost::getMetaData()
{
    if(getDocument()==NULL)
    {
        _metadata zero;
        ::memset(&zero,0,sizeof(zero));
        return zero;
    }
    _metadata md;
    ::memset((void *)&md,0,sizeof(md));
    static const char * metadataNames[] = {
        "DC.title",
        "Title",
        "DC.language",
        "DC.identifier",
        "DC.creator",
        "Author",
        "DC.date",
        "Date",
        "DC.description",
        "DC.issued",
        "DC.publisher",
        "Publisher",
        "DC.rights",
        "DC.type",
    };		//use these string to get metadata
    static const int numMetadataNames = sizeof(metadataNames) / sizeof(char *);
    dp::String attrs;
    dp::String metadata;
    for ( int nameIdx = 0; nameIdx < numMetadataNames; nameIdx++ )
    {
        int i = 0;
        do
        {
            const char * name = metadataNames[nameIdx];
#ifdef RMSDK_9_2 
            metadata = getDocument()->getMetadata(name, i++)->getValue ();
#else
            metadata = getDocument()->getMetadata(name, i++, &attrs);
#endif

            if ( !metadata.isNull() )
            {
                //	DEBUGINFO(": %s = %s %s\n",name,metadata.utf8(),attrs.utf8());
                switch(nameIdx)
                {
                    case 0:
                        ::memcpy(md.DC_title,metadata.utf8(),METADATASIZE1-1);
                        break;
                    case 1:
                        ::memcpy(md.Title,metadata.utf8(),METADATASIZE1-1);
                        break;
                    case 2:
                        ::memcpy(md.DC_language,metadata.utf8(),METADATASIZE1-1);
                        break;
                    case 3:
                        ::memcpy(md.DC_identifier,metadata.utf8(),METADATASIZE1-1);
                        break;
                    case 4:
                        ::memcpy(md.DC_creator,metadata.utf8(),METADATASIZE1-1);
                        break;
                    case 5:
                        ::memcpy(md.Author,metadata.utf8(),METADATASIZE1-1);
                        break;
                    case 6:
                        ::memcpy(md.DC_date,metadata.utf8(),METADATASIZE1-1);
                        break;
                    case 7:
                        ::memcpy(md.Date,metadata.utf8(),METADATASIZE1-1);
                        break;
                    case 8:
                        ::memcpy(md.DC_description,metadata.utf8(),METADATASIZE2-1);
                        break;
                    case 9:
                        ::memcpy(md.DC_issued,metadata.utf8(),METADATASIZE1-1);
                        break;
                    case 10:
                        ::memcpy(md.DC_publisher,metadata.utf8(),METADATASIZE1-1);
                        break;
                    case 11:
                        ::memcpy(md.Publisher,metadata.utf8(),METADATASIZE1-1);
                        break;
                    case 12:
                        ::memcpy(md.DC_rights,metadata.utf8(),METADATASIZE2-1);
                        break;
                    case 13:
                        ::memcpy(md.DC_type,metadata.utf8(),METADATASIZE1-1);
                        break;
                    default:
                        break;
                }
#if 0
                if ( !attrs.isNull() )
                {	
                    //getDocument()->releaseMetadata(attrs);
                }
                //getDocument()->releaseMetadata(metadata);
#endif
            }
        } while ( !metadata.isNull() );
    }
    md.pageCount = m_pageCount;//static_cast<int>(linuxHost->getDocument()->getPageCount()); //10.27
    return md;
}

int adobe::LinuxHost::getYrange(int *miny, int *maxy)
{
    if (m_renderer==NULL||m_document==NULL)
        return false;
#if 1
    double xmin, ymin, xmax, ymax;
    dpdoc::Rectangle rec; 
    dp::ref<dpdoc::Location> l_locationBeginning = m_renderer->getScreenBeginning();
    dp::ref<dpdoc::Location> l_locationEnd       = m_renderer->getScreenEnd();
    dp::ref<dpdoc::Location> l_locationCurrent   = NULL;
    dpdoc::RangeInfo*l_range =  NULL; 
    dpdoc::ContentIterator* l_cnIt;

    l_cnIt = m_document->getContentIterator( dpdoc::CV_TEXT,  l_locationBeginning);
    l_cnIt->next(static_cast<dpdoc::ContentIterationFlags>(MOVE_TEXT_FLAG));

#ifndef RMSDK_9_2        
    if (l_locationCurrent!=NULL) l_locationCurrent->release(); 
#endif
    l_locationCurrent = l_cnIt->getCurrentPosition();  

    l_range = m_renderer->getRangeInfo(l_locationBeginning, l_locationCurrent);
    if (l_range->getBoxCount()>0)
#ifndef RMSDK_9_2        
        l_range->getBox(0, &xmin, &ymin, &xmax, &ymax );
#else
     {
        l_range->getBox(0, false, &rec );
        xmin = rec.xMin;
        ymin = rec.yMin;
        xmax = rec.xMax;
        ymax = rec.yMax;
     }
#endif
    else
        xmin = ymin = xmax = ymax = 0.0;

    DEBUGINFO(  ":    Box %i, xMin = %f, yMin = %f, xMax = %f, yMax = %f.\n", 0, xmin, ymin, xmax, ymax );
    *miny = ymin;

    l_range->release();
#ifndef RMSDK_9_2        
    l_locationBeginning->release();
#endif

    l_cnIt = m_document->getContentIterator( dpdoc::CV_TEXT,  l_locationEnd);
    l_cnIt->previous(static_cast<dpdoc::ContentIterationFlags>(MOVE_TEXT_FLAG));

#ifndef RMSDK_9_2        
    if (l_locationCurrent!=NULL) l_locationCurrent->release(); 
#endif
    l_locationCurrent = l_cnIt->getCurrentPosition();  

    l_range = m_renderer->getRangeInfo(l_locationEnd, l_locationCurrent);
    if (l_range->getBoxCount()>0)
#ifndef RMSDK_9_2        
        l_range->getBox(0, &xmin, &ymin, &xmax, &ymax );
#else
     {
        l_range->getBox(0, false, &rec );
        xmin = rec.xMin;
        ymin = rec.yMin;
        xmax = rec.xMax;
        ymax = rec.yMax;
     }
#endif
    else
        xmin = ymin = xmax = ymax = 0.0;
    DEBUGINFO(  ":    Box %i, xMin = %f, yMin = %f, xMax = %f, yMax = %f.\n", 0, xmin, ymin, xmax, ymax );
    l_range->release();
#ifndef RMSDK_9_2        
    l_locationEnd->release();
    if (l_locationCurrent!=NULL) l_locationCurrent->release(); 
#endif
    *maxy = ymax;
    return *maxy-*miny;
#endif
#if 0
    double xmin, ymin, xmax, ymax;
    dpdoc::Location *l_locationBeginning = m_renderer->getScreenBeginning();
    dpdoc::Location *l_locationEnd       = m_renderer->getScreenEnd();
    dpdoc::Location *l_locationCurrent   = NULL;
    dpdoc::RangeInfo*l_range =  NULL; 
    dpdoc::ContentIterator* l_cnIt;

    l_cnIt = m_document->getContentIterator( dpdoc::CV_TEXT,  l_locationBeginning);
    l_cnIt->next(static_cast<dpdoc::ContentIterationFlags>(MOVE_TEXT_FLAG));

    if (l_locationCurrent!=NULL) l_locationCurrent->release(); 
    l_locationCurrent = l_cnIt->getCurrentPosition();  

    l_range = m_renderer->getRangeInfo(l_locationBeginning, l_locationCurrent);
    if (l_range->getBoxCount()>0)
        l_range->getBox(0, &xmin, &ymin, &xmax, &ymax );
    else
        xmin = ymin = xmax = ymax = 0.0;
    DEBUGINFO(  ":    Box %i, xMin = %f, yMin = %f, xMax = %f, yMax = %f.\n", 0, xmin, ymin, xmax, ymax );
    *miny = ymin;

    l_range->release();
    l_locationBeginning->release();

    l_cnIt = m_document->getContentIterator( dpdoc::CV_TEXT,  l_locationEnd);
    l_cnIt->previous(static_cast<dpdoc::ContentIterationFlags>(MOVE_TEXT_FLAG));

    if (l_locationCurrent!=NULL) l_locationCurrent->release(); 
    l_locationCurrent = l_cnIt->getCurrentPosition();  

    l_range = m_renderer->getRangeInfo(l_locationEnd, l_locationCurrent);
    if (l_range->getBoxCount()>0)
        l_range->getBox(0, &xmin, &ymin, &xmax, &ymax );
    else
        xmin = ymin = xmax = ymax = 0.0;
    DEBUGINFO(  ":    Box %i, xMin = %f, yMin = %f, xMax = %f, yMax = %f.\n", 0, xmin, ymin, xmax, ymax );
    l_range->release();
    l_locationEnd->release();
    *maxy = ymax;
    return *maxy-*miny;
#endif

}

dpdoc::Location* adobe::LinuxHost::xy2Location(int x, int y)
{


#if 0
    int temp = static_cast<int>(getCurrentTime());
    DEBUGINFO("current time1:%d", temp);
    dpdoc::Document * l_document;
    dpdoc::ContentIterator* l_cnIt;
    dpdoc::RangeInfo* l_range = NULL ;
    dpdoc::Location * l_locationBeginning;
    dpdoc::Location * l_locationEnd;
    dpdoc::Location * l_locationCurrent;
    dpdoc::Location * l_locationNext;    
    double xmin, ymin, xmax, ymax;


    l_document = getDocument();
    l_locationBeginning = m_renderer->getScreenBeginning();
    l_cnIt = l_document->getContentIterator( dpdoc::CV_TEXT,  l_locationBeginning);
    l_locationEnd       = m_renderer->getScreenEnd();
    l_locationCurrent   = NULL;
    l_cnIt->next(static_cast<dpdoc::ContentIterationFlags>(MOVE_TEXT_FLAG));
    if (l_locationCurrent!=NULL) l_locationCurrent->release(); 
    l_locationCurrent = l_cnIt->getCurrentPosition();    

    int count =0;
    while (l_locationCurrent->compare(l_locationEnd, NULL)!=0)
    {
        l_cnIt->next(static_cast<dpdoc::ContentIterationFlags>(MOVE_TEXT_FLAG));
        if (l_locationCurrent!=NULL) l_locationCurrent->release(); 
        l_locationCurrent = l_cnIt->getCurrentPosition();

        if (l_range != NULL) l_range->release();
        l_range = m_renderer->getRangeInfo(l_locationBeginning, l_locationCurrent);
        if (l_range->getBoxCount()>0)
            l_range->getBox(0, &xmin, &ymin, &xmax, &ymax );

        if (count++%100==0)
            DEBUGINFO(":%d", count);
    }
    temp = static_cast<int>(getCurrentTime());
    DEBUGINFO("current time2:%d", temp);
    return l_locationCurrent;
#endif
}

//WESTERN_SERIF_FONT_PATH=\"res:///fonts/MinionPro-Regular.otf\" \
WESTERN_SERIF_BOLD_FONT_PATH=\"res:///fonts/MinionPro-Bold.otf\" \
WESTERN_SERIF_ITALIC_FONT_PATH=\"res:///fonts/MinionPro-It.otf\" \
WESTERN_SERIF_BOLD_ITALIC_FONT_PATH=\"res:///fonts/MinionPro-BoldIt.otf\" \
WESTERN_SANS_SERIF_FONT_PATH=\"res:///fonts/MyriadPro-Regular.otf\" \
WESTERN_SANS_SERIF_BOLD_FONT_PATH=\"res:///fonts/MyriadPro-Bold.otf\" \
WESTERN_SANS_SERIF_ITALIC_FONT_PATH=\"res:///fonts/MyriadPro-It.otf\" \
WESTERN_SANS_SERIF_BOLD_ITALIC_FONT_PATH=\"res:///fonts/MyriadPro-BoldIt.otf\" \
WESTERN_MONOSPACE_FONT_PATH=\"res:///fonts/CourierStd.otf\" \
WESTERN_MONOSPACE_BOLD_FONT_PATH=\"res:///fonts/CourierStd-Bold.otf\" \
WESTERN_MONOSPACE_OBLIQUE_FONT_PATH=\"res:///fonts/CourierStd-Oblique.otf\" \
WESTERN_MONOSPACE_BOLD_OBLIQUE_FONT_PATH=\"res:///fonts/CourierStd-BoldOblique.otf\" \
SIMPLIFIED_CHINESE_GOTHIC_FONT_PATH=\"res:///fonts/AdobeHeitiStd-Regular.otf\" \
SIMPLIFIED_CHINESE_SONG_FONT_PATH=\"res:///fonts/AdobeHeitiStd-Regular.otf\" \
 \
 TRADITIONAL_CHINESE_GOTHIC_FONT_PATH=\"res:///fonts/AdobeMingStd-Light.otf\" \
 TRADITIONAL_CHINESE_MING_FONT_PATH=\"res:///fonts/AdobeMingStd-Light.otf\" \

void adobe::LinuxHost::checkForRequiredFonts()
{
	dpio::Stream * font;
	font = this->getResourceStream(dp::String(WESTERN_SERIF_FONT_PATH), 4);
	if ( font == NULL ) {
		LOGE( "Missing Western Serif font, check resources directory\n");
	}
	font->release();

	font = this->getResourceStream(dp::String(WESTERN_SERIF_BOLD_FONT_PATH), 4);
	if ( font == NULL ) {
		LOGE( "Missing Western Serif Bold font, check resources directory\n");
	}
	font->release();

	font = this->getResourceStream(dp::String(WESTERN_SERIF_ITALIC_FONT_PATH), 4);
	if ( font == NULL ) {
		LOGE( "Missing Western Serif Italic font, check resources directory\n");
	}
	font->release();

	font = this->getResourceStream(dp::String(WESTERN_SERIF_BOLD_ITALIC_FONT_PATH), 4);
	if ( font == NULL ) {
		LOGE( "Missing Western Serif Bold Italic font, check resources directory\n");
	}
	font->release();

#if 0
	font = this->getResourceStream(dp::String(SIMPLIFIED_CHINESE_GOTHIC_FONT_PATH), 4);
	if ( font == NULL ) {
		LOGE(  "Missing Chinese Simplified Gothic font, check resources directory\n");
	}
	font->release();
	
    font = this->getResourceStream(dp::String(SIMPLIFIED_CHINESE_SONG_FONT_PATH), 4);
	if ( font == NULL ) {
		LOGE( "Missing Chinese Simplified Song font, check resources directory\n");
	}
	font->release();
	font = this->getResourceStream(dp::String(TRADITIONAL_CHINESE_GOTHIC_FONT_PATH), 4);
	if ( font == NULL ) {
		LOGE( "Missing Chinese Traditional Gothic font, check resources directory\n");
	}
	font->release();
	
    font = this->getResourceStream(dp::String(TRADITIONAL_CHINESE_MING_FONT_PATH), 4);
	if ( font == NULL ) {
		LOGE( "Missing Chinese Traditional Ming font, check resources directory\n");
	}
	font->release();
#endif
}

bool adobe::LinuxHost::docReachHeadOrTail(bool head)
{
#if 1
    if (head)
        return m_is_at_beginning;
    else 
        return m_is_at_end;
#else
    int at = (m_pagepool->get_cursor()- m_offset+3)%3;
    dll_item *item = m_pagepool->get_item(at);
    if (head)
       return item->flag == ITEM_FLAG_HEAD;
     else
       return item->flag == ITEM_FLAG_TAIL;
#endif
}

//---------------------------LinuxSurface----------------------------

    adobe::LinuxSurface::LinuxSurface()
: m_xMin(0), m_yMin(0), m_xMax(0), m_yMax(0), m_buffer(NULL)
{
}

    adobe::LinuxSurface::LinuxSurface( bool monochrome, bool transparency, bool blend, int width, int height )
: m_xMin(0), m_yMin(0), m_xMax(width), m_yMax(height), g_scale(1.0),m_paletteSize(0)
{
    int stride;
    bitperpix = 3;
    if ( monochrome )
    {
        if( transparency )
        {
            m_format = dpdoc::PL_AL;
            stride = 2*width;
            bitperpix = 2;
        }
        else
        {
            m_format = dpdoc::PL_L;
            stride = width;
            bitperpix = 1;
            m_paletteSize = 1024;
        }
    }
    else
    {
        if( transparency )
        {
            if ( isLittleEndian() )
                m_format = dpdoc::PL_BGRA;
            else
                m_format = dpdoc::PL_ARGB;
            stride = 4*width;
            bitperpix = 4;
        }
        else
        {
            if ( isLittleEndian() )
                m_format = dpdoc::PL_BGR;
            else
                m_format = dpdoc::PL_RGB;
            stride = 3*width;
            bitperpix = 3;
        }
    }
    /*	//aleph 12.1  for bmp test
        m_buffer = new unsigned char[stride*height];
        ::memset( m_buffer, (transparency?0:blend?0xFF:0x80), stride*height );
        */
    m_transparency = transparency;
    m_blend = blend;
    m_size = stride*height;

    bmpsize = sizeof(FileHead)+sizeof(Infohead)+m_size+m_paletteSize;
    bmpbuffer = NULL;
#ifdef RMSDK_9_2 
    bmpbuffer = (unsigned char*)malloc(bmpsize*sizeof(unsigned char));
    if(!bmpbuffer)
    {
        DEBUGINFO("malloc bmp buffer error\n");
        return;
    }
#else
    bmpbuffer = new(std::nothrow) unsigned char[bmpsize];
#endif
    if(!bmpbuffer)
        DEBUGINFO(": new failed\n");
    DEBUGINFO(": bmp pix type %d , bmpbuffer addr : %x, size : %d\n",bitperpix, bmpbuffer,bmpsize);
    m_buffer = bmpbuffer + bmpsize - m_size;
    ::memset( m_buffer, (transparency?0:blend?0xFF:0x80), m_size );

    ::memset( (void *)&bmp_head, 0, sizeof bmp_head );
    ::memset( (void *)&bmp_info, 0, sizeof bmp_info );
    bmp_head.bfType=0x4d42;
    bmp_head.bfSize=m_size+sizeof(FileHead)+sizeof(Infohead);//24+head+info no quad    
    bmp_head.bfReserved1=bmp_head.bfReserved2=0;
    bmp_head.bfOffBits=bmp_head.bfSize-m_size;
    //finish the initial of head
    bmp_info.biSize=40;
    bmp_info.biWidth=((m_xMax - m_xMin)+3)&(~3);
    bmp_info.biHeight=-(m_yMax - m_yMin);
    bmp_info.biPlanes=1;
    bmp_info.biBitCount = bitperpix*8/*24*/;
    bmp_info.biCompress=0;
    bmp_info.biSizeImage=m_size;
    bmp_info.biXPelsPerMeter=0;
    bmp_info.biYPelsPerMeter=0;
    bmp_info.biClrUsed=0;
    bmp_info.biClrImportant=0;
    ::memcpy(bmpbuffer,&bmp_head,sizeof(FileHead));	//copy file head
    ::memcpy(bmpbuffer+sizeof(FileHead),&bmp_info,sizeof(Infohead));	//copy Image info head
    if(m_paletteSize == 1024)
    {
        for(int i=0; i<256; ++i)
        {
            RGB_BMP rgb;
            rgb.blue = i;
            rgb.green = i;
            rgb.red = i;
            rgb.reserved = 0;
            ::memcpy(bmpbuffer+sizeof(FileHead)+sizeof(Infohead)+i*4,&rgb,sizeof(rgb));
        }
    }
}

    adobe::LinuxSurface::LinuxSurface( int format, int xMin, int yMin, int xMax, int yMax )
: m_xMin(xMin), m_yMin(yMin), m_xMax(xMax), m_yMax(yMax)
{
}

adobe::LinuxSurface::~LinuxSurface()
{
    delete[] bmpbuffer;
}

int adobe::LinuxSurface::getSurfaceKind()
{
    return dpdoc::SK_RASTER;
}

int adobe::LinuxSurface::getPixelLayout()
{	
    return m_format;
}

static double g_gamma = 1.0;
unsigned char * adobe::LinuxSurface::getTransferMap( int channel )
{
    if( g_gamma != 0 )
    {
        static unsigned char transfer[256];
        if( transfer[255] == 0 )
        {
            for( int i = 0 ; i <= 255 ; i++ )
                transfer[i] = (unsigned char)::floor( 255*::pow(i/255.0, g_gamma) + 0.5 );
        }
        return transfer;
    }
    return NULL;
}

unsigned char * adobe::LinuxSurface::getDitheringClipMap( int channel )
{
    if ( m_format != dpdoc::PL_L || g_ditheringDepth == 0 || g_ditheringDepth > 3 )
        return NULL;
    return g_ditheringClipMap;
}

int adobe::LinuxSurface::getDitheringDepth( int channel )
{
    return m_format == dpdoc::PL_L ? g_ditheringDepth : 0;
}

unsigned char * adobe::LinuxSurface::checkOut( int xMin, int yMin, int xMax, int yMax, size_t * stride )
{

    printf("%d %s\n",__LINE__,  __func__);
    //    printf("int xMin, int yMin, int xMax, int yMax, bmp_info.biWidth,bitperpix , bmpsize, m_size\n ");
    //    printf("%8d%8d%8d%8d%8d%8d%8d%8d\n",xMin, yMin, xMax, yMax, bmp_info.biWidth, bitperpix, bmpsize, m_size);
    m_buffer = bmpbuffer + bmpsize - m_size;
    *stride = bmp_info.biWidth*bitperpix;
    return m_buffer + (yMin*bmp_info.biWidth + xMin)*bitperpix;

}

void adobe::LinuxSurface::checkIn( unsigned char * basePtr )
{
    printf("%d %s\n",__LINE__,  __func__);
}

void adobe::LinuxSurface::setScale(double scale)
{
    g_scale = scale;
}

//bmp width
int adobe::LinuxSurface::Width()
{
    return m_xMax;
}

//bmp height
int adobe::LinuxSurface::Height()
{
    return m_yMax;
}

void adobe::LinuxSurface::saveToBmpFile( const char * file )
{
    FILE * fp = fopen( file, "wb" );
    fwrite(bmpbuffer,bmpsize,1,fp);
    fclose(fp);
}

void adobe::LinuxSurface::clearbuffer()
{	
    //::memset( m_buffer, ::strchr(m_format, 'A') != NULL ? 0 : 0xFF, m_size );
    m_buffer = bmpbuffer + bmpsize - m_size;
    DEBUGINFO(": clear the Image buffer,addr: 0x%x, size: %d\n",m_buffer,m_size);
    ::memset( m_buffer, (m_transparency?0:m_blend?0xFF:0x80), m_size );
    DEBUGINFO(": clear the Image buffer,addr: 0x%x, size: %d\n",m_buffer,m_size);
}

unsigned char* adobe::LinuxSurface::getBmpData() 
{
    return bmpbuffer;
}

unsigned char* adobe::LinuxSurface::bmp_generator()
{
    ::memcpy(bmpbuffer,&bmp_head,sizeof(FileHead));
    ::memcpy(bmpbuffer+sizeof(FileHead),&bmp_info,sizeof(Infohead));
    return bmpbuffer;
}


// removed, the libpng.a in conflict with png lib of QT4.5
void adobe::LinuxSurface::saveToPNG( const char * fileName )
{
}

bool adobe::LinuxSurface::detectRedPixels()
{
    m_buffer = bmpbuffer + bmpsize - m_size;
    size_t pixWidth = dpdoc::Surface::getPixelWidth(m_format);
    if( pixWidth <= 2 )
        return true;
    size_t redIndex;
    size_t greenIndex;
    size_t blueIndex;
    switch( m_format )
    {
        case dpdoc::PL_RGB :
            redIndex = 0;
            greenIndex = 1;
            blueIndex = 2;
            break;
        case dpdoc::PL_BGR :
        case dpdoc::PL_BGRA :
            redIndex = 2;
            greenIndex = 1;
            blueIndex = 0;
            break;
        case dpdoc::PL_ARGB :
            redIndex = 1;
            greenIndex = 2;
            blueIndex = 3;
            break;
        default:
            return true;
    }
    int width = m_xMax - m_xMin;
    int height = m_yMax - m_yMin;
    size_t stride = pixWidth*width;
    unsigned char * line = m_buffer;
    for( int y = 0 ; y < height ; y++ )
    {
        unsigned char * pixel = line;
        for( int x = 0 ; x < width ; x++ )
        {
            if( pixel[redIndex] == 255 && pixel[greenIndex] == 0 && pixel[blueIndex] == 0 )
            {
                DEBUGINFO( ": red pixel at x=%d y=%d\n", x, y );
                return false;
            }
            pixel += pixWidth;
        }
        line += stride;
    }
    return true;
}

void adobe::MasterTimerImpl::release()
{
}

void adobe::MasterTimerImpl::setTimeout(int millisec)
{
    timeToFire = uft::Date::getCurrentTime() + millisec;
}

void adobe::MasterTimerImpl::cancel()
{
    timeToFire = 0;
}

bool adobe::MasterTimerImpl::active()
{
    return timeToFire != 0;
}

void adobe::MasterTimerImpl::fireWhenReady()
{
    if( !active() )
        return;
    while( true )
    {
        uft::time_t now = uft::Date::getCurrentTime();
        if( now < timeToFire )
        {
            printf( "Sleeping..." );
            ::sleep(1);
        }
        else
        {
            timeToFire = 0;
            dp::timerGetMasterClient()->timerFired(&masterTimer);
            break;
        }
    }
}
