# PlayQR
Android端扫描二维码,查看图片 
Server端使用Python Flask web框架管理数据

2016-05-12
测试阶段: 加入注册账户功能

2016-05-15
图片上传功能测试成功，完善用户注册功能

2016-05-17 
实现Android端对Server端数据的在线浏览

2016-05-18
完成本地图片的编辑删除上传功能

2016-05-20
实现Android端对在线信息的编辑删除操作

2016-05-21
完成简单的admin页面

restful api:
http://192.168.1.110:5000/playqr/api/v1.0/tasks get
http://192.168.1.110:5000/playqr/api/v1.0/tasks post

http://192.168.1.110:5000/playqr/api/v1.0/task/<int id> get
http://192.168.1.110:5000/playqr/api/v1.0/task/<int id> put
http://192.168.1.110:5000/playqr/api/v1.0/task/<int id> delete
