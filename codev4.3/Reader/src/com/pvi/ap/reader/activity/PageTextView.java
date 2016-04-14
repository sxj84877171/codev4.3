package com.pvi.ap.reader.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.pvi.ap.reader.data.common.Logger;

public class PageTextView extends /*EditText*/ TextView {
	public PageTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	};

	public List<Integer> pages;
	private boolean in_calculate_page_num = false;
	private boolean turn_on_drawing = false;
	boolean page_parsed = false;
	boolean measure_para_saved = false;
	boolean layout_para_saved = false;
	private int m_widthMeasureSpec;
	int m_heightMeasureSpec;
	private int m_left;
	int m_top;
	int m_right;
	int m_bottom;

	//private int draw_to_bitmap = 0;
	private static boolean debug = false;
	
	//private Layout layout_v;

	@Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, BufferType.EDITABLE);
    }
	
	private Context context_v  ;
	
	protected void onDraw(Canvas canvas_in) {
		if (turn_on_drawing) {
			super.onDraw(canvas_in);
		}
		if ((!page_parsed) && !in_calculate_page_num) {
			context_v = getContext();

			MebViewFileActivity MEB_activity_v = null;
			TxtReaderActivity txtActivity = null;
			ReadingOnlineActivity readActivity = null;

			Spanned spanned = null;
			SpannableStringBuilder span_strBuilder = null;

			if (context_v instanceof MebViewFileActivity) {
				MEB_activity_v = (MebViewFileActivity) context_v;
				spanned = MEB_activity_v.spanned;
			} else if (context_v instanceof TxtReaderActivity) {
				txtActivity = (TxtReaderActivity) context_v;
				spanned = txtActivity.spanned;
			} else if (context_v instanceof ReadingOnlineActivity) {
				readActivity = (ReadingOnlineActivity) context_v;
				spanned = readActivity.spanned;
			}
			if (spanned != null) {
				if (txtActivity == null) {
					span_strBuilder = new SpannableStringBuilder(spanned
							.toString().trim());
				} else {
					span_strBuilder = new SpannableStringBuilder(spanned
							.toString());
				}
			} else {
				span_strBuilder = new SpannableStringBuilder(" ");
			}

			pages = all_pages(span_strBuilder, false, true);

			page_parsed = true;

			if (MEB_activity_v != null) {
				MEB_activity_v.page_finished();
			}
			if (txtActivity != null) {
				txtActivity.page_finished();
			}
			if (readActivity != null) {
				readActivity.page_finished();
			}

			turn_on_drawing = true;

			this.postInvalidate();
		}
		;
	}

	public int trimToViewHeight(boolean updateView) {
		Layout layout_v = getLayout();
		if (layout_v == null) {
			return 0;
		}
		int trim_num = 0;
		int content_height = 0;
		int view_height = getHeight();
		content_height = layout_v.getHeight();
		if (content_height <= view_height) {
			return 0;
		}

		// Log.i("content_height", Long.toString(content_height));
		// Log.i("view height", Long.toString(view_height));
		CharSequence char_seq = getText();
		SpannableStringBuilder span_builder = (SpannableStringBuilder) char_seq;
		int last_line = layout_v.getLineForVertical(view_height);
//		last_line = last_line - 1;
		if (last_line < 0) {
			last_line = 0;
		}
		int line_start_offset = layout_v.getLineStart(last_line);
		int total_len = char_seq.length();
		CharSequence trim_str = char_seq.subSequence(line_start_offset,
				total_len);
		trim_num = total_len - line_start_offset;
		span_builder.delete(line_start_offset, total_len);
		content_height = layout_v.getHeight();
		int cnt = 0;
		while (content_height < view_height) {
			span_builder.append(trim_str.charAt(cnt));
			cnt = cnt + 1;
			content_height = layout_v.getHeight();
			if (cnt >= trim_num) {
				break;
			}
		}
		int str_len = span_builder.length();
		span_builder.delete(str_len - 1, str_len);
		trim_num = trim_num - cnt + 1;

		if (updateView) {
			invalidate();
			// Log.i("postProcessPage,adjusted to content_height",
			// Long.toString(content_height));
		}
		return trim_num;
	}

	public List<Integer> all_pages(SpannableStringBuilder span_text_in,
			boolean draw_on, boolean move_to_first_page) {
		/*
		 * draw_on always passed "true" in except all_pages() is called from
		 * onDraw() above move_to_firt_page: whether fill the TextView with text
		 * of first page automatically
		 */
		Layout layout_v = getLayout();
		if (layout_v == null) {
			boolean turn_on_drawing_save = turn_on_drawing;
			turn_on_drawing = false;
			relayout();
			this.invalidate();

			turn_on_drawing = turn_on_drawing_save;
		}
		return algorithm_2(span_text_in, draw_on, move_to_first_page);
	}

	static public List<Integer> all_pages(SpannableStringBuilder span_text_in,
			float display_density, int layout_width, int view_height,
			float text_size, float spacingmult, float spacingadd) {
		List<Integer> ret = new ArrayList<Integer>();
		try {
			SpannableStringBuilder span_txt_new = new SpannableStringBuilder(
					span_text_in.subSequence(0, span_text_in.length()));
			ret = algorithm_3(span_txt_new, layout_width, view_height/*
																	 * this.getHeight
																	 * ()
																	 */,
					display_density, text_size, spacingmult, spacingadd);
		} catch (OutOfMemoryError e) {
			Log.e("error: ", "out of memory when parse pages");
		} catch (Throwable e) {

		}
		return ret;

	}
	
	
	public static List<Integer> all_pages(SpannableStringBuilder span_text_in,
			int layout_width, int view_height,TextPaint mTextPaint,
			float spacingmult, float spacingadd) {
		List<Integer> ret = new ArrayList<Integer>();
		try {
			SpannableStringBuilder span_txt_new = new SpannableStringBuilder(
					span_text_in.subSequence(0, span_text_in.length()));
			ret = algorithm_3(span_txt_new, layout_width, view_height, mTextPaint, spacingmult, spacingadd);
		} catch (OutOfMemoryError e) {
			Log.e("error: ", "out of memory when parse pages");
		} catch (Throwable e) {

		}
		return ret;

	}

	// static Layout mLayout = null;
	static List<Integer> algorithm_3(SpannableStringBuilder span_builder,
			int layout_width, int view_height, float display_density,
			float text_size, float spacingmult, float spacingadd) {

		List<Integer> ret = new ArrayList<Integer>();
		TextPaint mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.density = display_density;///////-------------------------
		mTextPaint.setTextSize(text_size);//---------------

		int ellipsisWidth = layout_width;// 600;
		Layout mLayout = null;
		mLayout = new DynamicLayout((CharSequence) span_builder,
				(CharSequence) span_builder/* mTransformed, */, mTextPaint,
				layout_width, Layout.Alignment.ALIGN_NORMAL,
				spacingmult/* mSpacingMult */, spacingadd/* mSpacingAdd */,
				true/* mIncludePad */, null/* TextUtils.TruncateAt.START */,
				ellipsisWidth);
		try {

			int page_pos = 0;
			ret.add(page_pos);
			int total_len = span_builder.length();

			int left_len = total_len;
			int trim_num = trimToViewHeight_3(mLayout, view_height,
					span_builder, false);
			while (trim_num > 0) {
				page_pos = page_pos + (left_len - trim_num);
				ret.add(page_pos);

				span_builder.delete(0, left_len - trim_num);
				left_len = trim_num;

				trim_num = trimToViewHeight_3(mLayout, view_height,
						span_builder, false);
				if (trim_num == 0) {
					break;
				}
			}

		} catch (OutOfMemoryError e) {
			Log.e("error: ", "out of memory when parse pages");
		} catch (Exception e) {

		}

		return ret;
	}
	
	
	
	public static List<Integer> algorithm_3(SpannableStringBuilder span_builder,
			int layout_width, int view_height, TextPaint mTextPain,
			float spacingmult, float spacingadd) {

		List<Integer> ret = new ArrayList<Integer>();

		int ellipsisWidth = layout_width;
		Layout mLayout = null;
		mLayout = new DynamicLayout((CharSequence) span_builder,
				(CharSequence) span_builder, mTextPain,
				layout_width, Layout.Alignment.ALIGN_NORMAL,
				spacingmult, spacingadd,
				true, null,
				ellipsisWidth);
		

		try {

			int page_pos = 0;
			ret.add(page_pos);
			int total_len = span_builder.length();

			int left_len = total_len;
			int trim_num = trimToViewHeight_3(mLayout, view_height,
					span_builder, false);
			while (trim_num > 0) {
				page_pos = page_pos + (left_len - trim_num);
				ret.add(page_pos);

				span_builder.delete(0, left_len - trim_num);
				left_len = trim_num;

				trim_num = trimToViewHeight_3(mLayout, view_height,
						span_builder, false);
				if (trim_num == 0) {
					break;
				}
			}

		} catch (OutOfMemoryError e) {
			Logger.e("error: ", "out of memory when parse pages");
		} catch (Exception e) {

		}
		return ret;
	}

	List<Integer> algorithm_1(SpannableStringBuilder span_text_in,
			boolean draw_on, boolean move_to_first_page) {
		in_calculate_page_num = true; // do this assign first of all

	//	long time_start = System.currentTimeMillis();
		// Log.i("parsing page, start time (milliseconds): ",Long.toString(time_start));

		List<Integer> ret = new ArrayList<Integer>();

		try {

			SpannableStringBuilder first_page_text = null;

			int page_pos = 0;
			ret.add(page_pos);
			int total_len = span_text_in.length();

			int left_len = total_len;
			SpannableStringBuilder span_builder = new SpannableStringBuilder(
					span_text_in.subSequence(total_len - left_len, total_len));
			this.setText(span_builder);
			int trim_num = trimToViewHeight(false);
			boolean first_in_loop = true;
			while (trim_num > 0) {
				page_pos = page_pos + (left_len - trim_num);
				ret.add(page_pos);

				if (first_in_loop) {
					if (move_to_first_page) {
						first_page_text = new SpannableStringBuilder(
								span_text_in.subSequence(0, page_pos));

						turn_on_drawing = true;
						this.setText(first_page_text); // keep drawing of first
														// page staying on
														// screen while
														// calculate page count
						this.invalidate();
						turn_on_drawing = false;
					}
					first_in_loop = false;
				}

				left_len = trim_num;
				CharSequence char_seq = span_text_in.subSequence(total_len
						- left_len, total_len);
				span_builder = new SpannableStringBuilder(char_seq);

				this.setText(span_builder);

				trim_num = trimToViewHeight(false);
				if (trim_num == 0) {
					break;
				}
			}

			if (first_page_text != null) {// more than one page. go to first
											// page again
				turn_on_drawing = true;
				this.setText(first_page_text);
				this.invalidate();
				turn_on_drawing = false;
			}

			in_calculate_page_num = false; // exit function
			if (draw_on) {
				turn_on_drawing = true;
			}
		} catch (OutOfMemoryError e) {
			Log.e("error: ", "out of memory when parse pages");
		} catch (Exception e) {

		}
	//	long time_end = System.currentTimeMillis();
		// Log.i("parsing page, stop time (milliseconds): ",Long.toString(time_end));
		// Log.i("parsing page, total used time (seconds): ",Double.toString((time_end
		// - time_start)/1000.0));
		// Log.i("parsing page, total pages: ",Long.toString(ret.size()));

		return ret;
	}

	List<Integer> algorithm_2(SpannableStringBuilder span_text_in,
			boolean draw_on, boolean move_to_first_page) {
		in_calculate_page_num = true; // do this assign first of all

		//long time_start = System.currentTimeMillis();
		// Log.i("parsing page, start time (milliseconds): ",Long.toString(time_start));

		List<Integer> ret = new ArrayList<Integer>();

		try {

			SpannableStringBuilder first_page_text = null;

			int page_pos = 0;
			ret.add(page_pos);
			int total_len = span_text_in.length();

			int left_len = total_len;
			SpannableStringBuilder span_builder = new SpannableStringBuilder(
					span_text_in.subSequence(total_len - left_len, total_len));
			this.setText(span_builder);
			int trim_num = trimToViewHeight_2(false);
			boolean first_in_loop = true;
			while (trim_num > 0) {

				if (debug) {
					Log.i("Paging:  ", "start a page:");
					Log.i("Paging:  ", span_text_in.subSequence(page_pos,
							page_pos + (left_len - trim_num)).toString());
					Log.i("Pagine:  ", "end a page:");
				}

				page_pos = page_pos + (left_len - trim_num);
				ret.add(page_pos);

				if (first_in_loop) {
					if (move_to_first_page) {
						first_page_text = new SpannableStringBuilder(
								span_text_in.subSequence(0, page_pos));

						turn_on_drawing = true;
						this.setText(first_page_text); // keep drawing of first
														// page staying on
														// screen while
														// calculate page count
						this.invalidate();
						turn_on_drawing = false;
					}

					CharSequence char_seq = span_text_in.subSequence(page_pos,
							total_len);
					span_builder = new SpannableStringBuilder(char_seq);
					this.setText(span_builder);

					char_seq = getText();
					span_builder = (SpannableStringBuilder) char_seq;

					first_in_loop = false;

					left_len = total_len - page_pos;
				} else {

					span_builder.delete(0, left_len - trim_num);
					left_len = trim_num;
				}

				trim_num = trimToViewHeight_2(false);
				if (trim_num == 0) {
					break;
				}
			}

			if (first_page_text != null) {// more than one page. go to first
											// page again
				turn_on_drawing = true;
				this.setText(first_page_text);
				this.invalidate();
				turn_on_drawing = false;
			}

			in_calculate_page_num = false; // exit function
			if (draw_on) {
				turn_on_drawing = true;
			}
		} catch (OutOfMemoryError e) {
			Log.e("error: ", "out of memory when parse pages");
		} catch (Exception e) {

		}
	//	long time_end = System.currentTimeMillis();
		// Log.i("parsing page, stop time (milliseconds): ",Long.toString(time_end));
		// Log.i("parsing page, total used time (seconds): ",Double.toString((time_end
		// - time_start)/1000.0));
		// Log.i("parsing page, total pages: ",Long.toString(ret.size()));

		return ret;
	}

	public int trimToViewHeight_2(boolean updateView) {
		Layout layout_v = getLayout();
		if (layout_v == null) {
			return 0;
		}
		int trim_num = 0;
		int content_height = 0;
		int view_height = getHeight();
		content_height = layout_v.getHeight();
		if (content_height <= view_height) {
			return 0;
		}

		// Log.i("content_height", Long.toString(content_height));
		// Log.i("view height", Long.toString(view_height));
		CharSequence char_seq = getText();
		int last_line = layout_v.getLineForVertical(view_height);
//		last_line = last_line - 1;
		if (last_line < 0) {
			last_line = 0;
		}
		int line_start_offset = layout_v.getLineStart(last_line);
		int total_len = char_seq.length();
		trim_num = total_len - line_start_offset;

		if (updateView) {
			invalidate();
			// Log.i("postProcessPage,adjusted to content_height",
			// Long.toString(content_height));
		}
		return trim_num;
	}

	static public int trimToViewHeight_3(Layout layout_v, int view_height,
			SpannableStringBuilder char_seq, boolean updateView) {
		// Layout layout_v = getLayout();
		if (layout_v == null) {
			return 0;
		}
		int trim_num = 0;
		int content_height = 0;
		// int view_height = getHeight();
		content_height = layout_v.getHeight();
		if (content_height <= view_height) {
			return 0;
		}

		// Log.i("content_height", Long.toString(content_height));
		// Log.i("view height", Long.toString(view_height));
		// CharSequence char_seq = getText();
		int last_line = layout_v.getLineForVertical(view_height);
//		last_line = last_line - 1;
		if (last_line < 0) {
			last_line = 0;
		}
		int line_start_offset = layout_v.getLineStart(last_line);
		int total_len = char_seq.length();
		trim_num = total_len - line_start_offset;

		if (updateView) {
			// invalidate();
			// Log.i("postProcessPage,adjusted to content_height",
			// Long.toString(content_height));
		}
		return trim_num;
	}

	void relayout() {
		this.measure(m_widthMeasureSpec, m_heightMeasureSpec);
		this.layout(m_left, m_top, m_right, m_bottom);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (!measure_para_saved) {
			m_widthMeasureSpec = widthMeasureSpec;
			m_heightMeasureSpec = heightMeasureSpec;
			measure_para_saved = true;
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		if (!layout_para_saved) {
			m_left = left;
			m_top = top;
			m_right = right;
			m_bottom = bottom;
			layout_para_saved = true;
		}
		super.onLayout(changed, left, top, right, bottom);
	}
	
	@Override
	public boolean isFocused() {
		return true ;
	}

}
