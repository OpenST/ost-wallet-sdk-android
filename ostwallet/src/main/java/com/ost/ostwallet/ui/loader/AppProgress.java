package com.ost.ostwallet.ui.loader;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ost.ostwallet.R;

public class AppProgress extends RelativeLayout {
    private static final long TRANSLATION_DURATION = 600;
    private static final long START_DELAY = 200;
    private int PROGRESS_HEIGHT;
    private View mProgress;
    private float mInitialTranslation;
    private int mFinalTranslation;
    private RelativeLayout mProgressView;
    private int PROGRESS_WIDTH;
    private boolean mRunningProgress = false;

    public AppProgress(Context context) {
        super(context);
        init();
    }

    public AppProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AppProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        mProgressView =  (RelativeLayout) inflater.inflate(R.layout.progress_view, this, false);
        mProgress = mProgressView.findViewById(R.id.progress_intermediate);
        PROGRESS_HEIGHT = (int)getResources().getDimension(R.dimen.dp_5);
        PROGRESS_WIDTH = (int)getResources().getDimension(R.dimen.dp_50);
        mProgress.setLayoutParams(new RelativeLayout.LayoutParams(PROGRESS_WIDTH, PROGRESS_HEIGHT));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mProgressView.setLayoutParams(params);
        addView(mProgressView);
    }

    void start() {
        mRunningProgress = true;
        postDelayed(new Runnable() {
            @Override
            public void run() {
                mInitialTranslation = mProgress.getTranslationX() - PROGRESS_WIDTH;
                mFinalTranslation = mProgressView.getWidth() + PROGRESS_WIDTH;

                mProgress.animate().translationXBy(mProgressView.getWidth() + PROGRESS_WIDTH).setDuration(TRANSLATION_DURATION).setListener(animatorListener);
            }
        }, START_DELAY);
    }

    public void stop() {
        mRunningProgress = false;
        clearAnimation();
    }
    private Animator.AnimatorListener animatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (mRunningProgress) {
                mProgress.setTranslationX(mInitialTranslation);
                mProgress.animate().translationXBy(mFinalTranslation).setDuration(TRANSLATION_DURATION).setListener(animatorListener);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };
}