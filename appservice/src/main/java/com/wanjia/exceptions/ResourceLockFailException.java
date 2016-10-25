package com.wanjia.exceptions;

import com.wanjia.vo.cart.ShopCartBaseVo;

/**
 * Created by blake on 2016/10/9.
 */
public class ResourceLockFailException extends RuntimeException {


	private ResourceLockFailEntity resourceLockFailEntity ;

	public ResourceLockFailException(String msg,Exception e){
		super(msg,e);
	}

	public ResourceLockFailException(String msg,long shopId,long id,int type){
		super(msg);
		this.resourceLockFailEntity = new ResourceLockFailEntity(id,shopId,type);
	}

	public ResourceLockFailEntity getResourceLockFailEntity() {
		return resourceLockFailEntity;
	}

	public void setResourceLockFailEntity(ResourceLockFailEntity resourceLockFailEntity) {
		this.resourceLockFailEntity = resourceLockFailEntity;
	}

	class ResourceLockFailEntity{

		private long id ;
		private long shopId;
		private int type ;

		public ResourceLockFailEntity(long id, long shopId, int type) {
			this.id = id;
			this.shopId = shopId;
			this.type = type;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public long getShopId() {
			return shopId;
		}

		public void setShopId(long shopId) {
			this.shopId = shopId;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		@Override
		public String toString() {
			String productType = "";
			switch (type){
				case 1: productType= "住房"; break ;
				case 2: productType= "餐饮"; break ;
				case 3: productType= "特产"; break ;
				case 4: productType= "门票"; break ;
				case 5: productType= "农家特色游"; break ;

			}
			return productType+" : 店家id:"+shopId+" 产品id："+id+" 超出了可预订的数量" ;
		}
	}



}
