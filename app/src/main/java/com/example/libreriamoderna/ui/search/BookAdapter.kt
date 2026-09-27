package com.example.libreriamoderna.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.libreriamoderna.R
import com.example.libreriamoderna.data.model.Book
import com.example.libreriamoderna.databinding.ItemBookBinding

/**
 * Adapter of the search results list.
 * ListAdapter + DiffUtil only redraws the rows that actually changed.
 *
 * @param onBookClick callback invoked when the user taps a book.
 */
class BookAdapter(
    private val onBookClick: (Book) -> Unit
) : ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback) {

    /** Called when the RecyclerView needs a new row: inflates item_book.xml. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding, onBookClick)
    }

    /** Called to fill an existing row with the book at [position]. */
    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /** Custom ViewHolder: keeps the references to the views of one row. */
    class BookViewHolder(
        private val binding: ItemBookBinding,
        private val onBookClick: (Book) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            val context = binding.root.context

            binding.tvTitle.text = book.title ?: context.getString(R.string.unknown_title)
            binding.tvAuthors.text = book.authorsText ?: context.getString(R.string.unknown_author)
            binding.tvYear.text = book.firstPublishYear
                ?.let { context.getString(R.string.first_published, it) }
                ?: context.getString(R.string.unknown_year)

            // Glide downloads the cover in background and caches it
            Glide.with(binding.ivCover)
                .load(book.coverUrl)
                .placeholder(R.drawable.bg_cover_placeholder)
                .error(R.drawable.bg_cover_placeholder)
                .into(binding.ivCover)

            binding.root.setOnClickListener { onBookClick(book) }
        }
    }
}

/** Tells ListAdapter how to compare two books to detect changes. */
private object BookDiffCallback : DiffUtil.ItemCallback<Book>() {

    override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean =
        oldItem.key == newItem.key

    override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean =
        oldItem == newItem
}