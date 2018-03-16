package com.jingyue.lygame.bean;

/**
 * Created by liu hong liang on 2016/11/9.
 */
public class DeviceBean {
    public String deviceId ="";//	是	STRING	玩家设备ID IOS为openUDID ANDROID 为IMEI码
    public String userua="";//	是	STRING	玩家设备UA
    public String ipaddrid;//	否	INT	玩家IP所在地编号
    public String deviceinfo;//	否	STRING	玩家设备信息 包括手机号码,用户系统版本,双竖线隔开
    public String idfv;//	否	STRING	玩家设备IDFV 有传
    public String idfa;//	否	STRING	玩家设备IDFA 有传
    public String local_ip;//	否	STRING	玩家设备本地IP 有传
    public String login_ip="";
    public String mac;//	否	STRING	玩家设备MAC 有传

    public void setLogin_ip(String login_ip) {
        this.login_ip = login_ip;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public void setUserua(String userua) {
        this.userua = userua;
    }

    public void setDeviceinfo(String deviceinfo) {
        this.deviceinfo = deviceinfo;
    }


    public void setLocal_ip(String local_ip) {
        this.local_ip = local_ip;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
