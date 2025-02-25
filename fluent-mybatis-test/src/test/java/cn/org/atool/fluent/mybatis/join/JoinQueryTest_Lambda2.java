package cn.org.atool.fluent.mybatis.join;

import cn.org.atool.fluent.mybatis.base.crud.IQuery;
import cn.org.atool.fluent.mybatis.generate.ATM;
import cn.org.atool.fluent.mybatis.generate.entity.StudentEntity;
import cn.org.atool.fluent.mybatis.generate.mapper.StudentMapper;
import cn.org.atool.fluent.mybatis.generate.wrapper.HomeAddressQuery;
import cn.org.atool.fluent.mybatis.generate.wrapper.StudentQuery;
import cn.org.atool.fluent.mybatis.refs.FieldRef;
import cn.org.atool.fluent.mybatis.test.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class JoinQueryTest_Lambda2 extends BaseTest {
    @Autowired
    private StudentMapper mapper;

    @Test
    public void testInnerJoin() {
        ATM.dataMap.student.initTable(3)
            .userName.formatAutoIncrease("user_%d")
            .age.values(34)
            .homeAddressId.values(1, 3)
            .cleanAndInsert();
        ATM.dataMap.homeAddress.initTable(2)
            .id.values(3, 4)
            .address.values("address_1", "address_2")
            .cleanAndInsert();

        StudentQuery leftQuery = StudentQuery.emptyQuery("a1").selectAll()
            .where.age().eq(34)
            .end();
        HomeAddressQuery rightQuery = HomeAddressQuery.emptyQuery("a2")
            .where.address().like("address")
            .end();

        IQuery query = leftQuery
            .join(rightQuery)
            .onEq(FieldRef.Student.homeAddressId, FieldRef.HomeAddress.id).endJoin()
            .build();
        List<StudentEntity> entities = this.mapper.listEntity(query);
        db.sqlList().wantFirstSql().end("" +
            "FROM fluent_mybatis.student a1 " +
            "JOIN `home_address` a2 " +
            "ON a1.`home_address_id` = a2.`id` " +
            "WHERE a1.`age` = ? " +
            "AND a2.`address` LIKE ?");
        db.sqlList().wantFirstPara().eqList(34, "%address%");
        want.list(entities).eqDataMap(ATM.dataMap.student.entity(2)
            .id.values(2, 3)
            .homeAddressId.values(3)
            .age.values(34)
            .userName.values("user_2", "user_3")
        );
    }
}
