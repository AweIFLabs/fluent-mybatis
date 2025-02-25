package cn.org.atool.fluent.mybatis.segment;

import cn.org.atool.fluent.mybatis.base.IEntity;
import cn.org.atool.fluent.mybatis.base.crud.IBaseQuery;
import cn.org.atool.fluent.mybatis.base.crud.IWrapper;
import cn.org.atool.fluent.mybatis.base.entity.IMapping;
import cn.org.atool.fluent.mybatis.base.model.FieldMapping;
import cn.org.atool.fluent.mybatis.base.model.UniqueType;
import cn.org.atool.fluent.mybatis.metadata.TableMeta;
import cn.org.atool.fluent.mybatis.metadata.TableMetaHelper;
import cn.org.atool.fluent.mybatis.segment.fragment.IFragment;
import cn.org.atool.fluent.mybatis.segment.model.Parameters;
import cn.org.atool.fluent.mybatis.segment.model.WrapperData;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cn.org.atool.fluent.mybatis.If.isBlank;
import static cn.org.atool.fluent.mybatis.mapper.StrConstant.EMPTY;
import static cn.org.atool.fluent.mybatis.utility.MybatisUtil.notNull;

/**
 * 查询条件封装
 *
 * @param <E>  对应的实体类
 * @param <W>  更新器或查询器
 * @param <NQ> 对应的嵌套查询器
 * @author darui.wu
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class BaseWrapper<
    E extends IEntity,
    W extends IWrapper<E, W, NQ>,
    NQ extends IBaseQuery<E, NQ>
    >
    implements IWrapper<E, W, NQ> {
    private static final long serialVersionUID = 2674302532927710150L;

    protected IFragment table;
    /**
     * 表别名
     */
    @Getter
    protected String tableAlias;

    @Getter(AccessLevel.NONE)
    protected WrapperData data;

    @Getter
    protected Class entityClass;

    protected BaseWrapper(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    protected BaseWrapper(IFragment table, String tableAlias, Class<E> entityClass) {
        this(table, tableAlias, new Parameters(), entityClass);
    }

    protected BaseWrapper(IFragment table, String tableAlias, Parameters parameters, Class<E> entityClass) {
        notNull(entityClass, "entityClass must not null,please set entity before use this method!");
        this.table = table;
        this.tableAlias = isBlank(tableAlias) ? EMPTY : tableAlias.trim();
        this.data = new WrapperData(this, parameters);
        this.entityClass = entityClass;
    }

    /**
     * 返回字段映射关系
     *
     * @return 字段映射关系
     */
    public Optional<IMapping> mapping() {
        return Optional.empty();
    }

    @Override
    public IFragment table(boolean notFoundError) {
        if (this.table != null && this.table.notEmpty()) {
            return this.table;
        } else if (notFoundError) {
            return this.mapping().map(IMapping::table).orElseThrow(() -> new RuntimeException("table name not found."));
        } else {
            return this.mapping().map(IMapping::table).orElse(null);
        }
    }

    /**
     * 返回指定类型字段名称
     * 如果没有指定类型字段，返回null
     *
     * @return 指定类型的字段
     */
    public String fieldName(UniqueType type) {
        return this.mapping().flatMap(m -> m.findField(type)).map(c -> c.column).orElse(null);
    }

    /**
     * 表所有字段列表
     *
     * @return 所有字段
     */
    public List<String> allFields() {
        return this.mapping().map(IMapping::getAllColumns).orElse(Collections.emptyList());
    }

    public WrapperData data() {
        return data;
    }

    protected TableMeta getTableMeta() {
        return TableMetaHelper.getTableInfo(this.entityClass);
    }

    /**
     * 通过Wrapper直接设置变量共享关系
     *
     * @param parameters 参数
     */
    protected void sharedParameter(Parameters parameters) {
        this.data.getParameters().sharedParameter(parameters);
    }

    /**
     * 通过Wrapper直接设置变量共享关系
     *
     * @param wrapper BaseWrapper
     */
    protected void sharedParameter(BaseWrapper wrapper) {
        this.data.getParameters().sharedParameter(wrapper.data().getParameters());
    }

    /**
     * 返回字段对应的column映射
     *
     * @param column 数据库字段名称
     * @return 字段映射
     */
    public FieldMapping column(String column) {
        return this.column2mapping().get(column);
    }

    /**
     * 返回字段映射关系
     *
     * @return 字段映射
     */
    private Map<String, FieldMapping> column2mapping() {
        return this.mapping().map(IMapping::getColumnMap).orElse(Collections.EMPTY_MAP);
    }
}