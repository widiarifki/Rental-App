package com.widiarifki.outdoorrent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by Widia Rifkianti on 16/06/2016.
 */
public class AlertDialogFragment extends DialogFragment {

    /** called when createa the dialog **/
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Maaf")
                .setMessage("Terjadi Error. Silahkan coba lagi")
                .setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        return  dialog;
    }
}
