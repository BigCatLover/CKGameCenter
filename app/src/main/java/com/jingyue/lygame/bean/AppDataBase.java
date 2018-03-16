package com.jingyue.lygame.bean;

import android.support.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-06-11 9:34
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
@Database(name = AppDataBase.NAME, version = AppDataBase.VERSION)
public class AppDataBase {

    public static final String NAME = "lyGame";
    public static final int VERSION = 2;

    @Migration(version = 2,database = AppDataBase.class)
    public static class Migration2 extends BaseMigration {
        @Override
        public void migrate(@NonNull DatabaseWrapper database) {
            // run some code here
            // for example :
            // 修改数据库的字段的值
            // SQLite.update(Employee.class)
            //      .set(Employee_Table.status.eq("Invalid"))
            //      .where(Employee_Table.job.eq("Laid Off"))
            //      .execute(database); // required inside a migration to pass the wrapper
        }
    }
}
