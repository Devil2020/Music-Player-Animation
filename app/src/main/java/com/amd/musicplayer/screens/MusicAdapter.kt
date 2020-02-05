package com.amd.musicplayer.screens

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.amd.musicplayer.R
import com.amd.musicplayer.common.CirculeImage
import com.amd.musicplayer.common.ViewAnimator
import com.amd.musicplayer.entities.Music
import com.squareup.picasso.Picasso
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.music_item.*
import java.util.*
import kotlin.collections.ArrayList

interface MusicViewHolderListener {
    fun onDelet(fromPosition : Int , direction : Int)
    fun onSwip(fromPosition : RecyclerView.ViewHolder , toPosition : RecyclerView.ViewHolder)
}

class MusicAdapter: RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {

    private var musics : ArrayList<Music> ? = arrayListOf()
    private var listener : MusicViewHolderListener ?=null
    private lateinit var firstViewHolder : MusicViewHolder

    constructor(listener: MusicViewHolderListener){
        this.listener = listener
    }

    public fun getFirstHolder () : MusicViewHolder? {
        return firstViewHolder!!
    }
    public fun setMusics(musics: ArrayList<Music>){
        this.musics = musics
        notifyDataSetChanged()
    }
    public fun getMusics () : ArrayList<Music>{
        return musics!!
    }
    public  fun getListener() : MusicViewHolderListener {
        return listener!!
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        var view = LayoutInflater.from(parent?.context).inflate(R.layout.music_item,parent,false)
        return MusicViewHolder(view)
    }

    override fun getItemCount(): Int {
        return musics?.size!!
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        if (position == 0) {
            firstViewHolder = holder
        }
        holder.bind(musics?.get(position)!!,holder.root.context!! , position)
    }

    fun onDelet(fromPosition: Int, direction: Int) {
    musics?.removeAt(fromPosition)
    notifyItemRemoved(fromPosition)
    }

    fun onSwip(fromPosition: Int, toPosition: Int) {
        Collections.swap(musics , fromPosition , toPosition)
        notifyItemMoved(fromPosition,toPosition)
    }

    class MusicViewHolder(override val containerView: View?) :
        RecyclerView.ViewHolder(containerView!!)
        , LayoutContainer{

             fun bind (music :Music , context: Context , position:Int){
                 if(position == 0){
                     miniVisualizer.visibility = View.VISIBLE
                     miniVisualizer.animateBars()


                 }
                 else {
                     miniVisualizer.visibility = View.INVISIBLE
                     miniVisualizer.stopBars()
                 }
                 leftMusic.startAnimation( AnimationUtils.loadAnimation(context , R.anim.scale_then_translate) )
                 rightMusic.startAnimation(AnimationUtils.loadAnimation(context , R.anim.scale_then_translate_right) )
                 //ViewAnimator.translateLeftView(root)
                 ItemMusicName.setText(music.musicName)

                 ItemMusicDuration.setText(music.musicLength)

                 ItemMusicType.setText(music.musicType)

                 if(music.musicNumber == 0){
                    Picasso.get().load(R.drawable.miley).transform(CirculeImage()).into(ItemMusicImage)
                 }
                 else if(music.musicNumber == 1){
                     Picasso.get().load(R.drawable.adele).transform(CirculeImage()).into(ItemMusicImage)
                 }
                 else if(music.musicNumber == 2){
                     Picasso.get().load(R.drawable.asala).transform(CirculeImage()).into(ItemMusicImage)
                 }
                 else if(music.musicNumber == 3){
                     Picasso.get().load(R.drawable.eminmum).transform(CirculeImage()).into(ItemMusicImage)
                 }
                 else if(music.musicNumber == 4){
                     Picasso.get().load(R.drawable.shakira).transform(CirculeImage()).into(ItemMusicImage)
                 }
             }
        }

    }