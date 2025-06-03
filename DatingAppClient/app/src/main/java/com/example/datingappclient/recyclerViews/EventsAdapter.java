package com.example.datingappclient.recyclerViews;

import android.annotation.SuppressLint;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datingappclient.R;
import com.example.datingappclient.model.dto.EventDTO;
import com.example.datingappclient.model.dto.EventMemberDTO;
import com.example.datingappclient.utils.DateUtils;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class EventsAdapter extends ListAdapter<EventDTO, EventsAdapter.EventViewHolder> {

    private int expandedPosition = RecyclerView.NO_POSITION;
    private final OnJoinClickListener listener;
    private final int userId;

    public interface OnJoinClickListener {
        void onJoinClick(EventDTO event, OnClickResult callback);
        void onLeaveClick(EventDTO event, OnClickResult callback);
    }

    public EventsAdapter(OnJoinClickListener listener, int userId) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        this.userId = userId;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            boolean isExpanded = position == expandedPosition;
            holder.setExpandedState(isExpanded); // только анимируем visibility
        }
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, @SuppressLint("RecyclerView") int position) {
        EventDTO event = getItem(position);
        boolean isExpanded = position == expandedPosition;

        holder.bind(event, isExpanded, userId);

        // Клик по карточке
        holder.itemView.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            if (expandedPosition == adapterPosition) {
                // Нажали на уже раскрытую карточку → свернуть
                expandedPosition = RecyclerView.NO_POSITION;
                notifyItemChanged(adapterPosition, true); // payload
            } else {
                // Нажали на новую карточку
                int oldExpandedPosition = expandedPosition;
                expandedPosition = adapterPosition;
                if (oldExpandedPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(oldExpandedPosition, true);
                }
                notifyItemChanged(adapterPosition, true);
            }
        });

        // Клик по кнопке
        changeClickListener(event, holder);
    }

    public interface OnClickResult {
        void onClick(boolean success);
    }
    public void changeClickListener(EventDTO event, EventViewHolder holder) {
        holder.joinButton.setOnClickListener(view -> {
            if (event.userIsMember(userId)) {
                listener.onLeaveClick(event, callback -> {
                    holder.setupButton(callback);
                    changeClickListener(event, holder);
                    holder.updateCountMembers();
                });
            } else {
                listener.onJoinClick(event, callback -> {
                    holder.setupButton(callback);
                    holder.updateCountMembers();
                    changeClickListener(event, holder);
                });
            }
        });
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventTitle, eventTime, eventDescription, countMembers, fullDescription, eventLocation;
        LinearLayout membersList, cardRoot;
        MaterialButton joinButton;

        private EventDTO event;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventLocation = itemView.findViewById(R.id.eventLocation);
            eventTime = itemView.findViewById(R.id.eventTime);
            eventDescription = itemView.findViewById(R.id.eventDescription);
            countMembers = itemView.findViewById(R.id.countMembers);
            fullDescription = itemView.findViewById(R.id.fullDescription);
            membersList = itemView.findViewById(R.id.membersList);
            joinButton = itemView.findViewById(R.id.joinEventButton);

            cardRoot = itemView.findViewById(R.id.cardRoot);
        }

        public void bind(EventDTO event, boolean isExpanded, int userId) {
            this.event = event;

            // Title
            eventTitle.setText(event.getTitle());

            // Location
            String location = event.getLocation().isEmpty() ? "" : "Локация: " + event.getLocation();
            eventLocation.setText(location);

            // Format time
            if (event.getStartTime() == null && event.getEndTime() == null)
                eventTime.setText("Время не задано");
            else if (event.getEndTime() == null)
                eventTime.setText(DateUtils.formatTime(event.getStartTime()));
            else
                eventTime.setText(DateUtils.formatTimeRange(event.getStartTime(), event.getEndTime()));

            // Description
            eventDescription.setText(event.getDescription());
            fullDescription.setText(event.getDescription());

            boolean isMember = event.userIsMember(userId);
            setupButton(isMember);

            updateCountMembers();

            setExpandedState(isExpanded); // отдельно анимируем только это
        }

        private void setupButton(boolean isMember) {
            if (isMember) {
                joinButton.setText("Покинуть");
                joinButton.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.holo_dark_red));
            } else {
                joinButton.setText("Присоединиться");
                joinButton.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.green));
            }
        }

        public void setExpandedState(boolean isExpanded) {
            TransitionSet transitionSet = new TransitionSet();
            transitionSet.setOrdering(TransitionSet.ORDERING_SEQUENTIAL );
            transitionSet.addTransition(new ChangeBounds());
            transitionSet.addTransition(new Fade());
            transitionSet.setDuration(100); // настрой по вкусу

            TransitionManager.beginDelayedTransition(cardRoot, transitionSet);
            TransitionManager.beginDelayedTransition(cardRoot, new AutoTransition()); // cardRoot = родитель карточки

            fullDescription.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            eventDescription.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            membersList.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        }

        public void updateCountMembers() {
            String countMembersText = "Участников: " + event.getMembers().size();
            if (event.getCapacity() != null) countMembersText += " из " + event.getCapacity();
            countMembers.setText(countMembersText);
            membersList.removeAllViews();

            for (EventMemberDTO member : event.getMembers()) {
                TextView memberView = new TextView(itemView.getContext());
                memberView.setText("- " + member.getName());
                memberView.setTextSize(14);
                membersList.addView(memberView);
            }
        }
    }

    public static final DiffUtil.ItemCallback<EventDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<EventDTO>() {
        @Override
        public boolean areItemsTheSame(@NonNull EventDTO oldItem, @NonNull EventDTO newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()); // или ID, если есть
        }

        @Override
        public boolean areContentsTheSame(@NonNull EventDTO oldItem, @NonNull EventDTO newItem) {
            return oldItem.equals(newItem);
        }
    };
}
