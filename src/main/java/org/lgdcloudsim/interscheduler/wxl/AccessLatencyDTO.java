package org.lgdcloudsim.interscheduler.wxl;

import lombok.Data;

/**
 * @author 魏鑫磊
 * @date 2024/8/13 19:56
 */
@Data
public class AccessLatencyDTO {

    private Integer datacenterId;

    private Double value;

    public AccessLatencyDTO(){}
    public AccessLatencyDTO(Integer id, Double value){
        this.datacenterId = id;
        this.value = value;
    }
}
