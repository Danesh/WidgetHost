package com.example.danesh.surfacehost;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.example.danesh.surfaceservice.IMyAidlInterface;

import net.rdrei.android.viewpagerindicator.TabPageIndicator;
import net.rdrei.android.viewpagerindicator.TitlePageIndicator;
import net.rdrei.android.viewpagerindicator.UnderlinePageIndicator;


public class MainActivity extends Activity {
    IMyAidlInterface iMyAidlInterface, iMyAidlInterface2, iMyAidlInterface3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(3);
        //viewPager.setPageMargin(4);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if (position == 0) {
                    return "Doodle";
                } else if (position == 1) {
                    return "VideoPlayer";
                } else {
                    return "WebView";
                }
            }

            @Override
            public Object instantiateItem(ViewGroup collection, final int position) {
                final View v = new View(getBaseContext());
                v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));
                v.setBackgroundColor(Color.DKGRAY);
                collection.addView(v, position);
                v.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        int[] blah = new int[2];
                        v.getLocationOnScreen(blah);
                        Rect woot = new Rect();
                        viewPager.getHitRect(woot);
                        v.getLocalVisibleRect(woot);
                        try {
                            IMyAidlInterface myAidlInterface = iMyAidlInterface;
                            if (position == 1) {
                                myAidlInterface = iMyAidlInterface2;
                            } else if (position == 2) {
                                myAidlInterface = iMyAidlInterface3;
                            }
                            myAidlInterface.propertiesChanged(position, blah[0], blah[1], v.getWidth(), v.getHeight(), v.getLocalVisibleRect(woot));
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return v;
            }

            @Override
            public void destroyItem(ViewGroup collection, int position, Object view) {
                collection.removeView((View) view);
            }
        });
        Intent i = new Intent();
        i.setClassName("com.example.danesh.surfaceservice", "com.example.danesh.surfaceservice.MyService");
        bindService(i, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                try {
                    iMyAidlInterface.onPause();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, Context.BIND_AUTO_CREATE);
        i.setClassName("com.example.danesh.surfaceservice", "com.example.danesh.surfaceservice.MyService2");
        bindService(i, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                iMyAidlInterface2 = IMyAidlInterface.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                try {
                    iMyAidlInterface2.onPause();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, Context.BIND_AUTO_CREATE);
        i.setClassName("com.example.danesh.surfaceservice", "com.example.danesh.surfaceservice.MyService3");
        bindService(i, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                iMyAidlInterface3 = IMyAidlInterface.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                try {
                    iMyAidlInterface3.onPause();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, Context.BIND_AUTO_CREATE);
        TitlePageIndicator titleIndicator = (TitlePageIndicator)findViewById(R.id.titles);
        titleIndicator.setClipPadding(-200);
        titleIndicator.setViewPager(viewPager);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (iMyAidlInterface != null) {
            try {
                iMyAidlInterface.onPause();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (iMyAidlInterface != null) {
            try {
                iMyAidlInterface.onResume();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (iMyAidlInterface != null) {
            try {
                iMyAidlInterface.onPause();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
