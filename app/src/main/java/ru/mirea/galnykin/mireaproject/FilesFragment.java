package ru.mirea.galnykin.mireaproject;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FilesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FilesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilesFragment newInstance(String param1, String param2) {
        FilesFragment fragment = new FilesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_files, container, false);
    }

    private EditText fileNameField;
    private EditText fileContentField;
    private Button loadButton;
    private FloatingActionButton floatingButton;

    private static final String AES_KEY = "galnykindabsbo09";

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fileNameField = view.findViewById(R.id.titleField);
        fileContentField = view.findViewById(R.id.contentField);
        loadButton = view.findViewById(R.id.loadButton);
        floatingButton = view.findViewById(R.id.floatingButton);

        loadButton.setOnClickListener(view1 -> loadFromFile());
        floatingButton.setOnClickListener(view1 -> createPopup());
    }

    private void createPopup() {
        View popupView = LayoutInflater.from(getActivity()).inflate(R.layout.popup, null);
        final PopupWindow popupWindow = new PopupWindow(popupView, 1000, 1000, true);
        TextView encrypted = popupView.findViewById(R.id.currentFileContent);
        encrypted.setText(new String(encrypt(fileContentField.getText().toString())));
        Button saveButton = popupView.findViewById(R.id.saveButton);
        saveButton.setOnClickListener(view1 -> {saveToFile(); popupWindow.dismiss();});

        if (fileNameField.getText().toString().length() < 1 || fileContentField.getText().toString().length() < 1) {
            saveButton.setEnabled(false);
        } else {
            saveButton.setEnabled(true);
        }

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private byte[] encrypt(String input) {
        try {
            SecretKey secretKey = new SecretKeySpec(AES_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.encode(cipher.doFinal(input.getBytes(StandardCharsets.UTF_8)), Base64.DEFAULT);
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return new byte[0];
    }

    private String decrypt(byte[] cipherText) {
        try {
            SecretKey secretKey = new SecretKeySpec(AES_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.decode(cipherText, Base64.DEFAULT));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return "";
    }

    private void saveToFile() {
        String content = fileContentField.getText().toString();
        FileOutputStream outputStream;
        try {
            outputStream = getContext().openFileOutput(fileNameField.getText().toString(), Context.MODE_PRIVATE);
            outputStream.write(encrypt(content));
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void loadFromFile() {
        FileInputStream fin = null;
        try {
            fin = getContext().openFileInput(fileNameField.getText().toString());
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String content = decrypt(bytes);
            fileContentField.setText(content);
        } catch (IOException ex) {
            Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (fin != null)
                    fin.close();
            } catch (IOException ex) {
                Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}