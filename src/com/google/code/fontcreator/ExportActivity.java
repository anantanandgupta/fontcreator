package com.google.code.fontcreator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;




public class ExportActivity extends Activity{
	
	private Button sendButton;
	private EditText emailField; 
	private Spinner exportSpinner;
	private Context context;
	
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.exportactivitylayout);
			sendButton = (Button) findViewById(R.id.exportsendbutton);
			emailField = (EditText) findViewById(R.id.exportemailaddressbox);
			exportSpinner = (Spinner) findViewById(R.id.exportspinner);
			final String fontFiles[] = FontUtils.getFonts(context);
			
			sendButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View view) {
					// Selection of the spinner
					// Application of the Array to the Spinner
					ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, fontFiles);
					spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down vieww
					exportSpinner.setAdapter(spinnerArrayAdapter);
					
					String emailTo = "";
					emailTo = emailField.getText().toString();
					
					String emailCC = "";
					String subject = "Your Android Font!";
					String emailText = "In the attachment you will find your TTF file. Love, the FontMaker Team.";
					
					
					String attachment = "";
					List<String> filePaths = new ArrayList<String>();
					attachment = exportSpinner.getSelectedItem().toString();
					
					String attachmentPath = "";
					attachmentPath = (FontUtils.getFont(attachment, context)).getAbsolutePath();
					
					filePaths.add(attachmentPath);
					
					
					
					email(context, emailTo, emailCC, subject, emailText, filePaths);
					
										
					
				}

			});
			
		}
		
			
			

		
public static void email(Context context, String emailTo, String emailCC,
	    String subject, String emailText, List<String> filePaths)
	{
	    //need to "send multiple" to get more than one attachment
	    final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
	    emailIntent.setType("text/plain");
	    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, 
	        new String[]{emailTo});
	    emailIntent.putExtra(android.content.Intent.EXTRA_CC, 
	        new String[]{emailCC});
	    emailIntent.putExtra(Intent.EXTRA_SUBJECT, new String[]{subject});
		emailIntent.putExtra(Intent.EXTRA_TEXT, new String[]{emailText});
	    //has to be an ArrayList
	    ArrayList<Uri> uris = new ArrayList<Uri>();
	    //convert from paths to Android friendly Parcelable Uri's
	    for (String file : filePaths)
	    {
	        File fileIn = new File(file);
	        Uri u = Uri.fromFile(fileIn);
	        uris.add(u);
	    }
	    emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
	    context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}
}