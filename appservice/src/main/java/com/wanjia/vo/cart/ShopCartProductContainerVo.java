package com.wanjia.vo.cart;

import java.util.ArrayList;
import java.util.List;

/**
 * 存放购物车中的所有产品，用于返回给客户端
 * Created by blake on 2016/8/16.
 */
public class ShopCartProductContainerVo {

	private long shopId ;
	private String shopName ;

	private List<ShopCartRoomVo> roomVoList = new ArrayList<ShopCartRoomVo>();
	private List<ShopCartMealVo> mealVoList = new ArrayList<ShopCartMealVo>();
	private List<ShopCartSpecialtyVo> specialtyVoList = new ArrayList<ShopCartSpecialtyVo>();
    private ShopCartTravelContainerVo travelContainerVo = new ShopCartTravelContainerVo()  ;

	public ShopCartProductContainerVo(long shopId){
		this.shopId = shopId ;
	}

	public ShopCartTravelContainerVo getTravelContainerVo() {
		return travelContainerVo;
	}

	public void setTravelContainerVo(ShopCartTravelContainerVo travelContainerVo) {
		this.travelContainerVo = travelContainerVo;
	}

	public List<ShopCartRoomVo> getRoomVoList() {
		return roomVoList;
	}

	public List<ShopCartMealVo> getMealVoList() {
		return mealVoList;
	}

	public List<ShopCartSpecialtyVo> getSpecialtyVoList() {
		return specialtyVoList;
	}

	public void addRoomVo(ShopCartRoomVo roomVo){
		roomVoList.add(roomVo);
	}

	public void addMealVo(ShopCartMealVo mealVo){
		mealVoList.add(mealVo);
	}

	public void addSpecialtyVo(ShopCartSpecialtyVo specialtyVo){
		specialtyVoList.add(specialtyVo) ;
	}

	public void addTicketVo(ShopCartTravelTicketVo ticketVo){
		travelContainerVo.addTicketVo(ticketVo);
	}

	public void addGuideVo(ShopCartTravelGuideVo guideVo){
		travelContainerVo.addGuideVo(guideVo); ;
	}

	public void addFamilyActivityVo(ShopCartTravelFamilyActivityVo familyActivityVo){
		travelContainerVo.addFamilyActivityVo(familyActivityVo);
	}

	public long getShopId() {
		return shopId;
	}

	public void setShopId(long shopId) {
		this.shopId = shopId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}
}
