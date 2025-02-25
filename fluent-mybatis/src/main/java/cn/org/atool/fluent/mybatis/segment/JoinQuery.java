package cn.org.atool.fluent.mybatis.segment;

import cn.org.atool.fluent.mybatis.base.IEntity;
import cn.org.atool.fluent.mybatis.base.crud.BaseQuery;
import cn.org.atool.fluent.mybatis.base.crud.IBaseQuery;
import cn.org.atool.fluent.mybatis.base.crud.JoinBuilder;
import cn.org.atool.fluent.mybatis.base.crud.JoinToBuilder;
import cn.org.atool.fluent.mybatis.metadata.JoinType;
import cn.org.atool.fluent.mybatis.segment.model.PagedOffset;
import cn.org.atool.fluent.mybatis.segment.model.Parameters;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

import static cn.org.atool.fluent.mybatis.If.isBlank;
import static cn.org.atool.fluent.mybatis.utility.MybatisUtil.assertNotNull;

/**
 * 联合查询条件
 *
 * @param <QL>
 */
@SuppressWarnings({"rawtypes", "unused", "unchecked"})
@Accessors(chain = true)
public class JoinQuery<QL extends BaseQuery<?, QL>>
    extends BaseWrapper<IEntity, JoinQuery<QL>, JoinQuery<QL>>
    implements IBaseQuery<IEntity, JoinQuery<QL>>, JoinToBuilder<QL> {
    /**
     * 主查询条件
     */
    private final QL query;
    /**
     * join查询, 允许有多个join
     */
    private final List<BaseQuery> queries = new ArrayList<>();

    /**
     * 别名列表
     */
    private final List<String> alias = new ArrayList<>(8);

    @Override
    public String[] getAlias() {
        return this.alias.toArray(new String[0]);
    }

    /**
     * 如果有必要，需要显式设置query表别名
     *
     * @param query 左查询
     */
    public JoinQuery(QL query) {
        super(null);
        this.assertQueryAlias(query);
        this.query = query;
        Parameters parameters = new Parameters();
        this.query.sharedParameter(parameters);
        super.data = new JoinWrapperData(this.query, this.queries, parameters);
        this.alias.add(this.query.tableAlias);
    }

    @Override
    public <QR extends BaseQuery<?, QR>> JoinOn<QL, QR, JoinToBuilder<QL>> join(QR query) {
        return join(JoinType.Join, query);
    }

    @Override
    public <QR extends BaseQuery<?, QR>> JoinOn<QL, QR, JoinToBuilder<QL>> leftJoin(QR query) {
        return join(JoinType.LeftJoin, query);
    }

    @Override
    public <QR extends BaseQuery<?, QR>> JoinOn<QL, QR, JoinToBuilder<QL>> rightJoin(QR query) {
        return join(JoinType.RightJoin, query);
    }

    private <QR extends BaseQuery<?, QR>> JoinOn<QL, QR, JoinToBuilder<QL>> join(
        JoinType joinType, QR query) {
        this.assertQueryAlias(query);
        query.sharedParameter(this.query);

        this.queries.add(query);
        this.alias.add(query.tableAlias);
        return new JoinOn<>(this, this.query, joinType, query);
    }

    /**
     * 判断query查询表别名已经设置
     *
     * @param query BaseQuery
     */
    private void assertQueryAlias(BaseQuery query) {
        assertNotNull("query", query);
        if (isBlank(query.tableAlias)) {
            query.tableAlias = Parameters.alias();
        }
    }

    @Override
    public JoinBuilder<QL> select(String... columns) {
        for (String column : columns) {
            this.data.select(column);
        }
        return this;
    }

    @Override
    public JoinQuery<QL> distinct() {
        this.data.setDistinct(true);
        return this;
    }

    @Override
    public JoinQuery<QL> limit(int limit) {
        this.data.setPaged(new PagedOffset(0, limit));
        return this;
    }

    @Override
    public JoinQuery<QL> limit(int start, int limit) {
        this.data.setPaged(new PagedOffset(start, limit));
        return this;
    }

    @Override
    public JoinQuery<QL> last(String lastSql) {
        this.data.last(lastSql);
        return this;
    }

    @Override
    public JoinQuery<QL> build() {
        return this;
    }

    @Override
    public JoinQuery<QL> selectAll() {
        throw new RuntimeException("not support");
    }

    @Override
    public JoinQuery<QL> selectId() {
        throw new RuntimeException("not support");
    }

    /**
     * 查询条件 where ...
     */
    public final JoinQueryWhere where = new JoinQueryWhere(this);

    @Override
    public JoinQueryWhere where() {
        return this.where;
    }

    @Override
    public JoinWrapperData data() {
        return (JoinWrapperData) super.data;
    }

    @Override
    public List<String> allFields() {
        List<String> all = new ArrayList<>();
        for (BaseQuery query : this.queries) {
            all.addAll(query.allFields());
        }
        return all;
    }
}