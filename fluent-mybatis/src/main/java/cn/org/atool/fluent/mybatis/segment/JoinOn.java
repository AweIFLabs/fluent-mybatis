package cn.org.atool.fluent.mybatis.segment;

import cn.org.atool.fluent.mybatis.If;
import cn.org.atool.fluent.mybatis.base.IEntity;
import cn.org.atool.fluent.mybatis.base.IRef;
import cn.org.atool.fluent.mybatis.base.crud.BaseQuery;
import cn.org.atool.fluent.mybatis.base.crud.IQuery;
import cn.org.atool.fluent.mybatis.base.free.FreeQuery;
import cn.org.atool.fluent.mybatis.base.model.FieldMapping;
import cn.org.atool.fluent.mybatis.functions.IGetter;
import cn.org.atool.fluent.mybatis.metadata.JoinType;
import cn.org.atool.fluent.mybatis.segment.fragment.CachedFrag;
import cn.org.atool.fluent.mybatis.segment.fragment.Column;
import cn.org.atool.fluent.mybatis.segment.fragment.IFragment;
import cn.org.atool.fluent.mybatis.segment.fragment.JoiningFrag;
import cn.org.atool.fluent.mybatis.segment.where.BaseWhere;
import cn.org.atool.fluent.mybatis.utility.MappingKits;

import java.util.function.Function;

import static cn.org.atool.fluent.mybatis.base.free.FreeKit.newFreeQuery;
import static cn.org.atool.fluent.mybatis.utility.MybatisUtil.assertNotNull;

/**
 * 关联查询on条件设置
 *
 * @param <QL> 关联左查询类型
 * @param <QR> 关联右查询类型
 * @param <JB> JoinBuilder
 * @author darui.wu
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class JoinOn<QL extends BaseQuery<?, QL>, QR extends BaseQuery<?, QR>, JB> {
    private final JoinQuery<QL> joinQuery;

    private final QL onLeft;

    private final JoinType joinType;

    private final QR onRight;

    private final JoiningFrag ons = JoiningFrag.get().setDelimiter(" AND ").setFilter(If::notBlank);

    public JoinOn(JoinQuery<QL> joinQuery, QL qLeft, JoinType joinType, QR qRight) {
        this.joinQuery = joinQuery;
        this.joinType = joinType;
        /* 初始化左查询关联 */
        this.onLeft = this.emptyQuery(qLeft);
        /* 初始化右查询关联 */
        this.onRight = this.emptyQuery(qRight);
    }

    private <Q extends BaseQuery> Q emptyQuery(BaseQuery origQuery) {
        BaseQuery onQuery;
        if (origQuery instanceof FreeQuery) {
            onQuery = newFreeQuery(origQuery);
        } else {
            onQuery = IRef.instance().byEntity(origQuery.entityClass).emptyQuery();
        }
        onQuery.tableAlias = origQuery.tableAlias;
        onQuery.sharedParameter(this.joinQuery);
        return (Q) onQuery;
    }

    /**
     * 自由设置连接关系, 设置时需要加上表别名
     * 比如: t1.id = t2.id AND t1.is_deleted = t2.is_deleted AND t1.env = ?
     *
     * @param condition 手工设置的关联关系
     * @param args      关联关系参数列表
     * @return JoinOn
     */
    public JoinOn<QL, QR, JB> onApply(String condition, Object... args) {
        String sql = this.joinQuery.data().paramSql(null, condition, args);
        this.ons.add(CachedFrag.set(sql));
        return this;
    }

    /**
     * 关联关系设置, on left = right, 各取最后一个属性
     *
     * @param l 左查询条件, 取最后一个属性
     * @param r 右查询条件, 取最后一个属性
     * @return JoinOn
     */
    public JoinOn<QL, QR, JB> on(Function<QL, BaseWhere> l, Function<QR, BaseWhere> r) {
        this.ons.add(Column.set(onLeft, ((WhereApply) l.apply(onLeft)).current())
            .plus(" = ")
            .plus(Column.set(onRight, ((WhereApply) r.apply(onRight)).current())));
        return this;
    }

    /**
     * 关联关系设置 l(left column) = r(right column)
     *
     * @param l 左查询条件
     * @param r 右查询条件
     * @return JoinOn
     */
    public <LE extends IEntity, RE extends IEntity> JoinOn<QL, QR, JB> onEq(IGetter<LE> l, IGetter<RE> r) {
        Class lKlass = this.onLeft.entityClass;
        Class rKlass = this.onRight.entityClass;
        assertNotNull("left query entity class", lKlass);
        assertNotNull("right query entity class", rKlass);
        String lField = MappingKits.toColumn(lKlass, l);
        String rField = MappingKits.toColumn(rKlass, r);
        return this.onEq(lField, rField);
    }

    /**
     * 关联关系设置 l(left column) = r(right column)
     *
     * @param l 左关联字段
     * @param r 右关联字段
     * @return JoinOn
     */
    public JoinOn<QL, QR, JB> onEq(String l, String r) {
        this.ons.add(Column.set(this.onLeft, l).plus(" = ").plus(Column.set(this.onRight, r)));
        return this;
    }

    /**
     * 关联关系设置 l(left column) = r(right column)
     *
     * @param l 左关联字段
     * @param r 右关联字段
     * @return JoinOn
     */
    public JoinOn<QL, QR, JB> onEq(FieldMapping l, FieldMapping r) {
        this.ons.add(Column.set(this.onLeft, l).plus(" = ").plus(Column.set(this.onRight, r)));
        return this;
    }

    /**
     * 左表固定关联关系
     *
     * @param l 左查询条件
     * @return JoinOn
     */
    public JoinOn<QL, QR, JB> onLeft(Function<QL, BaseSegment<?, QL>> l) {
        return this.onQuery(this.onLeft, l);
    }

    /**
     * 右表固定关联关系
     *
     * @param r 右查询条件
     * @return JoinOn
     */
    public JoinOn<QL, QR, JB> onRight(Function<QR, BaseSegment<?, QR>> r) {
        return this.onQuery(this.onRight, r);
    }

    private <Q extends BaseQuery<?, Q>> JoinOn onQuery(IQuery query, Function<Q, BaseSegment<?, Q>> func) {
        Q onQuery = this.emptyQuery((BaseQuery) query);
        this.ons.add(func.apply(onQuery).end().data().where());
        return this;
    }

    /**
     * 结束关联设置
     *
     * @return JoinBuilder
     */
    public JB endJoin() {
        IFragment table = this.joinType.plus(this.onRight.data.table());
        if (!this.ons.isEmpty()) {
            table = table.plus(" ON ").plus(this.ons);
        }
        this.joinQuery.data().addTable(table);
        return (JB) this.joinQuery;
    }
}