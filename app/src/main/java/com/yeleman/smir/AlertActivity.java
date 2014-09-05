package com.yeleman.smir;

import android.app.Activity;
import android.telephony.SmsManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class AlertActivity extends Activity {

    private Spinner spinner;
    private Button btnSubmit;

   // protected EditText input_password;
   // protected EditText input_case;
   // protected EditText input_confirmed;
   // protected EditText input_death;

   private static final String[] order_diseases = {
        Constants.ebola, Constants.acute_flaccid_paralysis,
        Constants.influenza_a_h1n1, Constants.cholera,
        Constants.red_diarrhea, Constants.measles, Constants.yellow_fever,
        Constants.neonatal_tetanus, Constants.meningitis,
        Constants.rabies, Constants.acute_measles_diarrhea,
        Constants.other_notifiable_disease
    };

    @Override
   public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert);

         addItemsOnSpinner();
	     addListenerOnButton();

    }
        // add items into spinner dynamically
   public void addItemsOnSpinner() {

       spinner = (Spinner) findViewById(R.id.spinner);
       List<String> list = new ArrayList<String>();
       list.add("EBOLA");
       list.add("PFA");
       list.add("Grippe A H1N1");
       list.add("Choléra");
       list.add("Diarrhée rouge");
       list.add("Rougeole");
       list.add("Fièvre jaune");
       list.add("TNN");
       list.add("Méningite");
       list.add("Rage");
       list.add("Diarrhée sévère rougeole");
       list.add("Autre MADO");
       ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
          android.R.layout.simple_spinner_item, list);
       dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       spinner.setAdapter(dataAdapter);
    }


   public void addListenerOnButton() {

        spinner = (Spinner) findViewById(R.id.spinner);
        btnSubmit = (Button) findViewById(R.id.button);

        btnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               if (checkAllDataOK()) {
                  String sms_str = getSMSString();
                  Log.i("SMIR SMS-OUT", sms_str);
                  boolean succeeded = submitText(sms_str);
                  if (succeeded) {
                     resetAllFields();
                  }
               }
            }
        });
   }

   protected void resetAllFields() {
      // this.input_password.setText(null);
      // this.input_case.setText(null);
      // this.input_confirmed.setText(null);
      // this.input_death.setText(null);
   }

   protected boolean checkAllDataOK() {

      final EditText input_case = (EditText) findViewById(R.id.input_case);
      final EditText input_confirmed = (EditText) findViewById(R.id.input_confirmed);
      final EditText input_death = (EditText) findViewById(R.id.input_death);
      final EditText input_password = (EditText) findViewById(R.id.input_password);
      Vector<String> errors = new Vector<String>();

      SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
      String username = sharedPrefs.getString("username", "null");
      if (username.isEmpty()){
         errors.add("L'identifiant doit être renseigné dans le paramètre.");
      }

       if (!SharedChecks._check_not_empty(input_case)){
           errors.add("Champs cas suspect est obligatoire");
           displayErrorPopup(errors.get(0));
           return false;
       }
       if (!SharedChecks._check_not_empty(input_confirmed)){
           errors.add("Champs confirmé est obligatoire");
           displayErrorPopup(errors.get(0));
           return false;
       }
       if (!SharedChecks._check_not_empty(input_death)){
           errors.add("Champs décès est obligatoire");
           displayErrorPopup(errors.get(0));
           return false;
       }
       if (!SharedChecks._check_not_empty(input_password)){
           errors.add("Mot de passe est obligatoire");
           displayErrorPopup(errors.get(0));
           return false;
       }
       if (!SharedChecks._check_not_valid(input_case, input_confirmed)){
           errors.add("Nombre de confirmé ne doit pas être supérieur au nombre cas suspect.");
       }
      if (!SharedChecks._check_not_valid(input_confirmed, input_death)){
           errors.add("Nombre de décès ne doit pas être supérieur au nombre confirmé.");
       }
      if (errors.size() > 0) {
         // display Error Message
         displayErrorPopup(errors.get(0));
         return false;
      } else {
         return true;
      }
   }

   protected boolean submitText(String message) {
        // preferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String phoneNumber = sharedPrefs.getString("serverPhoneNumber", "null");
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), getString(R.string.notif_sms_sent), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getString(R.string.notif_sms_sent), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return false;
        }
        return true;
    }

   protected String getSMSString() {
       SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
       String username = sharedPrefs.getString("username", "null");
       String disease = String.valueOf(order_diseases[spinner.getSelectedItemPosition()]);

       final EditText input_case = (EditText) findViewById(R.id.input_case);
       final EditText input_confirmed = (EditText) findViewById(R.id.input_confirmed);
       final EditText input_death = (EditText) findViewById(R.id.input_death);
       final EditText input_password = (EditText) findViewById(R.id.input_password);

       // smir alert username password code suspected confirmed deaths
       String sms_text = Constants.KEYWORD;
       sms_text += Constants.SPACER;

       sms_text += Constants.KEYALERT;
       sms_text += Constants.SPACER;

       // USERNAME
       sms_text += username;
       sms_text += Constants.SPACER;

       // PASSWORD
       sms_text += input_password.getText().toString();
       sms_text += Constants.SPACER;

       sms_text += disease;
       sms_text += Constants.SPACER;

       sms_text += input_case.getText().toString();
       sms_text += Constants.SPACER;

       sms_text += input_confirmed.getText().toString();
       sms_text += Constants.SPACER;

       sms_text += input_death.getText().toString();
       sms_text += Constants.SPACER;

       return sms_text.trim();
   }

    protected void displayErrorPopup(String message) {
        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Erreur !");
        helpBuilder.setMessage("Impossible d'envoyer l'alerte :\n\n" + message + "\n\nVous devez corriger et re-envoyer.");
        helpBuilder.setIcon(R.drawable.ic_launcher);
        helpBuilder.setPositiveButton("Fermer et corriger",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                    }
                });

        // Remember, create doesn't show the dialog
        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }

}
