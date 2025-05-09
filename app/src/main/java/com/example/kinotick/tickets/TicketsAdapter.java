package com.example.kinotick.tickets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kinotick.R;

import java.util.List;

public class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.TicketViewHolder> {
    private static Context context;
    private List<Ticket> tickets;

    public TicketsAdapter(Context context, List<Ticket> tickets) {
        this.context = context;
        this.tickets = tickets;
    }

    private void showExpandedQrCode(Bitmap qrCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(qrCode);
        builder.setView(imageView)
                .setPositiveButton("OK", null)
                .show();
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = tickets.get(position);
        holder.bind(ticket);
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        private final TextView movieNameText;
        private final TextView dateTimeText;
        private final TextView seatText;
        private final ImageView qrCodeImage;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            movieNameText = itemView.findViewById(R.id.movie_name);
            dateTimeText = itemView.findViewById(R.id.date_time);
            seatText = itemView.findViewById(R.id.seat);
            qrCodeImage = itemView.findViewById(R.id.qr_code);
        }

        public void bind(Ticket ticket) {
            movieNameText.setText(ticket.getMovieName());
            dateTimeText.setText(ticket.getDateTime());
            seatText.setText(ticket.getSeat());

            // Генерация и установка QR-кода
            Bitmap qrCode = ticket.generateQRCode(300);
            if (qrCode != null) {
                qrCodeImage.setImageBitmap(qrCode);
                // Обработка клика на QR-код
                qrCodeImage.setOnClickListener(v -> {
                    showQrCodeDialog(ticket);
                });
            } else {
                qrCodeImage.setImageResource(R.drawable.ic_error);
            }
        }
        private void showQrCodeDialog(Ticket ticket) {
            // Создаем Bitmap большего размера для диалога
            Bitmap largeQrCode = ticket.generateQRCode(600);

            // Создаем диалог
            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            // Создаем ImageView для QR-кода
            ImageView imageView = new ImageView(context);
            imageView.setImageBitmap(largeQrCode);

            // Настраиваем отступы
            int padding = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 24, context.getResources().getDisplayMetrics());
            imageView.setPadding(padding, padding, padding, padding);

            // Настраиваем диалог
            builder.setTitle("QR-код билета")
                    .setMessage("Фильм: " + ticket.getMovieName() +
                            "\nДата: " + ticket.getDateTime() +
                            "\nМесто: " + ticket.getSeat())
                    .setView(imageView)
                    .setPositiveButton("Закрыть", null)
                    .show();
        }
    }
}
