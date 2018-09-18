# 多线程Reactor及主从Reactor模式
[代码来源](https://github.com/liuh2j/reactor-nio)
* Acceptor接收客户端发送来的SockChannel，均分给不同thread下的列表中
* WorkThread从线程列表中获取channel，并将channel注册到该线程下的Selector中(监听Read事件)，同时执行Handler中handle任务，监听不同事件，并给出不同的处理方式
* WorkThreadGroup中存在线程池，产生线程执行WorkThread中的任务
* 单个线程中拥有唯一一个Selector，用于这个线程内的循环遍历事件
* 可以通过线程池产生多个Acceptor，即主从模式，通过多线程接收不同客户端
* 关键核心点在于：单个线程维护自己线程内的Selector