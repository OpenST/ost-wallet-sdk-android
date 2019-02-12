package com.ost.mobilesdk.models.Impls;

import android.util.LruCache;

import com.ost.mobilesdk.models.entities.OstBaseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

abstract class OstBaseModelCacheRepository extends OstBaseModelRepository {

    private LruCache<String, OstBaseEntity> mLruCache;
    private HashMap<String, OstBaseEntity> mInMemoryMap;


    OstBaseModelCacheRepository(int lruSize) {
        this.mLruCache = new LruCache<>(lruSize);
        this.mInMemoryMap = new HashMap<>();
    }

    public void insert(final OstBaseEntity ostBaseEntity) {

        insertInCacheAndMemory(ostBaseEntity);

        super.insert(ostBaseEntity);

        removeInMemory(ostBaseEntity);
    }

    public void insertAll(final OstBaseEntity[] baseEntities) {

        insertInCacheAndMemory(baseEntities);

        super.insertAll(baseEntities);

        removeInMemory(baseEntities);
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

    public void delete(final String id) {
        super.delete(id);
        removeFromCache(id);
    }

    public void deleteAll() {
        super.deleteAll();
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