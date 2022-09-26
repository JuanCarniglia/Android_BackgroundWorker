package com.example.background.workers.filters

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Log
import java.io.File

import androidx.work.WorkerParameters
import java.lang.Exception

class GetAudioFilesWorker(context:Context, parameters:WorkerParameters) :
    BaseFilterWorker(context,parameters){

    val bigListOfFiles = arrayListOf<String>()

    override fun applyFilter():String{
        var rsContext:RenderScript?=null
        return try{

            val folder = "storage/self/primary/Android/media/com.whatsapp/WhatsApp/Media"

            val subFolders = arrayOf(
                "WhatsApp Audio",
                "WhatsApp Voice Notes"
            )

            val subFoldersLast = arrayOf(
                "AUD-20220831-WA0004.opus",
                "202211"
            )

            val res:String = ""
            File(folder + "/" + subFolders[0]).walk(FileWalkDirection.TOP_DOWN).forEach {
                Log.i("Audio: ", it.name);
            }

            try {
                while (true) {
                    val files = File(folder + "/" + subFolders[1] + "/" + subFoldersLast[1]).walk(FileWalkDirection.TOP_DOWN)
                    if (files.count() > 0) {
                        files.forEach {
                            Log.i("Voice Memo: ", it.name);
                        }
                    } else {
                        // no files here
                        Log.i("No files", "No files on " + folder + "/" + subFolders[1] + "/" + subFoldersLast[1])
                    }

                    subFoldersLast[1] = (subFoldersLast[1].toInt() + 1).toString()
                }
            } catch (error: Exception) {
                Log.i("End of Folders","No more folders... las one : " + subFoldersLast[1])
            }

            return res
        }finally{
            rsContext?.finish()
        }
    }
}
