package cn.org.atool.fluent.mybatis.test.batch;

import cn.org.atool.fluent.mybatis.base.BatchCrud;
import cn.org.atool.fluent.mybatis.base.model.FieldMapping;
import cn.org.atool.fluent.mybatis.generate.ATM;
import cn.org.atool.fluent.mybatis.generate.entity.StudentEntity;
import cn.org.atool.fluent.mybatis.generate.mapper.StudentMapper;
import cn.org.atool.fluent.mybatis.generate.wrapper.StudentQuery;
import cn.org.atool.fluent.mybatis.refs.FieldRef;
import cn.org.atool.fluent.mybatis.test.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.test4j.hamcrest.matcher.string.StringMode;

import java.util.List;

public class InsertSelectTest extends BaseTest {
    @Autowired
    private StudentMapper mapper;

    @Test
    void testInsertSelect() {
        ATM.dataMap.student.table(3)
            .address.values("address1", "address2", "address3")
            .age.values(34, 45, 55)
            .cleanAndInsert();
        int count = mapper.insertSelect(new String[]{"address", "age"},
            StudentQuery.emptyQuery()
                .select.address().age().end()
                .where.id().in(new long[]{1, 2, 3}).end()
        );
        db.sqlList().wantFirstSql()
            .eq("INSERT INTO fluent_mybatis.student (`address`, `age`) " +
                "SELECT `address`, `age` " +
                "FROM fluent_mybatis.student WHERE `id` IN (?, ?, ?)");
        want.number(count).eq(3);
        ATM.dataMap.student.table(6)
            .address.values("address1", "address2", "address3", "address1", "address2", "address3")
            .age.values(34, 45, 55, 34, 45, 55)
            .eqTable();
    }

    @Test
    void testSelect() {
        ATM.dataMap.student.table(3)
            .address.values("address1", "address2", "address3")
            .age.values(34, 45, 55)
            .cleanAndInsert();
        List list = mapper.listMaps(
            StudentQuery.emptyQuery()
                .select.address().age().end()
                .where.id().in(new long[]{1, 2, 3}).end()
        );
        db.sqlList().wantFirstSql()
            .eq("SELECT `address`, `age` FROM fluent_mybatis.student WHERE `id` IN (?, ?, ?)");
        want.list(list).sizeEq(3);
    }

    @Test
    void testSelect_alias() {
        ATM.dataMap.student.table(3)
            .address.values("address1", "address2", "address3")
            .age.values(34, 45, 55)
            .cleanAndInsert();
        List list = mapper.listMaps(
            StudentQuery.emptyQuery()
                .select.address("address_alias").age().end()
                .where.id().in(new long[]{1, 2, 3}).end()
        );
        db.sqlList().wantFirstSql()
            .eq("SELECT `address` AS address_alias, `age` " +
                "FROM fluent_mybatis.student WHERE `id` IN (?, ?, ?)");
        want.list(list).sizeEq(3);
    }

    @Test
    void testBatchInsertSelect() {
        ATM.dataMap.student.table().clean();
        mapper.batchCrud(BatchCrud.batch()
            .addInsert(newStudent("user1"), newStudent("user2"), newStudent("test1"))
            .addInsertSelect(ATM.table.student,
                new FieldMapping[]{
                    FieldRef.Student.userName,
                    FieldRef.Student.age,
                    FieldRef.Student.address},
                StudentQuery.emptyQuery().select.userName().apply("40", "'test address'").end()
                    .where.userName().likeLeft("user").end())
        );

        db.sqlList().wantFirstSql()
            .containsInOrder(
                "INSERT INTO fluent_mybatis.student(`gmt_created`, `gmt_modified`, `is_deleted`, `address`, `age`, `env`, `tenant`, `user_name`)",
                "INSERT INTO fluent_mybatis.student(`gmt_created`, `gmt_modified`, `is_deleted`, `address`, `age`, `env`, `tenant`, `user_name`)",
                "INSERT INTO fluent_mybatis.student(`gmt_created`, `gmt_modified`, `is_deleted`, `address`, `age`, `env`, `tenant`, `user_name`)")
            .end("INSERT INTO student (`user_name`, `age`, `address`) " +
                    "SELECT `user_name`, 40, 'test address' " +
                    "FROM fluent_mybatis.student " +
                    "WHERE `user_name` LIKE ?",
                StringMode.SameAsSpace);
        ATM.dataMap.student.table(5)
            .userName.values("user1", "user2", "test1", "user1", "user2")
            .age.values(20, 20, 20, 40, 40)
            .address.values("addr", "addr", "addr", "test address", "test address")
            .eqTable();
    }

    private StudentEntity newStudent(String name) {
        return new StudentEntity().setUserName(name).setAge(20).setAddress("addr");
    }
}