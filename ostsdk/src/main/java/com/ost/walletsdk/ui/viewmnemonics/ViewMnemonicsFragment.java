/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui.viewmnemonics;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ost.walletsdk.R;
import com.ost.walletsdk.ui.BaseFragment;
import com.ost.walletsdk.ui.uicomponents.AppBar;
import com.ost.walletsdk.ui.uicomponents.OstH2Label;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.ContentConfig;
import com.ost.walletsdk.ui.uicomponents.uiutils.content.StringConfig;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewMnemonicsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewMnemonicsFragment extends BaseFragment {

    private OstH2Label mOstTextView1;
    private OstH2Label mOstTextView2;
    private String mnemonics;
    private JSONObject contentConfig = ContentConfig.getInstance().getStringConfig("view_mnemonics").optJSONObject("show_mnemonics");
    public ViewMnemonicsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CreateSessionFragment.
     * @param mnemonics mnemonics
     */
    public static ViewMnemonicsFragment newInstance(String mnemonics) {
        ViewMnemonicsFragment fragment = new ViewMnemonicsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        fragment.mnemonics = mnemonics;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onCreateViewDelegate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.ost_fragment_view_mnemonics, container, true);

        mOstTextView1 = viewGroup.findViewById(R.id.mnemonics_tv_1);
        mOstTextView1.setGravity(Gravity.START);
        mOstTextView2 = viewGroup.findViewById(R.id.mnemonics_tv_2);
        mOstTextView2.setGravity(Gravity.START);

        TextView labelHeading = viewGroup.findViewById(R.id.labelHeading);
        labelHeading.setText(
                StringConfig.instance(contentConfig.optJSONObject("title_label")).getString()
        );

        TextView labelSubHeading = viewGroup.findViewById(R.id.labelSubHeading);
        labelSubHeading.setText(
                StringConfig.instance(contentConfig.optJSONObject("info_label")).getString()
        );

        TextView labelCaution = viewGroup.findViewById(R.id.labelCaution);
        labelCaution.setText(
                StringConfig.instance(contentConfig.optJSONObject("bottom_label")).getString()
        );

        labelCaution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMnemonicsFile();
            }
        });

        AppBar appBar = AppBar.newInstance(getContext(), false);
        setUpAppBar(viewGroup, appBar);
        showMnemonics(mnemonics);
    }

    private void createMnemonicsFile() {
        int width = 200;
        int height = 150;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);

        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setTextSize(9.f);

        String[] arr = mnemonics.split(" ");

        float text_height = paint.measureText(arr[0]);
        float text_width = paint.measureText(arr[0]);
        float x_coord = ((bitmap.getWidth()/2) - 3 * text_width);
        float x_coord_offset = ((bitmap.getWidth()/2) + text_width);

        for (int i = 0; i < arr.length; i++) {
            if (i < 6) {
                if (i == 5) {
                    canvas.drawText(String.format("%s. %s", i + 1, arr[i]), x_coord, text_height * (i + 1), paint);
                } else {
                    canvas.drawText(String.format("%s. %s", i + 1, arr[i]), x_coord, text_height * (i + 1), paint);
                }
            } else {
                if (i == 11) {
                    canvas.drawText(String.format("%s. %s", i + 1, arr[i]), x_coord_offset, text_height * (i - 5), paint);
                } else {
                    canvas.drawText(String.format("%s. %s", i + 1, arr[i]), x_coord_offset, text_height * (i - 5), paint);
                }
            }
        }

        File f = new File(getBaseActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),"mnemonics.jpg");
        try {
            f.createNewFile();


            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

        showToastMessage(String.format("12 Words file at %s",f.getAbsolutePath()),true);
    }


    private void showMnemonics(String string) {
        String[] arr = string.split(" ");
        String str1 = "";
        String str2 = "";
        for(int i=0; i <arr.length; i++){
            if(i<6){
                if (i == 5) {
                    str1 += String.format("%s. %s", i + 1, arr[i]);
                } else {
                    str1 += String.format("%s. %s\n\n", i + 1, arr[i]);
                }
            } else {
                if (i == 11) {
                    str2 += String.format("%s. %s", i + 1, arr[i]);
                } else {
                    str2 += String.format("%s. %s\n\n", i + 1, arr[i]);
                }
            }
        }
        mOstTextView1.setText(str1);
        mOstTextView2.setText(str2);
    }

    public void showError(String message) {

    }
}