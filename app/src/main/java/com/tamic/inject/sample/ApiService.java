package com.tamic.inject.sample;


import com.tamic.tamic_retrofit.core.Call;
import com.tamic.tamic_retrofit.core.ICallback;
import com.tamic.tamic_retrofit.core.TBody;
import com.tamic.tamic_retrofit.core.TGet;

/**
 * Created by Tamic on 2016-07-13.
 */
public interface ApiService {

    @TGet("service/getIpInfo.php")
    Call<IpResult> getData(@TBody("ip") String ip,

                      ICallback<IpResult> callBack);
}
