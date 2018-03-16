package com.jingyue.lygame.bean.internal;

import android.support.annotation.NonNull;
import android.view.View;

import com.jingyue.lygame.bean.GameBean;
import com.jingyue.lygame.bean.typeconvert.StringTypeConverter;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ColumnIgnore;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.AsyncModel;
import com.raizlabs.android.dbflow.structure.InvalidDBConfiguration;
import com.raizlabs.android.dbflow.structure.Model;
import com.raizlabs.android.dbflow.structure.ModelAdapter;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import java.util.List;

/**
 * Author       : yizhihao (Merlin)
 * Create time  : 2017-08-23 18:52
 * contact      :
 * 562536056@qq.com || yizhihao.hut@gmail.com
 */
public class BaseAdapterModel extends BaseItem implements Model{

    public BaseAdapterModel(int itemType){
        super(itemType);
    }

    public BaseAdapterModel(){
        super();
    }

    public static final String KEY = "keyUrl";

    @Column(name = KEY)
    public String keyUrl;

    @ColumnIgnore
    private transient ModelAdapter modelAdapter;

    @Override
    public void load() {
        getModelAdapter().load(this);
    }

    public void loadKey() {
        final ModelAdapter<BaseAdapterModel> modelAdapter = getModelAdapter();
        BaseAdapterModel model = SQLite.select()
                .from(modelAdapter.getModelClass())
                .where(modelAdapter.getPrimaryConditionClause(this)).querySingle();
        keyUrl = model.keyUrl;
    }

    @Override
    public void load(@NonNull DatabaseWrapper wrapper) {
        getModelAdapter().load(this, wrapper);
    }

    @Override
    public boolean save() {
        return getModelAdapter().save(this);
    }

    @Override
    public boolean save(@NonNull DatabaseWrapper databaseWrapper) {
        return getModelAdapter().save(this, databaseWrapper);
    }

    @Override
    public boolean delete() {
        return getModelAdapter().delete(this);
    }

    @Override
    public boolean delete(@NonNull DatabaseWrapper databaseWrapper) {
        return getModelAdapter().delete(this, databaseWrapper);
    }

    @Override
    public boolean update() {
        return getModelAdapter().update(this);
    }

    @Override
    public boolean update(@NonNull DatabaseWrapper databaseWrapper) {
        return getModelAdapter().update(this, databaseWrapper);
    }

    @Override
    public long insert() {
        return getModelAdapter().insert(this);
    }

    @Override
    public long insert(DatabaseWrapper databaseWrapper) {
        return getModelAdapter().insert(this, databaseWrapper);
    }

    @Override
    public boolean exists() {
        return getModelAdapter().exists(this);
    }

    @Override
    public boolean exists(@NonNull DatabaseWrapper databaseWrapper) {
        return getModelAdapter().exists(this, databaseWrapper);
    }

    @NonNull
    @Override
    public AsyncModel<? extends Model> async() {
        return new AsyncModel<>(this);
    }

    /**
     * @return The associated {@link ModelAdapter}. The {@link FlowManager}
     * may throw a {@link InvalidDBConfiguration} for this call if this class
     * is not associated with a table, so be careful when using this method.
     */
    public ModelAdapter getModelAdapter() {
        if (modelAdapter == null) {
            modelAdapter = FlowManager.getModelAdapter(getClass());
        }
        return modelAdapter;
    }

    @Override
    public int getLayout() {
        return 0;
    }
    @Override
    public BaseItem.BVH onCreateViewHolder(View parent, int viewType) {
        return null;
    }
}
