# BTBluetooth
1、Android蓝牙开发，连接BT无线地面气象诸元设备，获取实时数据  
2、SearchServer开启线程子线程获取数据、写数据，Handler反馈到界面显示数据  
3、BroadcastReceiver广播接收器，取得搜索到的蓝牙  
4、通过BluetoothSocket获取连接、取得设备的蓝牙获取到的数据  
---

##记：稍微阅读Handler源码之后，发现代码存在很大的内存泄露的风险  
问题：This Handler class should be static or leaks might occur  
问题出在：  
```
handler = new Handler() {  
            @Override  
            public void handleMessage(Message msg) {  
                super.handleMessage(msg);  
                /*balabala省略*/  
            }  
}
```
---

##原因是使用匿名内部类创建Handler时，会检查是否是static ，如果没有声明为 static 则会出现内存泄漏的警告  
```
public Handler(Callback callback, boolean async) {  
 // Hanlder 是匿名类、内部类、本地类时，如果没有声明为 static 则会出现内存泄漏的警告  
 if (FIND_POTENTIAL_LEAKS) {  
     final Class<? extends Handler> klass = getClass();  
     if ((klass.isAnonymousClass() || klass.isMemberClass() || klass.isLocalClass()) &&  
         (klass.getModifiers() & Modifier.STATIC) == 0) {  
         Log.w(TAG, "The following Handler class should be static or leaks might occur: " + klass.getCanonicalName());  
     }  
 }
 ``` 

 

解释：  
>当activity被finish的时候，延迟发送的消息仍然会存活在UI线程的消息队列中，直到10分钟后它被处理掉。  
这个消息持有activity的Handler的引用，Handler又隐式的持有它的外部类(这里就是SampleActivity)  
的引用。这个引用会一直存在直到这个消息被处理，所以垃圾回收机制就没法回收这个activity，内存泄露就  
发生了。  
>


大概说一下（个人理解），若是匿名内部类引用链为：handler持有activity的引用，activity持有Looper的引用，Looper持有MessageQueue的引用，MessageQueue中的Message持有Handler引用  
---

##最终建议：使用静态内部类并且在内部类中持有外部类的弱引用，因为弱引用不会导致无法回收的问题。

