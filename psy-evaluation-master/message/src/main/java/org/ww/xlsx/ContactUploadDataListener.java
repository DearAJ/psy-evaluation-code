package org.ww.xlsx;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import lombok.extern.slf4j.Slf4j;
import org.ww.domain.TelContact;
import org.ww.service.HotlineContactService;
import org.ww.utils.PhoneUtils;

import java.util.List;

@Slf4j
public class ContactUploadDataListener  implements ReadListener<TelContact> {


    private HotlineContactService hotlineContactService;

    private Long telGroupId;

    public ContactUploadDataListener(HotlineContactService hotlineContactService,
                                     Long telGroupId)
    {
        this.hotlineContactService = hotlineContactService;
        this.telGroupId = telGroupId;
    }

    private static final int BATCH_COUNT = 5;
    private List<TelContact> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    @Override
    public void invoke(TelContact data, AnalysisContext analysisContext) {
        if(data == null) return;
        data.setPhone(PhoneUtils.toFullPhoneNumber(data.getPhone()));
        data.setTelGroupId(telGroupId);

        cachedDataList.add(data);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        log.info("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */
    private void saveData() {
        hotlineContactService.batchSave(cachedDataList);
    }
}
