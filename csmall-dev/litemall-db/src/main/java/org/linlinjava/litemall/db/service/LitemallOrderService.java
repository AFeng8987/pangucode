package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallOrderMapper;
import org.linlinjava.litemall.db.dao.OrderMapper;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderExample;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class LitemallOrderService {
    @Resource
    private LitemallOrderMapper litemallOrderMapper;
    @Resource
    private OrderMapper orderMapper;


    public int add(LitemallOrder order) {
        order.setAddTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        return litemallOrderMapper.insertSelective(order);
    }
    public int batchInsert(List<LitemallOrder> orders) {
        return orderMapper.batchInsert(orders);
    }

    public int count(Integer userId) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return (int) litemallOrderMapper.countByExample(example);
    }

    public LitemallOrder findById(Integer orderId) {
        return litemallOrderMapper.selectByPrimaryKey(orderId);
    }


    public Map<String, Object> queryDetailById(Integer orderId) {
        return orderMapper.queryDetailById(orderId);
    }

    public LitemallOrder findById(Integer userId, Integer orderId) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andIdEqualTo(orderId).andUserIdEqualTo(userId).andDeletedEqualTo(false);
        return litemallOrderMapper.selectOneByExample(example);
    }

    private String getRandomNum(Integer num) {
        String base = "0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public int countByOrderSn(Integer userId, String orderSn) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andUserIdEqualTo(userId).andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
        return (int) litemallOrderMapper.countByExample(example);
    }

    // TODO 这里应该产生一个唯一的订单，但是实际上这里仍然存在两个订单相同的可能性
    public String generateOrderSn(Integer userId) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHMMSS");
        String now = df.format(LocalDateTime.now());
        String orderSn = now + getRandomNum(6);
        while (countByOrderSn(userId, orderSn) != 0) {
            orderSn = now + getRandomNum(6);
        }
        return orderSn;
    }

    public List<String> queryAPPListByOrderStatus(Integer userId, List<Short> orderStatus, Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        return orderMapper.queryAppList(userId, orderStatus);
    }

    public List<Map<String, Object>> querySelective(String consignee, String orderSn,String payOrderSn,String userName, LocalDate start, LocalDate end, List<Short> orderStatusArray, Integer page, Integer limit) {
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (start != null) {
            startTime = start.atStartOfDay();
        }
        if (end != null) {
            endTime = end.atTime(LocalTime.MAX);
        }
        PageHelper.startPage(page, limit);
        return orderMapper.queryOrder(consignee, orderSn, payOrderSn,userName, startTime, endTime, orderStatusArray);
    }

    public int updateWithOptimisticLocker(LitemallOrder order) {
        LocalDateTime preUpdateTime = order.getUpdateTime();
        order.setUpdateTime(LocalDateTime.now());
        return orderMapper.updateWithOptimisticLocker(preUpdateTime, order);
    }

    public void deleteById(Integer id) {
        litemallOrderMapper.logicalDeleteByPrimaryKey(id);
    }

    public int count() {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andDeletedEqualTo(false);

        return (int) litemallOrderMapper.countByExample(example);
    }

    public List<LitemallOrder> queryUnpaid(int minutes) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andOrderStatusEqualTo(OrderUtil.STATUS_CREATE).andDeletedEqualTo(false);
        return litemallOrderMapper.selectByExample(example);
    }

    public List<LitemallOrder> queryUnconfirm(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.minusDays(days);
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andOrderStatusEqualTo(OrderUtil.STATUS_SHIP).andShipTimeLessThanOrEqualTo(expired).andDeletedEqualTo(false);
        return litemallOrderMapper.selectByExample(example);
    }

    public List<LitemallOrder> queryClose() {
        LocalDateTime now = LocalDateTime.now();
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andOrderStatusBetween(OrderUtil.STATUS_CONFIRM, OrderUtil.STATUS_AUTO_CONFIRM).andEndTimeLessThanOrEqualTo(now).andDeletedEqualTo(false);
        return litemallOrderMapper.selectByExample(example);
    }

    public List<LitemallOrder> queryCloseOrder() {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andOrderStatusEqualTo(OrderUtil.STATUS_CLOSE).andDeletedEqualTo(false).andIsBenefitEqualTo(false);
        return litemallOrderMapper.selectByExample(example);
    }

    public LitemallOrder findBySn(String orderSn) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andOrderSnEqualTo(orderSn).andDeletedEqualTo(false);
        return litemallOrderMapper.selectOneByExample(example);
    }

    public List<LitemallOrder> findByPaySnAndStatus(String payOrderSn, int status) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andPayOrderSnEqualTo(payOrderSn).andDeletedEqualTo(false).andOrderStatusEqualTo((short) status);
        return litemallOrderMapper.selectByExample(example);
    }
    public List<LitemallOrder> findByPaySn(String payOrderSn) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andPayOrderSnEqualTo(payOrderSn).andDeletedEqualTo(false);
        return litemallOrderMapper.selectByExample(example);
    }

    public List<LitemallOrder> findByPaySn(String payOrderSn, Integer userId) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andUserIdEqualTo(userId).andPayOrderSnEqualTo(payOrderSn).andDeletedEqualTo(false);
        return litemallOrderMapper.selectByExample(example);
    }

    public Map<Object, Object> orderInfo(Integer userId) {
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andUserIdEqualTo(userId).andDeletedEqualTo(false);
        List<LitemallOrder> orders = litemallOrderMapper.selectByExampleSelective(example, LitemallOrder.Column.orderStatus);
        int unpaid = 0;
        int unship = 0;
        int unrecv = 0;
        if (!CollectionUtils.isEmpty(orders)) {
            for (LitemallOrder order : orders) {
                if (OrderUtil.isCreateStatus(order)) {
                    unpaid++;
                } else if (OrderUtil.isPayStatus(order)) {
                    unship++;
                } else if (OrderUtil.isShipStatus(order)) {
                    unrecv++;
                } else {
                    // do nothing
                }
            }
        }
        Map<Object, Object> orderInfo = new HashMap<Object, Object>();
        orderInfo.put("unpaid", unpaid);
        orderInfo.put("unship", unship);
        orderInfo.put("unrecv", unrecv);
        return orderInfo;

    }

    public List<LitemallOrder> queryComment(int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.minusDays(days);
        LitemallOrderExample example = new LitemallOrderExample();
        example.or().andDeletedEqualTo(false);
        return litemallOrderMapper.selectByExample(example);
    }

    public List<Map<String, Object>> deliveryList(String consignee, String orderSn, String payOrderSn,Integer plantId, LocalDate start, LocalDate end, Boolean deliveryStatus, Integer page, Integer limit) {

        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (start != null) {
            startTime = start.atStartOfDay();
        }
        if (end != null) {
            endTime = end.atTime(LocalTime.MAX);
        }
        PageHelper.startPage(page, limit);
        return orderMapper.queryOrderDelivery(consignee, orderSn, payOrderSn,plantId, startTime, endTime, deliveryStatus);
    }

    public int updateById(LitemallOrder order) {
        order.setUpdateTime(LocalDateTime.now());
        return litemallOrderMapper.updateByPrimaryKey(order);
    }

    public int reminderShipment(List<Integer> orderIds) {
        LitemallOrderExample example=new LitemallOrderExample();
        example.or().andIdIn(orderIds);
        LitemallOrder order=new LitemallOrder();
        order.setReminderShipment(true);
        return litemallOrderMapper.updateByExampleSelective(order,example);
    }


    public Map<String, Object> countDeliveryList(String consignee, String orderSn,String payOrderSn, Integer plantId, LocalDate start, LocalDate end) {
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        if (start != null) {
            startTime = start.atStartOfDay();
        }
        if (end != null) {
            endTime = end.atTime(LocalTime.MAX);
        }
        return orderMapper.countOrderDelivery(consignee, orderSn, payOrderSn, plantId, startTime, endTime);
    }
}
