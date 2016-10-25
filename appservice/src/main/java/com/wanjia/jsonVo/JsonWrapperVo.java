package com.wanjia.jsonVo;

import com.wanjia.vo.cart.ShopCartProductContainerVo;

import java.util.*;

/**
 * 为了 list map 对象 转换成json 返回给客户端
 * Created by blake on 2016/8/21.
 */
public class JsonWrapperVo<T> {

	private List<T> vos = new ArrayList<T>() ;


	public void add(T vo){
		vos.add(vo) ;
	}

	public void addCollection(Collection<T> voList){
		vos.addAll(voList);
	}

}
