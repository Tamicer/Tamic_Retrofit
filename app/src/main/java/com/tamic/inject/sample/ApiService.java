package com.tamic.inject.sample;

import com.tamic.inject.core.TBody;
import com.tamic.inject.core.Call;
import com.tamic.inject.core.TGet;
import com.tamic.inject.core.ICallback;

/**
 * Created by Tamic on 2016-07-13.
 */
public interface ApiService {

    @TGet("service/getIpInfo.php")
    Call<IpResult> getData(@TBody("ip") String ip,

                      ICallback<IpResult> callBack);
}
