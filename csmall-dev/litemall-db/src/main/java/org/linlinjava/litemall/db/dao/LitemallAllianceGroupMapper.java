package org.linlinjava.litemall.db.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallAllianceGroup;
import org.linlinjava.litemall.db.domain.LitemallAllianceGroupExample;

public interface LitemallAllianceGroupMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_group
     *
     * @mbg.generated
     */
    long countByExample(LitemallAllianceGroupExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_group
     *
     * @mbg.generated
     */
    int deleteByExample(LitemallAllianceGroupExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_group
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_group
     *
     * @mbg.generated
     */
    int insert(LitemallAllianceGroup record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_group
     *
     * @mbg.generated
     */
    int insertSelective(LitemallAllianceGroup record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_group
     *
     * @mbg.generated
     */
    LitemallAllianceGroup selectOneByExample(LitemallAllianceGroupExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_group
     *
     * @mbg.generated
     */
    LitemallAllianceGroup selectOneByExampleSelective(@Param("example") LitemallAllianceGroupExample example, @Param("selective") LitemallAllianceGroup.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_group
     *
     * @mbg.generated
     */
    List<LitemallAllianceGroup> selectByExampleSelective(@Param("example") LitemallAllianceGroupExample example, @Param("selective") LitemallAllianceGroup.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_group
     *
     * @mbg.generated
     */
    List<LitemallAllianceGroup> selectByExample(LitemallAllianceGroupExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_group
     *
     * @mbg.generated
     */
    LitemallAllianceGroup selectByPrimaryKeySelective(@Param("id") Integer id, @Param("selective") LitemallAllianceGroup.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_group
     *
     * @mbg.generated
     */
    LitemallAllianceGroup selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_group
     *
     * @mbg.generated
     */
    LitemallAllianceGroup selectByPrimaryKeyWithLogicalDelete(@Param("id") Integer id, @Param("andLogicalDeleted") boolean andLogicalDeleted);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_group
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") LitemallAllianceGroup record, @Param("example") LitemallAllianceGroupExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_group
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") LitemallAllianceGroup record, @Param("example") LitemallAllianceGroupExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_group
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(LitemallAllianceGroup record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_group
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(LitemallAllianceGroup record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_group
     *
     * @mbg.generated
     */
    int logicalDeleteByExample(@Param("example") LitemallAllianceGroupExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_alliance_group
     *
     * @mbg.generated
     */
    int logicalDeleteByPrimaryKey(@Param("id") Integer id);

    LitemallAllianceGroup selByAddress(@Param("address") String address);

    List<String> groupName();

}
