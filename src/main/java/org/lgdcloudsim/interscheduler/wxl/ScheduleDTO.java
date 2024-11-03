package org.lgdcloudsim.interscheduler.wxl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 魏鑫磊
 * @date 2024/4/27 22:22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {

    private Integer datacenterId;

    private Integer schedulerId;

    private CloudEnvDTO cloudEnv;

    private List<UserRequestDTO> userRequestList;

}
