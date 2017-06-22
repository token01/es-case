# elasticsearch
## （一）准备工作

### 创建es使用用户
~~~
adduser evn

sudo chmod a+w /etc/sudoers

dev ALL=(ALL) NOPASSWD: ALL

chmod 0440 /etc/sudoers
~~~
### 配置Java环境  
~~~
JAVA_HOME=/usr/local/jdk1.8.0_91

~~~
## （二）启动elasticsearch

### 下载安装包
~~~
wget https://download.elastic.co/elasticsearch/release/org/elasticsearch/distribution/tar/elasticsearch/2.3.3/elasticsearch-2.3.3.tar.gz
~~~
### 配置文件
~~~
vim elasticsearch.yml
network.host: 10.55.255.245
http.port: 9200
~~~
### 启动应用
~~~
nohup ./bin/elasticsearch & >/home/dev/opt/elasticsearch-2.3.3/logs/log.txt
~~~

### 安装插件
~~~
索引管理
sudo ./bin/plugin install mobz/elasticsearch-head
集群管理
sudo ./bin/plugin install lukas-vlcek/bigdesk
~~~
### 插件访问
~~~
http://10.55.255.245:9200/_plugin/head/
http://10.55.255.245:9200/_plugin/bigdesk/


~~~
## (三) elasticsearch api 操作
