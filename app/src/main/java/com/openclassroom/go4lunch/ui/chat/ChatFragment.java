package com.openclassroom.go4lunch.ui.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassroom.go4lunch.R;
import com.openclassroom.go4lunch.database.FirebaseHelper;
import com.openclassroom.go4lunch.models.Message;
import java.util.ArrayList;

/**
 * Created by de Meeûs Augustin on 10/03/2020
 **/
public class ChatFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private ArrayList<Message> messages = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chat_fragment, container, false);

        recyclerView = view.findViewById(R.id.rv_chat);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        chatAdapter = new ChatAdapter(messages);
        recyclerView.setAdapter(chatAdapter);

        Button sendButton = view.findViewById(R.id.bt_send);
        EditText editText = view.findViewById(R.id.et_message_to_send);

        sendButton.setOnClickListener(v -> FirebaseHelper.addMessage(new Message(editText.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getDisplayName().split(" ")[0], FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString()))
                .addOnCompleteListener(task -> {
                    Toast.makeText(getActivity(), R.string.message_sent, Toast.LENGTH_SHORT).show();
                    editText.setText("");
                    hideKeyboardFrom(getActivity(), view);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), R.string.error_sending, Toast.LENGTH_SHORT).show();
                }));

        getAllMessage();
        return view;
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void getAllMessage(){

        CollectionReference collectionReference = FirebaseHelper.getChatMessageCollection();
        collectionReference.addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                Toast.makeText(getActivity(), R.string.error_retrieving, Toast.LENGTH_SHORT).show();
                return;
            }
            messages.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                messages.add(doc.toObject(Message.class));
                chatAdapter.notifyDataSetChanged();
            }
            recyclerView.scrollToPosition(messages.size() - 1);
        });
    }
}
