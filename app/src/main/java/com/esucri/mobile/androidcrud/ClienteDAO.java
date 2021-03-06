package com.esucri.mobile.androidcrud;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private final String TABLE_CLIENTES = "Clientes";
    private DatabaseConnection gw;

    public ClienteDAO(Context ctx) {
        gw = DatabaseConnection.getInstance(ctx);
    }

    public boolean salvarCliente(String nome, Integer idade, String sexo, String uf, boolean vip) {
        return salvarCliente(0, nome, idade, sexo, uf, vip);
    }

    public boolean salvarCliente(int id, String nome, Integer idade, String sexo, String uf, boolean vip) {
        ContentValues cv = new ContentValues();
        cv.put("Nome", nome);
        cv.put("Idade", idade);
        cv.put("Sexo", sexo);
        cv.put("UF", uf);
        cv.put("Vip", vip ? 1 : 0);
        if (id > 0)
            return gw.getDatabase().update(TABLE_CLIENTES, cv, "ID=?", new String[]{id + ""}) > 0;
        else
            return gw.getDatabase().insert(TABLE_CLIENTES, null, cv) > 0;
    }

    public List<Cliente> findAll() {
        List<Cliente> clientes = new ArrayList<>();
        Cursor cursor = gw.getDatabase().rawQuery("SELECT * FROM Clientes", null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("ID"));
            String nome = cursor.getString(cursor.getColumnIndex("Nome"));
            Integer idade = cursor.getInt(cursor.getColumnIndex("Idade"));
            String sexo = cursor.getString(cursor.getColumnIndex("Sexo"));
            String uf = cursor.getString(cursor.getColumnIndex("UF"));
            boolean vip = cursor.getInt(cursor.getColumnIndex("Vip")) > 0;
            clientes.add(new Cliente(id, nome, idade, sexo, uf, vip));
        }
        cursor.close();
        return clientes;
    }

    public Cliente findLast() {
        Cursor cursor = gw.getDatabase().rawQuery("SELECT * FROM Clientes ORDER BY ID DESC", null);
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex("ID"));
            String nome = cursor.getString(cursor.getColumnIndex("Nome"));
            Integer idade = cursor.getInt(cursor.getColumnIndex("Idade"));
            String sexo = cursor.getString(cursor.getColumnIndex("Sexo"));
            String uf = cursor.getString(cursor.getColumnIndex("UF"));
            boolean vip = cursor.getInt(cursor.getColumnIndex("Vip")) > 0;
            cursor.close();
            return new Cliente(id, nome, idade, sexo, uf, vip);
        }
        return null;
    }

    public boolean removeClente(int id) {
        return gw.getDatabase().delete(TABLE_CLIENTES, "ID=?", new String[]{id + ""}) > 0;
    }
}
