package com.example.stltdd;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import org.jetbrains.annotations.NotNull;
import java.util.Objects;

public class ChiTietMHADFragment extends Fragment {
    DatabaseReference databaseReferenceCourses;
    String courseId;

    public ChiTietMHADFragment() {
        // Required empty public constructor
    }

    public ChiTietMHADFragment(String courseId) {
        this.courseId = courseId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chitietmhad,container,false);
        ImageButton imgbtEditCourse = view.findViewById(R.id.imgbtEditCourse);
        TextView tvCourseName = view.findViewById(R.id.tvCourseName);
        TextView tvCourseId = view.findViewById(R.id.tvCourseId);
        TextView tvCourseCredits = view.findViewById(R.id.tvCourseCredits);
        TextView tvCourseFaculty = view.findViewById(R.id.tvCourseFaculty);
        TextView tvCourseSpecilization = view.findViewById(R.id.tvCourseSpecilization);
        TextView tvCourseTimeTable = view.findViewById(R.id.tvCourseTimeTable);
        TextView tvCourseStartDate = view.findViewById(R.id.tvCourseStartDate);
        TextView tvCourseEndDate = view.findViewById(R.id.tvCourseEndDate);
        TextView tvCourseFee = view.findViewById(R.id.tvCourseFee);
        databaseReferenceCourses = FirebaseDatabase.getInstance().getReference("Courses");
        Query query = databaseReferenceCourses.orderByChild("course_id").equalTo(courseId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {      //hi???n th??? th??ng tin m??n h???c l??n fragment n??y
                    Courses course = dataSnapshot.getValue(Courses.class);
                    tvCourseName.setText(Objects.requireNonNull(course).getName());
                    tvCourseId.setText(course.getCourse_id());
                    tvCourseCredits.setText(String.valueOf(course.getCredits()));
                    tvCourseFaculty.setText(course.getFaculty());
                    tvCourseSpecilization.setText(course.getSpecilization());
                    tvCourseTimeTable.setText(course.getTimetable());
                    tvCourseStartDate.setText(course.getStartdate());
                    tvCourseEndDate.setText(course.getEnddate());
                    tvCourseFee.setText(String.valueOf(course.getFee()));
                }

                imgbtEditCourse.setOnClickListener(new View.OnClickListener() {     //x??? l?? s??? ki???n nh???n n??t s???a
                    @Override
                    public void onClick(View v) {
                        //region code tao giao di???n dialog
                        Dialog dialog = new Dialog(ChiTietMHADFragment.super.getContext());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_course_edit);
                        Window window = dialog.getWindow();
                        if (window == null) {
                            return;
                        }
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        WindowManager.LayoutParams windowAttribute = window.getAttributes();
                        windowAttribute.gravity = Gravity.CENTER;
                        window.setAttributes(windowAttribute);
                        dialog.setCancelable(false);
                        //endregion

                        //khai b??o bi???n
                        TextView tvCourseName = dialog.findViewById(R.id.tvCourseName);
                        TextView tvCourseId = dialog.findViewById(R.id.tvCourseId);
                        TextView tvCourseCredits = dialog.findViewById(R.id.tvCourseCredits);
                        TextView tvCourseFaculty = dialog.findViewById(R.id.tvCourseFaculty);
                        TextView tvCourseSpecilization = dialog.findViewById(R.id.tvCourseSpecilization);
                        TextView tvCourseFee = dialog.findViewById(R.id.tvCourseFee);
                        EditText etCourseTimeTable = dialog.findViewById(R.id.etCourseTimeTable);
                        Button btCancel = dialog.findViewById(R.id.btCancel);
                        Button btEdit = dialog.findViewById(R.id.btEdit);

                        //hi???n th??? th??ng tin m??n h???c l??n fragment
                        Query query1 = databaseReferenceCourses.orderByChild("course_id").equalTo(courseId);
                        query1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Courses course = dataSnapshot.getValue(Courses.class);
                                    tvCourseName.setText(course.getName());
                                    tvCourseId.setText(course.getCourse_id());
                                    tvCourseCredits.setText(String.valueOf(course.getCredits()));
                                    tvCourseFaculty.setText(course.getFaculty());
                                    tvCourseSpecilization.setText(course.getSpecilization());
                                    tvCourseFee.setText(String.valueOf(course.getFee()));
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            }
                        });

                        //set s??? ki???n cho dialog
                        //s??? ki???n ???n cancel
                        btCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        //s??? ki???n ???n edit
                        btEdit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String courseTimeTable = etCourseTimeTable.getText().toString().trim();
                                if (courseTimeTable.isEmpty()) {    //n???u r???ng
                                    Toast.makeText(ChiTietMHADFragment.super.getContext(), "Khung nh???p l???ch h???c tr???ng, l???ch h???c v???n nh?? c?? !", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {                            //n???u ko r???ng
                                    if (courseTimeTable.length() == 21) {           //n???u ????? 21 k?? t???
                                        String infoTiet = courseTimeTable.substring(0, 5);       //ki tu thu 5 phai la so
                                        String infoComma = courseTimeTable.substring(6, 8);      //ki tu thu 8 phai la so
                                        String infoSoTiet = courseTimeTable.substring(9, 14);
                                        String infoComma2 = courseTimeTable.substring(14, 16);
                                        String infoThu = courseTimeTable.substring(16, 20);      //ki tu thu 20 phai la so
                                        boolean check2Commma = infoComma.equals(", ") && infoComma2.equals(", ");
                                        boolean checkTietThu = infoTiet.equals("Ti???t ") && infoSoTiet.equals(" ti???t") && infoThu.equals("th??? ");
                                        boolean checkIsNumber = TextUtils.isDigitsOnly(courseTimeTable.substring(5, 6))
                                                && TextUtils.isDigitsOnly(courseTimeTable.substring(8, 9))
                                                && TextUtils.isDigitsOnly(courseTimeTable.substring(20, 21));
                                        Log.d("kiemtra ky tu thu 5, 8, 20 la so ",courseTimeTable.substring(5, 5));

                                        if (check2Commma && checkTietThu && checkIsNumber) {        //n???u t???t c??? c??c ??k tr??n ????ng
                                            DatabaseReference databaseReferenceEditCourses = FirebaseDatabase.getInstance().getReference("Courses").child(courseId);
                                            databaseReferenceEditCourses.child("timetable").setValue(courseTimeTable);
                                            Toast.makeText(ChiTietMHADFragment.super.getContext(), "???? s???a l???ch h???c !", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();

                                            AppCompatActivity appCompatActivity = (AppCompatActivity) ChiTietMHADFragment.super.getContext();
                                            ChiTietMHADFragment chiTietMHADFragment = new ChiTietMHADFragment(courseId);
                                            appCompatActivity.getSupportFragmentManager().beginTransaction().replace(R.id.frlListCourses, chiTietMHADFragment).addToBackStack(null).commit();
                                        } else {                                                    //n???u sai
                                            Toast.makeText(ChiTietMHADFragment.super.getContext(), "S???a l???ch h???c ph???i ????ng ?????nh d???ng nh?? b??n d?????i (x,y,z l?? s???) !", Toast.LENGTH_SHORT).show();
                                            Log.d("kiemtra abc ", courseTimeTable.substring(0,0));
                                        }
                                    } else {                    //n???u ko ph???i 21 k?? t???
                                        Toast.makeText(ChiTietMHADFragment.super.getContext(), "aaS???a l???ch h???c ph???i ????ng ?????nh d???ng nh?? b??n d?????i (x,y,z l?? s???) !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                        dialog.show();
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        return view;
    }
}