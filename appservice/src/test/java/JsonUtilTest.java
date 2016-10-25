import com.wanjia.utils.JsonUtil;
import com.wanjia.vo.cart.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by blake on 2016/8/13.
 */
public class JsonUtilTest {


	@Test
	public void shopCartRoomVo(){

		ShopCartRoomVo shopCartRoomVo = new ShopCartRoomVo() ;
		shopCartRoomVo.setShopId(1);
		//shopCartRoomVo.setShopName("店家_1");
		//shopCartRoomVo.setPrice(230);
		shopCartRoomVo.setBookNumber(1);
		//shopCartRoomVo.setAllowBookNumber(10);
		shopCartRoomVo.setRoomId(1);
	//	shopCartRoomVo.setRoomName("房型1");
		//shopCartRoomVo.setRoomType("双人床");
		shopCartRoomVo.setStartDate("2016-7-9");
		shopCartRoomVo.setEndDate("2016-7-10");

		String  str = JsonUtil.toJsonString(shopCartRoomVo);

		System.out.println(str);
	}

	@Test
	public void shopCartCourseVo(){

		ShopCartMealVo mealVo = new ShopCartMealVo();
		mealVo.setShopId(1);
		//mealVo.setShopName("店家_1");
		mealVo.setUserId(1);
		mealVo.setMealType(1);
		mealVo.setBookDate("2016-9-1");

		List<ShopCartCourseVo> courseVoList = new ArrayList<ShopCartCourseVo>();

		for(int i = 1 ; i <= 1 ; i++){

			ShopCartCourseVo courseVo = new ShopCartCourseVo() ;
			courseVo.setBookNumber(1);
			courseVo.setShopId(1);
		//	courseVo.setAllowBookNumber(5);
			courseVo.setCourseId(i);
			//courseVo.setPrice(123+i);
			//courseVo.setCourseName("菜品_"+i);
			courseVoList.add(courseVo) ;

		}
		mealVo.setCourseVoList(courseVoList);

		String str = JsonUtil.toJsonString(mealVo) ;
		System.out.println(str);
	}


	@Test
	public  void shopCartSpecialtyVo(){

		ShopCartSpecialtyVo shopCartSpecialtyVo = new ShopCartSpecialtyVo() ;
		shopCartSpecialtyVo.setShopId(1);
		//shopCartSpecialtyVo.setShopName("店家_1");
		shopCartSpecialtyVo.setUserId(1);
		shopCartSpecialtyVo.setBookDate("2016-9-1");
		//shopCartSpecialtyVo.setPrice(123);
		shopCartSpecialtyVo.setBookNumber(2);
		//shopCartSpecialtyVo.setAllowBookNumber(6);
		shopCartSpecialtyVo.setSpecialtyId(1);
		//shopCartSpecialtyVo.setSpecialtyName("腊肉");

		String str = JsonUtil.toJsonString(shopCartSpecialtyVo) ;
		System.out.println(str);
	}

	@Test
	public  void shopCartTicketVo(){

		ShopCartTravelTicketVo shopCartTravelTicketVo = new ShopCartTravelTicketVo() ;
		shopCartTravelTicketVo.setShopId(1);
		shopCartTravelTicketVo.setShopName("店家_1");
		shopCartTravelTicketVo.setUserId(1);
		shopCartTravelTicketVo.setBookNumber(1);
		shopCartTravelTicketVo.setMaxBookNumber(3);
		shopCartTravelTicketVo.setPrice(123);
		shopCartTravelTicketVo.setTicketDate("2016-9-1");
		shopCartTravelTicketVo.setTicketId(1);
		shopCartTravelTicketVo.setTicketName("成人票");
		shopCartTravelTicketVo.setTicketType("成人票");

		String str = JsonUtil.toJsonString(shopCartTravelTicketVo) ;
		System.out.println(str);
	}


	@Test
	public  void shopCartTravelGuideVo(){

		ShopCartTravelGuideVo shopCartTravelGuideVo = new ShopCartTravelGuideVo() ;
		shopCartTravelGuideVo.setShopId(1);
		shopCartTravelGuideVo.setShopName("店家_1");
		shopCartTravelGuideVo.setUserId(1);
		shopCartTravelGuideVo.setPrice(123);
		shopCartTravelGuideVo.setGuideDate("2016-9-1");
		shopCartTravelGuideVo.setGuideId(1);

		String str = JsonUtil.toJsonString(shopCartTravelGuideVo) ;
		System.out.println(str);
	}

	@Test
	public  void shopCartFamilyActivityVo(){

		ShopCartTravelFamilyActivityVo shopCartTravelFamilyActivityVo = new ShopCartTravelFamilyActivityVo() ;
		shopCartTravelFamilyActivityVo.setShopId(1);
		shopCartTravelFamilyActivityVo.setShopName("店家_1");
		shopCartTravelFamilyActivityVo.setUserId(1);
		shopCartTravelFamilyActivityVo.setPrice(123);
		shopCartTravelFamilyActivityVo.setFamilyActivityDate("2016-9-1");
		shopCartTravelFamilyActivityVo.setFamilyActivityId(1);
		shopCartTravelFamilyActivityVo.setBookNumber(1);
		shopCartTravelFamilyActivityVo.setMaxBookNumber(5);
		shopCartTravelFamilyActivityVo.setFamilyActivityName("游船");


		String str = JsonUtil.toJsonString(shopCartTravelFamilyActivityVo) ;
		System.out.println(str);
	}

}
