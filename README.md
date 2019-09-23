# Disruptor框架
* 参考basicJava模块下concurrent.compare包下，关于Disruptor和BlockingQueue性能对比

## quickStart
* 建立一个工厂Event类，用于创建Event类实例对象
* 需要有一个监听事件类，用于处理数据（Event类）
* 实例话Disruptor实例，配置一系列参数，编写Disruptor核心组件
* 编写生产者组件，向Disruptor容器中去投递数据