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

#ifndef __PAGEPOOL_H__
#define __PAGEPOOL_H__
typedef struct {
    unsigned char* bmp_data;
    int            page_num;
} ElemType ;
typedef enum
{
    ITEM_STATE_NULL,
    ITEM_STATE_READY,
    ITEM_STATE_DIRTY
}pool_item_state;

typedef struct double_link_list_item_type
{
    ElemType                            data;
    struct double_link_list_item_type*  next;
    struct double_link_list_item_type*  prev;
    pool_item_state                     stat;
}dll_item;

class PagePool
{
    public:
        PagePool(dll_item* ppool, int len);
        ~PagePool();
        int next();
        int prev();
        int jump();
        int reset();
        int get_cursor();
        dll_item* get_item(int at);

    private:
        dll_item pool[3];
        //dll_item* pool[3];
        int m_cursor;
        int m_size;

};
#endif
