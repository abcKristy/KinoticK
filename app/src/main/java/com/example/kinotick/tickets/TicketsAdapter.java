package com.example.kinotick.tickets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kinotick.R;

import java.util.List;

public class TicketsAdapter extends RecyclerView.Adapter<TicketsAdapter.TicketViewHolder> {

    private List<Ticket> tickets;

    public TicketsAdapter(List<Ticket> tickets) {
        this.tickets = tickets;
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

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            movieNameText = itemView.findViewById(R.id.movie_name);
            dateTimeText = itemView.findViewById(R.id.date_time);
            seatText = itemView.findViewById(R.id.seat);
        }

        public void bind(Ticket ticket) {
            movieNameText.setText(ticket.getMovieName());
            dateTimeText.setText(ticket.getDateTime());
            seatText.setText(ticket.getSeat());
        }
    }
}
