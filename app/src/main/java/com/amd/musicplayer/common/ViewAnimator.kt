package com.amd.musicplayer.common

import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation

class ViewAnimator {

    companion object {
        val duration = 800L
        fun translateLeftView ( view: View  ){
            var translateAnim = TranslateAnimation (-600f  ,view.pivotX , view.pivotY ,view.pivotY)
            translateAnim.duration = duration
            view.startAnimation(translateAnim)
        }
        fun translateRightView ( view: View  ){
            var translateAnim = TranslateAnimation (view.pivotX + 600  ,view.pivotX , view.pivotY ,view.pivotY)
            translateAnim.duration = duration
            view.startAnimation(translateAnim)
        }
        fun translateTopView ( view: View  ){
            var translateAnim = TranslateAnimation (view.pivotX  ,view.pivotX , -800f ,view.pivotY)
            translateAnim.duration = duration + 200
            view.startAnimation(translateAnim)
        }
        fun translateBottomView ( view: View  ){
            var translateAnim = TranslateAnimation (view.pivotX  ,view.pivotX , view.pivotY + 800 ,view.pivotY)
            translateAnim.duration = duration + 200
            view.startAnimation(translateAnim)
        }
        fun scaleUpFromOriginView ( view: View  ):Animation{
            var scaleAnim = ScaleAnimation(view.scaleX , view.scaleX * 1.5F , view.scaleY , view.scaleY * 1.5F)
            scaleAnim.duration = 300L
            view.startAnimation(scaleAnim)
            return  scaleAnim
        }
        fun scaleUpView ( view: View  ):Animation{
            var scaleAnim = ScaleAnimation(0f , view.scaleX , 0f , view.scaleY)
            scaleAnim.duration = duration
            view.startAnimation(scaleAnim)
            return  scaleAnim
        }
        fun rotateView (view : View) : Animation{
            var rotateAnimation = RotateAnimation(0f , 360f)
            scaleUpFromOriginView(view).setAnimationListener(object : Animation.AnimationListener{
                override fun onAnimationStart(animation: Animation?) {

                }

                override fun onAnimationRepeat(animation: Animation?) {

                }

                override fun onAnimationEnd(animation: Animation?) {
                    rotateAnimation.duration = duration + 200
                    view.startAnimation(rotateAnimation)
                }
            })


            return rotateAnimation
        }
    }

}