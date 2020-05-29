package br.com.bossini.fatec_ipi_noite_chat_com_firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mensagensRecyclerView;
    private ChatAdapter adapter;
    private List <Mensagem> mensagens;
    private EditText mensagemEditText;
    private FirebaseUser firebaseUser;
    private CollectionReference mensagensReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mensagensRecyclerView =
                findViewById(R.id.mensagensRecyclerView);
        mensagens = new ArrayList<>();
        adapter = new ChatAdapter(mensagens, this);
        mensagensRecyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        mensagensRecyclerView.setLayoutManager(linearLayoutManager);
        mensagemEditText = findViewById(R.id.mensagemEditText);
    }

    private void getRemoteMsgs (){

        mensagensReference.addSnapshotListener((snapshot, firebaseException) -> {
            mensagens.clear();
            for (DocumentSnapshot doc: snapshot.getDocuments()){
                Mensagem msg = doc.toObject(Mensagem.class);
                mensagens.add(msg);
            }
            Collections.sort(mensagens);
            adapter.notifyDataSetChanged();
        });

    }

    private void setupFirebase(){
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mensagensReference = FirebaseFirestore.getInstance().collection("mensagens");
        getRemoteMsgs();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupFirebase();
    }

    public void enviarMensagem (View v){
        String texto = mensagemEditText.getText().toString();
        Mensagem mensagem = new Mensagem (firebaseUser.getEmail(), new Date(), texto);
        esconderTeclado(v);
        mensagensReference.add(mensagem);
        mensagemEditText.getText().clear();
    }

    private void esconderTeclado (View v){
        InputMethodManager imm =
                (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}




class ChatViewHolder extends RecyclerView.ViewHolder{
    TextView dataNomeTextView;
    TextView mensagemTextView;

    ChatViewHolder (View raiz){
        super(raiz);
        this.dataNomeTextView = raiz.findViewById(R.id.dataNomeTextView);
        this.mensagemTextView = raiz.findViewById(R.id.mensagemTextView);
    }
}

class ChatAdapter extends RecyclerView.Adapter <ChatViewHolder>{
    private List <Mensagem> mensagens;
    private Context context;
    ChatAdapter (List <Mensagem> mensagens, Context context){
        this.mensagens = mensagens;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Mensagem m = mensagens.get(position);
        holder.mensagemTextView.setText(m.getTexto());
        holder.dataNomeTextView.setText(
                context.getString(
                    R.string.mensagem,
                    DateHelper.format(m.getDate()),
                    m.getUsuario()
                )
        );
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View raiz = inflater.inflate(R.layout.list_item, parent, false);
        return new ChatViewHolder (raiz);
    }

    @Override
    public int getItemCount() {
        return this.mensagens.size();
    }
}
