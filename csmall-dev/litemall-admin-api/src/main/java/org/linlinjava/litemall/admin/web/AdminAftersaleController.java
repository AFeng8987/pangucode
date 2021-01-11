package org.linlinjava.litemall.admin.web;

import com.alibaba.excel.EasyExcel;
import com.github.binarywang.wxpay.service.WxPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.service.AdminAfterSaleService;
import org.linlinjava.litemall.admin.service.LogHelper;
import org.linlinjava.litemall.admin.util.AdminResponseCode;
import org.linlinjava.litemall.admin.util.UploadDataListener;
import org.linlinjava.litemall.admin.vo.AfterSaleImportVo;
import org.linlinjava.litemall.core.notify.NotifyService;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallAftersale;
import org.linlinjava.litemall.db.service.LitemallAftersaleService;
import org.linlinjava.litemall.db.service.LitemallOrderGoodsService;
import org.linlinjava.litemall.db.service.LitemallProductFactoryService;
import org.linlinjava.litemall.db.util.AfterSaleUtil;
import org.linlinjava.litemall.db.util.AftersaleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/afterSale")
@Validated
@Api(value="退款售后",tags={"退款售后接口"})
//@ApiIgnore
public class AdminAftersaleController {
    private final Log logger = LogFactory.getLog(AdminAftersaleController.class);

    @Autowired
    private LitemallAftersaleService afterSaleService;
    @Autowired
    private AdminAfterSaleService adminAfterSaleService;

    @Autowired
    private LogHelper logHelper;


    @RequiresPermissions("admin:afterSale:list")
    @RequiresPermissionsDesc(menu = {"订单管理", "退款审核"}, button = "查询")
    @GetMapping("/list")
    public Object list(String orderSn,String payOrderSn, String afterSaleSn,
                       Integer refundStatus,Integer serviceType,String payId,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        List<Map<String,Object>> afterSaleList = afterSaleService.querySelective(orderSn,  payOrderSn,afterSaleSn, refundStatus, serviceType,payId,start,end,page, limit);
        Map<String,Object> map=afterSaleService.queryCountByStatus(orderSn,  payOrderSn,afterSaleSn,serviceType,payId,start,end);
        return ResponseUtil.okList(afterSaleList,map);
    }

    /**
     * 详情
     */
    @ApiOperation(value="退货审核详情",notes="退货审核详情")
    @RequiresPermissions("admin:afterSale:detail")
    @RequiresPermissionsDesc(menu = {"订单管理", "退款审核"}, button = "详情")
    @GetMapping("/detail/{id}")
    public Object detail(@PathVariable("id") Integer id) {

        logHelper.logOrderSucceed("查询订单退货审核详情");
        return ResponseUtil.ok(afterSaleService.detail(id));
    }

    @RequiresPermissions("admin:afterSale:returnGoodAudit")
    @RequiresPermissionsDesc(menu = {"订单管理", "退款审核"}, button = "退货审核")
    @PostMapping("/returnGoodAudit")
    public Object returnGoodAudit( @RequestBody LitemallAftersale afterSale) {
        Integer id = afterSale.getId();
        Integer checkStatus = afterSale.getStatus();
        String rejectReason = afterSale.getRejectReason();
        LitemallAftersale afterSaleOne = afterSaleService.findById(id);
        if(afterSaleOne == null){
            return ResponseUtil.fail(AdminResponseCode.AFTERSALE_NOT_ALLOWED, "售后退货退款不存在");
        }
        int status = afterSaleOne.getStatus();
        if(AfterSaleUtil.STATUS_GOODS_NOT_RECEIVED!=status&&AfterSaleUtil.STATUS_GOODS_RECEIVED!=status){
            return ResponseUtil.fail(AdminResponseCode.AFTERSALE_NOT_ALLOWED, "该订单不能进行退货审核操作");
        }
        switch (checkStatus){
            case AfterSaleUtil.STATUS_RETURN_ALLOWED:
                afterSaleOne.setStatus(AfterSaleUtil.STATUS_RETURN_ALLOWED);
                break;
            case AfterSaleUtil.STATUS_REJECT_RETURN:
                if (StringUtils.isBlank(rejectReason)){
                    return ResponseUtil.badArgumentValue("驳回原因必填");
                }
                afterSaleOne.setStatus(AfterSaleUtil.STATUS_REJECT_RETURN);
                afterSaleOne.setRejectReason(rejectReason);
                break;
            default:
                return ResponseUtil.fail(AdminResponseCode.AFTERSALE_DEFAULT, "未知操作");
        }
        adminAfterSaleService.returnApproval(afterSaleOne);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:afterSale:batch-returnGoodAudit")
    @RequiresPermissionsDesc(menu = {"订单管理", "退款审核"}, button = "批量退货审核")
    @PostMapping("/batch-returnGoodAudit")
    public Object batchReturnGoodAudit(@RequestBody String body) {
        List<Integer> ids = JacksonUtil.parseIntegerList(body, "ids");
        int checkStatus = JacksonUtil.parseInteger(body, "status");
        String rejectReason = JacksonUtil.parseString(body, "rejectReason");
        switch (checkStatus){
            case AfterSaleUtil.STATUS_RETURN_ALLOWED:
                break;
            case AfterSaleUtil.STATUS_REJECT_RETURN:
                if (StringUtils.isBlank(rejectReason)){
                    return ResponseUtil.badArgumentValue("驳回原因必填");
                }
                break;
            default:
                return ResponseUtil.fail(AdminResponseCode.AFTERSALE_DEFAULT, "未知操作");
        }
        for(Integer id : ids) {
            LitemallAftersale afterSale = afterSaleService.findById(id);
            if(afterSale == null){
                continue;
            }
            int status = afterSale.getStatus();
            if(AftersaleConstant.STATUS_WAIT_RECEIVE!=status&&AftersaleConstant.STATUS_RECEIVE!=status){
                continue;
            }
            afterSale.setStatus(checkStatus);
            afterSale.setRejectReason(rejectReason);
            adminAfterSaleService.returnApproval(afterSale);
        }
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:afterSale:returnAudit")
    @RequiresPermissionsDesc(menu = {"订单管理", "退款审核"}, button = "退款审核")
    @PostMapping("/returnAudit")
    public Object returnAudit(@RequestBody LitemallAftersale afterSale) {
        Integer id = afterSale.getId();
        Integer checkStatus = afterSale.getStatus();
        String rejectReason = afterSale.getRejectReason();
        LitemallAftersale afterSaleOne = afterSaleService.findById(id);
        if(afterSaleOne == null){
            return ResponseUtil.fail(AdminResponseCode.AFTERSALE_NOT_ALLOWED, "售后退款不存在");
        }
        if (afterSaleOne.getServiceType().equals(2)&&!afterSaleOne.getConfirmReturn()){
            return ResponseUtil.fail(AdminResponseCode.AFTERSALE_NOT_ALLOWED, "请先确认收到退货!");
        }
        int status = afterSaleOne.getStatus();
        if(AftersaleConstant.STATUS_REQUEST!=status){
            return ResponseUtil.fail(AdminResponseCode.AFTERSALE_NOT_ALLOWED, "该订单不能进行退款审核操作");
        }
        switch (checkStatus){
            case AftersaleConstant.STATUS_REFUND:
                afterSaleOne.setStatus(AftersaleConstant.STATUS_REFUND);
                break;
            case AftersaleConstant.STATUS_REJECT:
                if (StringUtils.isBlank(rejectReason)){
                    return ResponseUtil.badArgumentValue("驳回原因必填");
                }
                afterSaleOne.setStatus(AftersaleConstant.STATUS_REJECT);
                afterSaleOne.setRejectReason(rejectReason);
                break;
            default:
                return ResponseUtil.fail(AdminResponseCode.AFTERSALE_DEFAULT, "售后未知操作");
        }
        afterSaleOne.setHandleTime(LocalDateTime.now());
        adminAfterSaleService.refundApproval(afterSaleOne);
        return ResponseUtil.ok();
    }

    @RequiresPermissions("admin:afterSale:batch-returnAudit")
    @RequiresPermissionsDesc(menu = {"订单管理", "退款审核"}, button = "批量退款审核")
    @PostMapping("/batch-returnAudit")
    public Object batchReturnAudit(@RequestBody String body) {
        List<Integer> ids = JacksonUtil.parseIntegerList(body, "ids");
        int checkStatus = JacksonUtil.parseInteger(body, "status");
        String rejectReason = JacksonUtil.parseString(body, "rejectReason");
        switch (checkStatus){
            case AftersaleConstant.STATUS_REFUND:
                break;
            case AftersaleConstant.STATUS_REJECT:
                if (StringUtils.isBlank(rejectReason)){
                    return ResponseUtil.badArgumentValue("驳回原因必填");
                }
                break;
            default:
                return ResponseUtil.fail(AdminResponseCode.AFTERSALE_DEFAULT, "未知操作");
        }
        for(Integer id : ids) {
            LitemallAftersale afterSale = afterSaleService.findById(id);
            if(afterSale == null||AftersaleConstant.STATUS_REQUEST!= afterSale.getStatus()){
                continue;
            }
            //批量退款审核时，退货退款的售后单，必须确认收到退货。
            if (afterSale.getServiceType().equals(2)&&!afterSale.getConfirmReturn()){
                continue;
            }
            afterSale.setStatus(checkStatus);
            afterSale.setRejectReason(rejectReason);
            afterSale.setHandleTime(LocalDateTime.now());
            adminAfterSaleService.refundApproval(afterSale);
        }
        return ResponseUtil.ok();
    }


    @RequiresPermissions("admin:afterSale:updateStatus")
    @RequiresPermissionsDesc(menu = {"订单管理", "退款审核"}, button = "手动修改退款状态")
    @PostMapping("/updateStatus")
    public Object updateStatus(@RequestBody String body) {
        String afterSaleSn = JacksonUtil.parseString(body, "afterSaleSn");
        return adminAfterSaleService.updateStatus(afterSaleSn);
    }

    @RequiresPermissions("admin:afterSale:confirmReturn")
    @RequiresPermissionsDesc(menu = {"订单管理", "退款审核"}, button = "确认收到退货")
    @PostMapping("/confirm")
    public Object confirmReturn(@RequestBody String body) {
        String afterSaleSn = JacksonUtil.parseString(body, "afterSaleSn");
        return adminAfterSaleService.update(afterSaleSn);
    }
     /*   @RequiresPermissions("admin:afterSale:listExport")
    @RequiresPermissionsDesc(menu = {"订单管理", "退款审核"}, button = "数据导出")
    @PostMapping("/list/export")
    public Object listExport(@RequestBody OrderExportVo orderExportVo, HttpServletResponse response) {
        switch(orderExportVo.getType().toUpperCase()){
            case "ALL":
                //如果是全部导出，查询参数不能为空，否则不允许导出
                break;
            case "PART":
                //部分导出
                if(orderExportVo.getIds() == null || orderExportVo.getIds().size() == 0) {
                    return ResponseUtil.badArgumentValue();
                }
                break;
            default:
                return ResponseUtil.badArgumentValue();
        }
        adminAfterSaleService.listExport(orderExportVo.getIds(),orderExportVo.getOrderSn(),orderExportVo.getAfterSaleSn(),orderExportVo.getRefundStatus(),orderExportVo.getServiceType(),orderExportVo.getPayId(),orderExportVo.getStart(),orderExportVo.getEnd(),response);
        return null;
    }*/
 /*@RequiresPermissions("admin:afterSale:listImport")
    @RequiresPermissionsDesc(menu = {"订单管理", "退款审核"}, button = "数据导入")
    @PostMapping("/list/import")
    public String upload(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(), AfterSaleImportVo.class, new UploadDataListener(afterSaleService,logHelper)).sheet().doRead();
        return "success";
    }*/

}
