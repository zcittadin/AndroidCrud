package com.esucri.mobile.androidcrud;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ClienteHolder extends RecyclerView.ViewHolder {

    public TextView nomeCliente;
    public ImageButton btnEditar;
    public ImageButton btnExcluir;

    public ClienteHolder(View itemView) {
        super(itemView);
        nomeCliente = itemView.findViewById(R.id.nomeCliente);
        btnEditar = itemView.findViewById(R.id.btnEdit);
        btnExcluir = itemView.findViewById(R.id.btnDelete);
    }
}