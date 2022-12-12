package com.example.aplicacaofmuscan;

//Atividade Prof. Eugenio
//imports para código funcionar
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.aplicacaofmuscan.databinding.ActivityMainBinding;
import com.example.aplicacaofmuscan.databinding.ActivityPrincipalBinding;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

//tela principal do app onde tem funcao de scan, e lanterna
public class principalActivity extends AppCompatActivity {

    //declarações
    ActivityPrincipalBinding binding;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth mAuth;
    Button btnScanner;
    private ToggleButton toggleFlashLightOnOff;
    boolean hasCamera = false;
    boolean flashOn = false;


    @Override //método on create que é executado quando inicializado a classe
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPrincipalBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        mAuth = FirebaseAuth.getInstance();
        setContentView(view);

        //define os botoes
        btnScanner = findViewById(R.id.btnScanner);
        toggleFlashLightOnOff = findViewById(R.id.toggle_flashlight);

        //metodo quando clica no botao logout
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut(); //chama metodo de logout
                Toast.makeText(getApplicationContext(), "Você deslogou com sucesso!", Toast.LENGTH_SHORT).show();//mostra mensagem
                finish(); //encerra
            }
        });

        //metodo quando clica no botao scanner
        btnScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });

        //verifica se tem flash no celular
        hasCamera = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);


        //metodo quando clica no botao flash
        toggleFlashLightOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hasCamera){ //verifica se tem camera
                    if(flashOn){ //verifica se flash ta ligado
                        flashOn = false; // marca como off
                        toggleFlashLightOnOff.setTextOff("OFF"); //muda texto para off
                        try {
                            flashLightOff(); //chama metodo que desliga flash
                        } catch (CameraAccessException e) {
                            e.printStackTrace(); //se der erro mostra
                        }
                    }else{
                        flashOn = true; //verifica se flash ta ligado
                        toggleFlashLightOnOff.setTextOn("ON"); //muda texto para on
                        try {
                            flashLightOn(); //liga flash
                        } catch (CameraAccessException e) {
                            e.printStackTrace(); //se der erro
                        }

                    }
                }else{ //mostra mensagem de nennum flash encontrado no dispositivo
                    Toast.makeText(getApplicationContext(),"Nenhum Flash instalado no seu dispositivo", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    //metodo para desligar flash
    private void flashLightOff() throws CameraAccessException{
        //pega a camera traseira do celualr e desliga o flash exibindo mensagem
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String cameraId = cameraManager.getCameraIdList()[0];
        cameraManager.setTorchMode(cameraId, false);
        Toast.makeText(getApplicationContext(),"Flash desligado", Toast.LENGTH_SHORT).show();
    }


    //metodo para ligar flash
    private void flashLightOn() throws CameraAccessException{
    //pega a camera traseira do celualr e liga o flash exibindo mensagem
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String cameraId = cameraManager.getCameraIdList()[0];
        cameraManager.setTorchMode(cameraId, true);
        Toast.makeText(getApplicationContext(),"Flash Ligado", Toast.LENGTH_SHORT).show();

            }

    //metodo para escanear codigo
    private void scanCode(){
        ScanOptions options = new ScanOptions(); //define options para scanner
        options.setPrompt("Volume +/- para ligar/desligar o Flash"); //mostra mensagem no scanner
        options.setBeepEnabled(true); //ativa o bipe
        options.setOrientationLocked(true); //trava a orientação do dispositivo
        options.setCaptureActivity(CaptureAct.class); //extende a classe captureact do zxing
        barLaucher.launch(options); //chama o scanner

    }


    //mostra o resultado do scan na tela e quando usuario clica ela fecha
    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result -> {

        if(result.getContents() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(principalActivity.this);
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            }).show();
        }

    });
}