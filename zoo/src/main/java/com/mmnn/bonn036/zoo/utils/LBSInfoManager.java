package com.mmnn.bonn036.zoo.utils;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;
import java.util.List;

/**
 * Created by dz on 2015/05/10.
 */

public class LBSInfoManager {
    public final static int MAX_RETRY = 5;
    public final static double LALN_DEFAULT = -10000;
    public final static double LALN_NEAR_LIMIT = 0.001;
    private final static String TAG = "LBSInfoManager";
    private final static int CACHE_TIME = 1800000;
    private static String sCountry;
    private static String sProvince;
    private static String sCity;
    private static String sDistrict;
    private static double sLatitude = LALN_DEFAULT;//40.03;         -90~90
    private static double sLongitude = LALN_DEFAULT;//116.34;    -180~180
    private static int sTempC = -1;
    private static int sPm25 = -1;
    private static int sHumidity = -1;
    private static long sLastLocationTimeMillis = 0;
    private static long sLastWeatherTimeMillis = 0;
    private LocationManager mLocationManager;
    //	private LocationManagerProxy mAMapLocationManager;
    //	private LocationClient mBDLocationClient;
    private int mLocationRetry = 0;

    private Context mContext;
    //	private AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
//		@Override
//		public void onLocationChanged(AMapLocation aMapLocation) {
//			Log.d(TAG, "onLocationChanged");
//			if (aMapLocation != null
//					&& aMapLocation.getAMapException().getErrorCode() == 0) {
//				Log.d(TAG, "onLocationChanged aMapLocation: " + aMapLocation);
//				sLastLocationTimeMillis = System.currentTimeMillis();
//				sLatitude = aMapLocation.getLatitude();
//				sLongitude = aMapLocation.getLongitude();
//				sCountry = aMapLocation.getCountry();
//				sProvince = aMapLocation.getProvince();
//				sCity = aMapLocation.getCity();
//				sDistrict = aMapLocation.getDistrict();
//				mAMapLocationManager.removeUpdates(mAMapLocationListener);
//				onGetLocationDone(true);
//			} else {
//				mLocationRetry++;
//				Log.d(TAG, "onLocationChanged retry: " + mLocationRetry);
//				if (mLocationRetry >= MAX_RETRY) {
//					mAMapLocationManager.removeUpdates(mAMapLocationListener);
//					onGetLocationDone(false);
//				}
//			}
//		}
//
//		@Override
//		public void onLocationChanged(Location location) {
//			Log.d(TAG, "onLocationChanged");
//		}
//
//		@Override
//		public void onStatusChanged(String provider, int status, Bundle extras) {
//			Log.d(TAG, "onStatusChanged provider: " + provider + " status: " + status);
//		}
//
//		@Override
//		public void onProviderEnabled(String provider) {
//			Log.d(TAG, "onProviderEnabled provider: " + provider);
//		}
//
//		@Override
//		public void onProviderDisabled(String provider) {
//			Log.d(TAG, "onProviderDisabled provider: " + provider);
//		}
//	};
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
                Geocoder geocoder = new Geocoder(mContext);
                try {
                    List<Address> addresses = geocoder.getFromLocation(sLatitude, sLongitude, 1);
                    for (Address address : addresses) {
                        Log.i(TAG, "adress: " + address);
                        sCountry = address.getCountryName();
                        sProvince = address.getAdminArea();
                        sCity = address.getLocality();
                        sDistrict = address.getSubLocality();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mLocationManager.removeUpdates(mNativeLocationListener);
                onGetLocationDone(true);
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
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
//				Log.d(TAG, "gps status satellite status changed");
                    GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
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

    public LBSInfoManager(Context context) {
        if (context != null) {
            mContext = context.getApplicationContext();
        }
    }

    public void destroy() {
//		if (mSocialLocation != null) {
//			mSocialLocation.cancel();
//		}
//		if (mAMapLocationManager != null) {
//			mAMapLocationManager.removeUpdates(mAMapLocationListener);
//			mAMapLocationManager.destroy();
//		}
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mNativeLocationListener);
        }
    }

    public void getLocation(boolean isNew) {
        Log.i(TAG, "getLocation isNew: " + isNew);
        if (mContext == null) {
            return;
        }
        if (isNew || System.currentTimeMillis() - sLastLocationTimeMillis >= CACHE_TIME) {
            getLocationNative();
//			getLocationAMap();
//			getLocationBD();
            mLocationRetry = 0;
        } else {
            Log.i(TAG, "imidiate return");
        }
    }

//	private void getLocationAMap() {
//		if (mAMapLocationManager == null) {
//			mAMapLocationManager = LocationManagerProxy.getInstance(XMRCApplication.getInstance().getApplicationContext());
//			mAMapLocationManager.setGpsEnable(false);
//		}
////		此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
////		注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用removeUpdates()方法来取消定位请求
////		在定位结束后，在合适的生命周期调用destroy()方法
////		其中如果间隔时间为-1，则定位只定一次,
////		在单次定位情况下，定位无论成功与否，都无需调用removeUpdates()方法移除请求，定位sdk内部会移除
//		mAMapLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, 2000, 15, mAMapLocationListener);
//	}

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
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void onGetLocationDone(boolean isSuccess) {
        Log.i(TAG, "onGetLocationDone: " + isSuccess + " " + sLatitude + " " + sLongitude + " " + sCountry + " " + sProvince + " " + sCity + " " + sDistrict);
    }

    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setSpeedRequired(true);
        criteria.setSpeedAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setBearingRequired(true);
        criteria.setBearingAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setAltitudeRequired(true);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        return criteria;
    }

    private void getLocationNative() {
        Log.d(TAG, "getLocationNative");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        }
        mLocationManager.requestSingleUpdate(getCriteria(), mNativeLocationListener, null);
        String bestProvider = mLocationManager.getBestProvider(getCriteria(), true);
        if (bestProvider.equals(LocationManager.GPS_PROVIDER) &&
                !mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//			Toast.makeText(context, "请开启GPS导航...", Toast.LENGTH_SHORT).show();
//			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//			context.startActivity(intent);
        }
        Log.d(TAG, "bestProvider: " + bestProvider);
        if (bestProvider.equals(LocationManager.GPS_PROVIDER)) {
            mLocationManager.addGpsStatusListener(mGpsListener);
        } else {
            mLocationManager.removeGpsStatusListener(mGpsListener);
        }
        try {
//			mLocationManager.requestSingleUpdate(bestProvider, mNativeLocationListener, null);
            mLocationManager.requestLocationUpdates(bestProvider, 10000, 1, mNativeLocationListener);
        } catch (Exception e) {
            e.printStackTrace();
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

}
