package gmutils.security;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.annotation.RequiresPermission;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.Policy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import gmutils.logger.Logger;
import gmutils.security.Security;
import gmutils.storage.GeneralStorage;
import kotlin.Deprecated;

public class DeviceIdentifier {
    private final GeneralStorage deviceIdStorage;

    public DeviceIdentifier() {
        this(GeneralStorage.getInstance("devid"));
    }

    public DeviceIdentifier(GeneralStorage deviceIdStorage) {
        this.deviceIdStorage = deviceIdStorage;
        Logger.d().printMethod(() -> "contractor:::\n" +
                "Android SDK: " + Build.VERSION.SDK_INT + ", " +
                "Release: " + Build.VERSION.RELEASE
        );
    }

    //==============================================================================================

    private Security.EncryptDecryptInterface encDecInstance() {
        int key = this.getClass().getName().hashCode();
        return Security.getSimpleInstance(key);
    }

    private String decodeAndGetDeviceId(/*boolean dependOnMacAddress, boolean dependOnAndroidId*/) {
        String devId = deviceIdStorage.retrieve("devid", "");
        if (TextUtils.isEmpty(devId)) return "";

        String decText;
        try {
            decText = encDecInstance().decrypt(devId);
        } catch (Exception e) {
            decText = devId;
        }

//        String t1 = "DOM" + (dependOnMacAddress ? "1" : "0");
//        if (!decText.contains(t1)) return "";
//        String t2 = "DOA" + (dependOnAndroidId ? "1" : "0");
//        if (!decText.contains(t2)) return "";

        return decText;
    }

    private String encodeAndSaveDeviceId(String id, boolean byMac/*, boolean dependOnMacAddress, boolean dependOnAndroidId*/) {
        StringBuilder devId = new StringBuilder();
        devId.append(id);

        devId.append("-BY");
        devId.append(byMac ? "M" : "A");

//        devId.append("-DOM");
//        devId.append(dependOnMacAddress ? "1" : "0");
//        devId.append("-DOA");
//        devId.append(dependOnAndroidId ? "1" : "0");

        String encText;
        try {
            encText = encDecInstance().encrypt(devId.toString());
        } catch (Exception e) {
            encText = devId.toString();
        }

        deviceIdStorage.save("devid", encText);

        return devId.toString();
    }

    //==============================================================================================

    public String getSavedIdentifier() {
        Logger.d().printMethod();

        String deviceId = decodeAndGetDeviceId(/*dependOnMacAddress, dependOnAndroidId*/);
        if (!deviceId.isEmpty()) {
            Logger.d().print(() -> this.getClass().getSimpleName(), () -> "device id already obtained");
            return deviceId;
        }

        return "";
    }

    @RequiresPermission(allOf = {
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
    })
    public String getIdentifierBasedMac(Context context, boolean hashed) throws MacAddressSecuredException, SocketException, WifiEnabledException {
        Logger.d().printMethod(() -> "hashed: " + hashed);

        String id = "";
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Logger.d().print(() -> this.getClass().getSimpleName(), () -> "obtaining device id by mac (< android-10)");
            id = getMacAddress(hashed);
        }
        //
        else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            Logger.d().print(() -> this.getClass().getSimpleName(), () -> "obtaining device id by mac (== android-10)");
            if (isWifiEnabled(context)) {
                Logger.d().print(() -> this.getClass().getSimpleName(), () -> "wifi is enabled");
                throw new WifiEnabledException();
            } else {
                id = getMacAddress(hashed);
            }
        }

        if (!id.isEmpty()) {
            id = encodeAndSaveDeviceId(id, true/*, dependOnMacAddress, dependOnAndroidId*/);
        }

        return id;

    }

    public String getIdentifierBasedAndroidId(Context context, boolean hashed) {
        Logger.d().printMethod(() -> "hashed: " + hashed);

        String androidId = getAndroidId(context, hashed);
        String deviceId = encodeAndSaveDeviceId(androidId, false/*, false, true*/);
        return deviceId;
    }

    @RequiresPermission(allOf = {
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
    })
    public String getIdentifier(Context context, boolean hashed, boolean isMacPriority) throws WifiEnabledException {
        Logger.d().printMethod(() -> "hashed: " + hashed + ", isMacPriority: " + isMacPriority);

        String identifier = getSavedIdentifier();
        if (!identifier.isEmpty()) {
            return identifier;
        }

        try {
            identifier = getIdentifierBasedMac(context, hashed);
        } catch (WifiEnabledException e) {
            if (isMacPriority) throw e;
        } catch (Exception e) {
            identifier = "";
        }

        if (!identifier.isEmpty()) {
            return identifier;
        }

        identifier = getIdentifierBasedAndroidId(context, hashed);

        return identifier;
    }

    //==============================================================================================

    public boolean isWifiEnabled(Context context) {
        WifiManager wifi = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean wifiEnabled = wifi.isWifiEnabled();
        return wifiEnabled;
    }

    @RequiresPermission(allOf = {
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
    })
    @Deprecated(message = "valid until android-9 (api-28 (P))")
    public String getMacAddress(boolean hashed) throws MacAddressSecuredException, SocketException {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            Logger.d().printMethod(() -> "hashed: " + hashed);

            List<NetworkInterface> networkInterfaceList = Collections.list(
                    NetworkInterface.getNetworkInterfaces()
            );

            String stringMac = "";

            for (NetworkInterface networkInterface : networkInterfaceList) {
                Logger.d().printMethod(networkInterface::getName);

                if (networkInterface.getName().equalsIgnoreCase("wlan0")) {
                    Logger.d().printMethod(() -> ">>>>>>>>>>>>>>>>>>>> wlan0 is found <<<<<<<<<<<<<<<<<");

                    for (int i = 0; i < networkInterface.getHardwareAddress().length; i++) {
                        if (!stringMac.isEmpty()) stringMac += ":";

                        String stringMacByte = Integer.toHexString(networkInterface.getHardwareAddress()[i] & 0xFF);

                        if (stringMacByte.length() == 1) {
                            stringMacByte = "0" + stringMacByte;
                        }

                        stringMac = stringMac + stringMacByte.toUpperCase();
                    }

                    String finalStringMac1 = stringMac;
                    Logger.d().printMethod(() -> "MAC Address (" + networkInterface.getName() + "): " + finalStringMac1);

                    break;
                }
            }

            if (stringMac.isEmpty()) {
                throw new MacAddressSecuredException("Wifi is not provided");
            }

            if (hashed) {
                UUID deviceUuid = new UUID(
                        stringMac.hashCode(),
                        ((long) stringMac.hashCode() << 32) | ((long) stringMac.hashCode() >> 16)
                );
                String deviceId = deviceUuid.toString();
                stringMac = deviceId;
            }

            String finalStringMac = stringMac;
            Logger.d().printMethod(() -> "device-id: " + finalStringMac);

            return stringMac;
        } else {
            Logger.d().printMethod(() -> "NOT-ALLOWED-IN-SDK-" + Build.VERSION.SDK_INT);
            throw new MacAddressSecuredException("NOT-ALLOWED-IN-SDK-" + Build.VERSION.SDK_INT);
        }
    }

    @SuppressLint("HardwareIds")
    public String getAndroidId(Context context, boolean hashed) {
        String androidId = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        Logger.d().printMethod(() -> "android-id => " + androidId);

        if (hashed) {
            UUID deviceUuid = new UUID(
                    androidId.hashCode(),
                    ((long) androidId.hashCode() << 32) | ((long) androidId.hashCode() >> 16)
            );
            String deviceId = deviceUuid.toString();
            Logger.d().printMethod(() -> "hashed: " + hashed + " >>> device-id: " + deviceId);
            return deviceId;
        } else {
            Logger.d().printMethod(() -> "hashed: " + hashed + " >>> device-id: " + androidId);
            return androidId;
        }
    }

    //==============================================================================================

    @RequiresPermission(allOf = {
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
    })
    public String getAvailableNicInfo(Context context) {
        Logger.d().printMethod();

        Policy policy = Policy.getPolicy();
        Logger.d().print(() -> this.getClass().getSimpleName(), () -> "Policy: " + policy);
//        Logger.d().print(this.getClass().getSimpleName(), () -> "Policy.Type: " + policy.getType());
//        Policy.Parameters parameters = policy.getParameters();
//        Logger.d().print(this.getClass().getSimpleName(), () -> "Policy.Parameters: " + parameters);
//        Provider provider = policy.getProvider();
//        Logger.d().print(this.getClass().getSimpleName(), () -> "Policy.provider: " + provider);
//        Logger.d().print(this.getClass().getSimpleName(), () -> "Policy.provider.name: " + provider.getName());
//        Logger.d().print(this.getClass().getSimpleName(), () -> "Policy.provider.info: " + provider.getInfo());
//        Logger.d().print(this.getClass().getSimpleName(), () -> "Policy.provider.name: " + provider.getName());
//        Logger.d().print(this.getClass().getSimpleName(), () -> "Policy.provider.elements: " + Arrays.toString(Collections.list(provider.elements()).toArray()));
//        Logger.d().print(this.getClass().getSimpleName(), () -> "Policy.provider.entrySet: " + Arrays.toString(provider.entrySet().toArray()));
//        Logger.d().print(this.getClass().getSimpleName(), () -> "Policy.provider.Services: " + Arrays.toString(provider.getServices().toArray()));

        try {
            StringBuilder stringMac = new StringBuilder();

            stringMac.append("----------------------------\n")
                    .append("| ")
                    .append("Android-API:")
                    .append(Build.VERSION.SDK_INT)
                    .append(" |\n")
                    .append("----------------------------\n\n");

            stringMac.append("NetworkInterface:::");

            Logger.d().printMethod(() -> "Android Version: " + Build.VERSION.SDK_INT);

            List<NetworkInterface> networkInterfaceList = Collections.list(
                    NetworkInterface.getNetworkInterfaces()
            );

            for (NetworkInterface networkInterface : networkInterfaceList) {
                Logger.d().printMethod(networkInterface::getName);

                if (
                        networkInterface.getName().equals("wlan0")
                                ||
                                networkInterface.getHardwareAddress() != null
                ) {
                    Logger.d().print(() -> this.getClass().getSimpleName(), () -> ">>>>>>>>>>>>>>>>> FOUND-NIC => " + networkInterface.getName());
                }

                stringMac.append("\n-------------------------------------");

                stringMac.append("\nIndex: ");
                stringMac.append(networkInterface.getIndex());

                stringMac.append("\nName: ");
                stringMac.append(networkInterface.getName());

                stringMac.append("\nHardwareAddress: ");
                stringMac.append("\n\t\t");
                stringMac.append(Arrays.toString(networkInterface.getHardwareAddress()));
                if (networkInterface.getHardwareAddress() != null) {
                    stringMac.append("\n\t\t");

                    for (byte b : networkInterface.getHardwareAddress()) {
                        stringMac.append(String.format("%02X:", b));
                    }
                    stringMac.deleteCharAt(stringMac.length() - 1);
                }
//                    }
            }

            stringMac.append("\n\n\nAndroidId: ");
            stringMac.append("\n-------------------------------------");
            stringMac.append("\n\t\t").append(getAndroidId(context, false));
            stringMac.append("\n\t\t").append(getAndroidId(context, true));

            Logger.d().print(() -> this.getClass().getSimpleName(), () -> stringMac);

            return stringMac.toString();
        } catch (SocketException e) {
            Logger.d().printMethod(() -> "EXCEPTION:: " + e);
            return e.toString();
        }
    }

    //==============================================================================================

    public static class WifiEnabledException extends Exception {
        public WifiEnabledException() {
            this("Wifi is enabled, and it must be disabled for reading a correct mac address");
        }

        public WifiEnabledException(String message) {
            super(message);
        }
    }

    public static class MacAddressSecuredException extends Exception {
        public MacAddressSecuredException() {
        }

        public MacAddressSecuredException(String message) {
            super(message);
        }
    }

}
