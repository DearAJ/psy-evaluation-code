package org.ww.service.DubboService.Provider.ServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.ww.EntityDO.PsyIntervationFileDO;
import org.ww.result.WrapMapper;
import org.ww.result.Wrapper;
import org.ww.service.DubboService.Provider.PsyFileService;
import org.ww.service.impl.TaskServiceImpl;

@Slf4j
@DubboService
public class PsyFileServiceProvider implements PsyFileService {

    @Autowired
    TaskServiceImpl taskService;

    public Wrapper<Boolean> createIntervationSchemeItem(PsyIntervationFileDO fileDO) {


        try {
            Integer res = 0;
            if((res = taskService.checkInput(fileDO) )< 0 ) {
                return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ERROR_MESSAGE, false);
            }
            res = taskService.createIntervationSchemeItem(fileDO);
            if(res < 0){
                return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ERROR_MESSAGE,true);
            }else {
                return WrapMapper.wrap(Wrapper.SUCCESS_CODE, Wrapper.SUCCESS_MESSAGE, true);
            }
        }catch (Exception e){
            return WrapMapper.wrap(Wrapper.ERROR_CODE, Wrapper.ERROR_MESSAGE,false);
        }
    }


}