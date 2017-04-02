package com.kmk.httpiotivityproxy;

import android.util.Log;

import org.iotivity.base.ErrorCode;
import org.iotivity.base.ModeType;
import org.iotivity.base.OcConnectivityType;
import org.iotivity.base.OcException;
import org.iotivity.base.OcHeaderOption;
import org.iotivity.base.OcPlatform;
import org.iotivity.base.OcRepresentation;
import org.iotivity.base.OcResource;
import org.iotivity.base.PlatformConfig;
import org.iotivity.base.QualityOfService;
import org.iotivity.base.ServiceType;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

public class IoTivityRequest {
    private final String TAG = IoTivityRequest.class.getSimpleName();
    public String result;

    public void test(final String uri, final HttpServletResponse resp) {
        OcPlatform.OnResourceFoundListener onResourceFoundListener = new OcPlatform.OnResourceFoundListener() {
            @Override
            public void onResourceFound(OcResource ocResource) {
                if (ocResource == null) {
                    Log.d(TAG, "Found resource is invalid");
                    return;
                }
                Log.d(TAG, "Host address of the resource: " + ocResource.getHost());

                final OcResource.OnGetListener onGetListener = new OcResource.OnGetListener() {
                    @Override
                    public void onGetCompleted(List<OcHeaderOption> list, OcRepresentation ocRepresentation) {
                        Log.d(TAG, "GET request was successful");
                        Log.d(TAG, "Resource URI: " + ocRepresentation.getUri());
                        try {
                            result = "" + ocRepresentation.getValue("result");
                            Log.d(TAG, "Result: " + result);
                            synchronized (resp) {
                                resp.notify();
                            }
                        } catch (OcException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onGetFailed(Throwable throwable) {
                        if (throwable instanceof OcException) {
                            OcException ocEx = (OcException) throwable;
                            ErrorCode errCode = ocEx.getErrorCode();
                            Log.d(TAG, "Error code: " + errCode);
                        }
                        Log.d(TAG, "Failed to GET!");
                    }
                };

                String resourceName = "/" + uri.substring(7).split("/")[1];
                if (ocResource.getUri().equals(resourceName)) {
                    HashMap<String, String> queryParams = new HashMap<>();
                    try {
                        ocResource.get(queryParams, onGetListener);
                    } catch (OcException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "No resource: " + resourceName);
                }
            }

            @Override
            public void onFindResourceFailed(Throwable throwable, String s) {

            }
        };

        PlatformConfig platformConfig = new PlatformConfig(MainActivity.getMyContext(), ServiceType.IN_PROC, ModeType.CLIENT,
                "0.0.0.0", 0, QualityOfService.LOW
        );
        OcPlatform.Configure(platformConfig);
        String uriIp = uri.substring(7).split("/")[0];
        Log.d(TAG, "URI IP: " + uriIp);
        String requestUri = "coap://" + uriIp + OcPlatform.WELL_KNOWN_QUERY + "?rt=core.thing";
        try {
            OcPlatform.findResource("", requestUri, EnumSet.of(OcConnectivityType.CT_DEFAULT), onResourceFoundListener);
        } catch (OcException e) {
            e.printStackTrace();
        }
    }
}
