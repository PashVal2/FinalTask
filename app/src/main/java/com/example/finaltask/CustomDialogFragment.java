package com.example.finaltask;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
public class CustomDialogFragment extends DialogFragment {
    private ExitForMenu exitForMenu;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        exitForMenu = (ExitForMenu) context;
    }
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        assert getArguments() != null;
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        return builder
                .setTitle("Вы уверены что хотите выйти?")
                .setIcon(R.drawable.img1)
                .setMessage("Мы будем вас ждать")
                        .setPositiveButton("Oк", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                exitForMenu.IntentF();
                            }
                        })
                        .setNegativeButton("Oтмена", null)
                        .create();
    }
}
