# 打包后无法启动的问题

```kotlin
12:30:45.725 kotlin [main] INFO at io . avaje . applog . slf4j . Slf4jLogger . performLog (Slf4jLogger.java:122) -ebean version : 15.0.1
12:30:45.736 kotlin [main] INFO at io . avaje . applog . slf4j . Slf4jLogger . performLog (Slf4jLogger.java:122) -Loaded properties from[resource:application.yaml]
12:30:46.273 kotlin [main] INFO at io . avaje . applog . slf4j . Slf4jLogger . performLog (Slf4jLogger.java:122) -DataSource[db] autoCommit [false] transIsolation [READ_COMMITTED] min [2] max [200] in [505 ms]
Exception in thread "main" jakarta . persistence . PersistenceException : java . lang . IllegalStateException : Unable to determine the appropriate ebean platform given database product name [mysql] and ebean platform providers[SqlServer].With ebean 13 + we now have separate platforms(
    ebean - postgres,
    ebean - mysql etc
) and should use database specific platform dependency like ebean -postgres.Note that we can use ebean -platform - all to include all the platforms .
at io . ebeaninternal . server . core . DatabasePlatformFactory . create (DatabasePlatformFactory.java:58)
at io . ebeaninternal . server . core . DefaultContainer . setDatabasePlatform (DefaultContainer.java:215)
at io . ebeaninternal . server . core . DefaultContainer . createServer (DefaultContainer.java:105)
at io . ebeaninternal . server . core . DefaultContainer . createServer (DefaultContainer.java:33)
at io . ebean . DatabaseFactory . createInternal (DatabaseFactory.java:135)
at io . ebean . DatabaseFactory . create (DatabaseFactory.java:84)
at hxy . dragon . MainKt . main $lambda$7$lambda$6(Main.kt:111)
at io . javalin . event . EventManager . fireEvent (EventManager.kt:26)
at io . javalin . jetty . JettyServer . start (JettyServer.kt:90)
at io . javalin . Javalin . start (Javalin.java:123)
at hxy . dragon . MainKt . main (Main.kt:129)
at hxy . dragon . MainKt . main (Main.kt)
Caused by : java . lang . IllegalStateException : Unable to determine the appropriate ebean platform given database product name [mysql] and ebean platform providers[SqlServer].With ebean 13 + we now have separate platforms(
    ebean - postgres,
    ebean - mysql etc
) and should use database specific platform dependency like ebean -postgres.Note that we can use ebean -platform - all to include all the platforms .
at io . ebeaninternal . server . core . DatabasePlatformFactory . byDatabaseMeta (DatabasePlatformFactory.java:104)
at io . ebeaninternal . server . core . DatabasePlatformFactory . byDataSource (DatabasePlatformFactory.java:80)
at io . ebeaninternal . server . core . DatabasePlatformFactory . create (DatabasePlatformFactory.java:55)
... 11 more
```

从上面日志分析，指定的数据库产品就是mysql，但是实际上的提供只有SqlServer。 分析代码

![Snipaste_2024-03-19_13-36-37.png](assets/img/Snipaste_2024-03-19_13-36-37.png)

![Snipaste_2024-03-19_13-38-17.png](assets/img/Snipaste_2024-03-19_13-38-17.png)

![Snipaste_2024-03-19_13-40-42.png](assets/img/Snipaste_2024-03-19_13-40-42.png)

分析出来原因就很简单了，只要保证打包的时候，把自己需要的打包进去就好了。

```kotlin
// 打包
tasks.jar.configure {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest.attributes["Main-Class"] = mainKotlinClass
    from(configurations.runtimeClasspath.get().filter {
        // 只打包ebean-platform-mysql，除了这个，其他的不打包，免得SPI注入的文件给覆盖了。io.ebean.config.dbplatform.DatabasePlatformProvider
        it.name.endsWith("jar") && (it.name.contains("ebean-platform-mysql") || !it.name.contains("ebean-platform"))
    }.map { zipTree(it) })
}
```

![Snipaste_2024-03-19_13-42-45.png](assets/img/Snipaste_2024-03-19_13-42-45.png)

这也解释了为啥IDEA，或者.gradlew run 就可以直接运行，但是打包后无法运行，原因就是services的SPI配置文件最后只有一个，还是不对应的那个。

# 打包后运行出问题 jakarta.persistence.PersistenceException: hxy.dragon.model.Customer is NOT an Entity Bean registered with this server

https://github.com/ebean-orm/ebean/issues/3371

```kotlin
14:28:39.919 kotlin [JettyServerThreadPool-Virtual-9] ERROR at io.github.oshai.kotlinlogging.KLogger$DefaultImpls.error (KLogger.kt:810) - /customer/list发生异常 jakarta.persistence.PersistenceException: hxy.dragon.model.Customer is NOT an Entity Bean registered with this server?
jakarta.persistence.PersistenceException: hxy.dragon.model.Customer is NOT an Entity Bean registered with this server?
	at io.ebeaninternal.server.core.DefaultServer.desc(DefaultServer.java:1866)
	at io.ebeaninternal.server.core.DefaultServer.createQuery(DefaultServer.java:801)
	at io.ebeaninternal.server.core.DefaultServer.find(DefaultServer.java:789)
	at io.ebean.DB.find(DB.java:774)
	at hxy.dragon.service.CustomerService.list(CustomerService.kt:26)
	at hxy.dragon.controller.CustomerController.listCustomer(CustomerController.kt:27)
	at io.javalin.router.Endpoint.handle(Endpoint.kt:52)
	at io.javalin.router.ParsedEndpoint.handle(ParsedEndpoint.kt:15)
	at io.javalin.http.servlet.DefaultTasks.HTTP$lambda$9$lambda$7$lambda$6(DefaultTasks.kt:52)
	at io.javalin.http.servlet.JavalinServlet.handleTask(JavalinServlet.kt:99)
	at io.javalin.http.servlet.JavalinServlet.handleSync(JavalinServlet.kt:64)
	at io.javalin.http.servlet.JavalinServlet.handle(JavalinServlet.kt:50)
	at io.javalin.http.servlet.JavalinServlet.service(JavalinServlet.kt:30)
	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:587)
	at io.javalin.jetty.JavalinJettyServlet.service(JavalinJettyServlet.kt:52)
	at jakarta.servlet.http.HttpServlet.service(HttpServlet.java:587)
	at org.eclipse.jetty.servlet.ServletHolder.handle(ServletHolder.java:764)
	at org.eclipse.jetty.servlet.ServletHandler.doHandle(ServletHandler.java:529)
	at org.eclipse.jetty.server.handler.ScopedHandler.nextHandle(ScopedHandler.java:221)
	at org.eclipse.jetty.server.session.SessionHandler.doHandle(SessionHandler.java:1580)
	at org.eclipse.jetty.server.handler.ScopedHandler.nextHandle(ScopedHandler.java:221)
	at org.eclipse.jetty.server.handler.ContextHandler.doHandle(ContextHandler.java:1381)
	at org.eclipse.jetty.server.handler.ScopedHandler.nextScope(ScopedHandler.java:176)
	at org.eclipse.jetty.servlet.ServletHandler.doScope(ServletHandler.java:484)
	at org.eclipse.jetty.server.session.SessionHandler.doScope(SessionHandler.java:1553)
	at org.eclipse.jetty.server.handler.ScopedHandler.nextScope(ScopedHandler.java:174)
	at org.eclipse.jetty.server.handler.ContextHandler.doScope(ContextHandler.java:1303)
	at org.eclipse.jetty.server.handler.ScopedHandler.handle(ScopedHandler.java:129)
	at org.eclipse.jetty.server.handler.StatisticsHandler.handle(StatisticsHandler.java:173)
	at org.eclipse.jetty.server.handler.HandlerWrapper.handle(HandlerWrapper.java:122)
	at org.eclipse.jetty.server.Server.handle(Server.java:563)
	at org.eclipse.jetty.server.HttpChannel$RequestDispatchable.dispatch(HttpChannel.java:1598)
	at org.eclipse.jetty.server.HttpChannel.dispatch(HttpChannel.java:753)
	at org.eclipse.jetty.server.HttpChannel.handle(HttpChannel.java:501)
	at org.eclipse.jetty.server.HttpConnection.onFillable(HttpConnection.java:287)
	at org.eclipse.jetty.io.AbstractConnection$ReadCallback.succeeded(AbstractConnection.java:314)
	at org.eclipse.jetty.io.FillInterest.fillable(FillInterest.java:100)
	at org.eclipse.jetty.io.SelectableChannelEndPoint$1.run(SelectableChannelEndPoint.java:53)
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:572)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:317)
	at java.base/java.lang.VirtualThread.run(VirtualThread.java:311)
```

