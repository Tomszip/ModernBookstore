package com.example.libreriamoderna.ui.detail

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.libreriamoderna.R
import com.example.libreriamoderna.data.model.BookDetail
import com.example.libreriamoderna.databinding.FragmentBookDetailBinding
import com.example.libreriamoderna.util.UiState

/**
 * Shows the full detail of a book: cover, pages, publishers,
 * subjects, description and a link to Open Library.
 * Receives the book id through its arguments (see [newInstance]).
 */
class BookDetailFragment : Fragment() {

    // The view of a Fragment can be destroyed while the Fragment is still alive,
    // so the binding is nullable and cleared in onDestroyView (avoids memory leaks)
    private var _binding: FragmentBookDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val workId = requireArguments().getString(ARG_WORK_ID).orEmpty()

        // viewLifecycleOwner: stop observing when the view is destroyed
        viewModel.uiState.observe(viewLifecycleOwner) { state -> render(state) }
        viewModel.loadBookDetail(workId)
    }

    private fun render(state: UiState<BookDetail>) {
        binding.progressBar.isVisible = state is UiState.Loading
        binding.contentLayout.isVisible = state is UiState.Success
        binding.tvError.isVisible = state is UiState.Error

        when (state) {
            is UiState.Loading -> Unit
            is UiState.Success -> showDetail(state.data)
            is UiState.Error -> binding.tvError.setText(state.messageRes)
        }
    }

    private fun showDetail(detail: BookDetail) {
        binding.tvTitle.text = detail.title.ifEmpty { getString(R.string.unknown_title) }

        binding.tvPages.text = detail.numberOfPages
            ?.let { getString(R.string.detail_pages, it) }
            ?: getString(R.string.detail_pages_unknown)

        binding.tvPublishers.text = detail.publishers
            .joinToString(", ")
            .ifEmpty { getString(R.string.detail_no_data) }

        binding.tvSubjects.text = detail.subjects
            .joinToString(" • ")
            .ifEmpty { getString(R.string.detail_no_data) }

        binding.tvDescription.text = detail.description
            ?: getString(R.string.detail_no_description)

        Glide.with(this)
            .load(detail.coverUrl)
            .placeholder(R.drawable.bg_cover_placeholder)
            .error(R.drawable.bg_cover_placeholder)
            .into(binding.ivCover)

        binding.btnOpenLink.setOnClickListener { openInBrowser(detail.openLibraryUrl) }
    }

    /** Opens the book page in the browser with an implicit Intent. */
    private fun openInBrowser(url: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, url.toUri()))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(requireContext(), R.string.error_no_browser, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_WORK_ID = "arg_work_id"

        /** Factory method: the only way to create this Fragment with its arguments. */
        fun newInstance(workId: String): BookDetailFragment =
            BookDetailFragment().apply {
                arguments = bundleOf(ARG_WORK_ID to workId)
            }
    }
}