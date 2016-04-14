/*
 *		linux Ebook Adobereader
 *
 *		Aleph @PVIsz
 */
 
//--------------------------BMP---------------------------------
#ifndef _BMP_H_
#define _BMP_H_

#include <iostream>
using namespace std;
typedef long BOOL;
typedef long LONG;
typedef unsigned char BYTE;
typedef unsigned long DWORD;
typedef unsigned short WORD;
typedef struct {
        WORD    bfType;//2
        DWORD   bfSize;//4
        WORD    bfReserved1;//2
        WORD    bfReserved2;//2
        DWORD   bfOffBits;//4
}__attribute__((packed))FileHead;
typedef struct{
        DWORD      biSize;//4
        LONG       biWidth;//4
        LONG       biHeight;//4
        WORD       biPlanes;//2
        WORD       biBitCount;//2
        DWORD      biCompress;//4
        DWORD      biSizeImage;//4
        LONG       biXPelsPerMeter;//4
        LONG       biYPelsPerMeter;//4
        DWORD      biClrUsed;//4
        DWORD      biClrImportant;//4
}__attribute__((packed))Infohead;
/*typedef   struct
{
    unsigned   char     rgbBlue;   
    unsigned   char     rgbGreen;
    unsigned   char     rgbRed;  
    unsigned   char     rgbReserved;
}RGBQuad;//it may be useless*/
typedef struct 
{
    BYTE b;
    BYTE g;
    BYTE r;
}RGB_data;//RGB TYPE

typedef struct RGB_BMP_typ
{
	unsigned char blue;
	unsigned char green;
	unsigned char red;
	unsigned char reserved;
}RGB_BMP,*RGB_BMP_ptr;

#endif //_BMP_H_
