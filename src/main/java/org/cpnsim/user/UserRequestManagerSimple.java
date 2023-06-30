package org.cpnsim.user;

import org.cpnsim.request.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserRequestManagerSimple implements UserRequestManager {
    @Override
    public List<UserRequest> getUserRequestMap(double startTime, double endTime, int datacenterId) {
        List<UserRequest> userRequests = new ArrayList<>();
        UserRequestGenerator userRequestGenerator = new RandomUserRequestGenerator();
        int num = 3;
        for (double time = startTime; time < endTime; time += 10) {
            for (int j = 0; j < num; j++) {
                UserRequest userRequest = userRequestGenerator.generateAUserRequest();
                userRequest.setSubmitTime(time);
                userRequest.setBelongDatacenterId(datacenterId);
                userRequests.add(userRequest);
            }
        }
        return userRequests;
    }

    @Override
    public Map<Integer, List<UserRequest>> generateOnceUserRequests() {
        return null;
    }

    @Override
    public double getNextSendTime() {
        return 0;
    }
}
