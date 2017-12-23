**自定义异常捕获类**
比如友盟统计就是将异常信息捕获后发送到服务器。我们也可以自己做一个自己的CrashHandler。具体见代码。
使用方法：
- 在Application的onCreate()方法中对我们自己的CrashHandler进行初始化即可  

		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());`  

如有错误欢迎指正，邮箱yzytmac@163.com
