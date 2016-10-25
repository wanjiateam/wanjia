package com.wanjia.vo.restaurant;

/**菜品的详细信息 例如口味，配菜。。。。
 * Created by blake on 2016/7/18.
 */
public class ShopCourseDetailInfoVo {

    private  long shopId ;
    private  long courseId ;
    private String consist ;
    private String note ;
    private int taste ;

    public long getShopId() {
        return shopId;
    }

    public void setShopId(long shopId) {
        this.shopId = shopId;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public String getConsist() {
        return consist;
    }

    public void setConsist(String consist) {
        this.consist = consist;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getTaste() {
        return taste;
    }

    public void setTaste(int taste) {
        this.taste = taste;
    }
}
