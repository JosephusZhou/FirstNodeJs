package com.jz.firstnodejs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.URL

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    companion object {
        init {
            System.loadLibrary("native-lib")
            System.loadLibrary("node")
        }

        var _startedNodeAlready = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val jsSb = StringBuilder()
        var bufferReader: BufferedReader? = null
        try {
            bufferReader = BufferedReader(InputStreamReader(
                    assets.open("JD_DailyBonus.js"),
                    "UTF-8"))
            var line: String? = null
            while(true) {
                line = bufferReader.readLine()
                if (line == null) {
                    break
                }
                jsSb.append(line)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        } finally {
            bufferReader?.let {
                try {
                    it.close()
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
        }

        val jsStr = jsSb.toString()

        if (!_startedNodeAlready) {
            _startedNodeAlready = true

            launch {
                withContext(Dispatchers.IO) {
                    startNodeWithArguments(arrayOf(
                            "node",
                            "-e",
                            jsStr
                            /*"var http = require('http'); " +
                                    "var versions_server = http.createServer( (request, response) => { " +
                                    "  response.end('Versions: ' + JSON.stringify(process.versions)); " +
                                    "}); " +
                                    "versions_server.listen(3000);"*/
                    ))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }


    // native method
    external fun stringFromJNI(): String

    external fun startNodeWithArguments(arguments: Array<String>): Int
}