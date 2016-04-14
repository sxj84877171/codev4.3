package com.pvi.ap.reader.activity;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;
import com.pvi.ap.reader.activity.pviappframe.PviAlertDialog;
import com.pvi.ap.reader.data.common.Config;
import com.pvi.ap.reader.data.common.Logger;
import com.pvi.ap.reader.data.external.manager.CPManagerUtil;

/**
 * 书籍资讯界面Activity
 * 
 * @author rd038
 * @author rd040 马中庆
 * 
 */
public class InfoContentActivity extends PviActivity {

    private static final String TAG = "InfoContentActivity";
    TextView contentView;
    private List<Integer> total_pages;
    Spanned spanned;
    private Handler mHandler = new Handler();
    private String title = null; // 资讯标题

    private String src = null; // 资讯插图路径

    private String publishDate = null; // 资讯发布时间

    private String content = null; // 资讯内容

    private String bookNewsID = null;
    private TextView titleView;
    private TextView publishDateView;
    private ImageView imageView;

    
    private int currentPage = 1;
    private int totalPage = 0;
    private CharSequence charSequence;
    private int chapterIdLength;// 当前一章的长度
    
    private GlobalVar app;

    private Handler retryHandler = new Handler() {
        public void handleMessage(Message msg) {

            final PviAlertDialog pd = new PviAlertDialog(getParent());
            pd.setTitle(R.string.kyleHint02);
            pd.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onCreate(null);
                        }
                    });
            pd.setButton(DialogInterface.BUTTON_NEUTRAL, "取消",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            pd.show();

        };
    };

    private Handler newsHandler = new Handler() {
        public void handleMessage(Message msg) {
            setBookNewInfoToLay();
        };
    };

    public int getBookNewInfo(String bookNewsID) {

        showTip("数据获取中...");
        
        HashMap ahmHeaderMap = CPManagerUtil.getHeaderMap();
        HashMap ahmNamePair = CPManagerUtil.getAhmNamePairMap();
        ahmNamePair.put("bookNewsID", bookNewsID);
        HashMap responseMap = null;
        try {
            responseMap = NetCache.getBookNewsInfo(ahmHeaderMap, ahmNamePair);
        } catch (Exception e) {
            Logger.e("InfoContentActivity.getBookNewInfo()",
                    "Exception on get data from net", e);
            e.printStackTrace();
            return 1;
        }

        // System.out.println("返回zhuangtai："+responseMap.get("result-code"));
        if (responseMap == null) {
            Logger.e(TAG, "responseMap is null");
            return 1;
        }

        if (responseMap.get("ResponseBody") == null) {
            Logger.e(TAG, "ResponseBody is null");
            return 1;
        }
        byte[] responseBody = (byte[]) responseMap.get("ResponseBody");

        /*
         * try { System.out.println("返回的XML为：");
         * System.out.println(CPManagerUtil
         * .getStringFrombyteArray(responseBody)); } catch
         * (UnsupportedEncodingException e) { e.printStackTrace(); return 1;}
         */

        // 根据返回字节数组构造DOM
        Document dom = null;
        try {
            dom = CPManagerUtil.getDocumentFrombyteArray(responseBody);

            Element root = dom.getDocumentElement();
            NodeList n1 = root.getElementsByTagName("BookNews");
            for (int i = 0; i < n1.getLength(); i++) {
                Element entry = (Element) n1.item(i);

                NodeList childrens = entry.getChildNodes();
                for (int j = 0; j < childrens.getLength(); j++) {
                    Node node = childrens.item(j);
                    if (node != null) {
                        if ("title".equals(node.getNodeName())) {
                            if (node.getFirstChild() != null) {
                                this.title = node.getFirstChild()
                                        .getNodeValue();
                            } else {
                                this.title = "";
                            }
                        }
                        if ("publishTime".equals(node.getNodeName())) {
                            if (node.getFirstChild() != null) {
                                this.publishDate = node.getFirstChild()
                                        .getNodeValue();
                            } else {
                                this.publishDate = "";
                            }

                        }
                        if ("content".equals(node.getNodeName())) {
                            if (node.getFirstChild() != null) {
                                this.content = node.getFirstChild()
                                        .getNodeValue();
                            } else {
                                this.content = "";
                            }
                        }
                        if ("titleImage".equals(node.getNodeName())) {
                            if (node.getFirstChild() != null) {
                                if (!"".equals(node.getFirstChild()
                                        .getNodeValue())
                                        && !"null"
                                                .equalsIgnoreCase(node
                                                        .getFirstChild()
                                                        .getNodeValue())) {
                                    this.src = node.getFirstChild()
                                            .getNodeValue();
                                    // Logger.i(TAG, "image src :" + this.src);
                                }
                            } else {
                                this.src = null;
                            }
                        }
                    }
                }
            }

            // this.title = "dsadasd";

            // this.src = "/book_news_image/406/20090721093502185.jpg";

            // this.publishDate = "2009-10-10 12:23:34";

            // this.content = "ffdfsdf";
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
            // TODO: handle exception
        }
        return 0;
    }

    // // public int getBookNewInfoTest(String bookNewsID){
    //
    //
    // title = "龙文森的女孩";
    // publishDate = "2011-01-20 12:23:23";
    // this.content =
    // "<HTML><HEAD><TITLE>dfgsdfgsfgsg</TITLE><META content=\"text/html; charset=utf-8\" http-equiv=Content-Type/>"
    // +
    // "<META name=GENERATOR content=\"MSHTML 8.00.6001.18783\"/></HEAD><BODY style=\"FONT-SIZE: 16px\"><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P> <P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P> <P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P>"
    // +
    // "<P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P>"
    // +
    // "<P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P>" +
    // "<P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P>" +
    // "<P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P>" +
    // "<P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P>" +
    // "<P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P>" +
    // "<P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P>" +
    // "<P>救生阀架克鲁斯解放哈利快睡觉符合辣椒水看到你看就知道了进口货束带结发哈就开始带饭哈卡解放</P><BODY/></HTML>";
    // return 0;
    // }

    public Bitmap returnBitMap(String url) {
        URL myFileUrl = null;
        Bitmap bitmap = null;
        try {
            myFileUrl = new URL(url);
        } catch (MalformedURLException e) {
            Logger.i("InfoContentActivity.returnBitMap()",
                    "Exception on create BitMap from net", e);
            // e.printStackTrace();
        }
        try {
            if (myFileUrl != null) {
                HttpURLConnection conn = (HttpURLConnection) myFileUrl
                        .openConnection();
                if (conn != null) {
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
                }
            }
        } catch (IOException e) {
            // e.printStackTrace();
            Logger.i("InfoContentActivity.returnBitMap()",
                    "Exception on create BitMap from net", e);
        }
        return bitmap;
    }

    public void setBookNewInfoToLay() {

        titleView.setText(title);
        // 设置中文粗体
        TextPaint tp = titleView.getPaint();
        tp.setFakeBoldText(true);
        // 显示发布时间

        publishDateView.setText(publishDate);
        // 显示内容
        SpannableStringBuilder span_strBuilder = new SpannableStringBuilder(
                spanned.toString().trim());
        PageTextView pageView = (PageTextView) contentView;
        total_pages = pageView.all_pages(span_strBuilder, true, false);
        totalPage = total_pages.size();
        chapterIdLength = spanned.length();
        currentPage = 1;
        if (chapterIdLength == 0) {
            chapterIdLength = 1;
        }
        if (currentPage == totalPage) {
            charSequence = spanned.subSequence(
                    total_pages.get(currentPage - 1), chapterIdLength - 1);

        } else {
            charSequence = spanned.subSequence(
                    total_pages.get(currentPage - 1), total_pages
                            .get(currentPage));
        }
        pageView.setText(charSequence);
        totalPage = total_pages.size();
        
        updatePagerinfo("" + currentPage+" / "+totalPage);

        // 如果有插图显示插图
        if ((src != null) && (!"".equals(src))
                && ("null".equalsIgnoreCase(src))) {
            new Thread() {
                public void run() {
                    final Bitmap bmp = returnBitMap(Config
                            .getString("CPC_BASE_URL")
                            + src);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            imageView.setImageBitmap(bmp);
                        }
                    });
                }
            }.start();

        }
    }

    private void goToNextPage() {
        long TimeStart = System.currentTimeMillis();
        Logger.i("Time", "Mainpage Pressed" + Long.toString(TimeStart));
        currentPage = currentPage + 1;
        if (this.currentPage > this.totalPage) {
            currentPage = totalPage;
            /*
             * Toast.makeText(InfoContentActivity.this,
             * "This is the Last page!", Toast.LENGTH_LONG).show();
             */
            return;
        } else if (this.currentPage == this.totalPage) {
            charSequence = spanned.subSequence(total_pages
                    .get(this.currentPage - 1), chapterIdLength - 1);

        } else {
            charSequence = spanned.subSequence(total_pages
                    .get(this.currentPage - 1), total_pages
                    .get(this.currentPage));
        }
        contentView.setText(charSequence);
        updatePagerinfo("" + currentPage+" / "+totalPage);

    }

    private void goToUpPage() {

        long TimeStart = System.currentTimeMillis();
        Logger.i("Time", "Mainpage Pressed" + Long.toString(TimeStart));
        currentPage = currentPage - 1;
        if (currentPage < 1) {
            currentPage = 1;
            /*
             * Toast.makeText(InfoContentActivity.this,
             * "This is the first page!", Toast.LENGTH_LONG).show();
             */

            return;
        } else if (this.currentPage == this.totalPage) {
            charSequence = spanned.subSequence(total_pages
                    .get(this.currentPage - 1), chapterIdLength - 1);

        } else {
            charSequence = spanned.subSequence(
                    total_pages.get(currentPage - 1), total_pages
                            .get(currentPage));
        }
        contentView.setText(charSequence);
        updatePagerinfo("" + currentPage+" / "+totalPage);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        
        this.setContentView(R.layout.infocontent);
        
        app = ((GlobalVar) getApplicationContext());

        this.spanned = Html.fromHtml("");
        titleView = (TextView) findViewById(R.id.bookNewTitle);
        publishDateView = (TextView) findViewById(R.id.bookNewPublishDate);
        contentView = (TextView) findViewById(R.id.bookNewContent);
        imageView = (ImageView) findViewById(R.id.bookNewPic);

        // 左右键翻页
        contentView.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        return true;
                    }
                }

                if (event.getAction() == KeyEvent.ACTION_UP) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        OnPrevpage();
                        contentView.requestFocus();
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        OnNextpage();
                        contentView.requestFocus();
                        return true;
                    }
                }

                return false;
            }
        });
        
        super.onCreate(savedInstanceState);
    }

    public void page_finished() {
        if (content != null) {
            PageTextView pageView = (PageTextView) contentView;
            total_pages = pageView.pages;
        } else {
            PageTextView pageView = null;
            total_pages = null;
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        
        Intent revIntent = this.getIntent();
        Bundle revBundle = revIntent.getExtras();
        bookNewsID = revBundle.getString("bookNewsID");
        
        clearOld();

        new Thread() {
            public void run() {
                if (getBookNewInfo(bookNewsID) == 0) {
                    spanned = Html.fromHtml(content);      
                    
                    //因为此界面使用的分页控件，所以，必须先显示出来分页条！
                    mHandler.post(showPager);
                    
                    newsHandler.sendEmptyMessage(0);
                } else {
                    hideTip();
                    retryHandler.sendEmptyMessage(0);
                }

            }
        }.start();

    }
    
    private Runnable showPager = new Runnable(){

        @Override
        public void run() {            
            updatePagerinfo("1 / 1");
            showPager();       
            showMe(InfoContentActivity.class);
        }
        
    };

    private void clearOld() {
        titleView.setText("");
        publishDateView.setText("");
        contentView.setText("");
        hidePager();
        imageView.setImageResource(0);
    }

    @Override
    public void OnNextpage() {
        goToNextPage();
        super.OnNextpage();
    }

    @Override
    public void OnPrevpage() {
        goToUpPage();
        super.OnPrevpage();
    }

    @Override
    public void onAttachedToWindow() {
        Logger.e(TAG,"onAttachedToWindow() ");
        super.onAttachedToWindow();
    }



}
