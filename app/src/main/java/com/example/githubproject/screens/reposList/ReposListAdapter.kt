package com.example.githubproject.screens.reposList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.githubproject.databinding.ItemReposListBinding
import com.example.githubproject.domain.model.repos.RepoDomain

class ReposListAdapter(private val items: List<RepoDomain>): RecyclerView.Adapter<ReposListAdapter.ReposListViewHolder>() {
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReposListViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemReposListBinding = ItemReposListBinding.inflate(layoutInflater, parent, false)
        return ReposListViewHolder(itemReposListBinding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ReposListViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            onClickListener?.onClick(position, item)
        }
    }

    // Установка прослушки на нажатие
    fun setOnClickListener(listener: OnClickListener?) {
        this.onClickListener = listener
    }

    // Интерфейс для прослушки нажатия
    interface OnClickListener {
        fun onClick(position: Int, model: RepoDomain)
    }

    class ReposListViewHolder(private val itemReposListBinding: ItemReposListBinding): RecyclerView.ViewHolder(itemReposListBinding.root) {
        fun bind(item: RepoDomain) {
            itemReposListBinding.repoNameTv.text = item.name
            itemReposListBinding.repoLanguageTv.apply {
                text = item.language
            }
            itemReposListBinding.repoDescriptionTv.text = item.description
        }
    }
}