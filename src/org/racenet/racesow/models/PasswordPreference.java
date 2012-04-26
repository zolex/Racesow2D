package org.racenet.racesow.models;

import org.racenet.racesow.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Preference which shows a dialog with password and confirmation
 * 
 * @author soh#zolex
 *
 */
public class PasswordPreference extends EditTextPreference {

	OnClickListener listener;
	EditText pass;
	String message;
	
	/**
	 * Constructor
	 * 
	 * @param Context context
	 */
	public PasswordPreference(Context context) {
		
		super(context);
	}
	
	/**
	 * Constructor
	 * 
	 * @param Context context
	 * @param AttributeSet attrs
	 */
	public PasswordPreference(Context context, AttributeSet attrs) {
		 
		super(context, attrs);
	}
		 
	/**
	 * Constructor
	 * 
	 * @param Context context
	 * @param AttributeSet attrs
	 * @param int defStyle
	 */
	public PasswordPreference(Context context, AttributeSet attrs, int defStyle) {
		 
		super(context, attrs, defStyle);
	} 
	
	/**
	 * Set the listener for the positive button
	 * 
	 * @param OnClickListener listener
	 */
	public void setListener(OnClickListener listener) {
		
		this.listener = listener;
	}
	
	/**
	 * Set the dialog message
	 * 
	 * @param OnClickListener listener
	 */
	public void setMessage(String message) {
		 
		this.message = message;
	}
	 
	/**
	 * Get the entered password
	 * 
	 * @return String
	 */
	public String getPassword() {
		 
		return pass.getText().toString().trim();
	}
	 
	@Override
	/**
	 * Create the alert dialog with password and confirmation field.
	 * Validates the confirmation and calls the listener
	 */
	public void onClick() {
		
		RelativeLayout layout = (RelativeLayout)View.inflate(getContext(), R.layout.newpass, null);
		pass = (EditText)layout.findViewById(R.id.field_pass);
		pass.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
		final EditText conf = (EditText)layout.findViewById(R.id.field_conf);
		conf.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		conf.setTransformationMethod(PasswordTransformationMethod.getInstance());
		final TextView confError = (TextView)layout.findViewById(R.id.conf_error);
		final AlertDialog change = new AlertDialog.Builder(getContext())
			.setView(layout)
			.setMessage(this.message)
			.setCancelable(true)
			.setPositiveButton("Change", this.listener)
			.setNegativeButton("Cancel", null)
			.create();
		
		TextWatcher listener = new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				boolean valid;
				String password = pass.getText().toString().trim();
				String confirmation = conf.getText().toString().trim();
				if (password.length() > 0 &&
					password.equals(confirmation)) {
					
					valid = true;
					confError.setVisibility(View.GONE);
					
				} else {
					
					valid = false;
					confError.setVisibility(View.VISIBLE);
				}
				
				change.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(valid);
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			public void afterTextChanged(Editable s) {}
		};
		
		pass.addTextChangedListener(listener);
		conf.addTextChangedListener(listener);
		
		change.show();
		change.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
	}
}
