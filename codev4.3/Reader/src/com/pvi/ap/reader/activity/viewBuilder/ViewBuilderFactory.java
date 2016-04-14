package com.pvi.ap.reader.activity.viewBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.pvi.ap.reader.R;
import com.pvi.ap.reader.activity.MainpageInsideActivity;
import com.pvi.ap.reader.activity.SkinSetActivity;
import com.pvi.ap.reader.activity.pviappframe.PviActivity;

import com.pvi.ap.reader.data.common.Config;

/**
 * 
 * @author rd045 use singleton design pattern to get the factory
 */
public class ViewBuilderFactory {

	public static final String SKIN1 = SkinSetActivity.SKIN1;

	public static final String SKIN2 = SkinSetActivity.SKIN2;

	public static final String MAIN_PAGE_INSIDE_ACTIVITY = 
		MainpageInsideActivity.class.getName();

	private HashMap<String, Object[]> classMaps = null;

	private String nowSkinSetted = SKIN1;
	private static Configuration curconfig = null;

	private static ViewBuilderFactory _instance = new ViewBuilderFactory();

	public static ViewBuilderFactory getInstance() {
		return _instance;
	}
	public static ViewBuilderFactory getInstance(Configuration config) {
		curconfig = config;
		_instance = new ViewBuilderFactory();
		return _instance;
	}
	private ViewBuilderFactory() {
	
		classMaps = new HashMap<String, Object[]>();
		initMap();
	}

	/**
	 * initiate the map relation of the activity with skin workers set the class
	 * and activity skin layout to the map key is the activity name + skin name
	 */
	private void initMap() {
	
		String key = MAIN_PAGE_INSIDE_ACTIVITY + SKIN1;
		Object data[] = new Object[2];
//		data[0] = MainpageInsideViewBuilder1.class;
//		
//		if(curconfig!=null&&(curconfig.orientation==Configuration.ORIENTATION_LANDSCAPE))
//		{
//			data[1] = R.layout.newmainpageactivity_ui1_landscape;
//		}
//		else
//		{
//			data[1] = R.layout.newmainpageactivity_ui1;
//		}
		classMaps.put(key, data);
		
//		data = new Object[2];
//		key = MAIN_PAGE_INSIDE_ACTIVITY + SKIN2;
//		data[0] = MainpageInsideViewBuilder2.class;
//		data[1] = R.layout.newmainpageactivity_ui2;

		classMaps.put(key, data);

	}

	/**
	 * 
	 * @param activity
	 *            get the ViewWorker by the given activity
	 * @return
	 */
	public AbstractViewBuilder getViewBuilder(PviActivity activity) {
		// we can have another way to make those lines more better
		// work it later
//		SharedPreferences settings = activity.getSharedPreferences(Config
//				.getString("configFileName"), 0);
//		String state = settings.getString(SkinSetActivity.STATE, "");
//
//		if (SKIN1.equals(state)) {
			nowSkinSetted = SKIN1;
//		} else if (SKIN2.equals(state)) {
//			nowSkinSetted = SKIN2;
//		} else {
//			throw new IllegalArgumentException("No suitable Worker for "
//					+ activity.getClass().getName() + " in WorkerFactory");
//		}

		String key = activity.getClass().getName();
		
		//need delete
//		nowSkinSetted = SKIN2;
		
		key += nowSkinSetted;
		
		Object data[] = null;

		data = classMaps.get(key);
		return getSkinViewBuilder(data,activity);

	}

	/**
	 * 
	 * @param data
	 *            the Object array contains the viewWorker class and view Layout
	 *            resource id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public AbstractViewBuilder getSkinViewBuilder(Object data[],PviActivity activity) {

		AbstractViewBuilder instance = null;

		// first check the data
		if ( data==null || data.length < 2 || !(data[0] instanceof Class)) {
			return null;
		}

		Class<AbstractViewBuilder> cls = (Class<AbstractViewBuilder>) data[0];
		Class parTypes[] = new Class[2];
		parTypes[0] = PviActivity.class;
		parTypes[1] = Integer.TYPE;
		
		Object pars [] = {activity,data[1]};
		
		try {
			Constructor<AbstractViewBuilder> constructor = cls
					.getConstructor(parTypes);
			instance = (AbstractViewBuilder) constructor.newInstance(pars);
			 
		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (InstantiationException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (InvocationTargetException e) {

			e.printStackTrace();
		}

		return instance;

	}

}
