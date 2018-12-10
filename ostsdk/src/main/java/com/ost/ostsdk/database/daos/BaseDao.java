package com.ost.ostsdk.database.daos;

import com.ost.ostsdk.models.entities.BaseEntity;

public interface BaseDao {
    void insert(BaseEntity baseEntity);

    void insertAll(BaseEntity... baseEntity);

    void delete(BaseEntity baseEntity);

    BaseEntity[] getByIds(String[] ids);

    BaseEntity getById(String id);

    void deleteAll();
}