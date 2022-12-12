package com.example.aplicacaofmuscan;

//Atividade Prof. Eugenio
//imports para código funcionar
//Para esta Atividade da Faculdade foi utilizado libs do Google Auth, Firebase e Zxing para este código funcionar
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.aplicacaofmuscan.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;


//Classe principal que é executada na inicialização do APP
public class MainActivity extends AppCompatActivity {


    //declarações
    ActivityMainBinding binding;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth mAuth;

    @Override //método on create que é executado quando inicializado a classe
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();

        setContentView(view);

        mAuth = FirebaseAuth.getInstance();

        //configurações para o Google Sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("202596107817-090j3euudopg7anit9sp8chvt2jlijne.apps.googleusercontent.com")
                .requestEmail()
                .build();

        //Criação do Client do Google Sign In
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        //Ação para quando clicar no botão de Google Sign In
        binding.botaoGoogle.setOnClickListener(view1 -> {
                signIn(); //chama o método o declarado abaixo
        });


    }

    //método de login que irá fazer aparecer na tela o login do google
    private void signIn(){
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, 1);
    }


    //Método que fazz o Login propriamente dito
    private void loginComGoogle(String token){

        AuthCredential credential = GoogleAuthProvider.getCredential(token, null); //Cria a credencial
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful()){ //se for bem sucedido o login, exibe mensagem na tela e abre a tela inicial do scanner
                   Toast.makeText(getApplicationContext(), "Login com Google Efetuado com sucesso!", Toast.LENGTH_LONG).show();
                    abrePrincipal();
                    finish();
                }else{ //se não fizer login exibe a mensagem que nao foi possivel
                    Toast.makeText(getApplicationContext(), "Não foi possível efetuar seu Login com Google!", Toast.LENGTH_LONG).show();
                }
            }
        });



    }

    //metodo para abrir a tela principal do app
    private void abrePrincipal(){
        Intent intent = new Intent(getApplicationContext(), principalActivity.class);
        startActivity(intent);
    }


    //metodo que executa assim que o login é efetuado
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode==1){ //codigo definido lá em cima no metodo signIn()
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            try{
               GoogleSignInAccount conta = task.getResult(ApiException.class);
               loginComGoogle(conta.getIdToken());
            }catch(ApiException e){
                Toast.makeText(getApplicationContext(), "Nenhum usuário logado!", Toast.LENGTH_LONG).show();
            }

            }

    }



}