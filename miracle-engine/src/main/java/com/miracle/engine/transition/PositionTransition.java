package com.miracle.engine.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class PositionTransition extends Transition {

    private static final String PROPNAME_POSITION = "custom_transition:position";

    private static final Property<View, PointF> POSITION_PROPERTY =
            new Property<View, PointF>(PointF.class, "position") {
                @Override
                public PointF get(View view) {
                    return new PointF(view.getX(), view.getY());
                }

                @Override
                public void set(View view, PointF position) {
                    view.setX(position.x);
                    view.setY(position.y);
                }
            };

    public PositionTransition() {
        super();
    }

    public PositionTransition(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    private void captureValues(TransitionValues transitionValues) {
        View view = transitionValues.view;
        transitionValues.values.put(PROPNAME_POSITION, new PointF(view.getX(), view.getY()));
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }

        final View view = endValues.view;
        PointF startPosition = (PointF) startValues.values.get(PROPNAME_POSITION);
        PointF endPosition = (PointF) endValues.values.get(PROPNAME_POSITION);

        if (startPosition == null || endPosition == null || startPosition.equals(endPosition)) {
            return null;
        }

        ValueAnimator animator = ValueAnimator.ofObject(new PointFEvaluator(), startPosition, endPosition);
        animator.addUpdateListener(valueAnimator -> {
            PointF value = (PointF) valueAnimator.getAnimatedValue();
            view.setX(value.x);
            view.setY(value.y);
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setX(endPosition.x);
                view.setY(endPosition.y);
            }
        });
        return animator;
    }

    public static class PointFEvaluator implements android.animation.TypeEvaluator<PointF> {

        private final PointF mTempPointF = new PointF();

        @Override
        public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
            float x = startValue.x + (fraction * (endValue.x - startValue.x));
            float y = startValue.y + (fraction * (endValue.y - startValue.y));
            mTempPointF.set(x, y);
            return mTempPointF;
        }
    }
}