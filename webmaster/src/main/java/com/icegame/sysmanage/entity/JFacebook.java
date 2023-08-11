package com.icegame.sysmanage.entity;

/**
 * facebook点赞配置类<p>
 * Company: 南京雪糕网络科技有限公司<p>
 *
 * @author wzy
 * @since 2019/10/28 19:37
 */
public class JFacebook {

    private Long id;

    private Long yuanbao;

    private Long likenum;

    private String channel;

    private String channelgroup;

    public JFacebook() {
    }

    public JFacebook(String channel, String channelgroup) {
        this.channel = channel;
        this.channelgroup = channelgroup;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getYuanbao() {
        return yuanbao;
    }

    public void setYuanbao(Long yuanbao) {
        this.yuanbao = yuanbao;
    }

    public Long getLikenum() {
        return likenum;
    }

    public void setLikenum(Long likenum) {
        this.likenum = likenum;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannelgroup() {
        return channelgroup;
    }

    public void setChannelgroup(String channelgroup) {
        this.channelgroup = channelgroup;
    }
}
