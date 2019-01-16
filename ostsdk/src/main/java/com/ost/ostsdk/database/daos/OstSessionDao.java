package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.OstBaseEntity;
import com.ost.ostsdk.models.entities.OstTokenHolderSession;

@Dao
public abstract class OstSessionDao implements OstBaseDao {

    public void insert(OstBaseEntity baseEntity) {
        this.insert((OstTokenHolderSession) baseEntity);
    }

    public void insertAll(OstBaseEntity... baseEntity) {
        this.insertAll((OstTokenHolderSession[]) baseEntity);
    }

    @Insert
    public abstract void insert(OstTokenHolderSession ostTokenHolderSession);

    @Insert
    public abstract void insertAll(OstTokenHolderSession... ostTokenHolderSession);

    @Query("DELETE FROM token_holder_session WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM token_holder_session WHERE id IN (:ids)")
    public abstract OstTokenHolderSession[] getByIds(String[] ids);

    @Query("SELECT * FROM token_holder_session WHERE id=:id")
    public abstract OstTokenHolderSession getById(String id);

    @Query("DELETE FROM token_holder_session")
    public abstract void deleteAll();

    @Query("SELECT * FROM token_holder_session WHERE parent_id=:id")
    public abstract OstTokenHolderSession[] getByParentId(String id);
}
