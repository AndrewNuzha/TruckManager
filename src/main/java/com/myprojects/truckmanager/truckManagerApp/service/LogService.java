package com.myprojects.truckmanager.truckManagerApp.service;

import com.myprojects.truckmanager.truckManagerApp.model.Log;
import com.myprojects.truckmanager.truckManagerApp.model.Shipment;
import org.springframework.data.domain.Page;

public interface LogService {

    void writeShipmentCompletedLog(Shipment shipment);

    long getLogRecordsCount(Long userId);

    Page<Log> getLogsOnPageByUserId(Long userId, int pageHumber, int logsOnPageNumber);

}
