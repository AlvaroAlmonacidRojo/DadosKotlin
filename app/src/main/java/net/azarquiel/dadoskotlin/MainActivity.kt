package net.azarquiel.dadoskotlin

import android.graphics.drawable.AnimationDrawable
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.uiThread
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {


    lateinit var ivs: Array<ImageView>
    var frames:Array<AnimationDrawable> = Array(5,{AnimationDrawable()})
    var jugada = Array(5,{0})
    var rulando: Boolean = false
    var intentos: Int = 0
    var sp: SoundPool? = null
    var sonido: Int = 0
    var sonidoStream: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sp = SoundPool(5, AudioManager.STREAM_MUSIC, 0)
        sonido = sp!!.load(this, R.raw.sonido, 1)

        val anis = arrayOf(R.drawable.ani0, R.drawable.ani1,R.drawable.ani2, R.drawable.ani3, R.drawable.ani4)

        ivs = arrayOf(iv0,iv1,iv2,iv3,iv4)

        btnJugar.setOnClickListener({v ->btnJugarOnClick(v)  })

        for ((i,imgV) in ivs.withIndex()){

            imgV.setImageResource(android.R.color.transparent)
            imgV.setBackgroundResource(anis[i])
            frames[i] = imgV.background as AnimationDrawable
            inventaCara(imgV,i)
            imgV.setOnClickListener(this)


        }


    }

    private fun inventaCara(imgV: ImageView, i: Int){
        val n = Random().nextInt(5)
        jugada[i] = n

        val id = resources.getIdentifier("cara$n","drawable",packageName)
        imgV.setImageResource(id)
        val dado = Dado(false, n)
        imgV.tag = dado
    }

    private fun btnJugarOnClick(v: View){
        if(rulando) return
        rulando = true
        sonidoStream = sp!!.play(sonido, 1f, 1f, 1, -1, 1f)
        for(i in ivs.indices){


            if(!(ivs[i].tag as Dado).marcado)
                ivs[i].setImageResource(android.R.color.transparent)
                frames[i].start()
        }

        doAsync {
            SystemClock.sleep(1000)
            uiThread {
                rulando = false
                sp!!.stop(sonidoStream)
                for(i in ivs.indices){
                    frames[i].stop()
                    if(!(ivs[i].tag as Dado).marcado) {
                        inventaCara(ivs[i], i)
                    }
                }
                intentos++
                gameOver()



            }
        }






    }


    override fun onClick(v: View) {
        if(rulando) return

        val imgV: ImageView = v as ImageView

        val dado: Dado = imgV.tag as Dado

        if(dado.marcado) {

            val id = resources.getIdentifier("cara${dado.cara}", "drawable", packageName)
            imgV.setImageResource(id)
            dado.marcado = !dado.marcado

        }else{
            val id = resources.getIdentifier("cara${dado.cara}v", "drawable", packageName)
            imgV.setImageResource(id)
            dado.marcado = !dado.marcado
        }


    }

    private fun gameOver(){
        if(todosIguales()){
            for(i in ivs){
                val dado: Dado = i.tag as Dado
                if(!dado.marcado){
                    val foto = "cara${dado.cara}v"
                    val id = resources.getIdentifier(foto, "drawable", packageName)
                    i.setImageResource(id)
                    dado.marcado = !dado.marcado
                }
                i.setOnClickListener(null)

            }

            btnJugar.setOnClickListener(null)
            longToast("Congratulations $intentos intentos")
        }

    }

    private fun  todosIguales(): Boolean{

        val j: Int = jugada[0]

        for(ju in jugada){
            if(j!=ju){

                return false
            }
        }

        return true
    }


}

