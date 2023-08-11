package com.xuegao.PayServer.po;

import com.alibaba.fastjson.JSON;
import com.xuegao.core.db.DBWrapper;
import com.xuegao.core.db.po.BasePo;
import com.xuegao.core.db.po.annotation.Column;
import com.xuegao.core.db.po.annotation.Table;

@Table(tableName="advertise",proxoolAlias="proxool.PayServer")
public class AdPo extends BasePo {
    @Column
    private String uid;// 用户id,

    @Column
    private Integer sid;// '服务器ID',

    @Column
    private Integer pid;//'角色ID',

    @Column
    private String platform;// 平台 CSJ:穿山甲 YLH:优亮汇

    @Column
    private Long create_time;// 创建时间

    @Column
    private String trans_id; //交易id

    @Column
    private long reward_amount;//奖励数量

    @Column
    private String reward_name;//奖励id

    @Column
    private Integer statu; //0未到账,1发货成功

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public Long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Long create_time) {
        this.create_time = create_time;
    }

    public String getTrans_id() {
        return trans_id;
    }

    public Integer getStatu() {
        return statu;
    }

    public void setStatu(Integer statu) {
        this.statu = statu;
    }

    public void setTrans_id(String trans_id) {
        this.trans_id = trans_id;
    }

    public long getReward_amount() {
        return reward_amount;
    }

    public void setReward_amount(long reward_amount) {
        this.reward_amount = reward_amount;
    }

    public String getReward_name() {
        return reward_name;
    }

    public void setReward_name(String reward_name) {
        this.reward_name = reward_name;
    }

    public static AdPo findByTid(String tid) {
        long start = System.currentTimeMillis();
        DBWrapper dbWrapper = BasePo.fetchDBWrapper(AdPo.class);
        AdPo adPo = JSON.toJavaObject(dbWrapper.queryForBean("select * from advertise where trans_id=?", tid),
                AdPo.class);
        logger.info("findByTid:queryCost:"+(System.currentTimeMillis()-start));
        return adPo;
    }
}
