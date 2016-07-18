# Tamic_Retrofit
user-defined Retrofit.  剖解Retrofit源码，实现简单自定义的Retrofit的框架




作用
-

实现自定义Retrofit 的网络框架，只适用于初学者学习了解Retrofit内部原理

实现技术
--
反射，依赖注入，代理， 建造等模式等， 接口回调



# 用法

实例化Tamic （Retrofit）
--
     Tamic tamic = new Tamic.Builder(MainActivity.this)
                .baseUrl("http://ip.taobao.com/")
                .connectTimeout(5)
                .addLog(true)
                .build();
                
                
APIService 
--

    public interface ApiService {

    @TGet("service/getIpInfo.php")
    Call<IpResult> getData(@TBody("ip") String ip,ICallback<IpResult> callBack);
                    
                    }
     
    
Create Service
--
   
     ApiService service = tamic.create(ApiService.class);



Execute
--

      service.getData("21.22.11.33", new ICallback<IpResult>() {
            @Override
            public void success(IpResult ipResult) {
                // todo
            }

            @Override
            public void failed(Throwable e) {
                // todo
            }
        });
