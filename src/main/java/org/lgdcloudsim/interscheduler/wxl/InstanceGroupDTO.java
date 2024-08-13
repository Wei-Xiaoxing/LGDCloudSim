package org.lgdcloudsim.interscheduler.wxl;

import lombok.Data;
import org.lgdcloudsim.request.Instance;
import org.lgdcloudsim.request.InstanceGroup;

/**
 * @author 魏鑫磊
 * @date 2024/4/28 16:01
 */
@Data
public class InstanceGroupDTO {

    private int id;

    private long cpu;

    private long sto;

    private double accessDelay;

    private long time;

    public InstanceGroupDTO(){}
    public InstanceGroupDTO(InstanceGroup instanceGroup){
        this.setId(instanceGroup.getId());
        this.setCpu(instanceGroup.getCpuSum());
        this.setSto(instanceGroup.getStorageSum());
        this.setAccessDelay(instanceGroup.getAccessLatency());
        long time = 0;
        for (Instance instance: instanceGroup.getInstances()) {
            time += instance.getLifecycle();
        }
        time /= instanceGroup.getInstances().size();
        this.setTime(time);
    }
}
