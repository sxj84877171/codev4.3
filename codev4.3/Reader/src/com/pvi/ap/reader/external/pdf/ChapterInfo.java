package com.pvi.ap.reader.external.pdf;
public class ChapterInfo
{
    public int level;
    public int position;
    public String title;

    ChapterInfo()
    {
        title = new String("Default Title");
        level = 0;
        position = 1;
    }
}
