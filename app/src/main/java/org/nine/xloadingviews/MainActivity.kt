package org.nine.xloadingviews

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.nine.xloadingviews.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.apply {
            xView.setLoading(true)
            val adapter = TestAdapter(arrayListOf())
            xView.setAdapter(adapter)
            xView.setLoadingView(pb2)
            xView.setCustomEmptyView(tvEmpty)

            lifecycleScope.launch(Dispatchers.Default) {
                delay(3000)
                withContext(Dispatchers.Main) {
                    xView.setLoading(false)
                }

                delay(3000)
                withContext(Dispatchers.Main) {
                    adapter.add("1")
                }

                delay(3000)
                withContext(Dispatchers.Main) {
                    adapter.addAll(arrayListOf("2", "3"))
                }

                delay(3000)
                withContext(Dispatchers.Main) {
                    adapter.removeFirst()
                }

                delay(3000)
                withContext(Dispatchers.Main) {
                    adapter.resetAll()
                }
            }
        }
    }
}