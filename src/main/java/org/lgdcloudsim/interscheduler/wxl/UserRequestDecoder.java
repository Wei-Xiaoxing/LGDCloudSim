package org.lgdcloudsim.interscheduler.wxl;

import org.lgdcloudsim.request.InstanceGroup;
import org.lgdcloudsim.request.UserRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 魏鑫磊
 * @date 2024/4/27 15:45
 */
public class UserRequestDecoder {

    public static UserRequestDecodeResult toDTO(List<InstanceGroup> instanceGroups){
        UserRequestDecodeResult result;

        Map<UserRequest, List<InstanceGroup>> map=new HashMap<>();
        for (InstanceGroup instanceGroup :
                instanceGroups) {
            UserRequest userRequest=instanceGroup.getUserRequest();
            if (!map.containsKey(userRequest)){
                map.put(userRequest, new ArrayList<>());
            }
            map.get(userRequest).add(instanceGroup);
        }
        List<UserRequest> userRequestList = new ArrayList<>();
        List<UserRequestDTO> userRequestDTOList=new ArrayList<>();
        for (UserRequest userRequest :
                map.keySet()) {
            userRequestList.add(userRequest);
            List<InstanceGroup> instanceGroups1=map.get(userRequest);
            userRequestDTOList.add(new UserRequestDTO(userRequest, instanceGroups1));
        }
//        System.out.println(userRequestDTOList);
        return new UserRequestDecodeResult(userRequestList, userRequestDTOList);
    }

}
