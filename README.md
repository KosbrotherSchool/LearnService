Service 是 Android 應用程式提供的一個執行背景程式而不需與使用者互動的元件. 當使用者跳轉到不同的 Android 應用程式時, Service 依然會繼續執行它的任務. 因此, 可以將它看作在背後默默為我們執行程式工作的人.

Service 的用法:

1. Started
Stated 指的是藉由 startService() 開始一個背景程式 Service, 但是此 Service 與丟任務進來的元件(Activity)沒有相依, 如果在執行背景程式的過程中, 丟任務的原件先 Destroy() 掉了, 依然不會影響到 Service 的執行. 譬如: 上傳照片, 下載檔案...等等就滿適合的.

2. Bound
Bound 指的是藉由 bindService() 開始一個背景程式 Service, 讓丟任務進來的元件(Activity)與背景程式 Service 產生相依, 如果在背景程式執行的過程中, 丟任務的原件先 Destroy() 了, 那麼 Service 也會跟著 Destroy(). 當背景程式需要與前端元件溝通時, 我們會使用 Bound.

兩種用法的生命週期 LifeCycle 會不一樣, 直接看圖比較容易瞭解:




Service 的創造:

1. Service :
在主程序執行背景程式, 會使得Activity 的主程序變慢. 如果要執行長時間的背景程式, 必須在 Service 內寫 Thread* 來處理.

* Thread 可以想成是我們的工人, 當我們的程式工作量很大時, 可以同時分派給不同的 Thread.
* 負責執行 Activity 的工人叫做 MainThread.

2. IntentService :
IntentService 會在一個 worker thread 裡依序執行傳進來的 intent 任務, 完成任務後就會自行停止. 在比較長時間的執行中, 會使用 IntentService.

用 IntentService 的好處是不需要額外寫 Thread, 所以程式碼很簡潔, 壞處是只有它只有一個 Thread 工人, 如果有好幾個工作就必須排隊.

用 Service 比較複雜些, 但彈性比較大, 我們在範例裡會使用 Service.

Service 的註冊

我們的 Service 必須在 AndroidManifest.xml 裡註冊,
這樣才能被 Application 給找到 (跟 Activity 一樣!)

 <service android:exported="false" android:name=".TimerService">  
       <intent-filter>  
         <action android:name="com.example.learnservice.action.PLAY" />  
         <action android:name="com.example.learnservice.action.STOP" />  
       </intent-filter>  
 </service>  



Service 的範例 : 用 Service 做一個計時器 (程式碼連結)

作法上是這樣的,
1. 由 MainActivity 透過 intent 來啓動 Service
2. Service 藉由不同的 intent 訊息來判斷要開始倒數或停止
3. 開始倒數時產生一個 Thread 來執行倒數的工作, 同時傳送訊息給 Handler, 讓 Handler* 更新 MainActivity 畫面上的倒數數字.

*Handler 用來處理 Thread 發送的訊息; 在某些情況下, 我們可能有好幾個 Thread 同時工作, 因此 Thread 內去改變畫面顯示是不被 Android 允許的, 必須使用 Handler 來處理元件顯示.
