/*
 * Copyright 2019 OST.com Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 */

package com.ost.walletsdk.models;

import com.ost.walletsdk.models.entities.OstRule;

public interface OstRuleModel extends OstBaseModel {
    @Override
    OstRule getEntityById(String id);

    @Override
    OstRule[] getEntitiesByParentId(String id);
}
