/*
 * =======================================================================
 *
 *       Filename:  pagepool.cpp
 *
 *    Description:  
 *
 *        Version:  1.0
 *        Created:  11/03/2010 05:37:45 PM
 *
 *         Author:  Arthur Zhou (mn), arthur_z@eink.com
 *        Company:  eInk
 *
 * =======================================================================
 */
#include "A_debug.h"
#include "pagepool.h"
#include <stdio.h>

PagePool::PagePool(dll_item* ppool, int len)
{
    if (len!=3)
        printf("pool size should be 3\n");
    for(int i=0;i<len;i++)
    {
#if 1
        pool[i].data.bmp_data = (*(ppool+i)).data.bmp_data;
        pool[i].data.page_num = (*(ppool+i)).data.page_num;
        pool[i].next = ppool+ (i+1)%3;
        pool[i].prev = ppool+ (i+2)%3;
        pool[i].stat = ITEM_STATE_READY;
#else
        pool[i] = &(ppool[i]);
#endif
    }
    m_cursor = 1;
    m_size = 3;
}

PagePool::~PagePool()
{
}

int PagePool::next()
{
    pool[m_cursor].prev->stat = ITEM_STATE_DIRTY;
    m_cursor = (m_cursor+1)%m_size;
    return 0;
}

int PagePool::prev()
{
    pool[m_cursor].next->stat = ITEM_STATE_DIRTY;
    m_cursor = (m_cursor+2)%m_size;
    return 0;
}

int PagePool::jump()
{
    for(int i=0; i<m_size; i++)
    {
        pool[i].stat = ITEM_STATE_DIRTY;
    }
    m_cursor = 1;
    return 0;
}

int PagePool::reset()
{
    for(int i=0; i<m_size; i++)
    {
        pool[i].stat = ITEM_STATE_DIRTY;
    }
    m_cursor = 1;
    return 0;
}

int PagePool::get_cursor()
{
    return m_cursor;
}
dll_item* PagePool::get_item(int at)
{
    return &(pool[at%3]);
}
