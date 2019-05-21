/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package ost.com.demoapp.util;

import ost.com.demoapp.database.OstAppDatabase;
import ost.com.demoapp.entity.OstLogEvent;

public class DBLog {
    public void log(OstLogEvent ostLogEvent) {
        OstAppDatabase.getDatabase().ostLogEvent().insert(ostLogEvent);
    }

    public OstLogEvent[] getWalletEvents(int noOfEvents) {
        return OstAppDatabase.getDatabase().ostLogEvent().getLogs(noOfEvents);
    }
}