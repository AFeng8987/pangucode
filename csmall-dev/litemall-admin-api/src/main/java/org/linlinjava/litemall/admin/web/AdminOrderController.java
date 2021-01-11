package org.linlinjava.litemall.admin.web;

import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.service.AdminOrderService;
import org.linlinjava.litemall.admin.vo.OrderExportVo;
import org.linlinjava.litemall.core.express.ExpressService;
import org.linlinjava.litemall.core.notify.NotifyService;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/order")
@Validated
public class AdminOrderController {
    private final Log logger = LogFactory.getLog(AdminOrderController.class);

    @Autowired
    private AdminOrderService adminOrderService;
    @Autowired
    private ExpressService expressService;

    /**
     * 查询订单
     *
     * @param consignee
     * @param orderSn
     * @param orderStatusArray
     * @param page
     * @param limit
     * @return
     */
    @RequiresPermissions("admin:order:list")
    @RequiresPermissionsDesc(menu = {"订单管理", "订单列表"}, button = "查询")
    @GetMapping("/list")
    public Object list(String consignee, String orderSn,String userName,String payOrderSn,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                       @RequestParam(required = false) List<Short> orderStatusArray,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        logger.info("订单列表查询,参数：收货人："+consignee+"，子订单号："+orderSn+"，用户账号："+userName+"，主订单号："+payOrderSn);
        return adminOrderService.list(consignee, orderSn, payOrderSn,userName,start, end, orderStatusArray, page, limit);
    }


    /**
     * 订单详情
     *
     * @param id
     * @return
     */
    @RequiresPermissions("admin:order:read")
    @RequiresPermissionsDesc(menu = {"订单管理", "订单列表"}, button = "详情")
    @GetMapping("/detail")
    public Object detail(@NotNull Integer id) {
        return adminOrderService.detail(id);
    }

    /**
     * 订单退款
     * @return 订单退款操作结果
     */
    @RequiresPermissions("admin:orderExport:list")
    @RequiresPermissionsDesc(menu = {"订单管理", "订单导出"}, button = "列表查询")
    @GetMapping("/export/list")
    @ApiOperation(value = "分润订单查询")
    public Object orderExportList(String orderSn,String payOrderSn,String allianceName,String userName,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate orderAddStart,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate orderAddEnd,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate orderCloseStart,
                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate orderCloseEnd,
                                  @RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer limit) {
        logger.info("分润订单查询,参数：加盟商姓名："+allianceName+"，子订单号："+orderSn+"，用户账号："+userName+"，主订单号："+payOrderSn);
        return adminOrderService.orderExportList(orderSn, payOrderSn,allianceName,userName,orderAddStart,orderAddEnd,orderCloseStart,orderCloseEnd,page,limit);
    }

    /**
     * 订单退款
     * @return 订单退款操作结果
     */
    @RequiresPermissions("admin:orderExport:listExport")
    @RequiresPermissionsDesc(menu = {"订单管理", "订单导出"}, button = "导出")
    @PostMapping("/export")
    @ApiOperation(value = "分润订单导出")
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
        adminOrderService.listExport(orderExportVo.getIds(),orderExportVo.getOrderSn(),orderExportVo.getPayOrderSn(),orderExportVo.getAllianceName(),orderExportVo.getUserName(),orderExportVo.getOrderAddStart(),orderExportVo.getOrderAddEnd(),orderExportVo.getOrderCloseStart(),orderExportVo.getOrderCloseEnd(),response);
        return null;
    }



}
