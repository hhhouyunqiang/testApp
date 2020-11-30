package com.ringdata.base.net;

import com.blankj.utilcode.util.SPUtils;
import com.ringdata.base.app.ConfigType;
import com.ringdata.base.app.Latte;

import java.io.IOException;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import me.jessyan.progressmanager.ProgressManager;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public final class RestCreator {

    public static WeakHashMap<String, Object> getParams() {
        return new WeakHashMap<>();
    }

    public static WeakHashMap<String, String> getHeaders() {
        return new WeakHashMap<>();
    }

    /**
     * 构建OkHttp
     */
    private static final class OKHttpHolder {
        private static final int TIME_OUT = 60;

        private static OkHttpClient.Builder buildInterceptor() {
            OkHttpClient.Builder BUILDER = new OkHttpClient.Builder();
            BUILDER.addInterceptor(
                    new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                            String token = SPUtils.getInstance().getString("ACCESS_TOKEN");
                            Request original = chain.request();
                            Request request = original.newBuilder()
                                    .addHeader("Authorization", "Bearer " + token)
                                    .build();
                            return chain.proceed(request);
                        }
                    });
            return BUILDER;
        }

        //uums登录请求头
        private static OkHttpClient.Builder buildTokenInterceptor() {
            OkHttpClient.Builder BUILDER = new OkHttpClient.Builder();
            BUILDER.addInterceptor(
                    new Interceptor() {
                        @Override
                        public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
                            Request original = chain.request();
                            Request request = original.newBuilder()
                                    .addHeader("Authorization", "Basic bW9uZXR3YXJlOm1vbmV0d2FyZTIwMTkuQHNoLmNvbQ==")
//                                    .addHeader("content-type", "application/x-www-form-urlencoded;charset=utf-8")
                                    .build();
                            return chain.proceed(request);
                        }
                    });
            return BUILDER;
        }

        private static final OkHttpClient OK_HTTP_CLIENT = ProgressManager.getInstance().with(buildInterceptor())
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .build();

        private static final OkHttpClient OK_HTTP_CLIENT_TOKEN = ProgressManager.getInstance().with(buildTokenInterceptor())
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 构建全局Retrofit客户端
     */
    private static final class RetrofitHolder {
        private static final String BASE_URL = (String) Latte.getConfigurations().get(ConfigType.API_HOST);
        private static final Retrofit RETROFIT_CLIENT = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(OKHttpHolder.OK_HTTP_CLIENT)
                .addConverterFactory(ScalarsConverterFactory.create())
                //.addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static final class RetrofitTokenHolder {
        private static final String BASE_URL = (String) Latte.getConfigurations().get(ConfigType.API_HOST);
        private static final Retrofit RETROFIT_CLIENT = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(OKHttpHolder.OK_HTTP_CLIENT_TOKEN)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
    }

    /**
     * Service接口
     */
    private static final class RestServiceHolder {
        private static final RestService REST_SERVICE =
                RetrofitHolder.RETROFIT_CLIENT.create(RestService.class);
    }

    private static final class RestServiceTokenHolder {
        private static final RestService REST_SERVICE =
                RetrofitTokenHolder.RETROFIT_CLIENT.create(RestService.class);
    }

    public static RestService getRestService() {
        return RestServiceHolder.REST_SERVICE;
    }

    public static RestService getTokenRestService() {
        return RestServiceTokenHolder.REST_SERVICE;
    }

}
