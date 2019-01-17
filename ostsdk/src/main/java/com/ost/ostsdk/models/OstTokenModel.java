package com.ost.ostsdk.models;

import com.ost.ostsdk.models.entities.OstToken;

public interface OstTokenModel {

    OstToken insert(OstToken ostToken);

    OstToken getTokenById(String id);
}