package com.example.libreriamoderna.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.libreriamoderna.R
import com.example.libreriamoderna.databinding.ActivityDetailBinding

/**
 * Second screen of the app. It only hosts [BookDetailFragment],
 * which contains all the detail logic.
 */
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        binding.toolbar.setNavigationOnClickListener { finish() }

        val workId = intent.getStringExtra(EXTRA_WORK_ID)
        if (workId == null) {
            finish() // nothing to show without a book id
            return
        }

        // Only add the fragment the first time. After a rotation the
        // FragmentManager restores it automatically, avoiding duplicates.
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragmentContainer, BookDetailFragment.newInstance(workId))
            }
        }
    }

    /** Adds padding so the content is not drawn behind the system bars. */
    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    companion object {
        private const val EXTRA_WORK_ID = "extra_work_id"

        /** Builds the Intent to open this screen with the given book id. */
        fun createIntent(context: Context, workId: String): Intent =
            Intent(context, DetailActivity::class.java).putExtra(EXTRA_WORK_ID, workId)
    }
}