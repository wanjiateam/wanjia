package com.wanjia.entity.order;

public class UserOrderMealCourse {
    private Long id;

    private String fOrderId;

    private Long courseId;

    private Integer courseNumber;

    private Double price;

    public UserOrderMealCourse(Long id, String fOrderId, Long courseId, Integer courseNumber, Double price) {
        this.id = id;
        this.fOrderId = fOrderId;
        this.courseId = courseId;
        this.courseNumber = courseNumber;
        this.price = price;
    }

    public UserOrderMealCourse() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getfOrderId() {
        return fOrderId;
    }

    public void setfOrderId(String fOrderId) {
        this.fOrderId = fOrderId == null ? null : fOrderId.trim();
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(Integer courseNumber) {
        this.courseNumber = courseNumber;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}