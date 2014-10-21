package io.github.linxiaocong.sjtubbs.utilities;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

@SuppressWarnings("deprecation")
public class UrlDrawable extends BitmapDrawable {

	private Drawable mDrawable;

	public Drawable getDrawable() {
		return mDrawable;
	}

	public void setDrawable(Drawable drawable) {
		mDrawable = drawable;
	}

	@Override
	public void draw(Canvas canvas) {
		if (mDrawable != null) {
			mDrawable.draw(canvas);
		}
	}
}
