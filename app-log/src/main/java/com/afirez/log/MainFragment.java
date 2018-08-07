package com.afirez.log;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.polidea.rxandroidble2.RxBleClient;
import com.polidea.rxandroidble2.scan.ScanSettings;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {


    private static final String DEVICE_NAME_BLOOD = "eBlood-Pressure";
    private static final UUID UUID_BLOOD_NOTIFY = UUID.fromString("0000fff4-0000-1000-8000-00805f9b34fb");


    private static final String DEVICE_NAME_WEIGHT = "000FatScale01";
    private static final UUID UUID_WEIGHT_NOTIFY = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_WEIGHT_SERVICE = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");

    private static final String DEVICE_NAME_SUGAR = "Bioland-BGM";
    private static final UUID UUID_SUGAR_WRITEABLE_SERVICE = UUID.fromString("00001000-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_SUGAR_WRITABLE = UUID.fromString("00001001-0000-1000-8000-00805f9b34fb");
    private static final byte[] DATA_SUGAR_TO_WRITE = {0x5A, 0x0A, 0x03, 0x10, 0x05, 0x02, 0x0F, 0x21, 0x3B, (byte) 0xEB};
    private static final UUID UUID_SUGAR_NOTIFY = UUID.fromString("00001002-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_SUGAR_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    private static final UUID UUID_SUGAR_READABLE = UUID.fromString("00001003-0000-1000-8000-00805f9b34fb");

    private static final String DEVICE_NAME_THREE_IN_ONE = "BeneCheck";
    private static final UUID UUID_THREE_IN_ONE_NOTIFY = UUID.fromString("00002a18-0000-1000-8000-00805f9b34fb");


    private RxBleClient rxBleClient;
    private RxPermissions rxPermissions;
    private TextView tvLog;
    private ScrollView svLog;


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rxPermissions = new RxPermissions(getActivity());
        rxBleClient = RxBleClient.create(getActivity());
//        setRetainInstance(true);
//        Singleton.get(this);
    }

    private final Timber.Tree tree = new Timber.Tree() {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (tvLog != null) {
                tvLog.post(() -> tvLog.append(String.format("%s %s %s\n", tag, message, t == null ? "" : t.getMessage())));
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        tvLog = ((TextView) view.findViewById(R.id.tv_log));
        svLog = ((ScrollView) view.findViewById(R.id.sv_log));
        view.findViewById(R.id.btn_sugar_ml).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sugar();
            }
        });
        view.findViewById(R.id.btn_blood_ml).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detection(DEVICE_NAME_BLOOD, UUID_BLOOD_NOTIFY);
            }
        });
        view.findViewById(R.id.btn_weigh_ml).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detection(DEVICE_NAME_WEIGHT, UUID_WEIGHT_NOTIFY);
            }
        });
        view.findViewById(R.id.btn_three_in_one_ml).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detection(DEVICE_NAME_THREE_IN_ONE, UUID_THREE_IN_ONE_NOTIFY);
            }
        });
        return view;
    }

    private Disposable flowDisposable = Disposables.empty();
    private Disposable disposable = Disposables.empty();
    private Disposable scanDisposable = Disposables.empty();
    private Disposable disposable2 = Disposables.empty();

    private void detection(String deviceName, UUID nitofy) {
        if (tvLog != null) {
            tvLog.setText("");
        }
        disposable.dispose();
        disposable2.dispose();
        AtomicBoolean hasFind = new AtomicBoolean(false);
        AtomicBoolean hasComplete = new AtomicBoolean(false);
        disposable = Observable.just(1)
                .compose(rxPermissions.ensure(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN
                ))
                .flatMap(grated -> {
                    if (grated) {
                        return rxBleClient
                                .scanBleDevices(new ScanSettings.Builder()
                                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                                        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                                        .build()
                                );
                    }
                    return Observable.error(new RuntimeException());
                })
                .doOnNext(scanResult -> Timber.i("<- scan: %s %s",
                        scanResult.getBleDevice().getName(),
                        scanResult.getBleDevice().getMacAddress()))
                .filter(scanResult -> deviceName.equals(scanResult.getBleDevice().getName())
                        || (!TextUtils.isEmpty(scanResult.getBleDevice().getName())
                        && scanResult.getBleDevice().getName().contains(deviceName)))
                .doOnNext(scanResult -> Timber.i("<- scan [Found]: %s %s",
                        scanResult.getBleDevice().getName(),
                        scanResult.getBleDevice().getMacAddress()))
                .flatMap(scanResult -> {
                    if (hasFind.compareAndSet(false, true)) {
                        return rxBleClient.getBleDevice(scanResult.getBleDevice().getMacAddress())
                                .establishConnection(false);
                    }
                    return Observable.empty();
                })
                .flatMap(rxBleConnection -> rxBleConnection.setupNotification(nitofy)
                        .doOnSubscribe(disposable1 ->
                                Timber.i("setupNotification %s", nitofy.toString())))
                .flatMap(notification -> notification)
                .subscribeOn(Schedulers.io())
                .subscribe(bytes -> {
                    if (deviceName.equals(DEVICE_NAME_BLOOD)) {
                        parseBloodData(hasComplete, bytes);
                    } else if (deviceName.equals(DEVICE_NAME_WEIGHT)) {
                        parseWeightData(hasComplete, bytes);
                    } else if (deviceName.equals(DEVICE_NAME_THREE_IN_ONE)) {
                        parseThreeInOneData(hasComplete, bytes);
                    }
                }, Timber::i);


//        view.findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager fm = getFragmentManager();
//                if (fm != null) {
//                    DialogFragment login = (DialogFragment) fm.findFragmentByTag("login");
//                    if (login == null) {
//                        login = new BaseDialogFragment();
//                    }
//                    login.setTargetFragment(MainFragment.this, CODE_LOGIN);
//                    login.show(fm, "login");
//                }
//            }
//        });
    }

    private void observeState() {
        // switchMap makes sure that if the state will change the rxBleClient.scanBleDevices() will dispose and thus end the scan
// everything should work
// basically no functionality will work here
// scanning and connecting will not work
// scanning and connecting will not work
// scanning will not work
// Process scan result here.
// Handle an error here.
        flowDisposable.dispose();
        flowDisposable = rxBleClient.observeStateChanges()
                .switchMap(state -> {// switchMap makes sure that if the state will change the rxBleClient.scanBleDevices() will dispose and thus end the scan
                    Timber.i("state = %s ", state);
                    switch (state) {
                        case READY:
                            // everything should work
//                            return rxBleClient.scanBleDevices();
                        case BLUETOOTH_NOT_AVAILABLE:
                            // basically no functionality will work here
                        case LOCATION_PERMISSION_NOT_GRANTED:
                            // scanning and connecting will not work
                        case BLUETOOTH_NOT_ENABLED:
                            // scanning and connecting will not work
                        case LOCATION_SERVICES_NOT_ENABLED:
                            // scanning will not work
                        default:
                            return Observable.empty();
                    }
                })
                .subscribe(
                        rxBleScanResult -> {
                            // Process scan result here.
                        },
                        throwable -> {
                            // Handle an error here.
                        }
                );
    }

    private void sugar() {
        String deviceName = DEVICE_NAME_SUGAR;
        UUID nitofy = UUID_SUGAR_NOTIFY;

        disposable.dispose();
        if (tvLog != null) {
            tvLog.setText("");
        }
        AtomicBoolean hasFind = new AtomicBoolean(false);
        AtomicBoolean hasComplete = new AtomicBoolean(false);
        AtomicBoolean hasWrite = new AtomicBoolean(false);
        disposable = Observable.just(1)
                .compose(rxPermissions.ensure(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN
                )).flatMap(grated -> {
                    if (grated) {
                        return rxBleClient
                                .scanBleDevices(new ScanSettings.Builder()
                                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                                        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                                        .build()
                                );
                    }
                    return Observable.error(new RuntimeException());
                })
                .doOnSubscribe(disposable1 -> scanDisposable = disposable1)
                .doOnNext(scanResult -> Timber.i("<- scan: %s %s",
                        scanResult.getBleDevice().getName(),
                        scanResult.getBleDevice().getMacAddress()))
                .filter(scanResult -> deviceName.equals(scanResult.getBleDevice().getName()))
                .doOnNext(scanResult -> Timber.i("<- scan [Found]: %s %s",
                        scanResult.getBleDevice().getName(),
                        scanResult.getBleDevice().getMacAddress()))
                .flatMap(scanResult -> {
                    if (hasFind.compareAndSet(false, true)) {
                        scanDisposable.dispose();
//                        RxBleDevice bleDevice = rxBleClient.getBleDevice(scanResult.getBleDevice().getMacAddress());
                        return scanResult.getBleDevice().establishConnection(false);
                    }
                    return Observable.empty();
                })
                .flatMap(rxBleConnection -> rxBleConnection.setupNotification(nitofy)
                        .doOnSubscribe(disposable1 -> Timber.i("setupNotification %s", nitofy.toString()))
                        .doOnNext(observable -> {
                            if (hasWrite.compareAndSet(false, true)) {
                                disposable2 = rxBleConnection.writeCharacteristic(UUID_SUGAR_WRITABLE, DATA_SUGAR_TO_WRITE)
                                        .doOnSubscribe(disposable1 -> Timber.i("writeCharacteristic: %s %s",
                                                UUID_SUGAR_WRITABLE, DATA_SUGAR_TO_WRITE))
                                        .doOnSuccess(bytes -> Timber.i("<- writeCharacteristic: length = %s", String.valueOf(bytes.length)))
                                        .doOnDispose(() -> Timber.i("writeCharacteristic Dispose"))
                                        .delay(1, TimeUnit.SECONDS)
                                        .repeat(3)
                                        .subscribe();
                            }
                        }))
                .flatMap(notification -> notification)
                .doOnNext(bytes -> Timber.i("<- Notification: length = %s", bytes.length))
                .subscribeOn(Schedulers.io())
                .subscribe(bytes -> parseSugarData(hasComplete, bytes), Timber::i);
    }

    private void parseSugarData(AtomicBoolean hasComplete, byte[] bytes) {
        if (bytes.length >= 12
                && hasComplete.compareAndSet(false, true)) {
            float sugar = ((float) (bytes[10] << 8) + (float) (bytes[9] & 0xff)) / 18;
            Timber.i("sugar = %s", sugar);
        }
    }

    private void parseWeightData(AtomicBoolean hasComplete, byte[] bytes) {
        Timber.i("data");
        if (bytes.length == 14 && (bytes[1] & 0xff) == 221
                && hasComplete.compareAndSet(false, true)) {
            float weight = ((float) (bytes[2] << 8) + (float) (bytes[3] & 0xff)) / 10;
            float height = 172.0f / 100;
//            18.5 < bodyFat < 23.9
            float bodyFat = weight / (height * height);
            Timber.i("weight = %s, height = %s, bodyFat = %s", weight, height, bodyFat);
        }
    }

    private void parseBloodData(AtomicBoolean hasComplete, byte[] bytes) {
        if (bytes.length == 2) {
            Timber.i("%s %s %s", bytes[1] & 0xff, 0, 0);
            return;
        }
        if (hasComplete.compareAndSet(false, true) || bytes.length == 12) {
            Timber.i("%s %s %s", bytes[2] & 0xff, bytes[4] & 0xff, bytes[8]);
        }
    }

    private void parseThreeInOneData(AtomicBoolean hasComplete, byte[] bytes) {
        if (bytes.length < 13) {
            return;
        }
        int temp = ((bytes[11] & 0xff) << 8) + (bytes[10] & 0xff);
        int basic = (int) Math.pow(16, 3);
        int flag = temp / basic;
        int number = temp % basic;
        double result = number / Math.pow(10, 13 - flag);
        if (hasComplete.compareAndSet(false, true)) {
            int catogery = 0;
            if (bytes[1] == 65) {//血糖
                catogery = 1;
            } else if (bytes[1] == 81) {//尿酸
                catogery = 2;
            } else if (bytes[1] == 97) {//胆固醇
                catogery = 3;
            }
            Timber.i("%s %s", catogery, result);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        Timber.plant(tree);
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.uproot(tree);
        flowDisposable.dispose();
        disposable.dispose();
        disposable2.dispose();
    }

    private static final int CODE_LOGIN = 18;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (requestCode == CODE_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(activity, "OK", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Cancel", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
