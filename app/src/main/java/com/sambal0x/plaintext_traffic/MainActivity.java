package com.sambal0x.plaintext_traffic;

import android.os.Bundle;
import android.os.StrictMode;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private EditText editTextServerAddress;
    private EditText editTextPort;  // Added for port input
    private EditText editTextMessage;
    private Button buttonSend;
    private TextView textViewResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        editTextServerAddress = findViewById(R.id.editTextServerAddress);
        editTextPort = findViewById(R.id.editTextPort);  // Initialize port EditText
        editTextMessage = findViewById(R.id.editTextMessage);
        buttonSend = findViewById(R.id.buttonSend);
        textViewResponse = findViewById(R.id.textViewResponse);

        // Set default values
        editTextServerAddress.setText("192.168.4.81");
        editTextPort.setText("12345");  // Default port

        // Allow networking on the main thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        buttonSend.setOnClickListener(v -> {
            String serverAddress = editTextServerAddress.getText().toString().trim();
            String portString = editTextPort.getText().toString().trim();
            String message = editTextMessage.getText().toString();

            // Validate inputs
            if (serverAddress.isEmpty()) {
                textViewResponse.setText("Error: Server address cannot be empty");
                return;
            }

            if (portString.isEmpty()) {
                textViewResponse.setText("Error: Port cannot be empty");
                return;
            }

            if (message.isEmpty()) {
                textViewResponse.setText("Error: Message cannot be empty");
                return;
            }

            try {
                int serverPort = Integer.parseInt(portString);
                String response = sendPlainTextMessage(serverAddress, serverPort, message);
                textViewResponse.setText(response);
            } catch (NumberFormatException e) {
                textViewResponse.setText("Error: Invalid port number");
            } catch (Exception e) {
                textViewResponse.setText("Error: " + e.getMessage());
            }
        });
    }

    private String sendPlainTextMessage(String serverAddress, int serverPort, String message) {
        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send the plaintext message
            out.println(message);

            // Receive and return the response
            return in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}