package com.gcml.common.repository.debug.repository.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.gcml.common.repository.debug.repository.SheetEntity;

@Database(entities = {SheetEntity.class}, version = 1)
public abstract class SheetDb extends RoomDatabase{
    public abstract SheetDao sheetDao();
}
