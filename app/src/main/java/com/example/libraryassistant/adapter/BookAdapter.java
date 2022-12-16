package com.example.libraryassistant.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libraryassistant.R;
import com.example.libraryassistant.apiclient.Book;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    ArrayList<Book> listBook;

    public BookAdapter(ArrayList<Book> listBook) {
        this.listBook = listBook;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder holder = new ViewHolder(inflater.inflate(R.layout.book_item, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = listBook.get(position);
        holder.txtJudul.setText(book.getTitle());
        holder.txtAuthor.setText(book.getAuthor());
        holder.txtDesc.setText(book.getDescription());

        String sub_url = book.getImage().substring(22, 62);
        Log.i("test: ", sub_url);
        String url = "https://5426-36-73-1-19.ap.ngrok.io/" + sub_url;

        Picasso.get()
                .load(url)
                .into(holder.imgFoto);
    }

    @Override
    public int getItemCount() {
        return listBook.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgFoto;
        public TextView txtJudul, txtAuthor, txtDesc;
        public ConstraintLayout itemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFoto = itemView.findViewById(R.id.img_foto);
            txtJudul = itemView.findViewById(R.id.txt_judul);
            txtAuthor = itemView.findViewById(R.id.txt_author);
            txtDesc = itemView.findViewById(R.id.txt_desc);
        }
    }
}
