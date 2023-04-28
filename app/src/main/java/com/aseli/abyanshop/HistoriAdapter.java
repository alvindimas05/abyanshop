package com.aseli.abyanshop;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoriAdapter extends RecyclerView.Adapter<HistoriAdapter.HistoriHolder> {
    private Activity activity;
    private List<HistoriItem> items;
    public HistoriAdapter(Activity activity, List<HistoriItem> items){
        this.activity = activity;
        this.items = items;
    }

    @NonNull
    @Override
    public HistoriAdapter.HistoriHolder onCreateViewHolder(@NonNull ViewGroup parent, int type){
        LayoutInflater inflater = LayoutInflater.from(activity);
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.profil_histori, null, false);
        layout.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new HistoriHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoriHolder holder, int i) {
        holder.nama.setText(items.get(i).getNama());
        holder.jumlah.setText(items.get(i).getJumlah());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class HistoriHolder extends RecyclerView.ViewHolder {
        private TextView nama, jumlah;
        public HistoriHolder(@NonNull LinearLayout layout){
            super(layout);
            nama = layout.findViewById(R.id.histori_nama);
            jumlah = layout.findViewById(R.id.histori_jumlah);
        }
    }
}
class HistoriItem {
    private String nama, jumlah;
    public void setNama(String nama) {
        this.nama = nama;
    }
    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }
    public String getNama() {
        return nama;
    }
    public String getJumlah() {
        return jumlah;
    }
}