package org.etma.main.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;

import org.etma.main.R;
import org.etma.main.db.Member;
import org.etma.main.db.MemberRelationship;
import org.etma.main.db.UserOauth;
import org.etma.main.helpers.Util;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class AddMemberActivity extends AppCompatActivity {

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;

    @BindView(R.id.full_name)
    EditText fullName;

    @BindView(R.id.full_name_wrapper)
    TextInputLayout fullNameWrapper;

    @BindView(R.id.id_number)
    EditText idNumber;

    @BindView(R.id.id_number_wrapper)
    TextInputLayout idNumberWrapper;

    @BindView(R.id.cell_phone)
    EditText cellPhone;

    @BindView(R.id.cell_phone_wrapper)
    TextInputLayout cellPhoneWrapper;

    @BindView(R.id.email_address)
    EditText emailAddress;

    @BindView(R.id.email_address_wrapper)
    TextInputLayout emailAddressWrapper;

    private static EditText dateOfBirth;

    @BindView(R.id.date_of_birth_wrapper)
    TextInputLayout dateOfBirthWrapper;

    private long relationshipId;

    private Realm realm;
    private MemberRelationship relationship;
    private UserOauth user;

    private String action;

    private long memberId;

    private Member memberToEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);

        dateOfBirth = findViewById(R.id.date_of_birth);

        realm = Realm.getDefaultInstance();

        user = realm.where(UserOauth.class).findFirst();

        Intent intent = getIntent();

        try{
            action = intent.getExtras().getString("action");
        }catch (Exception e){
            action = "CREATE";
        }

        relationshipId = intent.getExtras().getLong("id");

        relationship = realm.where(MemberRelationship.class).equalTo("id", relationshipId).findFirst();

        if (action.equals("EDIT")){

            memberId = intent.getExtras().getLong("memberId");

            memberToEdit = realm.where(Member.class).equalTo("id", memberId).findFirst();

            fullName.setText(memberToEdit.getFullName());
            cellPhone.setText(memberToEdit.getCellphone());
            emailAddress.setText(memberToEdit.getEmailAddress());

            setTitle("Update Member");

        }

    }

    @OnClick(R.id.date_of_birth)
    public void onDateOfBirthTouch(){

        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @OnClick(R.id.add)
    public void onAddTouch(){

        if (action.equals("CREATE")){
            save();
        }else{
            update();
        }

    }

    private void save(){

        String memberName = fullName.getText().toString().trim();
        String memberPhone = cellPhone.getText().toString().trim();
        String memberEmail = emailAddress.getText().toString().trim();

        if (memberName.equals("") || memberName.length() < 2){
            fullNameWrapper.setErrorEnabled(true);
            fullNameWrapper.setError("Please enter a valid name");
        } else if (memberPhone.equals("") || memberPhone.length() != 9) {
            cellPhoneWrapper.setErrorEnabled(true);
            cellPhoneWrapper.setError("Please enter a valid phone number (e.g 7xxxxxxxx)!");
        }else if (!Util.isValidEmail(memberEmail)){
            emailAddressWrapper.setErrorEnabled(true);
            emailAddressWrapper.setError("Please enter a valid email address!");
        }else{

            RealmResults<Member> members = realm.where(Member.class).findAll();

            Member member = new Member();

            long lastMemberId;

            if (members.isEmpty()){
                member.setId(0);
            }else{
                lastMemberId = members.last().getId();
                member.setId( lastMemberId + 1);
            }

            member.setCustom1("");
            member.setCustom2("");
            member.setCustom3("");
            member.setActive("true");
            member.setLastModifierUserId("0");
            member.setDeletionTime(Util.getCurrentDate());
            member.setEmailAddress(memberEmail);
            member.setIsDeleted("false");
            member.setMemberId("0");
            member.setCreatorUserId(""+user.getUserId());
            member.setMemberRelationshipId(relationship.getRelationshipId());
            member.setCellphone("254" +memberPhone);
            member.setUserId(""+user.getUserId());
            member.setDeleterUserId("0");
            member.setCreationTime(Util.getCurrentDate());
            member.setFullName(memberName);
            member.setLastModificationTime(Util.getCurrentDate());
            member.setAmountPledged(0);
            member.setStatus("Draft");

            if (realm.isInTransaction()){
                realm.cancelTransaction();
            }

            realm.beginTransaction();
            realm.copyToRealm(member);
            realm.commitTransaction();

            Intent intent = new Intent(this, ManageMembersActivity.class);
            intent.putExtra("origin", "MAIN");
            startActivity(intent);

        }

    }

    private void update(){

        String memberName = fullName.getText().toString().trim();
        String memberPhone = cellPhone.getText().toString().trim();
        String memberEmail = emailAddress.getText().toString().trim();

        if (memberName.equals("") || memberName.length() < 2){
            fullNameWrapper.setErrorEnabled(true);
            fullNameWrapper.setError("Please enter a valid name");
        } else if (!Util.isValidMobile(memberPhone)) {
            cellPhoneWrapper.setErrorEnabled(true);
            cellPhoneWrapper.setError("Please enter a valid phone number!");
        }else if (!Util.isValidEmail(memberEmail)){
            emailAddressWrapper.setErrorEnabled(true);
            emailAddressWrapper.setError("Please enter a valid email address!");
        }else{

            if (realm.isInTransaction()){
                realm.cancelTransaction();
            }

            realm.beginTransaction();

            Member existing = realm.where(Member.class).equalTo("id", memberToEdit.getId()).findFirst();

            if (existing != null){

                existing.setCustom1("");
                existing.setCustom2("");
                existing.setCustom3("");
                existing.setActive("true");
                existing.setLastModifierUserId("0");
                existing.setDeletionTime(Util.getCurrentDate());
                existing.setEmailAddress(memberEmail);
                existing.setIsDeleted("false");
                existing.setMemberId(memberToEdit.getMemberId());
                existing.setCreatorUserId(""+user.getUserId());
                existing.setMemberRelationshipId(relationship.getRelationshipId());
                existing.setCellphone(memberPhone);
                existing.setUserId(""+user.getUserId());
                existing.setDeleterUserId("0");
                existing.setCreationTime(Util.getCurrentDate());
                existing.setFullName(memberName);
                existing.setLastModificationTime(Util.getCurrentDate());
                existing.setAmountPledged(0);
                existing.setStatus("UPDATED");

                realm.copyToRealmOrUpdate(existing);
                realm.commitTransaction();

                Intent intent = new Intent(this, ManageMembersActivity.class);
                intent.putExtra("origin", "MAIN");
                startActivity(intent);

            }

        }

    }

    public static class DatePickerFragment extends DialogFragment implements
            DatePickerDialog.OnDateSetListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {

            String datePicked = (month + 1) + "/" + day + "/" + year;
            updateView(datePicked);
        }
    }

    private static void updateView(String date) {
        dateOfBirth.setText(date);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.chatting_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity( new Intent(this, EndTimeMessageMainActivity.class));
    }

}
