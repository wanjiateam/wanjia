package com.wanjia.vo.cart;

import java.util.ArrayList;
import java.util.List;

/**
 * 存放购物车中的所有游的信息
 * Created by blake on 2016/8/16.
 */
public class ShopCartTravelContainerVo {

	private List<ShopCartTravelTicketVo> ticketVoList = new ArrayList<ShopCartTravelTicketVo>();
	private List<ShopCartTravelFamilyActivityVo> familyActivityVoList = new ArrayList<ShopCartTravelFamilyActivityVo>();
	private List<ShopCartTravelGuideVo> guideVoList = new ArrayList<ShopCartTravelGuideVo>();

	public List<ShopCartTravelTicketVo> getTicketVoList() {
		return ticketVoList;
	}

	public List<ShopCartTravelFamilyActivityVo> getFamilyActivityVoList() {
		return familyActivityVoList;
	}

	public List<ShopCartTravelGuideVo> getGuideVoList() {
		return guideVoList;
	}

	public void addTicketVo(ShopCartTravelTicketVo ticketVo){
		ticketVoList.add(ticketVo);
	}

	public void addGuideVo(ShopCartTravelGuideVo guideVo){
		guideVoList.add(guideVo) ;
	}

	public void addFamilyActivityVo(ShopCartTravelFamilyActivityVo familyActivityVo){
		familyActivityVoList.add(familyActivityVo);
	}
}
