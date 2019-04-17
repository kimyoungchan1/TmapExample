package com.dudck.example.tmap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.text.Layout;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapData.FindPathDataAllListenerCallback;
import com.skt.Tmap.TMapCircle;
import com.skt.Tmap.TMapData.*;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.LogManager;

import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

        private final String TMAP_API_KEY = "cd801069-d783-4402-a170-dcef9b3fe221";
        TMapView tmap;
        TMapData tmapdata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);

        tmap = new TMapView(this);

        tmap.setSKTMapApiKey(TMAP_API_KEY);

        linearLayoutTmap.addView(tmap);

        tmap.setIconVisibility(true);//현재위치로 표시될 아이콘을 표시할지 여부를 설정합니다.

        setGps();
        marker();


    }

    public final LocationListener mLocationListener;

    {
        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    tmap.setLocationPoint(longitude, latitude);
                    tmap.setCenterPoint(longitude, latitude);

                    TMapPoint tMapPoint = new TMapPoint(tmap.getLatitude(), tmap.getLongitude()); //tMapPoint 에는 현재 SKT본사가 잡혀있다
                    Log.i(String.valueOf(tMapPoint.getLatitude()), "서클 위도");
                    Log.i(String.valueOf(tMapPoint.getLongitude()), "서클 경도");
                    TMapCircle tMapCircle = new TMapCircle();
                    tMapCircle.setCenterPoint(tMapPoint);
                    tMapCircle.setRadius(300);
                    tMapCircle.setCircleWidth(2);
                    tMapCircle.setLineColor(Color.BLUE);
                    tMapCircle.setAreaColor(Color.GRAY);
                    tMapCircle.setAreaAlpha(100);
                    tmap.addTMapCircle("circle1", tMapCircle);




                    // polyline
                    //TMapPoint tMapPoint = new TMapPoint(tmap.getLatitude(), tmap.getLongitude());
                    //Log.i(String.valueOf(tMapPoint.getLatitude()), "폴리라인 위도");
                    //Log.i(String.valueOf(tMapPoint.getLongitude()), "폴리라인 경도");
                    //ArrayList<TMapPoint> alTMapPoint = new ArrayList<TMapPoint>();
                    //alTMapPoint.add( new TMapPoint(37.613251, 127.03008399999999) ); // 미아사거리
                    //alTMapPoint.add( new TMapPoint(tmap.getLatitude(), tmap.getLongitude()) ); // 현재위치
                    //Log.i(String.valueOf(tMapPoint.getLatitude()), "폴리라인2 위도");
                    //Log.i(String.valueOf(tMapPoint.getLongitude()), "폴리라인2 경도");
                    //TMapPolyLine tMapPolyLine = new TMapPolyLine();
                    //tMapPolyLine.setLineColor(Color.BLUE);
                    //tMapPolyLine.setLineWidth(2);
                    //for( int i=0; i<alTMapPoint.size(); i++ ) {
                    //    tMapPolyLine.addLinePoint( alTMapPoint.get(i) );
                    //}
                    //tmap.addTMapPolyLine("Line1", tMapPolyLine);


                    TMapPoint tMapPointStart = new TMapPoint(tmap.getLatitude(), tmap.getLongitude()); // 현재위치
                    TMapPoint tMapPointEnd = new TMapPoint(37.539705, 126.94604000000004); // 마포역(목적지)

                    tmapdata = new TMapData();
                    tmapdata.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, tMapPointStart, tMapPointEnd, new TMapData.FindPathDataListenerCallback() {
                        @Override
                        public void onFindPathData(TMapPolyLine polyLine) {
                            tmap.addTMapPath(polyLine);
                        }
                    });


                }
            }



            public void onProviderDisabled(String provider) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

        };
    }


    public void setGps() {

        final LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자(실내에선 NETWORK_PROVIDER 권장)
                1000, // 통지사이의 최소 시간간격 (miliSecond)
                1, // 통지사이의 최소 변경거리 (m)
                mLocationListener);



    }

    public void marker(){
        // 마커 아이콘
        TMapMarkerItem markerItem1 = new TMapMarkerItem();
        TMapMarkerItem markerItem2 = new TMapMarkerItem();

        TMapPoint tMapPoint1 = new TMapPoint(37.613251, 127.03008399999999); // 미아사거리 TMapPoint1에는 미아사거리
        TMapPoint tMapPoint2 = new TMapPoint(37.62655, 127.02602300000001); // 미아 TMapPoint2 에는 미아역이 잡혀있다

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker1);

        markerItem1.setIcon(bitmap); // 마커 아이콘 지정
        markerItem1.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        markerItem1.setTMapPoint( tMapPoint1 ); // 마커의 좌표 지정
        markerItem1.setName("미아사거리"); // 마커의 타이틀 지정
        markerItem1.setCanShowCallout(true);
        markerItem1.setCalloutTitle("미아사거리");
        markerItem1.setCalloutSubTitle("상행선 : 10: 57\r\n하행선 : 11:56");
        tmap.addMarkerItem("markerItem1", markerItem1); // 지도에 마커 추가
        tmap.setCenterPoint( 127.03008399999999, 37.613251 );

        markerItem2.setIcon(bitmap); // 마커 아이콘 지정
        markerItem2.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        markerItem2.setTMapPoint( tMapPoint2 ); // 마커의 좌표 지정
        markerItem2.setName("미아"); // 마커의 타이틀 지정
        tmap.addMarkerItem("markerItem2", markerItem2); // 지도에 마커 추가
        tmap.setCenterPoint( 127.02602300000001, 37.62655 );




    }

}

