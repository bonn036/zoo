package com.mmnn.zoo.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.mmnn.zoo.MyApp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by dz on 2015/05/10.
 */

public class LBSInfoManager {
    public final static int MSG_LBS_FAIL = 100;
    public final static int HOLD_TIME = 20000;
    public final static int MAX_RETRY = 5;
    public final static double LALN_DEFAULT = -10000;
    public final static double LALN_NEAR_LIMIT = 0.001;
    private final static String TAG = "LBSInfoManager";
    private final static int CACHE_TIME = 1800000;
    private static LBSInfoManager sInstance = null;
    private static String sCountry;
    private static String sProvince;
    private static String sCity;
    private static String sDistrict;
    private static List<String> sAdresses = new ArrayList<>();
    private static double sLatitude = LALN_DEFAULT;//40.03;         -90~90
    private static double sLongitude = LALN_DEFAULT;//116.34;    -180~180
    private static int sTempC = -1;
    private static int sPm25 = -1;
    private static int sHumidity = -1;
    private static long sLastLocationTimeMillis = 0;
    private static long sLastWeatherTimeMillis = 0;
    private LocationManager mLocationManager;
    private AsyncTask mGeocoderTask = null;

//    private AMapLocationClient mLocationClient;

    private ArrayList<LocationCallback> mMyLocationCallBacks = new ArrayList<>();
    //	private char[] lock = new char[0];
    private int mLocationRetry = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_LBS_FAIL) {
                onGetLocationDone(false);
            }
        }
    };

//    private AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
//        @Override
//        public void onLocationChanged(AMapLocation aMapLocation) {
//            Log.d(TAG, "onLocationChanged");
//            if (aMapLocation != null) {
//                switch (aMapLocation.getErrorCode()) {
//                    case 0: {
//                        Log.d(TAG, "onLocationChanged aMapLocation: " + aMapLocation);
//                        sLastLocationTimeMillis = System.currentTimeMillis();
//                        sLatitude = aMapLocation.getLatitude();
//                        sLongitude = aMapLocation.getLongitude();
//                        sCountry = aMapLocation.getCountry();
//                        sProvince = aMapLocation.getProvince();
//                        sCity = aMapLocation.getCity();
//                        sDistrict = aMapLocation.getDistrict();
//                        sAdresses.clear();
//                        onGetLocationDone(true);
//                        break;
//                    }
//                    case 12:
//                        Toast.makeText(MyApp.getInstance().getApplicationContext(), R.string.lbs_permission_tips, Toast.LENGTH_LONG).show();
//                        break;
//                }
//                return;
//            }
//            mLocationRetry++;
//            Log.d(TAG, "onLocationChanged retry: " + mLocationRetry);
//            if (mLocationRetry >= MAX_RETRY) {
//                onGetLocationDone(false);
//            }
//        }
//    };

    private LocationListener mNativeLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(TAG, "onLocationChanged: " + location);
            if (location != null) {
                Log.i(TAG, "onLocationChanged: "
                        + "\n getProvider: " + location.getProvider()
                        + "\n getLatitude: " + location.getLatitude()
                        + "\n getLongitude: " + location.getLongitude()
                        + "\n getAltitude: " + location.getAltitude()
                        + "\n getAccuracy: " + location.getAccuracy()
                        + "\n getBearing: " + location.getBearing()
                        + "\n getSpeed: " + location.getSpeed());
                if (location.getExtras() != null) {
                    Log.i(TAG, "getExtras: " + location.getExtras());
                }
                sLatitude = location.getLatitude();
                sLongitude = location.getLongitude();
                if (mGeocoderTask != null) {
                    mGeocoderTask.cancel(true);
                }
                mGeocoderTask = new AsyncTask<Void, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        Geocoder geocoder = new Geocoder(MyApp.getInstance().getApplicationContext());
                        if (Geocoder.isPresent()) {
                            try {
                                List<Address> addresses = geocoder.getFromLocation(sLatitude, sLongitude, 1);
                                for (Address address : addresses) {
                                    Log.i(TAG, "address: " + address);
                                    sCountry = address.getCountryName();
                                    sProvince = address.getAdminArea();
                                    sCity = address.getLocality();
                                    sDistrict = address.getSubLocality();
//                                    sAdresses.clear();
//                                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
//                                        Log.d(TAG, address.getAddressLine(i));
//                                        sAdresses.add(address.getAddressLine(i));
//                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                        return true;
                    }

                    @Override
                    protected void onPostExecute(Boolean success) {
                        if (ActivityCompat.checkSelfPermission(MyApp.getInstance().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                || ActivityCompat.checkSelfPermission(MyApp.getInstance().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            mLocationManager.removeUpdates(mNativeLocationListener);
                        }
                        onGetLocationDone(true);
                    }

                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            } else {
                mLocationRetry++;
                Log.d(TAG, "onLocationChanged retry: " + mLocationRetry);
                if (mLocationRetry >= MAX_RETRY) {
                    mLocationManager.removeUpdates(mNativeLocationListener);
                    onGetLocationDone(false);
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d(TAG, "onProviderDisabled");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d(TAG, "onProviderEnabled");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d(TAG, "onStatusChanged");
        }
    };
    private GpsStatus.Listener mGpsListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.d(TAG, "gps status first fix");
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS: {
//				Log.d(TAG, "gps status satellite status changed");
                    LocationManager locationManager = (LocationManager) MyApp.getInstance().getSystemService(Context.LOCATION_SERVICE);
                    GpsStatus gpsStatus = locationManager.getGpsStatus(null);
                    //获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite satellite = iters.next();
                        Log.d(TAG, "satelite: " + count +
                                "\nAzimuth: " + satellite.getAzimuth() +
                                "\nElevation: " + satellite.getElevation() +
                                "\nPrn: " + satellite.getPrn() +
                                "\nSnr: " + satellite.getSnr() +
                                "\nAlmanac: " + satellite.hasAlmanac() +
                                "\nEphemeris: " + satellite.hasEphemeris() +
                                "\nUsed in fix: " + satellite.usedInFix());
                        count++;
                    }
                    Log.d(TAG, "satellites: " + count);
                    break;
                }
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.d(TAG, "gps status started");
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.d(TAG, "gps status stoped");
                    break;
                default:
                    Log.d(TAG, "gps status default");
                    break;
            }
        }
    };

    private LBSInfoManager() {
    }

    public synchronized static LBSInfoManager getInstance() {
        if (sInstance == null) {
            sInstance = new LBSInfoManager();
        }
        return sInstance;
    }

    public static int getDistance(double la1, double ln1, double la2, double ln2) {
        Log.d(TAG, "getDistance: " + la1 + " " + la2 + " " + ln1 + " " + ln2);
//		double EARTH_RADIUS = 6371000.0;//地球半径

//		double radLat1 = rad(la1);
//		double radLat2 = rad(la2);
//		double a = radLat1 - radLat2;
//		double b = rad(ln1) - rad(ln2);
//		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
//		s = s * EARTH_RADIUS;
//		s = Math.round(s * 10000) / 10000;
//		Log.d(TAG, "getDistance result: " + s);

//		double dx = ln1 - ln2; // 经度差值
//		double dy = la1 - la2; // 纬度差值
//		double b = (la1 + la2) / 2.0; // 平均纬度
//		double Lx = rad(dx) * EARTH_RADIUS * Math.cos(rad(b)); // 东西距离
//		double Ly = EARTH_RADIUS * rad(dy); // 南北距离
//		double s = Math.sqrt(Lx * Lx + Ly * Ly);  // 用平面的矩形对角距离公式计算总距离
//		Log.d(TAG, "getDistance result: " + s);

        float[] results = new float[1];
        try {
            Location.distanceBetween(la1, ln1, la2, ln2, results);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getDistance result: " + results[0] + " " + (int) (results[0]));
        return Double.valueOf(results[0]).intValue();
    }

    public void destroy() {
//		if (mSocialLocation != null) {
//			mSocialLocation.cancel();
//		}
//        if (mLocationClient != null) {
//            mLocationClient.stopLocation();
//            mLocationClient.onDestroy();
//        }
        if (mGeocoderTask != null) {
            mGeocoderTask.cancel(true);
        }
        if (mLocationManager != null) {
            if (ActivityCompat.checkSelfPermission(MyApp.getInstance().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(MyApp.getInstance().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationManager.removeUpdates(mNativeLocationListener);
            }
        }
        mMyLocationCallBacks.clear();
    }

    public void getLocation(boolean isNew, final LocationCallback callback) {
        Log.i(TAG, "getLocation isNew: " + isNew);
        if (isNew || System.currentTimeMillis() - sLastLocationTimeMillis >= CACHE_TIME) {
            if (callback != null && !mMyLocationCallBacks.contains(callback)) {
                Log.d(TAG, "add callback");
                mMyLocationCallBacks.add(callback);
            }
            mLocationRetry = 0;
            mHandler.removeMessages(MSG_LBS_FAIL);
            mHandler.sendEmptyMessageDelayed(MSG_LBS_FAIL, HOLD_TIME);
            getLocationNative();
//            getLocationAMap();
//			getLocationBD();
        } else if (callback != null) {
            Log.i(TAG, "imidiate callback true");
            callback.onResult(true, sLatitude, sLongitude, sProvince, sCity, sDistrict, sAdresses);
        }
    }

//    private void getLocationAMap() {
//        if (mLocationClient == null) {
//            mLocationClient = new AMapLocationClient(MyApp.getInstance().getApplicationContext());
//            AMapLocationClientOption locationOption = new AMapLocationClientOption();
//            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//            locationOption.setOnceLocation(true);
////            locationOption.setInterval(30000);
//            mLocationClient.setLocationOption(locationOption);
//            mLocationClient.setLocationListener(mAMapLocationListener);
//        }
//        mLocationClient.startLocation();
//    }

    public boolean isPositionOn() {
//		Context context = XMRCApplication.getInstance().getApplicationContext();
//		PackageManager manager = context.getPackageManager();
//		try {
//			PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
//			for (String permission : packageInfo.requestedPermissions) {
//				Log.d(TAG, "permission: " + permission);
//			}
//		} catch (PackageManager.NameNotFoundException e) {
//			e.printStackTrace();
//		}
        LocationManager locationManager = (LocationManager) MyApp.getInstance().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void onGetLocationDone(boolean isSuccess) {
        Log.i(TAG, "onGetLocationDone: " + isSuccess + " " + sLatitude + " " + sLongitude + " " + sCountry + " " + sProvince + " " + sCity + " " + sDistrict);
        try {
            Log.i(TAG, "callback: " + isSuccess + " count: " + mMyLocationCallBacks.size());
            for (LocationCallback callback : mMyLocationCallBacks) {
                callback.onResult(isSuccess, sLatitude, sLongitude, sProvince, sCity, sDistrict, sAdresses);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMyLocationCallBacks.clear();
        if (isSuccess) {
            sLastLocationTimeMillis = System.currentTimeMillis();
        }
    }

    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
//        criteria.setSpeedRequired(true);
//        criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
//        criteria.setBearingRequired(true);
//        criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
//        criteria.setAltitudeRequired(true);
//        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
//        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        return criteria;
    }

//	private BDLocationListener mBDLocationListener = new BDLocationListener() {
//		@Override
//		public void onReceiveLocation(BDLocation location) {
//			Log.d(TAG, "BDLocationListener onReceiveLocation: " + location.getLatitude() + " " + location.getLongitude());
//		}
//	};

//	private void getLocationBD() {
//		if (mBDLocationClient == null) {
//			mBDLocationClient = new LocationClient(XMRCApplication.getInstance().getApplicationContext());
//			mBDLocationClient.registerLocationListener(mBDLocationListener);
//			LocationClientOption option = new LocationClientOption();
////		option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//			option.setOpenGps(false);// 打开gps
//			option.setCoorType("gcj02"); // 设置坐标类型, bd09ll, gcj02
//		//		option.setIsNeedAddress(true);
//		//		option.setScanSpan(1000);
//		//		option.setIgnoreKillProcess(true);
//		//		option.setEnableSimulateGps(false);
//		//		option.setIsNeedLocationDescribe(true);
//		//		option.setIsNeedLocationPoiList(true);
//		//		option.SetIgnoreCacheException(true);
//		//		option.setAddrType("all");
//		//		option.setLocationNotify(false);
//		//		option.setPriority(LocationClientOption.GpsFirst);
//		//		option.disableCache(true);
//			mBDLocationClient.setLocOption(option);
//			mBDLocationClient.start();
//		} else {
//			mBDLocationClient.stop();
//		}
//		mBDLocationClient.start();
//	}

//	private void getLocationBDOld(boolean isNew, final LocationCallback callback) {
//		Log.d(TAG, "getLocation");
//		if (isNew || System.currentTimeMillis() - sLastLocationTimeMillis >= CACHE_TIME) {
//			if (mSocialLocation == null) {
//				mSocialLocation = new SocialLocation(XMRCApplication.getInstance().getApplicationContext());
//			} else {
//				mSocialLocation.cancel();
//			}
//			mSocialLocation.getLocation(false, new Callback() {
//				@Override
//				public void onSuccess(String result) {
//					Log.d(TAG, "onSuccess: " + result);
//					boolean isSuccess = true;
//					try {
//						JSONObject resultObject = new JSONObject(result);
//						sProvince = resultObject.getString("province");
//						sCity = resultObject.getString("city");
//						sDistrict = resultObject.getString("district");
//						sLatitude = resultObject.getDouble("latitude");
//						sLongitude =  resultObject.getDouble("longitude");
//						isSuccess = true;
//					} catch (Exception e) {
//						e.printStackTrace();
//						isSuccess = false;
//					}
//					if (callback != null) {
//						callback.onResult(isSuccess, sLatitude, sLongitude, sProvince, sCity, sDistrict);
//					}
//				}
//				@Override
//				public void onFailed(String errorMsg) {
//					Log.d(TAG, "onFailed: " + errorMsg);
//					if (callback != null) {
//						callback.onResult(false, sLatitude, sLongitude, null, null, null);
//					}
//				}
//			});
//		} else {
//			if (callback != null) {
//				callback.onResult(true, sLatitude, sLongitude, sProvince, sCity, sDistrict);
//			}
//		}
//	}

    private void getLocationNative() {
        Log.d(TAG, "getLocationNative");
        Context context = MyApp.getInstance().getApplicationContext();
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        String bestProvider = mLocationManager.getBestProvider(getCriteria(), true);
        if (bestProvider == null) {
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                bestProvider = LocationManager.GPS_PROVIDER;
            }
            if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                bestProvider = LocationManager.NETWORK_PROVIDER;
            }
            if (bestProvider == null) {
                onGetLocationDone(false);
                return;
            }
        }
        Log.d(TAG, "bestProvider: " + bestProvider);
        try {
            if (ActivityCompat.checkSelfPermission(MyApp.getInstance().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(MyApp.getInstance().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                if (bestProvider.equals(LocationManager.GPS_PROVIDER)) {
//                    mLocationManager.addGpsStatusListener(mGpsListener);
//                } else {
//                    mLocationManager.removeGpsStatusListener(mGpsListener);
//                }
                mLocationManager.requestLocationUpdates(bestProvider, 10000, 500, mNativeLocationListener);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onGetLocationDone(false);
        }
//		Location lastLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//		if (lastLocation != null) {
//			Log.i(TAG, "lastLocation: "
//					+ "\n getProvider: " + lastLocation.getProvider()
//					+ "\n getLatitude: " + lastLocation.getLatitude()
//					+ "\n getLongitude: " + lastLocation.getLongitude()
//					+ "\n getAltitude: " + lastLocation.getAltitude()
//					+ "\n getAccuracy: " + lastLocation.getAccuracy()
//					+ "\n getBearing: " + lastLocation.getBearing()
//					+ "\n getSpeed: " + lastLocation.getSpeed()
//					+ "\n getExtras: " + lastLocation.getExtras().toString()
//			);
//			if (lastLocation.getExtras() != null) {
//				Log.i(TAG, "getExtras: " + lastLocation.getExtras());
//			}
//			Geocoder geocoder = new Geocoder(XMRCApplication.getInstance().getApplicationContext());
//			try {
//				List<Address>addresses = geocoder.getFromLocation(sLatitude, sLongitude, 5);
//				for (Address adress : addresses) {
//					Log.d(TAG, "adress: " + addresses);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
    }

//	private static double rad(double d) {
//		return d * Math.PI / 180.0;
//	}

    public interface LocationCallback {
        void onResult(Boolean isSuccess, double la, double ln, String province, String city, String district, final List<String> addresses);
    }

}
