package org.lgdcloudsim.interscheduler.wxl;

import lombok.Data;

import java.util.List;

/**
 * @author 魏鑫磊
 * @date 2024/11/12 22:38
 */
@Data
public class ScheduleResultDTO {

    private List<ScheduleInstanceGroupDTO> scheduleInstanceGroupList;

    private Double scheduleTime;
}
