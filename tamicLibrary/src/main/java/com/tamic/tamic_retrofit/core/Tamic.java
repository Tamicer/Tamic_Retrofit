package com.tamic.tamic_retrofit.core;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.SyncHttpClient;
import com.tamic.tamic_retrofit.core.util.ReflectionUtil;
import com.tamic.tamic_retrofit.core.util.Utils;
import com.tamic.tamic_retrofit.http.TamicHttpClient;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpStatus;

import static com.tamic.tamic_retrofit.core.Tamic.MethodType.*;

/**
 *  Tamic adapts a Java interface to HTTP calls by using annotations on the declared methods to
 *  define how requests are made. Create instances using {@linkplain Builder
 * the builder} and pass your interface to {@link #create} to generate an implementation.
 * Created by Tamic on 2016-07-13.
 *
 *{@link # https://github.com/NeglectedByBoss/Tamic_Retrofit}
 */
public final class Tamic {

    private static final String TAG = Tamic.class.getSimpleName();
    private static String baseUrl = null;
    private static AsyncHttpClient client;
    private static MethodType requestMethod;
    private static String path;
    private static Map<String, String> headers;
    private static Map<String, String> bodys;
    private static Context context;
    private static String contentType = "application/json";
    private static ICallback call;
    private static Platform platform;
    private static Object obj;
    private static Type objType;

    public enum MethodType {
        GET, POST, DELETE, PUT
    }


    Tamic(Context context, String baseUrl, AsyncHttpClient client) {
        this.context = context;
        this.baseUrl = baseUrl;
        this.client = client;
    }

    /**
     * public interface CategoryService {
     * &#64;POST("category/{cat}/")
     * Call&lt;List&lt;Item&gt;&gt; categoryList(@Path("cat") String a, @Query("page") int b);
     * }
     */
    public <T> T create(final Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class[]{clazz},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        performeParser(proxy, method, args);
                        return execute();
                    }
                });
    }


    /**
     * Parser ApiService
     */
    private void performeParser(Object proxy, Method method, Object[] args) throws ClassNotFoundException {

        Annotation[] annotations = method.getAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            if (annotations[i] instanceof TPost) {
                System.out.println("post");
                requestMethod = POST;
                path = ((TPost) annotations[i]).value();
            }
            if (annotations[i] instanceof TGet) {
                System.out.println("get");
                requestMethod = GET;
                path = ((TGet) annotations[i]).value();
            }
            Log.d(TAG, "get" + annotations[i]);
            Log.d(TAG, "path:" + path);

        }

        // Annotation
        Annotation[][] paraAnno = method.getParameterAnnotations();
        int length = paraAnno.length;
        int j = 0;
        for (int i = 0; i < length; i++) {
            Annotation[] annos = paraAnno[i];
            for (Annotation anno : annos) {

                Log.d(TAG, "annos" + anno + "/" + j);
                Log.d(TAG, "annos" + ((TBody) anno).value() + "/" + (String) args[j]);

                bodys.put(((TBody) anno).value(), args[j].toString());

                j++;
            }
        }
        Log.d(TAG, "get:" + requestMethod.toString());
        Log.d(TAG, "body:" + bodys.size());

        Object arg = args[args.length - 1];
        if (arg instanceof ICallback) {
            call = (ICallback) arg;
            System.out.println("call:");
        }
        // paramType
        Log.d(TAG, " paramTypeType: ");
        Type[] paramTypeList = method.getGenericParameterTypes();
        for (Type paramType : paramTypeList) {
            System.out.println("  " + paramType);
            // if Type is T
            if (paramType instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) paramType).getActualTypeArguments();

                Log.d(TAG, "  TypeArgument: ");
                for (Type mtype : types) {
                    Log.d(TAG, "   " + mtype);
                    this.objType = mtype;
                    obj = ReflectionUtil.getClass(objType);
                }
            }
        }

        Log.d(TAG, " returnType: ");
        Type returnType = method.getGenericReturnType();
        Log.d(TAG, "ReturnType:" + returnType);

        if (returnType instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) returnType)
                    .getActualTypeArguments();
            Log.d(TAG, "  TypeArgument: ");
            for (Type mtype : types) {
                Log.d(TAG, "   " + mtype);
                // Call call = ReflectionUtil.getClass(mtype);
            }
        }
    }

    private Object execute() {

        return platform.execute(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                loadMethodHandler();
                Looper.loop();
            }
        });
    }


    /**
     * @return
     */
    private static RequestHandle loadMethodHandler() {

        switch (requestMethod) {

            case GET:
                return TamicHttpClient.get(context, path, headers, bodys, new JsonHttpResponseHandler(true));
            case POST:
                return TamicHttpClient.post(context, path, headers, bodys, contentType, new JsonHttpResponseHandler());
            // more .......
        }

        return null;
    }


    /**
     * Mandatory Builder for the Builder
     */
    public static final class Builder {

        private static final int DEFAULT_TIMEOUT = 5;
        private String baseUrl;
        private Boolean isLog = false;
        private Context context;
        private AsyncHttpClient client;
        private int timeout;

        public Builder(Context context) {
            // Add the base url first. This prevents overriding its behavior but also
            // ensures correct behavior when using proxy that consume all types.
            //client = new AsyncHttpClient();
            bodys = new HashMap<>();
            this.context = context;


        }

        public Builder addLog(boolean isLog) {
            this.isLog = isLog;
            return this;
        }

        /**
         * Sets the default connect timeout for new connections. A value of 0 means no timeout,
         * otherwise values must be between 1 and {@link TimeUnit #MAX_VALUE} when converted to
         * milliseconds.
         */
        public Builder connectTimeout(int timeout) {
            if (timeout != -1) {

                this.timeout = timeout;

            } else {
                this.timeout = DEFAULT_TIMEOUT;

            }
            return this;
        }

        /**
         * Set an API base URL which can change over time.
         */
        public Builder baseUrl(String baseUrl) {
            this.baseUrl = Utils.checkNotNull(baseUrl, "baseUrl == null");
            return this;
        }

        /**
         * The HTTP client used for requests.
         * <p/>
         * <p/>
         * Note: This method <b>does not</b> make a defensive copy of {@code client}. Changes to its
         * settings will affect subsequent requests. Pass in a {@linkplain AsyncHttpClient#clone() cloned}
         * instance to prevent this if desired.
         */
        public Builder client(AsyncHttpClient client) {
            this.client = (Utils.checkNotNull(client, "client == null"));
            return this;
        }

        /**
         * Create the {@link Tamic} instance using the configured values.
         * <p/>
         */
        public Tamic build() {
            if (baseUrl == null) {
                throw new IllegalStateException("Base URL required.");
            }

            if (client == null) {
                client = new SyncHttpClient();

            }

            client.setTimeout(timeout);
            TamicHttpClient.setBaseUrl(baseUrl);

            TamicHttpClient.setClient(client);

            platform = Platform.get();

            return new Tamic(context, baseUrl, client);
        }
    }

    private static class JsonHttpResponseHandler extends AsyncHttpResponseHandler {

        public JsonHttpResponseHandler() {
        }

        public JsonHttpResponseHandler(Looper looper) {
            super(looper);
        }

        public JsonHttpResponseHandler(boolean usePoolThread) {
            super(usePoolThread);
        }

        @Override
        public void onStart() {
            Log.d(TamicHttpClient.TAG, " onStart");
            if( call != null) {
                call.start();
            }
            super.onStart();
        }

        @Override
        public void onFinish() {
            super.onFinish();
            if( call != null) {
                call.finish();
            }
        }


        @Override
        public void onCancel() {
            super.onCancel();
            if( call != null) {
                call.cancel();
            }
        }

        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            Log.d(TamicHttpClient.TAG, "onSuccess");
            for (Header tmp : headers) {
                Log.d(TamicHttpClient.TAG, tmp.getName() + ":" + tmp.getValue());
            }

            Log.d(TamicHttpClient.TAG, "response code: " + statusCode);
            if (statusCode == HttpStatus.SC_OK) {
                Log.d(TamicHttpClient.TAG, "OK:" + statusCode);
                String jstr = new String(responseBody);
                Log.d(TamicHttpClient.TAG, "responseBody :" + jstr);
                if (call != null) {
                    try {
                        try {
                            call.success(JSON.parseObject(jstr, ReflectionUtil.newInstance(objType).getClass()));
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        call.failed(e);
                    }
                }
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            if (error != null && error.getMessage() != null) {
                Log.d(TamicHttpClient.TAG, "onFailure : " + statusCode);
                Log.d(TamicHttpClient.TAG, "responseBody : " + new String(responseBody));
                Log.d(TamicHttpClient.TAG, error.getMessage());
            }

            if (call != null) {
                call.failed(error);
            }
        }

    }


}
