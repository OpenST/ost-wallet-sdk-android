/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.auth;

import ost.com.demoapp.ui.BaseView;

interface OnBoardingView extends BaseView {

    void refreshToken();

    void goToDashBoard();

    void scanForEconomy();

    void showUsernameError(String errorString);

    void showPasswordError(String errorString);
}
