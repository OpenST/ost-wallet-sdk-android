package com.ost.ostsdk.models.Impls;

import android.util.LruCache;

import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.models.TaskCompleteCallback;
import com.ost.ostsdk.models.entities.BaseEntity;
import com.ost.ostsdk.utils.DispatchAsync;

public abstract class BaseModelRepository {

    private LruCache<String, BaseEntity> mLruCache;

    BaseModelRepository(int lruSize) {
        this.mLruCache = new LruCache<>(lruSize);
    }

    public void insert(final BaseEntity baseEntity, final TaskCompleteCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                getModel().insert(baseEntity);
            }

            @Override
            public void onExecuteComplete() {
                callback.onTaskComplete();
            }
        }));
        insertInCache(baseEntity);
    }

    public void insertAll(final BaseEntity[] baseEntities, final TaskCompleteCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                getModel().insertAll(baseEntities);
            }

            @Override
            public void onExecuteComplete() {
                callback.onTaskComplete();
            }
        }));
        insertInCache(baseEntities);
    }

    public void delete(final BaseEntity baseEntity, final TaskCompleteCallback callback) {
        DispatchAsync.dispatch((new DispatchAsync.Executor() {
            @Override
            public void execute() {
                getModel().delete(baseEntity);
            }

            @Override
            public void onExecuteComplete() {
                callback.onTaskComplete();
            }
        }));
    }

    public BaseEntity[] getByIds(String[] ids) {

        return getModel().getByIds(ids);
    }

    public BaseEntity getById(String id) {
        if (null != mLruCache.get(id)) {
            return mLruCache.get(id);
        }
        return getModel().getById(id);
    }

    public void deleteAll(final TaskCompleteCallback callback) {
        getModel().deleteAll();
    }

    abstract BaseDao getModel();

    private void insertInCache(BaseEntity baseEntity) {
        this.mLruCache.put(baseEntity.getId(), baseEntity);
    }

    private void insertInCache(BaseEntity[] baseEntities) {
        for (BaseEntity baseEntity : baseEntities) {
            this.mLruCache.put(baseEntity.getId(), baseEntity);
        }
    }
}