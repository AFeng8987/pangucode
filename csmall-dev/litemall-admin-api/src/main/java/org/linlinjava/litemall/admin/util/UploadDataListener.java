package org.linlinjava.litemall.admin.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.linlinjava.litemall.admin.service.LogHelper;
import org.linlinjava.litemall.admin.vo.AfterSaleImportVo;
import org.linlinjava.litemall.db.dao.AftersaleMapper;
import org.linlinjava.litemall.db.domain.LitemallAdmin;
import org.linlinjava.litemall.db.domain.LitemallAftersale;
import org.linlinjava.litemall.db.service.LitemallAftersaleService;
import org.linlinjava.litemall.db.service.LitemallAllianceSaleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 导入模板读取类
 * @author chenruide
 *
 */
public class UploadDataListener extends AnalysisEventListener<AfterSaleImportVo>{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadDataListener.class);
    
    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5;
    List<LitemallAftersale> list = new ArrayList<>();

    /**
     * 假设这个是一个DAO，当然有业务逻辑这个也可以是一个service。当然如果不用存储这个对象没用。
     */
    private LitemallAftersaleService litemallAftersaleService;

    private LogHelper logHelper;

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     *
     * @param litemallAftersaleService
     */
    public UploadDataListener(LitemallAftersaleService litemallAftersaleService, LogHelper logHelper) {
        this.litemallAftersaleService = litemallAftersaleService;
        this.logHelper = logHelper;
    }


    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(AfterSaleImportVo data, AnalysisContext context) {
        LOGGER.info("解析到一条数据:{}", JSONObject.fromObject(data).toString());
        LitemallAftersale afterSale = new LitemallAftersale();
        if (StringUtils.isBlank(data.getAfterSaleSn())) {
            return;
        }
        switch (data.getStatusName()) {
            case "退款成功":
                afterSale.setStatus(5);
                break;
            default:
                return;
        }
        afterSale.setAftersaleSn(data.getAfterSaleSn());//售后单号
        afterSale.setUpdateTime(LocalDateTime.now()); //数据最后更新时间
        //afterSale.setReturnPayId(data.getReturnPayId());//上游退款单号
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser != null) {
            LitemallAdmin admin = (LitemallAdmin) currentUser.getPrincipal();
            if (admin != null) {
                afterSale.setLastOperator(admin.getId());
            }
        }

        list.add(afterSale);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (list.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            list.clear();
        }
    }




    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        LOGGER.info("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        LOGGER.info("{}条数据，开始存储数据库！", list.size());
       for (LitemallAftersale record : list) {
            Map<String, Object> map = new HashMap<>();
            try {
                map = updateData(record, map);
            } catch (Exception e) {
                LOGGER.info("-------------导入数据解析存储到数据库异常--------------" + e.getMessage());
                e.printStackTrace();
                map.put("code", -1);
                map.put("message", "本条数据处理出现异常");
            } finally {
                int code = (int) map.get("code");
                String afterSaleSn = map.get("afterSaleSn").toString();
                String type = map.get("type").toString();
                String message = map.get("message").toString();
                switch(code){
                    case 0:
                        logHelper.logOrderSucceed("人工退款--导入数据" , "将-退款中-状态更新为:"+type, message+"，退款单号-"+afterSaleSn);
                        break;
                    case -1:
                        logHelper.logOrderFail("人工退款--导入数据", "非退款中的数据" ,message+"，退款单号-"+afterSaleSn);
                        break;
                    case 1:
                        logHelper.logOrderFail("人工退款--导入数据", "数据更新失败:"+type ,message+"，退款单号-"+afterSaleSn);
                        break;
                    default:
                        break;
                }
            }
        }
        LOGGER.info("存储数据库成功！");
    }

    /**
     * 加上存储数据库
     */
    @Transactional
    public Map<String, Object> updateData(LitemallAftersale record, Map<String, Object> map) {
        LitemallAftersale afterSale = litemallAftersaleService.selectOneByAfterSaleSn(record.getAftersaleSn());
        if (null!=afterSale){
            LOGGER.info("处理退款单号"+record.getAftersaleSn()+"----------开始----------");
        }
        String payStatus = record.getStatus() == 5 ? "退款成功" : "未知操作";
        map.put("afterSaleSn", record.getAftersaleSn());
        map.put("code", 0);
        map.put("type", payStatus);
        if (null == afterSale) {
            map.put("message", "该提现单不存在");
            map.put("code", -1);
            return map;
        }
        if (afterSale.getStatus() != 3) {
            //当前非退款中的提现单，数据不做处理
            map.put("message", "导入数据-非待退款，不做处理");
            map.put("code", -1);
            return map;
        }

        if (0 == litemallAftersaleService.updateData(record)) {
            map.put("code", 1);
            map.put("message", "处理失败-原因：数据更新失败");
        } else {
            LOGGER.info("处理退款单号"+afterSale.getAftersaleSn()+"成功");
            map.put("message", "处理成功");
        }
        return map;
    }


    
}
