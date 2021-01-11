package org.linlinjava.litemall.db.dao;

import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallNodeRelation;

import java.util.List;
import java.util.Map;

public interface NodeRelationMapper {

    /**
     * a插入新的用户到节点关系表，集成父类所有关系，及断链情况
     */
    int insertNewNodeRelation(@Param("newUserId") Integer newUserId, @Param("parentId") Integer parentId);

    /**
     * a插入用户与邀请码这个人的关系到节点关系表，集成父类所有关系
     */
    int addParentNodeRelation(@Param("userId") Integer userId, @Param("parentId") Integer parentId);

    //批量插入数据
    int batchInsert(@Param("list") List<LitemallNodeRelation> list);

    /**
     * 查看该用户的直接下属，获取简单的用户信息
     * @param userId
     * @return
     */
    List<Map<String, Object>> subordinateUser(@Param("userId")Integer userId);

    List<Map<String, Object>> nonSubordinateUser(int userId);
}
