/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.ostwallet.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.ost.walletsdk.annotations.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ost.ostwallet.R;

/*
 * Provides simple access to base Android workflow dialogs
 */
public final class DialogFactory {

    public static Dialog createSimpleOkErrorDialog(Context context, String title, String message) {
        return createSimpleOkErrorDialog(context, title, message, null);
    }

    public static Dialog createSimpleOkErrorDialog(Context context, String title, String message, DialogInterface.OnClickListener onClickListener) {

        final Dialog dialog = new Dialog(context);
        // Include dialog.xml file
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.simple_dialog_view);
        dialog.getWindow().getAttributes().width = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().getAttributes().height = ViewGroup.LayoutParams.WRAP_CONTENT;

        TextView headingTextView = (TextView) dialog.findViewById(R.id.tv_heading);
        headingTextView.setText(title);

        TextView messageTextView = (TextView) dialog.findViewById(R.id.tv_sub_heading);
        messageTextView.setText(message);

        Button okButton = (Button) dialog.findViewById(R.id.buttonOk);
        // if decline button is clicked, close the custom dialog
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                if (null != onClickListener){
                    onClickListener.onClick(dialog, 0);
                }
                dialog.dismiss();
            }
        });
        return dialog;
    }

    public static Dialog createSimpleOkErrorDialog(Context context,
                                                   @StringRes int titleResource,
                                                   @StringRes int messageResource) {
        return createSimpleOkErrorDialog(context,
                titleResource,
                messageResource,
                null);
    }

    public static Dialog createSimpleOkErrorDialog(Context context,
                                                   @StringRes int titleResource,
                                                   @StringRes int messageResource,
                                                   DialogInterface.OnClickListener onClickListener) {

        return createSimpleOkErrorDialog(context,
                context.getString(titleResource),
                context.getString(messageResource),
                onClickListener);
    }

    public static Dialog createGenericErrorDialog(Context context, String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.dialog_error_title))
                .setMessage(message)
                .setNeutralButton(R.string.dialog_action_ok, null);
        return alertDialog.create();
    }

    public static Dialog createGenericErrorDialog(Context context, @StringRes int messageResource) {
        return createGenericErrorDialog(context, context.getString(messageResource));
    }

    public static ProgressDialog createProgressDialog(Context context, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        return progressDialog;
    }

    public static ProgressDialog createProgressDialog(Context context,
                                                      @StringRes int messageResource) {
        return createProgressDialog(context, context.getString(messageResource));
    }
}