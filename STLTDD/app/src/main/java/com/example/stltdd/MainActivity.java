package com.example.stltdd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    //reference view
    AutoCompleteTextView actvRole;
    TextInputEditText metEmail, metPassword;
    Button btLogin;
    FirebaseAuth firebaseAuth;
    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Code for Roles dropdown auto complete view
        actvRole = findViewById(R.id.actvRole);
        ArrayAdapter<CharSequence> rolesadapter = ArrayAdapter.createFromResource(this,R.array.roles,R.layout.dropdown_item_role);
        actvRole.setText(rolesadapter.getItem(0).toString(),false); //this make default value
        actvRole.setAdapter(rolesadapter);

        //assign variables
        constraintLayout = findViewById(R.id.constraintLayout);
        actvRole = findViewById(R.id.actvRole);     //dropdown ch???c v???
        metEmail = findViewById(R.id.metEmail);
        metPassword = findViewById(R.id.metPassword);
        btLogin = findViewById(R.id.btLogin);
        firebaseAuth = FirebaseAuth.getInstance();

        //activity
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = Objects.requireNonNull(metEmail.getText()).toString().trim();
                String password = Objects.requireNonNull(metPassword.getText()).toString().trim();
                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {      //?? email v?? password tr???ng
                    openSnackBar(2);
                }
                else if (TextUtils.isEmpty(email)) {        //?? email tr???ng
                    metEmail.setError("Vui l??ng ??i???n email");
                } else if (TextUtils.isEmpty(password)) {   //?? password tr???ng
                   openSnackBar(1);
                } else {                                    //c??? 2 ?? ?????u ???? nh???p, chuy???n sang b?????c x??c th???c v?? login b??n d?????i
                    loginToApp(email,password);
                }
            }
            private void openSnackBar(int count) {      //h??m g???i snackbar n???u password tr???ng | (email, password) tr???ng
                Snackbar.make(constraintLayout,count == 1 ? "Vui l??ng ??i???n m???t kh???u" : "Vui l??ng ??i???n email v?? m???t kh???u",Snackbar.LENGTH_SHORT)
                        .setAction("????ng", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {}
                        }).show();
            }
        });
    }

    String real_role = "";
    private void loginToApp(String email, String password) {
        //login v??o v?? x??c th???c actv_role c?? kh???p v???i real_role ?
        //n???u kh??ng th?? out ra v?? hi???n ra snackbar th??ng b??o

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
                    Query query = databaseReference.orderByChild("userId").equalTo(firebaseUser.getUid());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            String actv_role = actvRole.getText().toString().trim();
                            if (actv_role.equals("Sinh vi??n")) {
                                actv_role = "sinhvien";
                            } else if (actv_role.equals("Ph??? huynh")) {
                                actv_role = "phuhuynh";
                            } else {
                                actv_role = "admin";
                            }
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                GeneralUser gu = dataSnapshot.getValue(GeneralUser.class);
                                real_role = gu.getRole();
                                Log.d("kiemtra role ",real_role);
                                if (real_role.equals(actv_role)) {      //n???u ????ng real_role v?? actv_role kh???p nhau th?? s??? ????ng nh???p v?? chuy???n v??o m??n h??nh ????ng nh???p
                                    loginToRole(real_role);
                                } else {                                //n???u sai th?? hi???n snackbar th??ng b??o l???i v?? signout ra
                                    openSnackBar("sai th??ng tin ????ng nh???p");
                                    firebaseAuth.signOut();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    }
                    );
                } else {
                    String errorText = Objects.requireNonNull(task.getException()).getMessage();
                    if (Objects.requireNonNull(errorText).contains("badly formatted")) {
                        openSnackBar("nh???p sai ?????nh d???ng email");
                    } else if (Objects.requireNonNull(errorText).contains("no user")) {
                        openSnackBar("nh???p sai email (kh??ng c?? email n??y)");
                    } else if (Objects.requireNonNull(errorText).contains("The password is invalid")){
                        openSnackBar("nh???p sai m???t kh???u");
                    } else if (Objects.requireNonNull(errorText).contains("blocked all requested")){
                        openSnackBar("nh???p qu?? nhi???u l???n. Vui l??ng ch??? trong gi??y l??t");
                    } else if (Objects.requireNonNull(errorText).contains("network error")) {
                        openSnackBar("kh??ng c?? k???t n???i internet");
                    } else {
                        openSnackBar(errorText);
                    }
                }
            }
            private void openSnackBar(String text) {
                Snackbar.make(constraintLayout,(text.contains("nh???p") || text.contains("kh??ng")) ? ("B???n " + text) : (text), Snackbar.LENGTH_SHORT)
                        .setAction("????ng", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {}
                        }).show();
            }
            private void loginToRole(String result) {           //login v?? chuy???n v??o m??n h??nh ????ng nh???p
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                switch (result) {
                    case "sinhvien":
                        intent = new Intent(MainActivity.this, HomeSVActivity.class);
                        break;
                    case "phuhuynh":
                        intent = new Intent(MainActivity.this, HomePHActivity.class);
                        break;
                    case "admin":
                        intent = new Intent(MainActivity.this, HomeADActivity.class);
                    default:
                        break;
                }
                startActivity(intent);
            }
        });

    }
}