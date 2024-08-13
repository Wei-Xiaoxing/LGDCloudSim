package org.lgdcloudsim.interscheduler.wxl;

import lombok.Data;
import org.lgdcloudsim.request.UserRequest;

import java.util.List;

/**
 * @author 魏鑫磊
 * @date 2024/8/13 20:04
 */
@Data
public class UserRequestDecodeResult {

    private List<UserRequest> userRequestList;

    private List<UserRequestDTO> userRequestDTOList;

    public UserRequestDecodeResult(){}
    public UserRequestDecodeResult(List<UserRequest> list1, List<UserRequestDTO> list2){
        userRequestList = list1;
        userRequestDTOList = list2;
    }
}
