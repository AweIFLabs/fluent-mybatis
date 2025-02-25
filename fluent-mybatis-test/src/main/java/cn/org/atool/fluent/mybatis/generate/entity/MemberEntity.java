package cn.org.atool.fluent.mybatis.generate.entity;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.LogicDelete;
import cn.org.atool.fluent.mybatis.annotation.RefMethod;
import cn.org.atool.fluent.mybatis.annotation.TableField;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.IEntity;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import cn.org.atool.fluent.mybatis.functions.TableSupplier;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * MemberEntity: 数据映射实体定义
 *
 * @author Powered By Fluent Mybatis
 */
@SuppressWarnings({"unchecked"})
@Data
@Accessors(
    chain = true
)
@EqualsAndHashCode(
    callSuper = false
)
@FluentMybatis(
    table = "t_member",
    schema = "fluent_mybatis"
)
public class MemberEntity extends RichEntity {
  private static final long serialVersionUID = 1L;

  /**
   * 主键id
   */
  @TableId("id")
  private Long id;

  /**
   * 更新时间
   */
  @TableField(
      value = "gmt_modified",
      insert = "now()",
      update = "now()"
  )
  private Date gmtModified;

  /**
   * 是否逻辑删除
   */
  @TableField(
      value = "is_deleted",
      insert = "0"
  )
  @LogicDelete
  private Boolean isDeleted;

  /**
   * 年龄
   */
  @TableField("age")
  private Integer age;

  /**
   * 创建时间
   */
  @TableField("gmt_created")
  private Date gmtCreated;

  /**
   * 0:男孩; 1:女孩
   */
  @TableField("is_girl")
  private Boolean isGirl;

  /**
   * 学校
   */
  @TableField("school")
  private String school;

  /**
   * 名字
   */
  @TableField("user_name")
  private String userName;

  @Override
  public Serializable findPk() {
    return this.id;
  }

  @Override
  public Consumer<Long> pkSetter() {
    return this::setId;
  }

  @Override
  public final Class<? extends IEntity> entityClass() {
    return MemberEntity.class;
  }

  @Override
  public final MemberEntity changeTableBelongTo(TableSupplier supplier) {
    return super.changeTableBelongTo(supplier);
  }

  @Override
  public final MemberEntity changeTableBelongTo(String table) {
    return super.changeTableBelongTo(table);
  }

  /**
   * 实现 @see cn.org.atool.fluent.mybatis.refs.Ref
   */
  @RefMethod("isDeleted = isDeleted && memberId = id")
  public List<MemberFavoriteEntity> findMyFavorite() {
    return super.invoke("findMyFavorite", true);
  }

  /**
   * 实现 @see cn.org.atool.fluent.mybatis.refs.Ref
   */
  @RefMethod
  public List<MemberEntity> findExFriends() {
    return super.invoke("findExFriends", true);
  }

  /**
   * 实现 @see cn.org.atool.fluent.mybatis.refs.Ref
   */
  @RefMethod
  public MemberEntity findCurrFriend() {
    return super.invoke("findCurrFriend", true);
  }
}
