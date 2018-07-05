package com.example.android.filmspeek;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

/**
 * The only thing done here is animating the lens icon in the splash screen/launch screen.
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Find the lens ImageView in the splash screen.
        ImageView lensImageView = findViewById(R.id.ic_lens);

        // Create an animation set for the lens ImageView.
        AnimationSet animation = new AnimationSet(false);
        // Set FillAfter to true, so the view won't reset after animations end.
        animation.setFillAfter(true);

        // Create an animation that make the lens icon move straight left.
        Animation straightLeftAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -1,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        straightLeftAnimation.setDuration(300);
        // Set the LinearInterpolator to the animation to uniform its play velocity.
        // The same as below.
        straightLeftAnimation.setInterpolator(new LinearInterpolator());

        // Create the first semicircle animation through the custom animation class below.
        Animation firstSemicircleAnimation = new SemicircleAnimation(0, 2);
        // Set the start offset right after the animation above ended.
        firstSemicircleAnimation.setStartOffset(300);
        firstSemicircleAnimation.setDuration(350);
        firstSemicircleAnimation.setInterpolator(new LinearInterpolator());

        // Second semicircle animation for the lens icon.
        Animation secondSemicircleAnimation = new SemicircleAnimation(0, -1);
        secondSemicircleAnimation.setStartOffset(650);
        secondSemicircleAnimation.setDuration(350);
        secondSemicircleAnimation.setInterpolator(new LinearInterpolator());

        // Create an animation that makes the lens icon enlarged by two times.
        Animation enlargeAnimation = new ScaleAnimation(1, 2, 1, 2,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        enlargeAnimation.setDuration(1000);

        // Add the four animations above to the set.
        animation.addAnimation(straightLeftAnimation);
        animation.addAnimation(firstSemicircleAnimation);
        animation.addAnimation(secondSemicircleAnimation);
        animation.addAnimation(enlargeAnimation);

        // Start the animation set on the lens ImageView.
        lensImageView.startAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // When animation set ended, intent to the MainActivity.
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                // It's IMPORTANT to finish the SplashActivity, so user won't reach it afterwards.
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * A simple custom Animation class that does semicircle motion horizontally.
     * <p>
     * Note:
     * 1. The semicircle motion only supports in horizontal direction and always go clockwise.
     * 2. The input parameters always represent as percentage (where 1.0 is 100%).
     */
    private class SemicircleAnimation extends Animation {

        private final float mFromXValue, mToXValue;
        private float mRadius;

        private SemicircleAnimation(float fromX, float toX) {
            mFromXValue = fromX;
            mToXValue = toX;
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);

            float fromXDelta = resolveSize(Animation.RELATIVE_TO_SELF, mFromXValue, width, parentWidth);
            float toXDelta = resolveSize(Animation.RELATIVE_TO_SELF, mToXValue, width, parentWidth);

            // Calculate the radius of the semicircle motion.
            // Note: Radius can be negative here.
            mRadius = (toXDelta - fromXDelta) / 2;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float dx = 0;
            float dy = 0;

            if (interpolatedTime == 0) {
                t.getMatrix().setTranslate(dx, dy);
                return;
            }

            float angleDeg = (interpolatedTime * 180f) % 360;
            float angleRad = (float) Math.toRadians(angleDeg);

            dx = (float) (mRadius * (1 - Math.cos(angleRad)));
            dy = (float) (-mRadius * Math.sin(angleRad));

            t.getMatrix().setTranslate(dx, dy);
        }
    }
}
