package org.lgdcloudsim.interscheduler.wxl;

import lombok.Data;

import java.util.List;

/**
 * @author 魏鑫磊
 * @date 2024/4/27 22:20
 */
@Data
public class DatacenterDTO {

    private Integer id;

    private Double cpuPrice;

    private Double stoPrice;

    private List<SchedulerDTO> schedulerList;
}
