package com.ost.ostsdk.database.daos;

import com.ost.ostsdk.models.entities.OstBaseEntity;

public interface OstBaseDao {
    void insert(OstBaseEntity baseEntity);

    void insertAll(OstBaseEntity... baseEntity);

    void delete(String id);

    OstBaseEntity[] getByIds(String[] ids);

    OstBaseEntity getById(String id);

    void deleteAll();

    OstBaseEntity[] getByParentId(String id);
}