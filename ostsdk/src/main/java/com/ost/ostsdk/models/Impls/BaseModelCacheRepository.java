package com.ost.ostsdk.models.Impls;

import android.util.LruCache;

import com.ost.ostsdk.models.TaskCallback;
import com.ost.ostsdk.models.entities.OstBaseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

abstract class BaseModelCacheRepository extends BaseModelRepository {

    private LruCache<String, OstBaseEntity> mLruCache;
    private HashMap<String, OstBaseEntity> mInMemoryMap;


    BaseModelCacheRepository(int lruSize) {
        this.mLruCache = new LruCache<>(lruSize);
        this.mInMemoryMap = new HashMap<>();
    }

    public void insert(final OstBaseEntity baseEntity, final TaskCallback callback) {

        //check in cache for for entity with same uts
        OstBaseEntity oldEntity = getById(baseEntity.getId());
        if (null != oldEntity && oldEntity.getUts() >= baseEntity.getUts()) {
            return;
        }

        super.insert(baseEntity, new TaskCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
                removeInMemory(baseEntity);
            }
        });
        insertInCacheAndMemory(baseEntity);
    }

    public void insertAll(final OstBaseEntity[] baseEntities, final TaskCallback callback) {

        super.insertAll(baseEntities, new TaskCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
                removeInMemory(baseEntities);
            }
        });
        insertInCacheAndMemory(baseEntities);
    }

    public OstBaseEntity getById(String id) {
        if (null != mLruCache.get(id)) {
            return mLruCache.get(id);
        }
        if (null != mInMemoryMap.get(id)) {
            return mInMemoryMap.get(id);
        }
        return super.getById(id);
    }

    public OstBaseEntity[] getByIds(String[] ids) {
        String[] failedCacheIdsList = getIdsNotInCache(ids);
        OstBaseEntity[] baseEntities = super.getByIds(failedCacheIdsList);
        return buildResultSet(ids, baseEntities);
    }

    public void delete(final String id, final TaskCallback callback) {
        super.delete(id, new TaskCallback() {
            @Override
            public void onSuccess() {
                removeFromCache(id);
                callback.onSuccess();
            }
        });
    }

    public void deleteAll(final TaskCallback callback) {
        super.deleteAll(new TaskCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }
        });
        mLruCache.evictAll();
        mInMemoryMap = new HashMap<>();
    }

    private OstBaseEntity[] buildResultSet(String[] ids, OstBaseEntity[] baseEntities) {
        HashMap<String, OstBaseEntity> baseEntityHashMap = new HashMap<>();
        for (OstBaseEntity baseEntity : baseEntities) {
            baseEntityHashMap.put(baseEntity.getId(), baseEntity);
        }
        List<OstBaseEntity> resultSet = new ArrayList<>();
        for (String id : ids) {
            OstBaseEntity cacheEntity = mLruCache.get(id);
            if (null == cacheEntity) {
                cacheEntity = mInMemoryMap.get(id);
            }
            if (null == cacheEntity) {
                if (null != baseEntityHashMap.get(id)) {
                    resultSet.add(baseEntityHashMap.get(id));
                } else {
                    resultSet.add(null);
                }
            } else {
                resultSet.add(cacheEntity);
            }
        }
        return (OstBaseEntity[]) resultSet.toArray();
    }

    private String[] getIdsNotInCache(String[] ids) {
        ArrayList<String> idsList = new ArrayList<>();
        for (String id : ids) {
            if (null == mLruCache.get(id) && null == mInMemoryMap.get(id)) {
                idsList.add(id);
            }
        }

        return (String[]) idsList.toArray();
    }

    private void insertInCacheAndMemory(OstBaseEntity baseEntity) {
        this.mLruCache.put(baseEntity.getId(), baseEntity);
        this.mInMemoryMap.put(baseEntity.getId(), baseEntity);
    }

    private void removeInMemory(OstBaseEntity baseEntity) {
        this.mInMemoryMap.remove(baseEntity.getId());
    }

    private void removeFromCache(String id) {
        this.mLruCache.remove(id);
    }

    private void removeFromCache(String[] ids) {
        for (String id : ids) {
            this.mLruCache.remove(id);
        }
    }

    private void insertInCacheAndMemory(OstBaseEntity[] baseEntities) {
        for (OstBaseEntity baseEntity : baseEntities) {
            this.mLruCache.put(baseEntity.getId(), baseEntity);
            this.mInMemoryMap.put(baseEntity.getId(), baseEntity);
        }
    }

    private void removeInMemory(OstBaseEntity[] baseEntities) {
        for (OstBaseEntity baseEntity : baseEntities) {
            this.mInMemoryMap.remove(baseEntity.getId());
        }
    }
}