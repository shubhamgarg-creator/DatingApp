package com.datingapp.dependencies.interceptors;
/**
 * @package com.datingapp
 * @subpackage dependencies.interceptors
 * @category NetWorkInterceptor
 * @author Trioangle Product Team
 * @version 1.0
 **/

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

/*****************************************************************
 NetWork Interceptor
 ****************************************************************/
public class NetworkInterceptor implements Interceptor {
    private Context mContext;

    public NetworkInterceptor(Context context) {
        mContext = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        try {
            if (!isOnline(mContext)) {
                response = new Response.Builder()
                        .protocol(Protocol.HTTP_1_1)
                        .message("No internet")
                        .body(getNetworkFailResponse(response))
                        .code(600)
                        .request(chain.request())
                        .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return (netInfo != null && netInfo.isConnected());
    }

    private ResponseBody getNetworkFailResponse(Response response) {
        JSONObject jsonObject = new JSONObject();
        MediaType contentType = response.body().contentType();
        try {
            jsonObject.put("code", 600);
            jsonObject.put("status", "Cancel");
            jsonObject.put("message", "No network connection");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ResponseBody.create(contentType, jsonObject.toString());
    }
}
