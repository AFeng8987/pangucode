package org.linlinjava.litemall.db.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallGoodsActivity;
import org.linlinjava.litemall.db.domain.LitemallGoodsActivityExample;

public interface LitemallGoodsActivityMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_goods_activity
     *
     * @mbg.generated
     */
    long countByExample(LitemallGoodsActivityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_goods_activity
     *
     * @mbg.generated
     */
    int deleteByExample(LitemallGoodsActivityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_goods_activity
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_goods_activity
     *
     * @mbg.generated
     */
    int insert(LitemallGoodsActivity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_goods_activity
     *
     * @mbg.generated
     */
    int insertSelective(LitemallGoodsActivity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_goods_activity
     *
     * @mbg.generated
     */
    LitemallGoodsActivity selectOneByExample(LitemallGoodsActivityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_goods_activity
     *
     * @mbg.generated
     */
    LitemallGoodsActivity selectOneByExampleSelective(@Param("example") LitemallGoodsActivityExample example, @Param("selective") LitemallGoodsActivity.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_goods_activity
     *
     * @mbg.generated
     */
    List<LitemallGoodsActivity> selectByExampleSelective(@Param("example") LitemallGoodsActivityExample example, @Param("selective") LitemallGoodsActivity.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_goods_activity
     *
     * @mbg.generated
     */
    List<LitemallGoodsActivity> selectByExample(LitemallGoodsActivityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_goods_activity
     *
     * @mbg.generated
     */
    LitemallGoodsActivity selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") LitemallGoodsActivity.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_goods_activity
     *
     * @mbg.generated
     */
    LitemallGoodsActivity selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_goods_activity
     *
     * @mbg.generated
     */
    LitemallGoodsActivity selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_goods_activity
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") LitemallGoodsActivity record, @Param("example") LitemallGoodsActivityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_goods_activity
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") LitemallGoodsActivity record, @Param("example") LitemallGoodsActivityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_goods_activity
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(LitemallGoodsActivity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_goods_activity
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(LitemallGoodsActivity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_goods_activity
     *
     * @mbg.generated
     */
    int logicalDeleteByExample(@Param("example") LitemallGoodsActivityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_goods_activity
     *
     * @mbg.generated
     */
    int logicalDeleteByPrimaryKey(Integer id);
}