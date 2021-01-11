package org.linlinjava.litemall.db.service;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.db.dao.LitemallNodeRelationMapper;
import org.linlinjava.litemall.db.dao.NodeRelationMapper;
import org.linlinjava.litemall.db.domain.LitemallNodeRelation;
import org.linlinjava.litemall.db.domain.LitemallNodeRelationExample;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LitemallNodeRelationService {
    private final Log logger = LogFactory.getLog(LitemallNodeRelationService.class);

    @Resource
    private LitemallNodeRelationMapper litemallNodeRelationMapper;

    @Resource
    private NodeRelationMapper nodeRelationMapper;

    //插入新的用户到节点关系表（有父类），集成父类所有关系，及断链情况
    public void insertNewNodeRelation(Integer newUserId, Integer parentId) {
        logger.info("增加了新用户" + newUserId + "和其邀请人" + parentId + "链条关系");
        nodeRelationMapper.insertNewNodeRelation(newUserId, parentId);
    }

    //插入新的用户到节点关系表（无父类）
    public void insertNewNodeRelationNoRel(Integer newUserId) {
        LitemallNodeRelation nodeRelation = new LitemallNodeRelation();
        nodeRelation.setAncestor(newUserId);
        nodeRelation.setDescendant(newUserId);
        litemallNodeRelationMapper.insertSelective(nodeRelation);
    }

    //查询当前用户链上父节点的加盟商不包括自己
    public LitemallNodeRelation queryParentNodeByUserId(Integer userId) {
        LitemallNodeRelationExample example =new LitemallNodeRelationExample();
        example.or().andAncestorNotEqualTo(userId).andDescendantEqualTo(userId).andNodeFlagEqualTo(true);
        return litemallNodeRelationMapper.selectOneByExample(example);
    }

    //查询当前用户链上父节点的加盟商包括自己
    public LitemallNodeRelation queryAllianceByUserId(Integer userId) {
        LitemallNodeRelationExample example =new LitemallNodeRelationExample();
        example.or().andDescendantEqualTo(userId).andNodeFlagEqualTo(true);
        return litemallNodeRelationMapper.selectOneByExample(example);
    }



    /**
     * 查询当前用户链上父节点
     * @param descendant 用户id
     * @param myself 是否包含自己，包含true,不包含false
     * @return
     */
    public List<LitemallNodeRelation> queryAncestorByDescendant(Integer descendant,boolean myself) {
        LitemallNodeRelationExample example =new LitemallNodeRelationExample();
        LitemallNodeRelationExample.Criteria criteria=example.createCriteria();
        criteria.andDescendantEqualTo(descendant);
        if (!myself) {
            criteria.andDistanceNotEqualTo((byte) 0);
        }
        return litemallNodeRelationMapper.selectByExample(example);
    }

    /**
     * 查询当前用户链上子节点
     * @param Ancestor
     * @param myself
     * @return
     */
    public List<LitemallNodeRelation> queryDescendantByAncestor(Integer Ancestor,boolean myself) {
        LitemallNodeRelationExample example =new LitemallNodeRelationExample();
        LitemallNodeRelationExample.Criteria criteria=example.createCriteria();
        criteria.andAncestorEqualTo(Ancestor);
        if (!myself) {
            criteria.andDistanceNotEqualTo((byte) 0);
        }
        return litemallNodeRelationMapper.selectByExample(example);
    }

    /**
     * 加盟商的业务调用，处理关系表转变
     * @param userId
     * @param type
     */
    public void updateNodeRelation(Integer userId, boolean type) {
        //true为添加false为删除
        if (type) {
            changAlliance(userId);
        } else {
            changUserAndDescendant(userId,false);
        }
    }

    /**
     * 由普通会员变成加盟商
     * @param userId
     */
    public  void changAlliance(Integer userId){
        //查询父类链条上的加盟商
        LitemallNodeRelation nodeRelation= queryParentNodeByUserId(userId);
        //查所有的父类
        List<LitemallNodeRelation> ancestorList=queryAncestorByDescendant(userId,false);
        if (CollectionUtils.isNotEmpty(ancestorList)){
            //当前升级加盟商的用户有邀请关系链时，下游包含自己必须和祖先节点断除关系
            List<LitemallNodeRelation> descendantList=queryDescendantByAncestor(userId,true);
            List<Integer> ancestors=ancestorList.stream().map(p -> p.getAncestor()).collect(Collectors.toList());
            List<Integer> descendants=descendantList.stream().map(p -> p.getDescendant()).collect(Collectors.toList());
            LitemallNodeRelationExample example=new LitemallNodeRelationExample();
            example.or().andAncestorIn(ancestors).andDescendantIn(descendants);
            litemallNodeRelationMapper.deleteByExample(example);
        }
        if (null!=nodeRelation){
            //将子孙节点移交给加盟商且属性node_flag为true
            changAncestor(userId,nodeRelation.getAncestor());
        }
        //所有子孙节点关系中node_flag改变成true
        changUserAndDescendant(userId,true);
    }



    /**
     * 处理Node表加盟商的子孙节点数据字段属性node_flag更新为type的传参
     * @param userId
     * @param type  true变加盟商，false,变普通会员
     */
    public void changUserAndDescendant(Integer userId,boolean type){
        LitemallNodeRelation changeNodeRelation=new LitemallNodeRelation();
        changeNodeRelation.setNodeFlag(type);
        LitemallNodeRelationExample example =new LitemallNodeRelationExample();
        example.or().andAncestorEqualTo(userId);
        litemallNodeRelationMapper.updateByExampleSelective(changeNodeRelation,example);
    }


    /**
     * 将子孙节点移交给加盟商且属性node_flag为true
     * @param userId
     */
    public void changAncestor(Integer userId,Integer allianceUserId){
        LitemallNodeRelation changeNodeRelation=new LitemallNodeRelation();
        changeNodeRelation.setAncestor(allianceUserId);
        changeNodeRelation.setNodeFlag(true);
        LitemallNodeRelationExample example =new LitemallNodeRelationExample();
        example.or().andAncestorEqualTo(userId).andDistanceNotEqualTo((byte) 0);
        litemallNodeRelationMapper.updateByExampleSelective(changeNodeRelation,example);
    }

    /**
     * 查看该用户的直接下属，获取简单的用户信息
     * @param userId
     * @return
     */
    public List<Map<String, Object>> subordinateUser(int userId) {
       return nodeRelationMapper.subordinateUser(userId);
    }

    public List<Map<String, Object>> nonSubordinateUser(int userId) {
        return nodeRelationMapper.nonSubordinateUser(userId);
    }
}
