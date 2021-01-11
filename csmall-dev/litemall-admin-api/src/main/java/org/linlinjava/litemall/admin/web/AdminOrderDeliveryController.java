package org.linlinjava.litemall.admin.web;

import cn.hutool.log.StaticLog;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.admin.service.AdminOrderService;
import org.linlinjava.litemall.core.express.ExpressService;
import org.linlinjava.litemall.core.express.SfExpressService;
import org.linlinjava.litemall.core.express.dao.ExpressInfo;
import org.linlinjava.litemall.core.express.dao.ShipperInfo;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/delivery")
@Validated
public class AdminOrderDeliveryController {
    private final Log logger = LogFactory.getLog(AdminOrderDeliveryController.class);

    @Autowired
    private AdminOrderService adminOrderService;
    @Autowired
    private ExpressService expressService;


    /**
     * 订单发货，根据工厂数据分离
     *
     * @param consignee
     * @param orderSn
     * @param plantId 工厂ID
     * @param deliveryStatus
     * @param page
     * @param limit
     * @return
     */
    @RequiresPermissions("admin:delivery:list")
    @RequiresPermissionsDesc(menu = {"订单发货", "订单发货"}, button = "查询")
    @GetMapping("/list")
    public Object list(String consignee, String orderSn,String payOrderSn,Integer plantId,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate start,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end,
                       @RequestParam(required = false) Boolean deliveryStatus,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        return adminOrderService.deliveryList(consignee, orderSn,payOrderSn,plantId, start, end, deliveryStatus, page, limit);
    }

    /**
     * 当前订单的货品清单
     */
    @RequiresPermissions("admin:delivery:goodsDetail")
    @RequiresPermissionsDesc(menu = {"订单发货", "订单发货"}, button = "货品清单")
    @GetMapping("/goodsDetail")
    public Object goodsDetail(@NotNull Integer id) {
        return adminOrderService.goodsDetail(id);
    }


    /**
     * 发货
     *
     * @param body 订单信息，{ orderId：xxx, shipSn: xxx, shipChannel: xxx }
     * @return 订单操作结果
     */
    @RequiresPermissions("admin:delivery:ship")
    @RequiresPermissionsDesc(menu = {"订单发货", "订单发货"}, button = "发货")
    @PostMapping("/ship")
    public Object ship(@RequestBody String body) {
        return adminOrderService.ship(body);
    }

    /**
     * 物流单号识别
     * @param discernNum 识别订单号
     * @return 订单操作结果
     */
    @RequiresPermissions("admin:delivery:discern")
    @RequiresPermissionsDesc(menu = {"订单发货", "订单发货"}, button = "物流单号识别")
    @GetMapping("/{discernNum}")
    public Object discern(@PathVariable String discernNum) {
        return  ResponseUtil.ok(expressService.getShippersInfo(discernNum));
    }


}
