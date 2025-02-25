package cn.org.atool.fluent.mybatis.segment.fragment;

import java.util.function.Supplier;

import static cn.org.atool.fluent.mybatis.mapper.StrConstant.*;
import static cn.org.atool.fluent.mybatis.utility.MybatisUtil.isTableName;

/**
 * 常量代码片段
 *
 * @author darui.wu
 */
public interface Fragments {
    Column EMPTY_COLUMN = Column.set(EMPTY, EMPTY);

    CachedFrag SEG_SPACE = CachedFrag.set(SPACE);
    /**
     * 单字符 '*'
     */
    CachedFrag SEG_ASTERISK = CachedFrag.set(ASTERISK);
    /**
     * 空字符
     */
    CachedFrag SEG_EMPTY = CachedFrag.set(EMPTY);
    /**
     * count(1)
     */
    CachedFrag SEG_COUNT_1 = CachedFrag.set("COUNT(1)");

    /**
     * 构造IFragment
     *
     * @param supplier Supplier<String>
     * @return IFragment
     */
    static IFragment fragment(Supplier<String> supplier) {
        if (supplier == null) {
            return null;
        }
        return m -> {
            String table = supplier.get();
            if (isTableName(table)) {
                return m.db().wrap(table);
            } else {
                return table;
            }
        };
    }

    static IFragment fragment(String segment) {
        return m -> segment;
    }
}