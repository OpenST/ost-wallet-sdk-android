/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.workflow.createsession;

import ost.com.demoapp.ui.BasePresenter;

class CreateSessionPresenter extends BasePresenter<CreateSessionView> {

    private static final String LOG_TAG = "OstCreateSessionPresenter";


    private CreateSessionPresenter() {

    }

    static CreateSessionPresenter getInstance() {
        return new CreateSessionPresenter();
    }
}