package yellow.jogging.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import yellow.jogging.beans.Jogging;
import yellow.jogging.db.dao.JoggingDao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("api/authenticated/jogging")
public class JoggingController {

    SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    JoggingDao joggingDao;


    @GetMapping
    public ResponseEntity getJogging(@RequestParam(value = "id", required = false) Integer id) {
        Map<String, Object> answer = new HashMap<>();
        boolean hasError = false;
        if (id == null) {
            List<Jogging> joggingList = joggingDao.getJoggingList();

            answer.put("list", joggingList);
            answer.put("totalNumRecords", joggingList.size());
        } else {
            Jogging jogging = joggingDao.getJogging(id);
            if (jogging == null) {
                hasError = true;
                answer.put("errorMessage", String.format("Can't find jogging for id %s", id));
            } else {
                answer.put("jogging", jogging);
            }
        }
        ResponseEntity response;
        if (hasError) {
            response = ResponseEntity.badRequest().body(answer);
        } else {
            response = ResponseEntity.ok(answer);
        }
        return response;
    }

    @PostMapping
    public ResponseEntity createJogging(@RequestPart("json") Jogging jogging) {
        Map<String, Object> answer = new HashMap<>();
        boolean hasError = false;
        List<String> errors = validateJogging(jogging);
        if (errors.size() == 0) {
            joggingDao.createJogging(jogging);
            answer.put("jogging", jogging);
        } else {
            hasError = true;
            answer.put("errors", errors);
        }
        ResponseEntity response;
        if (hasError) {
            response = ResponseEntity.badRequest().body(answer);
        } else {
            response = ResponseEntity.ok(answer);
        }
        return response;
    }

    @PutMapping
    public ResponseEntity updateJogging(@RequestBody Jogging jogging) {
        Map<String, Object> answer = new HashMap<>();
        boolean hasError = false;
        List<String> errors = validateJogging(jogging);
        if (errors.size() == 0) {
            boolean success = joggingDao.updateJogging(jogging);
            if (success) {
                answer.put("jogging", jogging);
            } else {
                hasError = true;
                answer.put("errorMessage", "Can't update jogging. Check input data.");
            }

        } else {
            hasError = true;
            answer.put("errors", errors);
        }
        ResponseEntity response;
        if (hasError) {
            response = ResponseEntity.badRequest().body(answer);
        } else {
            response = ResponseEntity.ok(answer);
        }
        return response;
    }

    @DeleteMapping
    public ResponseEntity deleteJogging(@RequestParam(value = "id") int id) {
        Map<String, Object> answer = new HashMap<>();
        boolean success = joggingDao.deleteJogging(id);
        if(success){
            answer.put("message",String.format("Jogging with id %s successfully deleted",id));
        }else{
            answer.put("errorMessage",String.format("Can't find jogging for id %s to delete",id));
        }
        return ResponseEntity.ok(answer);
    }

    @GetMapping("/statistic")
    public ResponseEntity getJoggingStatistic(@RequestParam(value = "dateFrom") String pDateFrom,
                                                @RequestParam(value = "dateTo") String pDateTo) {
        Map<String, Object> answer = new HashMap<>();
        boolean hasError = false;
        Date dateFrom = null;
        Date dateTo = null;
        try {
            dateFrom = dt.parse(pDateFrom);
            dateTo = dt.parse(pDateTo);
        } catch (ParseException e) {
            hasError = true;
            answer.put("errorMessage","Wrong date format. Must be yyyy-MM-DD");
        }
        if(!hasError && dateFrom.after(dateTo)){
            hasError = true;
            answer.put("errorMessage", "DateFrom should be before DateTo");
        }else{
            int totaldDistance= 0;
            double avSpeed = 0, avTime = 0;
            List<Jogging> joggingList = joggingDao.getJoggingListForPeriod(dateFrom,dateTo);
            for(Jogging jogging : joggingList){
                double distance = (double) jogging.getDistance();
                double duration  = (double) jogging.getDuration();
                totaldDistance += distance;
                avSpeed += distance/duration;
                avTime += duration;
            }
            avTime /= joggingList.size();
            avSpeed /= joggingList.size();
            Map<String,Object> period = new HashMap<>();
            period.put("dateFrom",dateFrom);
            period.put("dateTo",dateTo);
            answer.put("period",period);
            answer.put("averageSpeed", avSpeed);
            answer.put("averageTime", avTime);
            answer.put("totalDistance", totaldDistance);
        }
        ResponseEntity response;
        if (hasError) {
            response = ResponseEntity.badRequest().body(answer);
        } else {
            response = ResponseEntity.ok(answer);
        }
        return response;
    }


    private List<String> validateJogging(Jogging jogging) {
        List<String> errorMessages = new ArrayList<>();
        if (jogging.getDate() == null) {
            errorMessages.add("Date should be present");
        }
        if (jogging.getDistance() <= 0) {
            errorMessages.add("Distance should be positive");
        }
        if (jogging.getDuration() <= 0) {
            errorMessages.add("Duration should be positive");
        }
        return errorMessages;
    }




}
