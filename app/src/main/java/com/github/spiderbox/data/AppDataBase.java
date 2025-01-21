package com.github.spiderbox.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.github.spiderbox.cache.Cache;
import com.github.spiderbox.cache.CacheDao;
import com.github.spiderbox.cache.VodCollect;
import com.github.spiderbox.cache.VodCollectDao;
import com.github.spiderbox.cache.VodRecord;
import com.github.spiderbox.cache.VodRecordDao;


/**
 * 类描述:
 *
 * @author pj567
 * @since 2020/5/15
 */
@Database(entities = {Cache.class, VodRecord.class, VodCollect.class}, version = 1)
public abstract class AppDataBase extends RoomDatabase {
    public abstract CacheDao getCacheDao();

    public abstract VodRecordDao getVodRecordDao();

    public abstract VodCollectDao getVodCollectDao();
}
