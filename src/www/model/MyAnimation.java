package www.model;

import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;

public class MyAnimation extends Animation {

	private int halfWidth;

	private int halfHeight;

	@Override
	public void initialize(int width, int height, int parentWidth,

	int parentHeight) {

		super.initialize(width, height, parentWidth, parentHeight);

		setDuration(1000);

		setFillAfter(true);

		halfWidth = width / 2;

		halfHeight = height / 2;

		setInterpolator(new LinearInterpolator());

	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {

		final Matrix matrix = t.getMatrix();

		matrix.preScale(interpolatedTime, interpolatedTime);

		matrix.preRotate(interpolatedTime * 360);

		matrix.preTranslate(-halfWidth, -halfHeight);

		matrix.postTranslate(halfWidth, halfHeight);

	}

}
