package com.ost.ostsdk.database.daos;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.ost.ostsdk.models.entities.MultiSigOperation;

@Dao
public interface MutiSigOperationDao {
    @Insert
    void insert(MultiSigOperation multiSigOperation);

    @Insert
    void insertAll(MultiSigOperation... multiSigOperation);

    @Delete
    void delete(MultiSigOperation multiSigOperation);

    @Query("SELECT * FROM multi_sig_operation WHERE id IN (:ids)")
    MultiSigOperation getByIds(double[] ids);

    @Query("SELECT * FROM multi_sig_operation WHERE id=:id")
    MultiSigOperation getById(double id);

    @Query("DELETE FROM multi_sig_operation")
    void deleteAll();
}
