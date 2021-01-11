package org.linlinjava.litemall.wx.web;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.util.RegexUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallAddress;
import org.linlinjava.litemall.db.service.LitemallAddressService;
import org.linlinjava.litemall.db.service.LitemallRegionService;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.service.GetRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 用户收货地址服务
 */
@RestController
@RequestMapping("/wx/address")
@Validated
public class WxAddressController extends GetRegionService {
	private final Log logger = LogFactory.getLog(WxAddressController.class);

	@Autowired
	private LitemallAddressService addressService;



	/**
	 * 用户收货地址列表
	 *
	 * @param userId 用户ID
	 * @return 收货地址列表
	 */
	@GetMapping("list")
	public Object list(@LoginUser Integer userId) {
		if (userId == null) {
			return ResponseUtil.unlogin();
		}
		logger.info("用户id:"+userId+"正在查询收货地址");
		List<LitemallAddress> addressList = addressService.queryByUid(userId);
		return ResponseUtil.okList(addressList);
	}

	/**
	 * 收货地址详情
	 *
	 * @param userId 用户ID
	 * @param id     收货地址ID
	 * @return 收货地址详情
	 */
	@GetMapping("detail")
	public Object detail(@LoginUser Integer userId, @NotNull Integer id) {
		if (userId == null) {
			return ResponseUtil.unlogin();
		}
		logger.info("用户id:"+userId+"正在查询收货地址详情ID："+id);
		LitemallAddress address = addressService.query(userId, id);
		if (ObjectUtils.isEmpty(address)) {
			return ResponseUtil.badArgumentValue("该地址不存在!");
		}
		return ResponseUtil.ok(address);
	}

	private Object validate(LitemallAddress address) {
		String name = address.getName();
		if (StringUtils.isAnyBlank(name,address.getTel()))
			return ResponseUtil.badArgumentValue("收货人姓名和电话不能为空");
		if (!RegexUtil.isMobileExact(address.getTel()))
			return ResponseUtil.badArgumentValue("手机号码格式错误");
		if (!RegexUtil.isUsername(name)||name.length()>20)
			return ResponseUtil.badArgumentValue("收货人姓名仅支持{英文、数字、_、中文'}且长度不能超过20不能少于2");
		if (StringUtils.isAnyBlank(address.getProvince(),address.getCity(),address.getCounty(),address.getStreet(),address.getAddressDetail())||address.getAddressDetail().length()>100)
			return ResponseUtil.badArgumentValue("省市区街道详细地址都必填且详细地址长度不能超过100");
		if (ObjectUtils.isEmpty(address.getIsDefault()))
			return ResponseUtil.badArgumentValue("是否默认地址不能传空");
		return null;
	}

	/**
	 * 添加收货地址
	 *
	 * @param userId  用户ID
	 * @param address 用户收货地址
	 * @return 添加或更新操作结果
	 */
	@PostMapping("save")
	public Object save(@LoginUser Integer userId, @RequestBody LitemallAddress address) {
		if (userId == null) {
			return ResponseUtil.unlogin();
		}
		Object error = validate(address);
		if (error != null) {
			return error;
		}
		logger.info("用户id:"+userId+",正在添加收货地址，收货人："+ address.getName());
		address.setUserId(userId);
		addressService.add(address);
		return ResponseUtil.ok(address.getId());
	}
	/**
	 * 添加或更新收货地址
	 *
	 * @param userId  用户ID
	 * @param address 用户收货地址
	 * @return 添加或更新操作结果
	 */
	@PostMapping("update")
	public Object update(@LoginUser Integer userId, @RequestBody LitemallAddress address) {
		if (userId == null) {
			return ResponseUtil.unlogin();
		}
		Object error = validate(address);
		if (error != null) {
			return error;
		}
		if (ObjectUtils.isEmpty(address.getId())){
			return  ResponseUtil.badArgumentValue("编辑地址时，ID不能为空");
		}
		address.setUserId(userId);
		addressService.update(address);
		return ResponseUtil.ok(address.getId());
	}

	/**
	 * 删除收货地址
	 *
	 * @param userId  用户ID
	 * @param address 用户收货地址，{ id: xxx }
	 * @return 删除操作结果
	 */
	@PostMapping("delete")
	public Object delete(@LoginUser Integer userId, @RequestBody LitemallAddress address) {
		if (userId == null) {
			return ResponseUtil.unlogin();
		}
		Integer id = address.getId();
		if (id == null) {
			return ResponseUtil.badArgument();
		}
		LitemallAddress litemallAddress = addressService.query(userId, id);
		if (ObjectUtils.isEmpty(litemallAddress)) {
			return ResponseUtil.badArgumentValue("该地址不存在!");
		}
		addressService.delete(id);
		return ResponseUtil.ok("删除地址ID："+id+"收货人:"+litemallAddress.getName());
	}
}
