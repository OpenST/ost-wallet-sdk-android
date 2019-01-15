package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.BaseEntity;
import com.ost.ostsdk.models.entities.Token;

@Dao
public abstract class TokenDao implements BaseDao {

    public void insert(BaseEntity baseEntity) {
        this.insert((Token) baseEntity);
    }

    public void insertAll(BaseEntity... baseEntity) {
        this.insertAll((Token[]) baseEntity);
    }

    @Insert
    public abstract void insert(Token token);

    @Insert
    public abstract void insertAll(Token... token);

    @Query("DELETE FROM Token WHERE id=:id")
    public abstract void delete(String id);

    @Query("SELECT * FROM Token WHERE id IN (:ids)")
    public abstract Token[] getByIds(String[] ids);

    @Query("SELECT * FROM Token WHERE id=:id")
    public abstract Token getById(String id);

    @Query("DELETE FROM Token")
    public abstract void deleteAll();

    @Query("SELECT * FROM Token WHERE parent_id=:id")
    public abstract Token[] getByParentId(String id);
}