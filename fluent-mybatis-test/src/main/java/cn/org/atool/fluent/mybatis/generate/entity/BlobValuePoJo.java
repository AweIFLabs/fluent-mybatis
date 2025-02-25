package cn.org.atool.fluent.mybatis.generate.entity;

import cn.org.atool.fluent.mybatis.annotation.FluentMybatis;
import cn.org.atool.fluent.mybatis.annotation.TableField;
import cn.org.atool.fluent.mybatis.annotation.TableId;
import cn.org.atool.fluent.mybatis.base.IEntity;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import cn.org.atool.fluent.mybatis.customize.ICustomizedMapper;
import cn.org.atool.fluent.mybatis.functions.TableSupplier;
import java.io.Serializable;
import java.util.function.Consumer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.ibatis.type.BlobTypeHandler;

/**
 * BlobValuePoJo: 数据映射实体定义
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
    table = "blob_value",
    schema = "fluent_mybatis",
    superMapper = ICustomizedMapper.class,
    suffix = "PoJo"
)
public class BlobValuePoJo extends RichEntity {
  private static final long serialVersionUID = 1L;

  /**
   * 主键id
   */
  @TableId("id")
  private Long id;

  /**
   */
  @TableField(
      value = "blob_value",
      typeHandler = BlobTypeHandler.class
  )
  private byte[] blobValue;

  /**
   */
  @TableField("max")
  private Long max;

  /**
   */
  @TableField("min")
  private Long min;

  /**
   */
  @TableField("origin")
  private String origin;

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
    return BlobValuePoJo.class;
  }

  @Override
  public final BlobValuePoJo changeTableBelongTo(TableSupplier supplier) {
    return super.changeTableBelongTo(supplier);
  }

  @Override
  public final BlobValuePoJo changeTableBelongTo(String table) {
    return super.changeTableBelongTo(table);
  }
}
