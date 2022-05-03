# 编译
```bash
mvn clean install -Dmaven.test.skip=true -Dcheckstyle.skip=true
```
# something
## broker负载均衡
plusar的负载均衡，按照topic的bundle来做负载；负载均衡任务，定时计算各个broker的负载情况，按照阈值或过载策略来进行broker流量的卸载；被卸载的topic bundle在发送消息时，再通过broker的最小负载路由选址策略，去绑定topic与broker的关系