package com.riversanskiriti.utils;

import android.animation.Animator;
import android.content.Context;
import android.view.View;
import android.view.ViewAnimationUtils;

/**
 * Created by arpit on 25/11/16.
 */
public class Animation {
    private Context mContext;
    private Listener animationListener;
    private  boolean selfVisible = false;

    public Animation(Context context, Listener listener) {
        mContext = context;
        animationListener = listener;
    }

    public void circularShrinkToogle(final View view, final View rootView, final int requestCode) {
        selfVisible = false;
        int centerX = (rootView.getLeft() + rootView.getRight()) / 2;
        int centerY = (rootView.getTop() + rootView.getBottom()) / 2;
        int startRadius = 0;
        int endRadius = (int) Math
                .hypot(rootView.getWidth(), rootView.getHeight());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Animator anim = null;
            if (view.getVisibility() == View.VISIBLE) {
                anim = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, endRadius, startRadius);
            } else {
                anim = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);

            }
            anim.setDuration(1200);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    animationListener.onAnimationStart(animation, requestCode);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if(!selfVisible){
                        view.setVisibility(View.GONE);
                    }
                    animationListener.onAnimationEnd(animation, requestCode);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            if (view.getVisibility() == View.GONE) {
                selfVisible = true;
                view.setVisibility(View.VISIBLE);
            }
            anim.start();
        } else {
            animationListener.onAnimationEnd(null, requestCode);
        }
    }

    public interface Listener {
        // you can define any parameter as per your requirement
        public void onAnimationStart(Animator animation, int requestCode);

        public void onAnimationEnd(Animator animation, int requestCode);
    }
}


