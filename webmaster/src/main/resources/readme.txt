在 master 服务器更目录新建xls上传目录

[root@master]# cd /
[root@master]# mkdir -p /xls/
[root@master]# chmod -R 755 /xls/


在 slave 服务器新建xls上传以及备份目录

[root@slave]# cd /
[root@slave]# mkdir xls
[root@slave]# chmod -R 755 /xls/



1. 执行init.sql
2. 登录到后台
    2.1 在 登录支付 -> 地址配置 菜单修改登录服和支付服的地址，以及接口(这个要先配置，添加区服的时候会同步数据)
    2.2 在 GM工具 ->  公告列表 菜单对应渠道的游戏公告
    2.3 在 接口配置 -> 配置列表 修改相关参数的路径地址
    2.4 在 客服管理 -> 预设消息 设置默认的显示信息
    2.5 在 客服管理 -> 自动回复 设置下班期间给玩家的回复信息