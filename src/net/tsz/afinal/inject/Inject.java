package net.tsz.afinal.inject;

import java.lang.reflect.Field;

import net.tsz.afinal.annotation.view.EventListener;
import net.tsz.afinal.annotation.view.ViewInject;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;

/**
 * @author jiwei
 * @Version V1.0
 * @Date 2014-7-2 下午3:58:27
 * @Description
 * 
 */
public class Inject {
	public static void initInjectedView(Activity activity) {
		initInjectedView(activity, activity.getWindow().getDecorView());
	}

	public static void initInjectedView(Object injectedSource, View sourceView) {
		initInjectedView(injectedSource, sourceView, null);
	}

	public static void initInjectedView(Object injectedSource, View sourceView, Object disPatchEventSource) {
		Field[] fields = injectedSource.getClass().getDeclaredFields();
		View view;
		if (fields != null && fields.length > 0) {
			try {
				for (Field field : fields) {
					field.setAccessible(true);

					// just inject view and view's subclass, otherwise skip
					if (!View.class.isAssignableFrom(field.getType()) || field.get(injectedSource) != null) {
						continue;
					}

					ViewInject viewInject = field.getAnnotation(ViewInject.class);
					if (viewInject != null) {

						int viewId = viewInject.id();
						view = sourceView.findViewById(viewId);
						field.set(injectedSource, view);

						EventListener listener = null;
						if (disPatchEventSource != null) {
							listener = new EventListener(disPatchEventSource);
						} else {
							listener = new EventListener(injectedSource);
						}
						if (viewInject.click()) {
							view.setOnClickListener(listener);
						}

						if (viewInject.longClick()) {
							view.setOnLongClickListener(listener);
						}

						// AdapterView be in common use
						if (viewInject.itemClick()) {
							if (view instanceof AdapterView<?>) {
								((AdapterView<?>) view).setOnItemClickListener(listener);
							}
						}

						if (viewInject.itemLongClick()) {
							if (view instanceof AdapterView<?>) {
								((AdapterView<?>) view).setOnItemLongClickListener(listener);
							}
						}

						if (viewInject.select()) {
							if (view instanceof AdapterView<?>) {
								((AdapterView<?>) view).setOnItemSelectedListener(listener);
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
