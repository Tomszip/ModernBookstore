package com.example.libreriamoderna.ui.search

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.libreriamoderna.data.model.Book
import com.example.libreriamoderna.databinding.ActivityMainBinding
import com.example.libreriamoderna.ui.detail.DetailActivity
import com.example.libreriamoderna.util.UiState

/**
 * Search screen: lets the user search books and shows the results.
 * It only renders the state exposed by [SearchViewModel].
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: SearchViewModel by viewModels()
    private val bookAdapter = BookAdapter { book -> onBookClicked(book) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        setupRecyclerView()
        setupSearchView()
        observeUiState()
    }

    /** Adds padding so the content is not drawn behind the system bars. */
    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupRecyclerView() {
        binding.rvBooks.layoutManager = LinearLayoutManager(this)
        binding.rvBooks.adapter = bookAdapter
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchBooks(query.orEmpty())
                binding.searchView.clearFocus() // hides the keyboard
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })
    }

    /** Observes the ViewModel: every new state redraws the screen. */
    private fun observeUiState() {
        viewModel.uiState.observe(this) { state -> render(state) }
    }

    private fun render(state: UiState<List<Book>>) {
        binding.progressBar.isVisible = state is UiState.Loading
        binding.rvBooks.isVisible = state is UiState.Success
        binding.tvMessage.isVisible = state is UiState.Error

        when (state) {
            is UiState.Loading -> Unit
            is UiState.Success -> bookAdapter.submitList(state.data)
            is UiState.Error -> binding.tvMessage.setText(state.messageRes)
        }
    }

    /** Opens the detail screen passing only the book id. */
    private fun onBookClicked(book: Book) {
        val workId = book.workId ?: return
        startActivity(DetailActivity.createIntent(this, workId))
    }
}