/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.ui;

public interface BaseView {

    void showToastMessage(String text, Boolean isSuccess);

    void showToastMessage(int textRes, Boolean isSuccess);

    void goBack();

    void close();

    void showProgress(boolean show);

    void showProgress(boolean show, String progressString);
}

