package org.racenet.racesow.models;

import org.racenet.racesow.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
public class RecoveryPreference extends EditTextPreference {

	OnClickListener requestListener;
	OnClickListener recoveryListener;
	EditText code;
	
	/**
	 * Constructor
	 * 
	 * @param Context context
	 */
	public RecoveryPreference(Context context) {
		
		super(context);
	}
	
	/**
	 * Constructor
	 * 
	 * @param Context context
	 * @param AttributeSet attrs
	 */
	public RecoveryPreference(Context context, AttributeSet attrs) {
		 
		super(context, attrs);
	}
		 
	/**
	 * Constructor
	 * 
	 * @param Context context
	 * @param AttributeSet attrs
	 * @param int defStyle
	 */
	public RecoveryPreference(Context context, AttributeSet attrs, int defStyle) {
		 
		super(context, attrs, defStyle);
	} 
	
	/**
	 * Set the listener for forgot password button
	 * 
	 * @param OnClickListener listener
	 */
	public void setRequestListener(OnClickListener listener) {
		
		this.requestListener = listener;
	}
	
	/**
	 * Set the listener for the positive button
	 * 
	 * @param OnClickListener listener
	 */
	public void setRecoveryListener(OnClickListener listener) {
		
		this.recoveryListener = listener;
	}
	 
	/**
	 * Get the entered password
	 * 
	 * @return String
	 */
	public String getCode() {
		 
		return code.getText().toString().trim();
	}
	 
	@Override
	/**
	 * Create the alert dialog with password and confirmation field.
	 * Validates the confirmation and calls the listener
	 */
	public void onClick() {
		
		new AlertDialog.Builder(getContext())
			.setTitle("Password recovery")
			.setMessage("Did you forget your password or want to enter a revocery code?")
			.setCancelable(true)
			.setPositiveButton("Forgot password", requestListener)
			.setNegativeButton("Enter code", new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					
					code = new EditText(getContext());
					new AlertDialog.Builder(getContext())
						.setView(code)
						.setTitle("Password recovery")
						.setMessage("Enter your recovery code.")
						.setCancelable(true)
						.setPositiveButton("OK", recoveryListener)
						.setNegativeButton("Cancel", null)
						.show();
				}
			})
			.show();
	}
}
