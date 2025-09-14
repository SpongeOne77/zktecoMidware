package com.zkteco.attpush.entity.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Component
@ConfigurationProperties(prefix = "deviceConfig")
public class DeviceConfig {
    private Map<String, List<Device>> areaDevices;

    private Map<String, String> snToArea = new HashMap<>();

    private Map<String, Device> snToDevice = new HashMap<>();

    @PostConstruct
    public void initSnToAreaMap() {
        if (areaDevices == null) {
            return;
        }
        for (Map.Entry<String, List<Device>> entry : areaDevices.entrySet()) {
            String area = entry.getKey();
            for (Device device : entry.getValue()) {
                snToArea.put(device.getSN(), area);
                snToDevice.put(device.getSN(), device);
            }
        }
    }

    public List<String> getSnsByArea(String area) {
        List<Device> devices = areaDevices.getOrDefault(area, Collections.emptyList());
        return devices.stream().map(Device::getSN).collect(Collectors.toList());
    }

    /**
     * get devices info by area
     * @param area
     * @return list of devices
     */
    public List<Device> getDevicesByArea(String area) {
        return areaDevices.getOrDefault(area, Collections.emptyList());
    }

    public String getAreaBySn(String sn) {
        return snToArea.get(sn);
    }

    // get devices info from same region by sn
    public List<Device> getDevicesBySn(String sn) {
        return getDevicesByArea(snToArea.get(sn));
    }

    public Device getDeviceBySn(String sn) {
        return snToDevice.get(sn);
    }

    public String getDirectionBySn(String sn) {
        Device device = snToDevice.get(sn);
        return device != null ? device.getDirection() : null;
    }

    private List<String> adminList;
}
