package com.myprojects.truckmanager.truckManagerApp.service.impl;

import com.myprojects.truckmanager.truckManagerApp.model.Log;
import com.myprojects.truckmanager.truckManagerApp.model.Shipment;
import com.myprojects.truckmanager.truckManagerApp.model.User;
import com.myprojects.truckmanager.truckManagerApp.repository.LogRepository;
import com.myprojects.truckmanager.truckManagerApp.repository.UserRepository;
import com.myprojects.truckmanager.truckManagerApp.service.LogService;
import com.myprojects.truckmanager.truckManagerApp.validation.IAuthenticationFacade;
import org.apache.commons.math3.util.Precision;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogRepository logRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IAuthenticationFacade authenticationFacade;

    @Override
    @Transactional
    public void writeShipmentCompletedLog(Shipment shipment) {
        Log shipmentCompletedLog = new Log();
        String logText = String.format("Shipment from %s to %s completed. Income %f received",
                shipment.getDepartureLocation().getCity(), shipment.getArrivalLocation().getCity(),
                Precision.round(shipment.getIncome(), 2));

        shipmentCompletedLog.setLogText(logText);
        shipmentCompletedLog.setLogTime(new Timestamp(new Date().getTime()));
        shipmentCompletedLog.setUser(shipment.getCompany().getUser());

        logRepository.save(shipmentCompletedLog);
    }

    @Transactional(readOnly = true)
    @Override
    public long getLogRecordsCount(Long userId) {
        return logRepository.findLogByUser_Id(userId).size();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Log> getLogsOnPageByUserId(Long userId, int pageHumber, int logsOnPageNumber) {
        return logRepository.findLogOnPageByUser_Id(userId, PageRequest.of(pageHumber, logsOnPageNumber));
    }

    private User getUserFromAuth() {
        String userNickName = authenticationFacade.getAuthentication().getName();
        return userRepository.findWithRolesByNickName(userNickName);
    }
}
