package cn.org.atool.fluent.mybatis.test.basedao;

import cn.org.atool.fluent.mybatis.customize.StudentExtDao;
import cn.org.atool.fluent.mybatis.generate.ATM;
import cn.org.atool.fluent.mybatis.generate.entity.StudentEntity;
import cn.org.atool.fluent.mybatis.test.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.test4j.hamcrest.matcher.string.StringMode;

/**
 * @author darui.wu 2019/10/29 9:32 下午
 */
@SuppressWarnings("unchecked")
public class SaveOrUpdateTest extends BaseTest {
    @Autowired
    private StudentExtDao dao;

    @Test
    public void test_saveOrUpdate() {
        ATM.dataMap.student.initTable(3)
            .env.values("test_env")
            .cleanAndInsert();
        dao.saveOrUpdate(new StudentEntity().setId(3L).setUserName("test_111").setAge(30));
        // 验证执行的sql: 先判断主键记录有没有
        db.sqlList().wantFirstSql().eq("" +
            "SELECT COUNT(*) FROM fluent_mybatis.student WHERE `id` = ? LIMIT ?, ?", StringMode.SameAsSpace);
        db.sqlList().wantSql(1).eq("" +
            "UPDATE fluent_mybatis.student SET `gmt_modified` = now(), `age` = ?, `user_name` = ? " +
            "WHERE `id` = ?");
        db.table(ATM.table.student).queryWhere("id=3")
            .eqDataMap(ATM.dataMap.student.table(1)
                .userName.values("test_111")
                .age.values(30)
            );
    }

    @Test
    public void test_saveOrUpdate_2() {
        ATM.dataMap.student.initTable(3)
            .cleanAndInsert();
        dao.saveOrUpdate(new StudentEntity().setId(4L).setUserName("test_111").setAge(30));
        db.sqlList().wantFirstSql().eq("" +
            "SELECT COUNT(*) " +
            "FROM fluent_mybatis.student WHERE `id` = ? LIMIT ?, ?", StringMode.SameAsSpace);
        db.sqlList().wantSql(1).contains("INSERT INTO fluent_mybatis.student");
        db.table(ATM.table.student).count().eq(4);
        db.table(ATM.table.student).queryWhere("id=4")
            .eqDataMap(ATM.dataMap.student.table(1)
                .userName.values("test_111")
                .age.values(30)
            );
    }
}
