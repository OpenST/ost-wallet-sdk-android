package com.ost.ostwallet.ui.loader;

import android.animation.Animator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

import com.ost.ostwallet.R;

/**
 * simple custom view of a beating heart which is achieved by a scaling animation
 */
public class HeartBeatView extends AppCompatImageView {

    private static final String TAG = "HeartBeatView";

    private static final float DEFAULT_SCALE_FACTOR = 0.1f;
    private static final int DEFAULT_DURATION = 500;
    private static final long START_DELAY = 800;
    private static final float ROTATION_ANGLE = -135f;
    private Drawable heartDrawable;

    private boolean heartBeating = false;

    float scaleFactor = DEFAULT_SCALE_FACTOR;
    float reductionScaleFactor = -scaleFactor;
    int duration = DEFAULT_DURATION;

    public HeartBeatView(Context context) {
        super(context);
        init();
    }

    public HeartBeatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeartBeatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        //make this not mandatory
        heartDrawable = ContextCompat.getDrawable(getContext(), R.drawable.pepo_white_icon);
        setImageDrawable(heartDrawable);
    }

    /**
     * toggles current heat beat state
     */
    public void toggle() {
        if (heartBeating) {
            stop();
        } else {
            start();
        }
    }

    /**
     * Starts the heat beat/pump animation
     */
    public void  start() {
        heartBeating = true;

        final SpringAnimation rotationAnimation = createSpringAnimation(
                this, SpringAnimation.ROTATION,
                ROTATION_ANGLE, SpringForce.STIFFNESS_LOW, SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);


        rotationAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean b, float v, float v1) {
                //we ignore heartBeating as we want to ensure the heart is reduced back to original size
                animate().scaleXBy(reductionScaleFactor).scaleYBy(reductionScaleFactor).setDuration(duration).setListener(scaleDownListener);
            }
        });

        postDelayed(new Runnable() {
            @Override
            public void run() {
                rotationAnimation.start();
            }
        }, START_DELAY);
    }

    /**
     * Stops the heat beat/pump animation
     */
    public void stop() {
        heartBeating = false;
        clearAnimation();
    }

    /**
     * is the heart currently beating
     *
     * @return
     */
    public boolean isHeartBeating() {
        return heartBeating;
    }

    public int getDuration() {
        return duration;
    }


    private static final int milliInMinute = 60000;

    /**
     * set the duration of the beat based on the beats per minute
     *
     * @param bpm (positive int above 0)
     */
    public void setDurationBasedOnBPM(int bpm) {
        if (bpm > 0) {
            duration = Math.round((milliInMinute / bpm) / 3f);
        }
    }


    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
        reductionScaleFactor = -scaleFactor;
    }

    private SpringAnimation createSpringAnimation(View view,
                                                  DynamicAnimation.ViewProperty property,
                                                  float finalPosition,
                                                  float stiffness,
                                                  float dampingRatio) {
        SpringAnimation animation = new SpringAnimation(view, property);
        SpringForce spring = new SpringForce(finalPosition);
        spring.setStiffness( stiffness );
        spring.setDampingRatio( dampingRatio );
        animation.setSpring( spring );
        return animation;
    }

    private final Animator.AnimatorListener scaleUpListener = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            //we ignore heartBeating as we want to ensure the heart is reduced back to original size
            animate().scaleXBy(reductionScaleFactor).scaleYBy(reductionScaleFactor).setDuration(duration).setListener(scaleDownListener);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }
    };


    private final Animator.AnimatorListener scaleDownListener = new Animator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (heartBeating) {
                //duration twice as long for the upscale
                animate().scaleXBy(scaleFactor).scaleYBy(scaleFactor).setDuration(duration).setListener(scaleUpListener);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }
    };

}