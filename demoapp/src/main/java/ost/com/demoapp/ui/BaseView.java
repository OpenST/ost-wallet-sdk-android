/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui;

public interface BaseView {

    void showToastMessage(String text);

    void showToastMessage(int textRes);

    void goBack();

    void close();

    void showProgress(boolean show);

    void showProgress(boolean show, String progressString);
}

