package com.ost.ostsdk.models.Impls;

import android.util.LruCache;

import com.ost.ostsdk.database.daos.BaseDao;
import com.ost.ostsdk.models.TaskCompleteCallback;
import com.ost.ostsdk.models.entities.BaseEntity;
import com.ost.ostsdk.utils.DispatchAsync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

abstract class BaseModelRepository {

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
                if (callback != null) {
                    callback.onTaskComplete();
                }
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
                if (callback != null) {
                    callback.onTaskComplete();
                }
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
                if (callback != null) {
                    callback.onTaskComplete();
                }
            }
        }));
    }

    public BaseEntity[] getByIds(String[] ids) {
        String[] failedCacheIdsList = getIdsNotInCache(ids);
        BaseEntity[] baseEntities = getModel().getByIds(failedCacheIdsList);
        return buildResultSet(ids, baseEntities);
    }

    private BaseEntity[] buildResultSet(String[] ids, BaseEntity[] baseEntities) {
        HashMap<String, BaseEntity> baseEntityHashMap = new HashMap<>();
        for (BaseEntity baseEntity : baseEntities) {
            baseEntityHashMap.put(baseEntity.getId(), baseEntity);
        }
        List<BaseEntity> resultSet = new ArrayList<>();
        for (String id : ids) {
            BaseEntity cacheEntity = mLruCache.get(id);
            if (null == cacheEntity) {
                if (null != baseEntityHashMap.get(id)) {
                    resultSet.add(baseEntityHashMap.get(id));
                } else {
                    //TODO::
                    //Throw exception Or print warning
                }
            } else {
                resultSet.add(cacheEntity);
            }
        }
        return (BaseEntity[]) resultSet.toArray();
    }

    private String[] getIdsNotInCache(String[] ids) {
        ArrayList<String> idsList = new ArrayList<>();
        for (String id : ids) {
            if (null == mLruCache.get(id)) {
                idsList.add(id);
            }
        }

        return (String[]) idsList.toArray();
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