/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.ui.dashboard;

import ost.com.demoapp.entity.Transaction;
import ost.com.demoapp.ui.BaseView;

interface WalletView extends BaseView {
    void updateBalance(String balance, String usdBalance);
    void notifyDataSetChanged();
    void openTransactionView(Transaction transaction);
}