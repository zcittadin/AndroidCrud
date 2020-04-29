package com.esucri.mobile.androidcrud;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ClienteAdapter adapter;

    Cliente clienteEditado = null;

    private boolean isConnected = false;

    private static final int IS_BLUETOOTH_ACTIVE = 1;
    private static final int REQUEST_CONNECTION = 2;
    private static final int MESSAGE_READ = 3;

    private static String MAC = null;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice device = null;
    BluetoothSocket socket = null;

    StringBuilder bluetoothBuilder = new StringBuilder();
    MenuItem bluetoothButton, clockButton, thermoButton;
    MainActivity.ConnectedThread connectedThread;
    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    Handler messageHandler, requestHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //verifica se começou agora ou se veio de uma edição
        Intent intent = getIntent();
        if (intent.hasExtra("cliente")) {
            findViewById(R.id.includemain).setVisibility(View.INVISIBLE);
            findViewById(R.id.includecadastro).setVisibility(View.VISIBLE);
            findViewById(R.id.fab).setVisibility(View.INVISIBLE);
            clienteEditado = (Cliente) intent.getSerializableExtra("cliente");
            EditText txtNome = (EditText) findViewById(R.id.txtNome);
            EditText txtIdade = (EditText) findViewById(R.id.txtIdade);
            Spinner spnEstado = (Spinner) findViewById(R.id.spnEstado);
            CheckBox chkVip = (CheckBox) findViewById(R.id.chkVip);

            txtNome.setText(clienteEditado.getNome());
            txtIdade.setText(clienteEditado.getIdade().toString());
            chkVip.setChecked(clienteEditado.getVip());
            spnEstado.setSelection(getIndex(spnEstado, clienteEditado.getUf()));
            if (clienteEditado.getSexo() != null) {
                RadioButton rb;
                if (clienteEditado.getSexo().equals("M"))
                    rb = (RadioButton) findViewById(R.id.rbMasculino);
                else
                    rb = (RadioButton) findViewById(R.id.rbFeminino);
                rb.setChecked(true);
            }
        } else {
            clienteEditado = null;
            EditText txtNome = (EditText) findViewById(R.id.txtNome);
            EditText txtIdade = (EditText) findViewById(R.id.txtIdade);
            Spinner spnEstado = (Spinner) findViewById(R.id.spnEstado);
            CheckBox chkVip = (CheckBox) findViewById(R.id.chkVip);
            RadioButton rb = (RadioButton) findViewById(R.id.rbMasculino);

            txtNome.setText("");
            txtIdade.setText("");
            chkVip.setChecked(false);
            spnEstado.setSelection(1);
            rb.setChecked(true);

        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.includemain).setVisibility(View.INVISIBLE);
                findViewById(R.id.includecadastro).setVisibility(View.VISIBLE);
                findViewById(R.id.fab).setVisibility(View.INVISIBLE);
            }
        });

        Button btnCancelar = (Button) findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.includemain).setVisibility(View.VISIBLE);
                findViewById(R.id.includecadastro).setVisibility(View.INVISIBLE);
                findViewById(R.id.fab).setVisibility(View.VISIBLE);
                EditText txtNome = (EditText) findViewById(R.id.txtNome);
                EditText txtIdade = (EditText) findViewById(R.id.txtIdade);
                Spinner spnEstado = (Spinner) findViewById(R.id.spnEstado);
                CheckBox chkVip = (CheckBox) findViewById(R.id.chkVip);
                RadioButton rb = (RadioButton) findViewById(R.id.rbMasculino);

                txtNome.setText("");
                txtIdade.setText("");
                chkVip.setChecked(false);
                spnEstado.setSelection(1);
                rb.setChecked(true);
                Intent intent = getIntent();
                if (intent.hasExtra("cliente")) {
                    intent.removeExtra("cliente");
                }
            }
        });

        Button btnSalvar = (Button) findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //carregando os campos
                EditText txtNome = (EditText) findViewById(R.id.txtNome);
                EditText txtIdade = (EditText) findViewById(R.id.txtIdade);
                Spinner spnEstado = (Spinner) findViewById(R.id.spnEstado);
                RadioGroup rgSexo = (RadioGroup) findViewById(R.id.rgSexo);
                CheckBox chkVip = (CheckBox) findViewById(R.id.chkVip);

                //pegando os valores
                String nome = txtNome.getText().toString();
                Integer idade = new Integer(String.valueOf(txtIdade.getText()));
                String uf = spnEstado.getSelectedItem().toString();
                boolean vip = chkVip.isChecked();
                String sexo = rgSexo.getCheckedRadioButtonId() == R.id.rbMasculino ? "M" : "F";

                //salvando os dados
                ClienteDAO dao = new ClienteDAO(getBaseContext());
                boolean sucesso;
                if (clienteEditado != null)
                    sucesso = dao.salvar(clienteEditado.getId(), nome, idade, sexo, uf, vip);
                else
                    sucesso = dao.salvar(nome, idade, sexo, uf, vip);

                if (sucesso) {
                    Cliente cliente = dao.retornarUltimo();
                    if (clienteEditado != null) {
                        adapter.atualizarCliente(cliente);
                        clienteEditado = null;
                    } else
                        adapter.adicionarCliente(cliente);

                    txtNome.setText("");
                    txtIdade.setText("");
                    rgSexo.setSelected(false);
                    spnEstado.setSelection(0);
                    chkVip.setChecked(false);

                    Snackbar.make(view, "Cliente salvo com sucesso.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    findViewById(R.id.includemain).setVisibility(View.VISIBLE);
                    findViewById(R.id.includecadastro).setVisibility(View.INVISIBLE);
                    findViewById(R.id.fab).setVisibility(View.VISIBLE);
                } else {
                    Snackbar.make(view, "Erro ao salvar o cliente!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        configurarRecycler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.btn_bluetooth) {
            bluetoothButton = item;
            if (bluetoothAdapter == null) {
                Toast.makeText(getApplicationContext(), "Bluetooth não suportado.", Toast.LENGTH_LONG).show();
                finish();
            } else if (!bluetoothAdapter.isEnabled()) {
                Intent activateBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(activateBluetooth, IS_BLUETOOTH_ACTIVE);
            } else {
                if (isConnected) {
                    //disconnect
                    try {
                        socket.close();
                        isConnected = false;
                        //requestHandler.removeCallbacks(runnableCode);
                        item.setIcon(R.drawable.ic_bluetooth_disabled_white_24dp);
                        Toast.makeText(getApplicationContext(), "Bluetooth desconectado.", Toast.LENGTH_LONG).show();
                    } catch (IOException ex) {
                        Toast.makeText(getApplicationContext(), "Ocorreu um erro: " + ex, Toast.LENGTH_LONG).show();
                    }
                } else {
                    //connect
                    Intent openDeviceList = new Intent(MainActivity.this, ListDevices.class);
                    startActivityForResult(openDeviceList, REQUEST_CONNECTION);
                }
            }
        }
        if (id == R.id.btn_text) {
            Context context = MainActivity.this;
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            AlertDialog alertDialog;
            final EditText input = new EditText(MainActivity.this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            builder.setPositiveButton("Ok", (dialog, whichButton) -> {
                String message = input.getText().toString();
                if (isConnected) {
                    connectedThread.send("T " + message);
                }
            });
            builder.setNegativeButton("Cancela", (dialog, whichButton) -> {

            });
            alertDialog = builder.create();
            alertDialog.setView(input);
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IS_BLUETOOTH_ACTIVE:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Bluetooth ativado.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Bluetooth não ativado. O app será encerrado.", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            case REQUEST_CONNECTION:
                if (resultCode == Activity.RESULT_OK) {
                    MAC = data.getExtras().getString(ListDevices.MAC_ADDRESS);
                    device = bluetoothAdapter.getRemoteDevice(MAC);
                    try {
                        socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                        socket.connect();
                        isConnected = true;
                        connectedThread = new ConnectedThread(socket);
                        connectedThread.start();
                        bluetoothButton.setIcon(R.drawable.ic_bluetooth_connected_green_24dp);
                        //requestHandler.post(runnableCode);
                        Toast.makeText(getApplicationContext(), "Conectado: " + MAC, Toast.LENGTH_LONG).show();
                    } catch (IOException ex) {
                        isConnected = false;
                        Toast.makeText(getApplicationContext(), "Erro ao conectar: " + ex, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Falha ao obter o endereço MAC.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void configurarRecycler() {
        // Configurando o gerenciador de layout para ser uma lista.
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Adiciona o adapter que irá anexar os objetos à lista.
        ClienteDAO dao = new ClienteDAO(this);
        adapter = new ClienteAdapter(dao.retornarTodos());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket _socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    String dataBt = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI activity
                    messageHandler.obtainMessage(MESSAGE_READ, bytes, -1, dataBt).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void send(String dataSend) {
            byte[] msgBuffer = dataSend.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
            }
        }
    }
}
