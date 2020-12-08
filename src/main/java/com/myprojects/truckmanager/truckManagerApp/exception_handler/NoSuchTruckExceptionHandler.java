package com.myprojects.truckmanager.truckManagerApp.exception_handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class NoSuchTruckExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<TruckIncorrectData> handleNoSuchEmployeeException(NoSuchTruckException exception) {
        TruckIncorrectData data = new TruckIncorrectData();
        data.setInfo(exception.getMessage());
        return new ResponseEntity<>(data, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<TruckIncorrectData> handleException(Exception exception) {
        TruckIncorrectData data = new TruckIncorrectData();
        data.setInfo(exception.getMessage());
        return new ResponseEntity<>(data, HttpStatus.BAD_REQUEST);
    }

}
