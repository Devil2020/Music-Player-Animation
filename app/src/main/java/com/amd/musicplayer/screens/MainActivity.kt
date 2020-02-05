package com.amd.musicplayer.screens

import android.Manifest
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.OvershootInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.amd.musicplayer.common.ViewAnimator
import com.amd.musicplayer.R
import com.amd.musicplayer.entities.Music
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.root
import kotlinx.android.synthetic.main.music_item.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class MainActivity : AppCompatActivity() , MusicViewHolderListener{

    var count = 0
    var musics : ArrayList<Music> ? =null
    var mediaPlayer : MediaPlayer ? =null
    var isPlayed = false
    var runnable : Runnable ? =null
    var handler :Handler ?=null
    var minCount = 0
    var secCount = 0
    var actualSec = ""
    var actualMin = ""
    var lastRandom = 0
    var currentViewHolder : MusicAdapter.MusicViewHolder ?=null
    var musicAdapter : MusicAdapter ?=null
    var random = Random(6)
    var colors = arrayListOf<Int>(R.color.colorAccent , R.color.colorGreen , R.color.colorPirbule ,
        R.color.colorPrimary , R.color.colorPrimaryDark , R.color.colorYelow)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPlayer(R.raw.wrecking_ball_song)
        var canvas = Canvas()
        initMusics()
        musicAdapter =  MusicAdapter(this)
        musicRecyclerView.apply {
            adapter = musicAdapter
            layoutManager = LinearLayoutManager(baseContext)
            (adapter as MusicAdapter).notifyDataSetChanged()
            var itemTouchHelper = ItemTouchHelper(MusicItemTouchHelper(adapter as MusicAdapter))
            itemTouchHelper.attachToRecyclerView(this)
        }
        initAnimation()
        initListeners()
        }
    private fun startAnimateVisualizer(){
        if(currentViewHolder !=null){
            currentViewHolder?.miniVisualizer?.animateBars()
        }
        else {
            if (count >= 1) {
                musicAdapter?.getFirstHolder()?.miniVisualizer?.animateBars()
            }
        }
    }
    private fun stopAnimateVisualizer(){
        if (currentViewHolder != null) {
            currentViewHolder?.miniVisualizer?.stopBars()
        }
        else {
            musicAdapter?.getFirstHolder()?.miniVisualizer?.stopBars()
        }
    }
    private fun initAnimation (){
        ViewAnimator.translateLeftView(infoAnimation)
        ViewAnimator.translateLeftView(newChip)
        ViewAnimator.translateLeftView(solangeChip)
        ViewAnimator.scaleUpView(playMusic)
        ViewAnimator.translateRightView(menuButton)
        ViewAnimator.translateBottomView(milyeDown)
        //ViewAnimator.translateRightView(milyeDown)
        ViewAnimator.translateTopView(milyeUp)
        //ViewAnimator.translateLeftView(milyeUp)

    }
    private fun initListeners (){
        playMusic.setOnClickListener {

            ViewAnimator.scaleUpFromOriginView(playMusic).setAnimationListener(object :Animation.AnimationListener{
                override fun onAnimationStart(animation: Animation?) {
                    if (isPlayed == false) {
                        playMusic.backgroundTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.colorPrimaryDark))
                        playMusic.setImageDrawable(resources.getDrawable(R.drawable.ic_pause_black_24dp))
                        startPlayer()
                        startAnimateVisualizer()
                    }
                    else {
                        playMusic.setImageDrawable(resources.getDrawable(R.drawable.ic_play_arrow_black_24dp))
                        pausePlayer()
                        ViewAnimator.scaleUpFromOriginView(durationChip)
                        stopAnimateVisualizer()
                    }
                }

                override fun onAnimationRepeat(animation: Animation?) {

                }
                override fun onAnimationEnd(animation: Animation?) {
                    if(count == 0) {
                        var destinationConstrains = ConstraintSet()
                        destinationConstrains.clone(baseContext,
                            R.layout.activity_main_end
                        )
                        val transition = ChangeBounds()
                        transition.duration = 1500
                        transition.interpolator = AnticipateOvershootInterpolator()
                        TransitionManager.beginDelayedTransition(root, transition)
                        destinationConstrains.applyTo(root)
                        musicAdapter?.setMusics(musics as ArrayList<Music>)
                        count ++
                    }
                }
            })

        }
        backButton.setOnClickListener {
            if(mediaPlayer?.isPlaying!!){
                playMusic.setImageDrawable(resources.getDrawable(R.drawable.ic_play_arrow_black_24dp))
                pausePlayer()
                ViewAnimator.scaleUpFromOriginView(durationChip)
                stopAnimateVisualizer()

            }
            playMusic.backgroundTintList = ColorStateList.valueOf (resources.getColor(android.R.color.white))
            playMusic.setImageDrawable(resources.getDrawable(R.drawable.ic_play_arrow_orange_24dp))
            count = 0
            var  destinationConstrains =ConstraintSet()
            destinationConstrains.clone(this, R.layout.activity_main)
            val transition = ChangeBounds()
            transition.interpolator = AnticipateOvershootInterpolator()
            transition.duration = 1500
            TransitionManager.beginDelayedTransition(root,transition)
            destinationConstrains.applyTo(root)
            musicAdapter?.setMusics(arrayListOf())
        }
        shuffleMusic.setOnClickListener {
            ViewAnimator.scaleUpFromOriginView(it)
        }
        loopMusic.setOnClickListener {
            ViewAnimator.scaleUpFromOriginView(it)
        }
//        searchScreen.setOnClickListener {
//            playMusic.backgroundTintList = ColorStateList.valueOf (resources.getColor(android.R.color.white))
//            playMusic.setImageDrawable(resources.getDrawable(R.drawable.ic_search_black_24dp))
//            searchScreen.visibility = View.INVISIBLE
//            count = 0
//            var  destinationConstrains =ConstraintSet()
//            destinationConstrains.clone(this, R.layout.activity_main_search)
//            val transition = ChangeBounds()
//            transition.interpolator = OvershootInterpolator()
//            transition.duration = 1500
//            TransitionManager.beginDelayedTransition(root,transition)
//            destinationConstrains.applyTo(root)
//        }
    }
    private fun initMusics(){
        musics = arrayListOf( Music(
            musicName = "Wrecking Ball" ,
            musicType = "BoB" ,
            musicLength = "3:41" ,
            musicNumber = 0 ,
            musicianName = "Miley Cyrus" ,
            songDate = "January 30 , 2020"
        ) , Music(
            musicName = "Godzilla" ,
            musicType = "Rap" ,
            musicLength = "3:31" ,
            musicNumber = 3 ,
            musicianName = "Eminmum" ,
            songDate = "Feb 3 , 2020"
        ) , Music(
            musicName = "Waka Waka" ,
            musicType = "BoB" ,
            musicLength = "3:20" ,
            musicNumber = 4,
            musicianName = "Shakira" ,
            songDate = "Feb 2 , 2020"
        ) )
    }
    private fun initPlayer (resource : Int){
           mediaPlayer = MediaPlayer.create(baseContext , resource)

           mediaPlayer?.setOnCompletionListener {
            blast.hide()
               playMusic.setImageDrawable(resources.getDrawable(R.drawable.ic_play_arrow_black_24dp))
               mediaPlayer?.pause()
               isPlayed = false
               pauseEditChip()
           }
            if(mediaPlayer?.audioSessionId != -1) {
                blast.setAudioSessionId(mediaPlayer?.audioSessionId!!)
            }
           mediaPlayer?.setScreenOnWhilePlaying(true)
    }
    private fun startPlayer (){
        if(!mediaPlayer?.isPlaying!!){
            mediaPlayer?.start()
            startEditChip()
            //ViewAnimator.scaleUpFromOriginView(durationChip)
        }
        isPlayed = true
    }
    private fun pausePlayer (){
        mediaPlayer?.pause()
        isPlayed = false
        pauseEditChip()
    }
    override fun onStop() {
        super.onStop()
        mediaPlayer?.stop()
        blast.release()

    }
    fun getColor() : Int{
        var index = 0
        while(true){
            index = random.nextInt(0 , 5)
            if(index != lastRandom){
            lastRandom = index
                break
            }
        }
        return index
    }
    fun startEditChip(){
        handler = Handler()
        runnable = Runnable {

            if(secCount % 5 == 0){
                var color = getColor()
                blast.setColor(colors[color])

            }
            if(secCount >= 60){
               minCount ++
                secCount = 0
            }
            if (secCount.toString().length <= 1){
                actualSec = "0"+secCount
            }
            if (minCount.toString().length <=1 ){
                actualMin = "0"+ minCount
            }
            if (minCount.toString().length ==2 ){
                actualMin = minCount.toString()
            }
            if (secCount.toString().length ==2 ){
                actualSec = secCount.toString()
            }
            durationChip.text = actualMin + " : " + actualSec
            if (mediaPlayer?.isPlaying!!) {
                handler?.postDelayed(
                    runnable, 1000
                )
            }
            secCount++
        }
        runnable?.run()

    }
    fun pauseEditChip (){
        handler?.removeCallbacks(runnable)
    }
    override fun onDelet(fromPosition: Int, direction: Int) {
        musicAdapter?.onDelet(fromPosition, direction)
    }
    override fun onSwip(fromPosition: RecyclerView.ViewHolder, toPosition: RecyclerView.ViewHolder) {
        musicAdapter?.onSwip(fromPosition.adapterPosition, toPosition.adapterPosition)
        if(!mediaPlayer?.isPlaying!!){
            playMusic.setImageDrawable(resources.getDrawable(R.drawable.ic_pause_black_24dp))
        }
        if (fromPosition.adapterPosition == 1 && toPosition.adapterPosition == 0){
            if (musicAdapter?.getMusics()?.get(0)?.musicName == "Wrecking Ball"){
                playNext(R.raw.wrecking_ball_song , 1)

            }
            else if (musicAdapter?.getMusics()?.get(0)?.musicName == "Godzilla"){
                playNext(R.raw.godzilla , 2)
            }
            else {
                playNext(R.raw.waka , 3)
            }
            currentViewHolder = toPosition as MusicAdapter.MusicViewHolder
            ( toPosition as MusicAdapter.MusicViewHolder).apply {
                miniVisualizer.visibility = View.VISIBLE
                miniVisualizer.animateBars()
            }
            ( fromPosition as MusicAdapter.MusicViewHolder).apply {
                miniVisualizer.visibility = View.INVISIBLE
                miniVisualizer.stopBars()
            }
        }

    }
    private fun playNext (resource : Int , position : Int){
        mediaPlayer?.pause()
        mediaPlayer?.release()
        pauseEditChip()
        initPlayer(resource)
        secCount = 0
        minCount = 0
        durationChip.text = "00:00"
        changeUi()
        startPlayer()
    }
    private fun changeUi (){
        if (musicAdapter?.getMusics()?.get(0)?.musicNumber == 0){
            musicName.setText(musicAdapter?.getMusics()?.get(0)?.musicName)
            milyeUp.setImageDrawable( resources.getDrawable( R.drawable.m_up ))
            milyeDown.setImageDrawable( resources.getDrawable( R.drawable.m_down ))
            topColor.setImageDrawable ( resources.getDrawable( R.drawable.orange_rounded_up ))
            bottomColor.setImageDrawable ( resources.getDrawable( R.drawable.orange_rounded_down ))
            songName.text = musicName.text.toString()
            singerName.text ="By : "+ musicAdapter?.getMusics()?.get(0)?.musicianName
            date.text = musicAdapter?.getMusics()?.get(0)?.songDate
        }
        else if (musicAdapter?.getMusics()?.get(0)?.musicNumber == 3){
            musicName.setText(musicAdapter?.getMusics()?.get(0)?.musicName)
            milyeUp.setImageDrawable(resources.getDrawable( R.drawable.em1 ))
            milyeDown.setImageDrawable(resources.getDrawable( R.drawable.em2 ))
            topColor.setImageDrawable (resources.getDrawable( R.drawable.blue_rounded_up ))
            bottomColor.setImageDrawable( resources.getDrawable( R.drawable.blue_rounded_down ))
            songName.text = musicName.text.toString()
            singerName.text ="By : "+ musicAdapter?.getMusics()?.get(0)?.musicianName
            date.text = musicAdapter?.getMusics()?.get(0)?.songDate
        }
        else {
            musicName.setText(musicAdapter?.getMusics()?.get(0)?.musicName)
            milyeUp.setImageDrawable(resources.getDrawable( R.drawable.sh1 ))
            milyeDown.setImageDrawable(resources.getDrawable( R.drawable.sh2 ))
            topColor.setImageDrawable (resources.getDrawable( R.drawable.yellow_rounded_up ))
            bottomColor.setImageDrawable (resources.getDrawable( R.drawable.yellow_rounded_down ))
            songName.text = musicName.text.toString()
            singerName.text ="By : "+ musicAdapter?.getMusics()?.get(0)?.musicianName
            date.text = musicAdapter?.getMusics()?.get(0)?.songDate
        }
    }
}
