package com.taidii.app.http;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.taidii.app.GlobalParams;
import com.taidii.app.utils.LogUtils;
import com.taidii.app.utils.ThreadPool;
import com.google.gson.JsonObject;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;


public final class HttpManager {
    private final static OkHttpClient requestClient;
    private static final OkHttpClient downloadClient;
    public final static String TOKEN_PREFIX = "JWT ";
    private final static MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");
    private final static String EMPTY_JSON = new JsonObject().toString();
    private final static Handler handler = new Handler(Looper.getMainLooper());
    public final static int METHOD_POST = 1;
    public final static int METHOD_PATCH = 2;
    public final static int METHOD_PUT = 3;

    static {
        int timeout = 30;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS);
        builder.addInterceptor(new LoggingInterceptor());
        requestClient = builder.build();
        downloadClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    private static <T> void executeCall(final Request request, final OnResponse<T> onResponse) {
        if (onResponse == null) {
            throw new NullPointerException("OnResponse is null!");
        }
        final Call call = requestClient.newCall(request);
        final String reqUrl = request.url().toString();
        if (onResponse != null) {
            onResponse.onStart();
        }
        ThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                Response response = null;
                try {
                    response = call.execute();
                    if (response == null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onResponse.onResultNull(reqUrl);
                                onResponse.onCompleted();
                                failedLog(-1, "response null", reqUrl);
                            }
                        });
                        return;
                    }
                    if (response.isSuccessful()) {
                        String result = response.body().string();
                        final T t = onResponse.analyseResult(result);
                        // 标记请求里的额外成功
                        onResponse.setSuccess(true);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onResponse.onSuccess(t);
                                onResponse.onCompleted();
                            }
                        });
                    } else {
                        final int code = response.code();
                        final String msg = response.message();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                onResponse.onFailed(code, msg, reqUrl);
                                onResponse.onCompleted();
                                failedLog(code, msg, reqUrl);
                            }
                        });
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            onResponse.onError(e, reqUrl);
                            onResponse.onCompleted();
                            failedLog(-1, e.getMessage(), reqUrl);
                        }
                    });
                } finally {
                    if (response != null) {
                        response.body().close();
                    }
                }
            }
        });
    }

    public static <T> void postFormForQiNiu(String url, ArrayMap<String, String> params, Object tag,
                                            OnResponse<T> onResponse) {
        // 构造Multipart请求体，并设置类型为Form
        MultipartBody.Builder multiBuilder = new MultipartBody.Builder().setType(MultipartBody
                .FORM);
        // params是存放参数的ArrayMap
        Set<Map.Entry<String, String>> entrySet = params.entrySet();
        // 遍历参数集合，添加到请求体
        for (Map.Entry<String, String> entry : entrySet) {
            File file = new File(entry.getValue());
            if (file.exists()) {
                // addFormDataPart方法三个参数的方法，分别对应要提交的key，value和文件对象
                multiBuilder.addFormDataPart(entry.getKey(), entry.getValue(), RequestBody.create
                        (null, file));
            } else {
                // addFormDataPart方法三个参数的方法，分别对应要提交的key，value
                multiBuilder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        url = checkUrlPrefix(url);
        // 构造Request对象，方法为POST
        Request.Builder reqBuilder = new Request.Builder().url(url).post(multiBuilder.build());
        // 根据需要添加的header信息
        reqBuilder.addHeader("Authorization", TOKEN_PREFIX + GlobalParams.token);
        // 设置tag
        if (tag != null) {
            reqBuilder.tag(tag.getClass().getName());
        }
        Request request = reqBuilder.build();
        executeCall(request, onResponse);
    }

    private static void addHeadersToRequest(ArrayMap<String, String> headers, Request.Builder builder) {
        if (headers != null && headers.size() > 0) {
            Set<Map.Entry<String, String>> entrySet = headers.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

    private static <T> void basePost(String url, ArrayMap<String, String> headers, JsonObject params, Object tag,
                                     OnResponse<T> onResponse) {
        final RequestBody reqBody = RequestBody.create(MEDIA_TYPE_JSON, params == null ?
                EMPTY_JSON : params.toString());
        url = checkUrlPrefix(url);
        Request.Builder builder = new Request.Builder().url(url);
        if (tag != null) {
            if (tag instanceof JsonObject) {
                throw new IllegalArgumentException("tag can not be JsonObject.");
            }
            builder.tag(tag);
        }
        addHeadersToRequest(headers, builder);
        Request request = builder.post(reqBody).build();
        executeCall(request, onResponse);
    }

    public static <T> void postForm(String url, ArrayMap<String, String> params, Object tag, OnResponse<T> onResponse) {
        post(url, params, tag, METHOD_POST, onResponse);
    }

    public static <T> void post(String url, ArrayMap<String, String> params, Object tag, int method, OnResponse<T>
            onResponse) {
        // 构造Multipart请求体，并设置类型为Form
        MultipartBody.Builder multiBuilder = new MultipartBody.Builder().setType(MultipartBody
                .FORM);
        // params是存放参数的ArrayMap
        Set<Map.Entry<String, String>> entrySet = params.entrySet();
        // 遍历参数集合，添加到请求体
        for (Map.Entry<String, String> entry : entrySet) {
            File file = new File(entry.getValue());
            if (file.exists()) {
                // addFormDataPart方法三个参数的方法，分别对应要提交的key，value和文件对象
                multiBuilder.addFormDataPart(entry.getKey(), entry.getValue(), RequestBody.create
                        (null, file));
            } else {
                // addFormDataPart方法三个参数的方法，分别对应要提交的key，value
                multiBuilder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        url = checkUrlPrefix(url);
        // 构造Request对象，方法为POST
        Request.Builder reqBuilder = new Request.Builder().url(url);
        // 根据需要添加的header信息
        reqBuilder.addHeader("Authorization", TOKEN_PREFIX + GlobalParams.token);
        if (method == METHOD_POST) {
            reqBuilder.post(multiBuilder.build());
        } else if (method == METHOD_PUT) {
            reqBuilder.put(multiBuilder.build());
        } else if (method == METHOD_PATCH) {
            reqBuilder.patch(multiBuilder.build());
        }
        // 设置tag
        if (tag != null) {
            if (tag instanceof ArrayMap) {
                throw new IllegalArgumentException("tag can not be ArrayMap.");
            }
            reqBuilder.tag(tag);
        }
        Request request = reqBuilder.build();
        executeCall(request, onResponse);
    }

    public static <T> void postNoToken(String url, ArrayMap<String, String> headers, JsonObject params, Object tag,
                                       OnResponse<T> onResponse) {
        basePost(url, headers, params, tag, onResponse);
    }

    public static <T> void post(String url, ArrayMap<String, String> headers, JsonObject params, Object tag,
                                OnResponse<T> onResponse) {
        if (headers == null) {
            headers = new ArrayMap<>();
        }
        if (!TextUtils.isEmpty(GlobalParams.token)) {
            headers.put("Authorization", TOKEN_PREFIX + GlobalParams.token);
        }
        basePost(url, headers, params, tag, onResponse);
    }

    public static <T> void post(String url, JsonObject params, Object tag, OnResponse<T> onResponse) {
        post(url, null, params, tag, onResponse);
    }

    public static <T> void post(String url, Object tag, OnResponse<T> onResponse) {
        post(url, null, null, tag, onResponse);
    }

    public static <T> void getNoToken(String url, ArrayMap<String, String> headers, ArrayMap<String, String> params,
                                      Object tag, final OnResponse<T> onResponse) {
        url = checkUrlPrefix(url);
        Request.Builder builder = new Request.Builder().url(handleUrl(url, params));
        if (tag != null) {
            if (tag instanceof ArrayMap) {
                throw new IllegalArgumentException("tag can not be ArrayMap.");
            }
            builder.tag(tag);
        }
        addHeadersToRequest(headers, builder);
        Request request = builder.get().build();
        executeCall(request, onResponse);
    }

    @NonNull
    private static String checkUrlPrefix(String url) {
        if (!url.startsWith("http")) {
            url = ApiContainer.API_HOST + url;
        }
        return url;
    }

    private static String handleUrl(String url, ArrayMap<String, String> params) {
        if (params == null || params.size() <= 0) {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        Set<Map.Entry<String, String>> entrySet = params.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        return sb.substring(0, sb.length() - 1);
    }

    public static <T> void get(String url, ArrayMap<String, String> headers, ArrayMap<String, String> params, Object
            tag, final OnResponse<T> onResponse) {
        if (headers == null) {
            headers = new ArrayMap<>();
        }
        headers.put("Authorization", TOKEN_PREFIX + GlobalParams.token);
        getNoToken(url, headers, params, tag, onResponse);
    }

    public static <T> void get(String url, ArrayMap<String, String> params, Object tag, OnResponse<T> response) {
        get(url, null, params, tag, response);
    }

    public static <T> void get(String url, Object tag, OnResponse<T> response) {
        get(url, null, null, tag, response);
    }

    public static <T> void put(String url, JsonObject params, Object tag, OnResponse<T> onResponse) {
        final RequestBody reqBody = RequestBody.create(MEDIA_TYPE_JSON, params == null ?
                EMPTY_JSON : params.toString());
        url = checkUrlPrefix(url);
        Request.Builder builder = new Request.Builder().url(url);
        if (tag != null) {
            if (tag instanceof ArrayMap) {
                throw new IllegalArgumentException("tag can not be ArrayMap.");
            }
            builder.tag(tag);
        }
        builder.addHeader("Authorization", TOKEN_PREFIX + GlobalParams.token);
        Request request = builder.put(reqBody).build();
        executeCall(request, onResponse);
    }

    public static <T> void put(String url, ArrayMap<String, String> params, Object tag, OnResponse<T> response) {
        post(url, params, tag, METHOD_PUT, response);
    }

    public static void download(final String fileUrl, final String targetPath, final String targetFileName, final
    DownloadHandler downloadHandler) {
        final Request request = new Request.Builder().url(fileUrl).build();
        requestClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
                if (handler != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            downloadHandler.onComplete(false, null);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) {
                File dir = new File(targetPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                final File targetFile = new File(dir, targetFileName);
                if (targetFile.exists()) {
                    targetFile.delete();
                }
                BufferedOutputStream bos = null;
                try {
                    bos = new BufferedOutputStream(new FileOutputStream
                            (targetFile));
                    byte[] bts = new byte[1024];
                    int length = -1;
                    InputStream stream = response.body().byteStream();
                    while ((length = stream.read(bts)) != -1) {
                        bos.write(bts, 0, length);
                    }
                    bos.flush();
                    if (handler != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                downloadHandler.onComplete(true, targetFile);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    onFailure(call, e);
                } finally {
                    if (bos != null) {
                        try {
                            bos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (response != null) {
                        response.body().close();
                    }
                }
            }
        });
    }

    public static <T> void delete(String url, Object tag, OnResponse<T> onResponse) {
        url = checkUrlPrefix(url);
        Request.Builder builder = new Request.Builder().url(url);
        if (tag != null) {
            if (tag instanceof JsonObject) {
                throw new IllegalArgumentException("tag cannot be JsonObject.");
            }
            if (tag instanceof ArrayMap) {
                throw new IllegalArgumentException("tag cannot be ArrayMap.");
            }
            builder.tag(tag.getClass().getName());
        }
        builder.addHeader("Authorization", TOKEN_PREFIX + GlobalParams.token);
        Request request = builder.delete().build();
        executeCall(request, onResponse);
    }

    public static <T> void patch(String url, JsonObject params, Object tag, OnResponse<T> onResponse) {
        final RequestBody reqBody = RequestBody.create(MEDIA_TYPE_JSON, params == null ?
                EMPTY_JSON : params.toString());
        url = checkUrlPrefix(url);
        Request.Builder builder = new Request.Builder().url(url);
        if (tag != null) {
            if (tag instanceof JsonObject) {
                throw new IllegalArgumentException("tag cannot be JsonObject.");
            }
            builder.tag(tag.getClass().getName());
        }
        builder.header("Authorization", TOKEN_PREFIX + GlobalParams.token);
        Request request = builder.patch(reqBody).build();
        executeCall(request, onResponse);
    }

    public static void downloadSync(String downloadUrl, Object tag, final String downloadPath, final String fileName,
                                    final DownloaderListener listener) {
        OkHttpClient tmpClient = downloadClient.newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder().body(
                                new ProgressResponseBody(originalResponse.body(), listener))
                                .build();
                    }
                })
                .build();

        File path = new File(downloadPath);
        final File file = new File(downloadPath, fileName);
        if (!path.exists()) {
            path.mkdirs();
        }
        Request.Builder builder = new Request.Builder().url(downloadUrl);
        if (tag != null) {
            if (tag instanceof JsonObject) {
                throw new IllegalArgumentException("tag cannot be JsonObject.");
            }
            if (tag instanceof ArrayMap) {
                throw new IllegalArgumentException("tag cannot be ArrayMap.");
            }
            builder.tag(tag.getClass().getName());
        }
        Request request = builder.build();
        Response response = null;
        try {
            response = tmpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                //将返回结果转化为流，并写入文件
                int len;
                byte[] buf = new byte[2048];
                InputStream inputStream = response.body().byteStream();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                while ((len = inputStream.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, len);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
            } else {
                listener.onFail(new IOException(response.message()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (response != null && response.body() != null) {
                response.body().close();
            }
        }
    }

    public static void download(String downloadUrl, Object tag, final String downloadPath, final String fileName,
                                final DownloaderListener listener) {
        OkHttpClient tmpClient = downloadClient.newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder().body(
                                new ProgressResponseBody(originalResponse.body(), listener))
                                .build();
                    }
                })
                .build();

        File path = new File(downloadPath);
        final File file = new File(downloadPath, fileName);
        if (!path.exists()) {
            path.mkdirs();
        }
        Request.Builder builder = new Request.Builder().url(downloadUrl);
        if (tag != null) {
            if (tag instanceof JsonObject) {
                throw new IllegalArgumentException("tag cannot be JsonObject.");
            }
            if (tag instanceof ArrayMap) {
                throw new IllegalArgumentException("tag cannot be ArrayMap.");
            }
            builder.tag(tag.getClass().getName());
        }
        Request request = builder.build();
        //发送异步请求
        tmpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                listener.onFail(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //将返回结果转化为流，并写入文件
                int len;
                byte[] buf = new byte[2048];
                InputStream inputStream = response.body().byteStream();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                while ((len = inputStream.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, len);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
            }
        });
    }

    public static void cancel(Object tag) {
        if (tag == null) {
            requestClient.dispatcher().cancelAll();
            LogUtils.d("Cancel All HTTP Request!!!");
            return;
        }
        List<Call> calls = requestClient.dispatcher().queuedCalls();
        for (int i = calls.size() - 1; i >= 0; i--) {
            Call call = calls.get(i);
            if (call.request().tag().equals(tag.getClass().getName())) {
                LogUtils.d("Cancel HTTP Request With Tag[%s]", tag.getClass().getName());
                call.cancel();
            }
        }
    }

    private static void failedLog(int code, String msg, String url) {
        LogUtils.d("Request Failed[code=%s,msg=%s,url=%s]", code, msg, url);
    }


    public abstract static class OnResponse<T> {
        /**
         * 在请求成功的情况下额外的标记,用来表示操作是否是成功
         */
        private boolean success = false;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public void onStart() {
        }

        /**
         * 解析服务器返回的数据,非主线程
         *
         * @param body 服务器返回的字符串
         * @return 所需要被解析成的类型
         */
        public abstract T analyseResult(String body);

        /**
         * 此方法只有服务器成功返回诗句时回调
         *
         * @param result
         */
        public abstract void onSuccess(T result);

        public void onCompleted() {

        }

        /**
         * 网络请求失败的回调，包括网络错误请求结果，网络请求失败等
         *
         * @param code
         * @param msg
         */
        public void onFailed(int code, String msg, String url) {
            switch (code) {
                case HttpURLConnection.HTTP_CLIENT_TIMEOUT:
                    //PromptManager.showToast(R.string.network_timeout);
                    break;
                case HttpURLConnection.HTTP_INTERNAL_ERROR:
                    //PromptManager.showToast(R.string.msg_server_error);
                    break;
                case HttpURLConnection.HTTP_BAD_REQUEST:
                   // PromptManager.showToast(R.string.request_error);
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                   // PromptManager.showToast(R.string.operation_failed);
                    break;
                case HttpURLConnection.HTTP_FORBIDDEN:
                  //  PromptManager.showToast(R.string.has_no_permission);
                    break;
                case -1:
                default:
                   // PromptManager.showToast(R.string.request_error);
                    break;
            }
        }

        public void onError(IOException e, String url) {
            onFailed(-1, e.getMessage(), url);
        }

        public void onResultNull(String url) {
            onFailed(-1, null, url);
        }
    }

    static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            ResponseBody responseBody = response.body();
            String content = responseBody.string();
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append("Request:");
            logBuilder.append("\n\t");
            logBuilder.append(request.url());
            logBuilder.append("\n");
            logBuilder.append("Response:");
            logBuilder.append("\n\t");
            logBuilder.append("code=").append(response.code()).append(",");
            logBuilder.append("message=").append(response.message()).append(",");
            logBuilder.append("\n\t");
            logBuilder.append("body=").append(content);
            LogUtils.d(logBuilder.toString());
            return response
                    .newBuilder()
                    .body(ResponseBody.create(responseBody.contentType(), content))
                    .build();
        }
    }

    public interface DownloadHandler {
        void onComplete(boolean success, File file);
    }

    private static class ProgressResponseBody extends ResponseBody {
        private final ResponseBody responseBody;
        private final DownloaderListener downloaderListener;
        private BufferedSource bufferedSource;

        public ProgressResponseBody(ResponseBody responseBody, DownloaderListener
                downloaderListener) {
            this.responseBody = responseBody;
            this.downloaderListener = downloaderListener;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }


        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (bufferedSource == null) {
                bufferedSource = Okio.buffer(source(responseBody.source()));
            }
            return bufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {
                long totalBytesRead = 0L;
                long startTime = System.nanoTime();

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    long bytesRead = super.read(sink, byteCount);
                    totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                    float kb = totalBytesRead / 1024;
                    double second = (System.nanoTime() - startTime) * Math.pow(10, -9);
                    downloaderListener.update(totalBytesRead, responseBody.contentLength(),
                            kb / second, bytesRead == -1);
                    return bytesRead;
                }
            };
        }
    }


    public interface DownloaderListener {
        /**
         * @param bytesRead     已下载字节数
         * @param contentLength 总字节数
         * @param done          是否下载完成
         */
        void update(long bytesRead, long contentLength, double speed, boolean done);

        void onFail(Exception e);
    }
}
