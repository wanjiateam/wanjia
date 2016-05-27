package com.wanjia.entity;

import java.util.Date;

public class UserInfo {
    private Long uid;

    private String username;

    private String userrealname;

    private String passwd;

    private String phonenumber;

    private String email;

    private Byte shopowner;

    private Date registertime;

    private Date birthday;

    private String headimageurl;

    private String useridcard;

    private Byte usertype;

    public UserInfo(Long uid, String username, String userrealname, String passwd, String phonenumber, String email, Byte shopowner, Date registertime, Date birthday, String headimageurl, String useridcard, Byte usertype) {
        this.uid = uid;
        this.username = username;
        this.userrealname = userrealname;
        this.passwd = passwd;
        this.phonenumber = phonenumber;
        this.email = email;
        this.shopowner = shopowner;
        this.registertime = registertime;
        this.birthday = birthday;
        this.headimageurl = headimageurl;
        this.useridcard = useridcard;
        this.usertype = usertype;
    }

    public UserInfo() {
        super();
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getUserrealname() {
        return userrealname;
    }

    public void setUserrealname(String userrealname) {
        this.userrealname = userrealname == null ? null : userrealname.trim();
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd == null ? null : passwd.trim();
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber == null ? null : phonenumber.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public Byte getShopowner() {
        return shopowner;
    }

    public void setShopowner(Byte shopowner) {
        this.shopowner = shopowner;
    }

    public Date getRegistertime() {
        return registertime;
    }

    public void setRegistertime(Date registertime) {
        this.registertime = registertime;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getHeadimageurl() {
        return headimageurl;
    }

    public void setHeadimageurl(String headimageurl) {
        this.headimageurl = headimageurl == null ? null : headimageurl.trim();
    }

    public String getUseridcard() {
        return useridcard;
    }

    public void setUseridcard(String useridcard) {
        this.useridcard = useridcard == null ? null : useridcard.trim();
    }

    public Byte getUsertype() {
        return usertype;
    }

    public void setUsertype(Byte usertype) {
        this.usertype = usertype;
    }
}